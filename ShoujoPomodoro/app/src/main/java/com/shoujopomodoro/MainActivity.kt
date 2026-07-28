package com.shoujopomodoro

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.shoujopomodoro.ui.navigation.NavGraph
import com.shoujopomodoro.ui.theme.ShoujoPomodoroTheme
import kotlinx.coroutines.delay
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ShoujoPomodoro)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isFirstLaunch = savedInstanceState == null

        setContent {
            var showSplash by remember { mutableStateOf(isFirstLaunch) }

            if (isFirstLaunch) {
                LaunchedEffect(Unit) {
                    delay(5_000L)
                    showSplash = false
                }
            }

            ShoujoPomodoroTheme {
                if (showSplash) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1A1633)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.splash_bg),
                            contentDescription = "Splash",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(activity = this@MainActivity)
                    }
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
