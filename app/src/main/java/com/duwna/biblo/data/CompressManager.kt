package com.duwna.biblo.data

import android.net.Uri
import com.duwna.biblo.App
import com.duwna.biblo.utils.FileUtil.toFile
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.resolution
import java.io.File

object CompressManager {

    suspend fun compressImage(srcUri: Uri, resolution: Resolution): File {
        val ctx = App.appContext
        val srcFile = srcUri.toFile(ctx)
        return Compressor.compress(ctx, srcFile) {
            when (resolution) {
                Resolution.LOW -> resolution(100, 100)
                Resolution.DEFAULT -> default()
            }
        }
    }

    enum class Resolution { LOW, DEFAULT }
}