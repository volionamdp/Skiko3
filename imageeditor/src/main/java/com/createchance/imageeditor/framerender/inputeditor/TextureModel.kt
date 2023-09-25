package com.createchance.imageeditor.framerender.inputeditor

import android.graphics.Bitmap
import android.util.Log
import com.createchance.imageeditor.utils.OpenGlUtils

class TextureModel {
    private var currentImageLinkPath: String? = ""
    private var nextImageLinkPath: String? = ""
    private var currentImageTextureId = -1
    private var nextImageTextureId = -1
    fun loadTexture(isCurrent: Boolean, bitmap: Bitmap, key: String?) {
        if (getLinkImagePath(isCurrent) != key && !bitmap.isRecycled) {
            Log.d("kkkkkz", "loadTexture:$isCurrent $key")
            if (isCurrent) {
                currentImageTextureId =
                    OpenGlUtils.loadTexture(bitmap, currentImageTextureId, false)
                currentImageLinkPath = key
            } else {
                nextImageTextureId =
                    OpenGlUtils.loadTexture(bitmap, nextImageTextureId, false)
                nextImageLinkPath = key
            }
        }
    }

    fun getImageTextureId(isCurrent: Boolean): Int {
        return if (isCurrent) {
            currentImageTextureId
        } else {
            nextImageTextureId
        }
    }

    private fun getLinkImagePath(isCurrent: Boolean): String? {
        return if (isCurrent) {
            currentImageLinkPath
        } else {
            nextImageLinkPath
        }
    }

    fun deleteTexture() {
        currentImageTextureId = -1
        nextImageTextureId = -1
        reset()
    }

    fun reset() {
        currentImageLinkPath = ""
        nextImageLinkPath = ""
    }

}