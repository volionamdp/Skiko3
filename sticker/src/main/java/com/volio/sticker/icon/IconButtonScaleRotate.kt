package com.volio.sticker.icon

import android.graphics.drawable.Drawable
import android.util.SizeF
import android.view.MotionEvent
import com.volio.sticker.CornerGravity
import com.volio.sticker.StickerView
import com.volio.sticker.dp

class IconButtonScaleRotate(
    drawable: Drawable?,
    cornerGravity: CornerGravity,
    size: SizeF = SizeF(32.dp, 24.dp)
) : IconButton(drawable, cornerGravity, size) {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent) {
        super.onActionDown(stickerView, event)
    }

    override fun onActionMove(stickerView: StickerView?, event: MotionEvent) {
        stickerView?.zoomAndRotateCurrentSticker(event)
    }
}