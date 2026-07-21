package com.shoujopomodoro

import android.app.Application
import com.shoujopomodoro.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ShoujoPomodoroApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        // Initially write default language to SharedPreferences so that
        // MainActivity.attachBaseContext() can read it synchronously.
        // This prevents a race condition where DataStore hasn't loaded yet.
        val sp = getSharedPreferences("app_locale_prefs", MODE_PRIVATE)
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
    }
}
