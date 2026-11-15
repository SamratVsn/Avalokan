/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

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
