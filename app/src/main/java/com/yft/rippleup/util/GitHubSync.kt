package com.yft.rippleup.util

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * GitHub-backed cloud sync — "GitHub handles everything" with zero servers.
 *
 * HOW IT WORKS (secure by design):
 *  1. The user pastes their OWN GitHub token (classic token with `gist` scope,
 *     or fine-grained with gists read/write) — created at github.com/settings/tokens.
 *  2. The token is stored in EncryptedSharedPreferences (Android Keystore).
 *  3. Backup writes a SECRET gist named "ripplup-sync" in the USER'S OWN
 *     GitHub account — we never see the data, no shared server, no app secrets.
 *  4. Restore pulls the gist on any device where the user links the same token.
 *
 * Uses HttpURLConnection + org.json (both on-platform) — no extra dependencies.
 */
class GitHubSync(context: Context) {

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "ripplup_secure",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    companion object {
        private const val KEY_TOKEN = "gh_token"
        private const val KEY_GIST_ID = "gist_id"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val GIST_NAME = "ripplup-sync"
        private const val FILE_NAME = "ripplup-stats.json"
        private const val API = "https://api.github.com"
    }

    var token: String
        get() = prefs.getString(KEY_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_TOKEN, value.trim()).apply()

    val hasToken: Boolean get() = token.isNotEmpty()

    val lastSync: Long get() = prefs.getLong(KEY_LAST_SYNC, 0L)

    fun clearToken() = prefs.edit().remove(KEY_TOKEN).remove(KEY_GIST_ID).apply()

    // --- GitHub REST helpers -----------------------------------------------------

    private suspend fun request(method: String, path: String, body: String? = null): Pair<Int, String> = withContext(Dispatchers.IO) {
        val conn = URL(API + path).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = method
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            conn.setRequestProperty("User-Agent", "RipplUp-Android")
            if (body != null) {
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
            val code = conn.responseCode
            val text = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() } ?: ""
            code to text
        } catch (e: Exception) {
            -1 to (e.message ?: "network error")
        } finally {
            conn.disconnect()
        }
    } // withContext

    /** Validates the token by fetching the authenticated user. */
    suspend fun validateToken(t: String): Result<String> {
        val saved = token
        token = t // temporarily use the candidate
        val (code, body) = request("GET", "/user")
        val login = try { JSONObject(body).optString("login", "") } catch (_: Exception) { "" }
        token = saved
        return if (code == 200 && login.isNotEmpty()) Result.success(login)
        else Result.failure(IllegalStateException(
            if (code == 401) "Invalid token (check it has the gist scope)"
            else "GitHub error $code"))
    }

    /** Finds (or creates) the secret sync gist; returns its id. */
    private suspend fun gistId(): Result<String> {
        prefs.getString(KEY_GIST_ID, null)?.let { return Result.success(it) }
        val (code, body) = request("GET", "/gists?per_page=100")
        if (code != 200) return Result.failure(IllegalStateException("GitHub error $code"))
        val arr = org.json.JSONArray(body)
        for (i in 0 until arr.length()) {
            val g = arr.optJSONObject(i) ?: continue
            val desc = g.optString("description", "")
            if (desc == GIST_NAME) {
                val id = g.optString("id")
                prefs.edit().putString(KEY_GIST_ID, id).apply()
                return Result.success(id)
            }
        }
        // create it
        val payload = JSONObject().apply {
            put("description", GIST_NAME)
            put("public", false)
            put("files", JSONObject().put(FILE_NAME, JSONObject().put("content", "{}")))
        }
        val (c2, b2) = request("POST", "/gists", payload.toString())
        val id = try { JSONObject(b2).optString("id", "") } catch (_: Exception) { "" }
        return if (c2 in 200..299 && id.isNotEmpty()) {
            prefs.edit().putString(KEY_GIST_ID, id).apply()
            Result.success(id)
        } else Result.failure(IllegalStateException("create gist failed ($c2)"))
    }

    /** Backs the stats payload up to the secret gist. */
    suspend fun backup(payload: Map<String, Any>): Result<String> {
        if (!hasToken) return Result.failure(IllegalStateException("no token"))
        val id = gistId().getOrElse { return Result.failure(it) }
        val json = JSONObject(payload).toString()
        val body = JSONObject().apply {
            put("files", JSONObject().put(FILE_NAME, JSONObject().put("content", json)))
        }
        val (code, _) = request("PATCH", "/gists/$id", body.toString())
        return if (code in 200..299) {
            prefs.edit().putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply()
            Result.success("backed up")
        } else Result.failure(IllegalStateException("GitHub error $code"))
    }

    /** Restores stats from the gist; returns the parsed values via [apply]. */
    suspend fun restore(apply: suspend (name: String, points: Int, co2: Double, streak: Int, actions: Int) -> Boolean): Result<Boolean> {
        if (!hasToken) return Result.failure(IllegalStateException("no token"))
        val id = gistId().getOrElse { return Result.failure(it) }
        val (code, body) = request("GET", "/gists/$id")
        if (code != 200) return Result.failure(IllegalStateException("GitHub error $code"))
        val content = try {
            JSONObject(body)
                .getJSONObject("files").getJSONObject(FILE_NAME)
                .getString("content")
        } catch (e: Exception) {
            return Result.failure(IllegalStateException("no backup found"))
        }
        val o = JSONObject(content)
        if (o.optString("app") != "ripplup") return Result.failure(IllegalStateException("backup is not a RipplUp file"))
        val ok = apply(
            o.optString("name", "Friend"),
            o.optInt("points", 0),
            o.optDouble("co2SavedKg", 0.0),
            o.optInt("streak", 0),
            o.optInt("ecoActions", 0),
        )
        return if (ok) Result.success(true)
        else Result.failure(IllegalStateException("restore rejected by integrity guard"))
    }
}
