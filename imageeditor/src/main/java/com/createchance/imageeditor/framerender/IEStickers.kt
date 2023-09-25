package com.createchance.imageeditor.framerender

import android.opengl.GLES20
import com.createchance.imageeditor.EglUtil
import com.createchance.imageeditor.IEManager
import com.createchance.imageeditor.IRenderTarget
import com.createchance.imageeditor.drawers.StickerImageDrawer
import com.createchance.imageeditor.mapper.mapToText
import com.volio.sticker.TextGenerator
import com.volio.vn.models.sticker.StickerEditModel
import com.volio.vn.models.sticker.StickerTextEditModel
import java.util.concurrent.CopyOnWriteArrayList

class IEStickers {
    var target: IRenderTarget? = null
    private var mDrawer: StickerImageDrawer? = null
    private var listRelease: CopyOnWriteArrayList<StickerTime> = CopyOnWriteArrayList()
    private val listSticker: CopyOnWriteArrayList<StickerTime> = CopyOnWriteArrayList()
    fun load(currentClipId: String) {
        target?.let { target ->
            listSticker.iterator().forEach {
                if (it.textModel.contains(currentClipId) && (it.idTexture == EglUtil.NO_TEXTURE || it.screenWidth != target.surfaceWidth || it.screenHeight != target.surfaceHeight)) {
                    val bitmap = TextGenerator.createText(
                        IEManager.getInstance().context,
                        target.surfaceWidth.toFloat(),
                        target.surfaceHeight.toFloat(), it.textModel.mapToText()
                    )
                    it.width = bitmap.width
                    it.height = bitmap.height
                    it.idTexture = EglUtil.loadTexture(bitmap, it.idTexture, false)
                    it.screenWidth = target.surfaceWidth
                    it.screenHeight = target.surfaceHeight
                }

            }
        }
    }

    fun setData(listStickerEdit: List<StickerEditModel>) {
        val list: MutableList<StickerTime> = mutableListOf()
        val listOldAdd: MutableList<StickerTime> = mutableListOf()
        listStickerEdit.forEach { stickerEditModel ->
            if (stickerEditModel is StickerTextEditModel) {
                var check: Boolean = false
                for (stickerTime in listSticker) {
                    if (stickerTime.textModel.uuid == stickerEditModel.uuid) {
                        list.add(stickerTime)
                        listOldAdd.add(stickerTime)
                        stickerTime.textModel.setData(stickerEditModel)
                        check = true
                        break
                    }
                }
                if (!check) {
                    list.add(StickerTime(1, 1, 1, 1, stickerEditModel))
                }
            }
        }
        if (listOldAdd.size != listSticker.size) {
            listSticker.removeAll(listOldAdd.toSet())
            listRelease.addAll(listSticker)
        }
        listSticker.clear()
        listSticker.addAll(list)
    }

    fun draw(time: Long, currentClipId: String, nextClipId: String) {
        if (mDrawer == null) {
            mDrawer = StickerImageDrawer()
        }
        load(currentClipId)
        target?.let { target ->
            if (!(target.isPreview && !IEManager.getInstance().isPlaying())) {
                target.bindOffScreenFrameBuffer()
                target.attachOffScreenTexture(target.outputTextureId)
                GLES20.glClearColor(0f, 0f, 0f, 0f)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

                mDrawer?.draw(
                    target.inputTextureId,
                    0,
                    0, // Vẽ ở góc 0;0
                    target.surfaceWidth,
                    target.surfaceHeight, // Vẽ đủ chiều rông và dài
                    false,
                    1f,
                    1f, // KO scale
                    0f,
                    0f, 0f
                )
                listSticker.iterator().forEach {
                    if (it.textModel.contains(currentClipId)) {
                        mDrawer?.draw(
                            it.idTexture,
                            0,
                            0,
                            target.surfaceWidth,
                            target.surfaceHeight,
                            true,
                            it.textModel.scale * it.width / target.surfaceWidth,
                            it.textModel.scale * it.height / target.surfaceHeight,
                            it.textModel.getCenterX() * 2 - 1,
                            -(it.textModel.getCenterY() * 2 - 1),
                            -it.textModel.rotate
                        )
                    }
                }
                target.swapTexture()
            }
        }
        release(currentClipId)
    }

    fun release(currentImageUuid: String, isNotCheck: Boolean = false) {
        val listDelete: MutableList<Int> = mutableListOf()
        listSticker.iterator().forEach {
            if (!it.textModel.contains(currentImageUuid) || isNotCheck) {
                listDelete.add(it.idTexture)
                it.idTexture = EglUtil.NO_TEXTURE
            }
        }
        if (listDelete.size > 0) {
            val array = listDelete.toIntArray()
            GLES20.glDeleteTextures(array.size, array, 0)
        }
    }

    companion object {
    }

    class StickerTime(
        var screenWidth: Int,
        var screenHeight: Int,
        var width: Int,
        var height: Int,
        var textModel: StickerTextEditModel,
        var idTexture: Int = EglUtil.NO_TEXTURE,
    )
}