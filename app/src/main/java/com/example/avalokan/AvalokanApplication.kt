package com.example.avalokan

import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AvalokanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Bootstrap the anonymous session off the cold-start path. Nothing on
        // the first frame needs it, and doing it here on the main thread only
        // delays the splash screen going away.
        Thread {
            val auth = FirebaseAuth.getInstance()
            // Bootstrap only when signed out. Starting a sign-in over an
            // existing session would replace a user who has already signed in.
            if (auth.currentUser != null) return@Thread
            auth.signInAnonymously()
                .addOnSuccessListener { result ->
                    Log.i("AvalokanRepo", "Anonymous auth UID: ${result.user?.uid}")
                }
                .addOnFailureListener { e ->
                    Log.w("AvalokanRepo", "Anonymous sign-in failed", e)
                }
        }.start()
    }
}
