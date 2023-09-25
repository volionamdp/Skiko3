package com.createchance.imageeditor.framerender

import android.opengl.GLES20
import com.createchance.imageeditor.IRenderTarget
import com.createchance.imageeditor.drawers.BaseImageDrawer

class IERenderDefaultScreen {
    private var mRenderTarget: IRenderTarget? = null
    private var mDrawer: BaseImageDrawer? = null
    fun setRenderTarget(target: IRenderTarget?) {
        mRenderTarget = target
    }

    fun draw() {
        mRenderTarget?.let { target ->
            if (mDrawer == null) mDrawer = BaseImageDrawer()
            target.bindDefaultFrameBuffer()
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            mDrawer?.draw(
                target.inputTextureId,
                0,
                0,
                target.surfaceWidth,
                target.surfaceHeight,
                false,
                1f,
                1f,
                0f,
                0f
            )
        }
    }
}