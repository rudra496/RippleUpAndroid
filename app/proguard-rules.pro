# RipplUp production R8 rules.

# --- Kotlin coroutines ---
-dontwarn kotlinx.coroutines.**

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- ML Kit barcode (ships consumer rules; keep API surface) ---
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# --- CameraX ---
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# --- Security Crypto (EncryptedSharedPreferences uses reflection on prefs classes) ---
-keep class androidx.security.crypto.** { *; }

# --- JSON model classes used by GitHub sync (org.json is on-platform) ---
-keepclassmembers class com.yft.rippleup.util.GitHubSync { *; }

# --- Keep Compose (R8 handles it natively; just silence harmless warnings) ---
-dontwarn androidx.compose.**

# Remove logging noise in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
