package com.createchance.imageeditor.framerender

import android.graphics.Bitmap
import android.media.ExifInterface
import android.opengl.GLES20
import android.util.Log
import com.createchance.imageeditor.IEManager
import com.createchance.imageeditor.IRenderTarget
import com.createchance.imageeditor.drawers.BaseImageDrawer
import com.createchance.imageeditor.transitions.PagTransition
import org.libpag.PAGFile
import org.libpag.PAGPlayer
import org.libpag.PAGScaleMode
import org.libpag.PAGSurface
import javax.microedition.khronos.opengles.GL10

/**
 * Image clip.
 *
 * @author createchance
 * @date 2018/12/24
 */
class IETheme internal constructor(
    val mImageFilePath: String,
    var startTime: Long = 0,
    var endTime: Long = 50000
) {
    private val mBitmap: Bitmap? = null
    private var lastWidth = 1
    private var lastHeight = 1
    private val mDuration: Long = endTime - startTime
    private var pagDuration: Long = 1
    var baseId = -1
        private set
    private val mExifInterface: ExifInterface? = null
    private var mRenderTarget: IRenderTarget? = null
    private var mDrawer: BaseImageDrawer? = null
    private var pagPlayer: PAGPlayer? = null
    private val mScaleX = 1f
    private val mScaleY = 1f
    private val mTranslateX = 0f
    private val mTranslateY = 0f
    private var isLoadPag = false
    private var pagSurface: PAGSurface? = null
    fun setRenderTarget(target: IRenderTarget?) {
        mRenderTarget = target
    }

    fun loadPag() {
        if (!isLoadPag && pagPlayer == null) {
            val time = System.currentTimeMillis()
            isLoadPag = true
            val pagFile = PAGFile.Load(mImageFilePath)
            if (pagFile != null) {
                pagDuration = pagFile.duration()
                pagPlayer = PAGPlayer()
                pagPlayer?.composition = pagFile
                pagPlayer?.setScaleMode(PAGScaleMode.Zoom)
            }
            isLoadPag = false
        }
    }

    fun loadTexture() {
        if (mRenderTarget != null) {
            if (mRenderTarget!!.surfaceWidth != lastWidth || mRenderTarget!!.surfaceHeight != lastHeight) {
                releaseTexture()
            }
            if (pagPlayer != null && baseId == -1) {
                baseId = initRenderTarget()
                pagSurface = PAGSurface.FromTexture(
                    baseId,
                    mRenderTarget!!.surfaceWidth,
                    mRenderTarget!!.surfaceHeight,
                    true
                )
                if (pagSurface != null) {
                    pagPlayer!!.surface = pagSurface
                }
                lastWidth = mRenderTarget!!.surfaceWidth
                lastHeight = mRenderTarget!!.surfaceHeight
            }
        }
    }

    fun render(localTime: Long) {
        loadTexture()
        mRenderTarget?.let { mRenderTarget ->
            if (mDrawer == null) {
                mDrawer = BaseImageDrawer()
            }
            pagPlayer?.progress = (localTime * 1000f % pagDuration / pagDuration).toDouble()
            pagPlayer?.flush()

            mRenderTarget.bindOffScreenFrameBuffer()
            mRenderTarget.attachOffScreenTexture(mRenderTarget.outputTextureId)
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            mDrawer?.draw(
                mRenderTarget.inputTextureId,
                0,
                0, // Vẽ ở góc 0;0
                mRenderTarget.surfaceWidth,
                mRenderTarget.surfaceHeight, // Vẽ đủ chiều rông và dài
                false,
                1f,
                1f, // KO scale
                0f,
                0f
            )
            if (baseId > 0 && pagPlayer != null) {
                mDrawer?.draw(
                    baseId,
                    0,
                    0,
                    mRenderTarget.surfaceWidth,
                    mRenderTarget.surfaceHeight,
                    false,
                    mScaleX,
                    mScaleY,
                    mTranslateX,
                    mTranslateY
                )
            } else {
                Log.e("DucLH", "Loiox")
            }
            mRenderTarget.swapTexture()
        }
    }

    private fun initRenderTarget(): Int {
        val id = intArrayOf(0)
        GLES20.glGenTextures(1, id, 0)
        if (id[0] == 0) {
            return 0
        }
        val textureId = id[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MIN_FILTER,
            GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MAG_FILTER,
            GL10.GL_NEAREST.toFloat()
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_S,
            GL10.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_T,
            GL10.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mRenderTarget!!.surfaceWidth,
            mRenderTarget!!.surfaceHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textureId
    }

    fun releaseData() {
        if (pagPlayer != null) {
            baseId = -1
            pagPlayer!!.release()
            pagPlayer = null
        }
    }

    fun releaseTexture() {
        if (baseId != -1) {
            GLES20.glDeleteTextures(1, intArrayOf(baseId), 0)
            releaseSurface()
            baseId = -1
        }
    }

    fun releaseSurface() {
        if (pagSurface != null) {
            pagSurface?.clearAll()
            pagSurface?.freeCache()
            pagSurface?.release()
        }
    }

    companion object {
        private const val TAG = "IEClip"
    }

}