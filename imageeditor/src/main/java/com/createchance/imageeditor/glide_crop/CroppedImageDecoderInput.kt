package com.createchance.imageeditor.glide_crop

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

class CroppedImageDecoderInput(
        val resId: String?,
        val viewWidth: Int,
        val viewHeight: Int,
        val horizontalOffset: Int = 0,
        val verticalOffset: Int = 0
)