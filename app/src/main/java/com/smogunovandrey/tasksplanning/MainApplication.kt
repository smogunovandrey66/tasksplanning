package com.smogunovandrey.tasksplanning

import android.app.Application
import com.smogunovandrey.tasksplanning.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}