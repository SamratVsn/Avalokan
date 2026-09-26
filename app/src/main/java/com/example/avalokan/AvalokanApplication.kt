package com.example.avalokan

import android.app.Application
import com.example.avalokan.data.AppContainer
import com.example.avalokan.data.AppDataContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AvalokanApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}