package com.volio.sticker.icon

import android.graphics.drawable.Drawable
import android.util.SizeF
import android.view.MotionEvent
import com.volio.sticker.CornerGravity
import com.volio.sticker.StickerView
import com.volio.sticker.dp
import com.volio.sticker.model.StickerModel

class IconButtonEdit(
    drawable: Drawable?,
    cornerGravity: CornerGravity,
    size: SizeF = SizeF(32.dp, 24.dp),
    val onClick:(StickerModel)->Unit
) : IconButton(drawable, cornerGravity, size) {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent) {
        super.onActionDown(stickerView, event)
        stickerView?.getCurrentSticker()?.let {
            onClick(it.getStickerData())
        }
    }
}