package com.duwna.biblo.data

import android.content.Context
import android.net.Uri
import com.duwna.biblo.utils.FileUtil.toFile
import com.duwna.biblo.utils.tryOrNull
import com.google.firebase.storage.StorageReference
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.tasks.await
import java.io.File

class UploadImageManager(
    private val context: Context,
    private val storage: StorageReference
) {

    suspend fun uploadOrNull(
        name: String,
        path: String,
        imgUri: Uri?,
        resolution: Resolution = Resolution.LOW
    ): String? {
        imgUri ?: return null

        return try {
            uploadImage(path, name, imgUri, resolution)
        } catch (t: Throwable) {
            null
        }
    }

    private suspend fun uploadImage(
        path: String,
        name: String,
        imgUri: Uri,
        resolution: Resolution
    ): String {

        val compressedFile = compressImage(imgUri, resolution)

        val ref = storage
            .child(path)
            .child(name)

        ref.putStream(compressedFile.inputStream()).await()

        return ref.downloadUrl.await().toString()
    }

    private suspend fun compressImage(srcUri: Uri, resolution: Resolution): File {
        val srcFile = srcUri.toFile(context)
        if (resolution == Resolution.HIGH) return srcFile
        return Compressor.compress(context, srcFile) {
            when (resolution) {
                Resolution.LOW -> resolution(100, 100)
                Resolution.DEFAULT -> default()
                else -> throw IllegalArgumentException("Unknown resolution")
            }
        }
    }

    suspend fun deleteImage(path: String, name: String) {
        storage.child(path).child(name).delete().await()
    }

    enum class Resolution { LOW, DEFAULT, HIGH }
}