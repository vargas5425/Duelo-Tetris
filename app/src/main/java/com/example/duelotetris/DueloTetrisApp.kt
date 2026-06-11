package com.example.duelotetris

import android.app.Application
import com.example.duelotetris.di.AppContainer

class DueloTetrisApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}