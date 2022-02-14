package com.swaytwig.undergrounddutchgirl

import android.content.Context
import androidx.core.content.ContextCompat
import java.io.File

class Game {
    companion object {
        private var gameAssetDirCache: String? = null

        fun gameAssetDir(context: Context): String {
            if (gameAssetDirCache != null)
                return gameAssetDirCache!!

            val storages: Array<String> by lazy {
                ContextCompat.getExternalFilesDirs(context, null)
                    .filterNotNull()
                    .map { it.path.substringBefore("/Android/data", "") }
                    .filter { it.isNotEmpty() }
                    .toTypedArray()
            }

            val platformDir = "Android/data/com.smartjoy.LastOrigin_C"
            var baseDir = ""
            for (storage in storages) {
                val dir = "${storage}/${platformDir}/files/UnityCache/Shared"
                val f = File(dir)
                if (f.exists()) {
                    baseDir = dir
                    break
                }
            }

            gameAssetDirCache = baseDir
            return gameAssetDirCache!!
        }
    }
}