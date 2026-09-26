package com.example.avalokan.data

import android.content.Context

interface AppContainer {
    //add all the repositories
    //i.e. val avalokanRepository : AvalokanRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    //implementation for repositories
    //override val avalokanRepository : AvalokanRepository by lazy
}