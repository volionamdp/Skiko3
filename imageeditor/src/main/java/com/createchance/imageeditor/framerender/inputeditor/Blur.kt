package com.createchance.imageeditor.framerender.inputeditor

import android.opengl.GLES20
import com.createchance.imageeditor.EglUtil
import com.createchance.imageeditor.RenderContext
import com.createchance.imageeditor.drawers.ThreeXThreeSampleDrawer

/**
 * 3 x 3 sampling operator.
 *
 * @author createchance
 * @date 2018/11/27
 */
class Blur internal constructor(private val renderContext: RenderContext) {
    var widthStep = 0f
    var heightStep = 0f
    private var mSampleFactor = MIN_SAMPLE_FACTOR
    var repeatTimes = MIN_REPEAT
    var sampleKernel: FloatArray? = SIGMA_1_5_GAUSSIAN_SAMPLE_KERNEL
    private val lastSample = -1f
    private var mInputTextureIndex = 0
    private var mOutputTextureIndex = 1
    private var scWidth = 1
    private var scHeight = 1
    private var isCurrent: Boolean = true
    private val inputTextureId: Int
        get() = if (isCurrent) {
            currentOffScreenIds[mInputTextureIndex]
        } else {
            nextOffScreenIds[mInputTextureIndex]
        }
    private val outputTextureId: Int
        get() = if (isCurrent) {
            currentOffScreenIds[mOutputTextureIndex]
        } else {
            nextOffScreenIds[mOutputTextureIndex]
        }

    fun checkRational(): Boolean {
        return sampleKernel != null && sampleKernel!!.size == 9
    }

    // 0 -> 100
    fun setBlurSize(progress: Int) {
        mSampleFactor = MIN_SAMPLE_FACTOR + progress * (MAX_SAMPLE_FACTOR - MIN_SAMPLE_FACTOR) / 100
        repeatTimes = MIN_REPEAT + progress * (MAX_REPEAT - MIN_REPEAT) / 100
    }

    private fun clearFrameBuffer() {
        renderContext.attachOffScreenTexture(outputTextureId)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        renderContext.attachOffScreenTexture(inputTextureId)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }

    //render blur với texture là kết cấu cần blur và trả về kết cấu đã được blur
    fun render(texture: Int, width: Int, height: Int, isCurrent: Boolean): Int {
//        Log.d(TAG, "exec: --------------" + repeatTimes)
        val time = System.currentTimeMillis()
        clearFrameBuffer()
        if (mDrawer == null) {
            mDrawer = ThreeXThreeSampleDrawer()
        }
        val textureWidth = width / mSampleFactor
        val textureHeight = height / mSampleFactor
        //        if (mSampleFactor != lastSample || getInputTextureId() < 0 ||) {
//            EglUtil.deleteTextures(mOffScreenTextureIds);
//            mOffScreenTextureIds = EglUtil.createTextures(textureWidth, textureHeight, mOffScreenTextureIds.length);
//            lastSample = mSampleFactor;
//        }
        setupTexture(textureWidth, textureHeight)
        renderContext.attachOffScreenTexture(outputTextureId)
        mDrawer!!.setWidthStep(1.0f / textureWidth)
        mDrawer!!.setHeightStep(1.0f / textureHeight)
        mDrawer!!.setSampleKernel(sampleKernel)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawer!!.draw(
            texture,
            0,
            0,
            textureWidth,
            textureHeight
        )
        swapTexture()
        for (i in 0 until repeatTimes) {
            renderContext.attachOffScreenTexture(outputTextureId)
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            mDrawer!!.draw(
                inputTextureId,
                0,
                0,
                textureWidth,
                textureHeight
            )
            swapTexture()
        }
//        Log.d(TAG, "exec: " + (System.currentTimeMillis() - time))
        return inputTextureId
    }

    // loại bỏ kết cấu
    fun release() {

    }


    private fun setupTexture(width: Int, height: Int) {
        if (outputTextureId <= 0 || inputTextureId <= 0 || scWidth != width || scHeight != height) {
            scWidth = width
            scHeight = height
            EglUtil.updateTextures(currentOffScreenIds, width, height)
            EglUtil.updateTextures(nextOffScreenIds, width, height)
        }
    }

    fun swapTexture() {
        val tmp = mInputTextureIndex
        mInputTextureIndex = mOutputTextureIndex
        mOutputTextureIndex = tmp
    }

    companion object {
        private const val MAX_REPEAT = 8
        private const val MIN_REPEAT = 2
        private const val MIN_SAMPLE_FACTOR = 3
        private const val MAX_SAMPLE_FACTOR = 15
        private val currentOffScreenIds = IntArray(2)
        private val nextOffScreenIds = IntArray(2)
        private var mDrawer: ThreeXThreeSampleDrawer? = null

        fun deleteOffScreen() {
            currentOffScreenIds[0] = -1
            currentOffScreenIds[1] = -1
            nextOffScreenIds[0] = -1
            nextOffScreenIds[1] = -1
        }

        val SIGMA_1_5_GAUSSIAN_SAMPLE_KERNEL = floatArrayOf(
            0.0947416f, 0.118318f, 0.0947416f,
            0.118318f, 0.147716f, 0.118318f,
            0.0947416f, 0.118318f, 0.0947416f
        )
    }

}