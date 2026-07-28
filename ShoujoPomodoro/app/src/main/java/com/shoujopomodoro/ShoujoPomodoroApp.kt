package com.shoujopomodoro

import android.app.Application
import com.shoujopomodoro.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class ShoujoPomodoroApp : Application() {

    lateinit var container: AppContainer
        private set

    companion object {
        private const val PREFS_NAME = "app_locale_prefs"
        private const val DEFAULT_BGM_FILENAME = "To be Continued - Lofi.mp4"
    }

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        // Initially write default language to SharedPreferences so that
        // MainActivity.attachBaseContext() can read it synchronously.
        // This prevents a race condition where DataStore hasn't loaded yet.
        val sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val currentLocale = sp.getString("current_locale", null)
        if (currentLocale == null) {
            sp.edit().putString("current_locale", "en").apply()
        }

        // Then sync DataStore to SharedPreferences as values change
        val settingsRepo = container.timerSettingsRepository
        CoroutineScope(Dispatchers.Main).launch {
            settingsRepo.settingsFlow.collect { settings ->
                sp.edit().putString("current_locale", settings.language).apply()
            }
        }

        // Initialize default background music on first launch
        initDefaultMusic()
    }

    private fun initDefaultMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val musicDir = File(filesDir, "music")
                if (!musicDir.exists()) musicDir.mkdirs()

                val destFile = File(musicDir, DEFAULT_BGM_FILENAME)

                // Always ensure the file exists (self-healing)
                if (!destFile.exists() || destFile.length() <= 0) {
                    resources.openRawResource(R.raw.default_bgm).use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                // Ensure path is in DataStore
                if (destFile.exists() && destFile.length() > 0) {
                    val settingsRepo = container.timerSettingsRepository
                    val currentSettings = settingsRepo.settingsFlow.first()
                    val currentPaths = currentSettings.musicPaths.toMutableList()
                    if (!currentPaths.contains(destFile.absolutePath)) {
                        currentPaths.add(0, destFile.absolutePath)
                        settingsRepo.updateMusicPaths(currentPaths)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
