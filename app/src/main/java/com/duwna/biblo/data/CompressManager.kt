package com.duwna.biblo.data

import android.content.Context
import android.net.Uri
import com.duwna.biblo.utils.FileUtil.toFile
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.resolution
import java.io.File
import javax.inject.Inject

class CompressManager @Inject constructor(private val context: Context) {

    suspend fun compressImage(srcUri: Uri, resolution: Resolution): File {
        val srcFile = srcUri.toFile(context)
        return Compressor.compress(context, srcFile) {
            when (resolution) {
                Resolution.LOW -> resolution(100, 100)
                Resolution.DEFAULT -> default()
            }
        }
    }

    enum class Resolution { LOW, DEFAULT }
}