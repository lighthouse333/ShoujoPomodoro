package com.shoujopomodoro

import android.app.Application
import com.shoujopomodoro.di.AppContainer

class ShoujoPomodoroApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
