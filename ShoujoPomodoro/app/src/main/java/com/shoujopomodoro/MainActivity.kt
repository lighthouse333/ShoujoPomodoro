package com.shoujopomodoro

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shoujopomodoro.ui.navigation.NavGraph
import com.shoujopomodoro.ui.theme.ShoujoPomodoroTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoujoPomodoroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(activity = this@MainActivity)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val languageCode = getLanguageFromPrefs(newBase)
        val wrappedContext = applyLanguage(newBase, languageCode)
        super.attachBaseContext(wrappedContext)
    }

    companion object {
        private fun getLanguageFromPrefs(context: Context): String {
            return try {
                val prefs = context.getSharedPreferences("app_locale_prefs", Context.MODE_PRIVATE)
                prefs.getString("current_locale", "en") ?: "en"
            } catch (e: Exception) {
                "en"
            }
        }

        private fun applyLanguage(context: Context, languageCode: String): Context {
            val locale = when (languageCode) {
                "zh" -> Locale.SIMPLIFIED_CHINESE
                else -> Locale.ENGLISH
            }
            val config = Configuration(context.resources.configuration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocales(LocaleList(locale))
            } else {
                @Suppress("DEPRECATION")
                config.locale = locale
            }
            return context.createConfigurationContext(config)
        }
    }
}
