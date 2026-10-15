package com.example.avalokan

import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AvalokanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { result ->
                Log.i("AvalokanRepo", "Anonymous auth UID: ${result.user?.uid}")
            }
            .addOnFailureListener { e ->
                Log.w("AvalokanRepo", "Anonymous sign-in failed", e)
            }
    }
}