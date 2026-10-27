package com.example.avalokan.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.avalokan.data.security.safeHttpUrl

/** Opens [url] in the device's default browser. Returns false if the URL is empty or no handler exists. */
fun Context.openUrlInBrowser(url: String): Boolean {
    val normalized = safeHttpUrl(url) ?: return false
    return try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalized)))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
