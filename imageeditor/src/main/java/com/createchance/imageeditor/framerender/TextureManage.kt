package com.createchance.imageeditor.framerender

import com.createchance.imageeditor.framerender.inputeditor.Blur
import com.createchance.imageeditor.framerender.inputeditor.InputRender

object TextureManage {
    fun resetTexture() {
        InputRender.resetTexture()
    }

    fun deleteTexture() {
        Blur.deleteOffScreen()
        InputRender.deleteOffScreen()
    }
}