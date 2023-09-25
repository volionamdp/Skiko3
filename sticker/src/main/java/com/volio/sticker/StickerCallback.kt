package com.volio.sticker

import com.volio.sticker.sticker.Sticker

interface StickerCallback {
    fun updateData(sticker: Sticker)
}