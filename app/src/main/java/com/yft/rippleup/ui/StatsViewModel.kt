package com.yft.rippleup.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yft.rippleup.data.repo.ActivityItem
import com.yft.rippleup.data.repo.LogResult
import com.yft.rippleup.data.repo.StatsRepository
import com.yft.rippleup.data.repo.UserSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.yft.rippleup.util.GitHubSync

/**
 * Single app-wide ViewModel. Holds the repository and exposes reactive state;
 * action logging is guarded (cooldowns + daily caps) and results are surfaced
 * to callers via callbacks for immediate UI feedback.
 */
class StatsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StatsRepository(app.applicationContext)
    val gitHubSync = GitHubSync(app.applicationContext)

    val snapshot: StateFlow<UserSnapshot?> = repo.snapshot.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null,
    )

    val recentActivity: StateFlow<List<ActivityItem>> = repo.recentActivity.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList(),
    )

    /** Fires +1 each time an action is successfully logged (confetti trigger). */
    val pulseTick = MutableStateFlow(0)

    init { viewModelScope.launch { repo.ensureSeeded() } }

    fun logAction(
        actionKey: String,
        title: String,
        points: Int,
        co2Kg: Double,
        colorTag: String,
        iconTag: String,
        onResult: (LogResult) -> Unit = {},
    ) {
        viewModelScope.launch {
            val result = repo.logAction(actionKey, title, points, co2Kg, colorTag, iconTag)
            if (result is LogResult.Success) pulseTick.value += 1
            onResult(result)
        }
    }

    fun completeOnboarding(name: String) {
        viewModelScope.launch { repo.completeOnboarding(name) }
    }

    fun redeem(cost: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repo.redeem(cost)) }
    }

    fun backupToGithub(onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val payload = repo.backupPayload()
            onResult(gitHubSync.backup(payload))
        }
    }

    fun restoreFromGithub(onResult: (Result<Boolean>) -> Unit) {
        viewModelScope.launch {
            val res = gitHubSync.restore { name, points, co2, streak, actions ->
                repo.restoreFromBackup(name, points, co2, streak, actions)
            }
            onResult(res.map { it })
        }
    }
}
