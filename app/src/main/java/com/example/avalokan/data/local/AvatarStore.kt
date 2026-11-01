package com.example.avalokan.data.local

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Device-local avatar storage.
 *
 * Avatars are picked from a `content://` URI, copied into app-private storage,
 * and exposed as a `file://` URI. Only that local URI is persisted; a
 * `content://` URI must never be written to DataStore or Firestore because it
 * is meaningless on another device or after the picker grant expires.
 */
interface AvatarStore {
    /** Copies [uri] into private storage and returns a `file://` URI, or null on failure. */
    suspend fun import(uri: String): String?

    /** Deletes every locally stored avatar. */
    suspend fun delete()
}

class LocalAvatarStore @Inject constructor(
    @ApplicationContext appContext: Context,
) : AvatarStore {

    private val context = appContext.applicationContext

    // Serialises imports and deletes so one operation cannot remove a file
    // another operation is still writing.
    private val ioMutex = Mutex()

    private val avatarDir: File
        get() = File(context.filesDir, AVATAR_DIR).apply { mkdirs() }

    override suspend fun import(uri: String): String? = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val source = Uri.parse(uri)
            if (source.scheme != "content") {
                Log.w(TAG, "Refusing avatar URI with scheme ${source.scheme}")
                return@withLock null
            }

            val mimeType = context.contentResolver.getType(source)
            if (mimeType != null && !mimeType.startsWith("image/")) {
                Log.w(TAG, "Refusing non-image avatar type $mimeType")
                return@withLock null
            }

            // A unique name keeps Coil from serving a stale cached bitmap for
            // the previous avatar. Old files are removed once the new one is safe.
            val target = try {
                File.createTempFile("avatar_", ".jpg", avatarDir)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to create avatar file", e)
                return@withLock null
            }
            try {
                val input = context.contentResolver.openInputStream(source)
                    ?: run {
                        target.delete()
                        return@withLock null
                    }
                input.use { stream ->
                    target.outputStream().use { output -> stream.copyTo(output) }
                }
                avatarDir.listFiles()
                    ?.filter { it != target }
                    ?.forEach { it.delete() }
                Uri.fromFile(target).toString()
            } catch (e: CancellationException) {
                target.delete()
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Failed to import avatar", e)
                target.delete()
                null
            }
        }
    }

    override suspend fun delete() {
        withContext(Dispatchers.IO) {
            ioMutex.withLock {
                avatarDir.listFiles()?.forEach { it.delete() }
            }
        }
    }

    private companion object {
        const val TAG = "AvatarStore"
        const val AVATAR_DIR = "avatars"
    }
}
