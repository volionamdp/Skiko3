package com.createchance.imageeditor.framerender

import android.opengl.GLES20
import android.util.Log
import com.createchance.imageeditor.GlFramebufferObject
import com.createchance.imageeditor.IRenderTarget
import com.createchance.imageeditor.drawers.BaseImageDrawer
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.DirectContext
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FramebufferFormat
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin
import org.jetbrains.skia.paragraph.FontCollection
import org.jetbrains.skia.paragraph.ParagraphBuilder
import org.jetbrains.skia.paragraph.ParagraphStyle
import org.jetbrains.skia.paragraph.TextStyle
import org.jetbrains.skiko.currentSystemTheme


class Text {
    private var frameBuffer = GlFramebufferObject()
    private var context: DirectContext? = null
    private var renderTarget: BackendRenderTarget? = null
    private var surface: Surface? = null
    private var canvas: Canvas? = null
    private var mRenderTarget: IRenderTarget? = null
    private var mDrawer: BaseImageDrawer? = null

    fun setRenderTarget(target: IRenderTarget?) {
        mRenderTarget = target
    }

    fun setUp(width: Int, height: Int) {
        frameBuffer.setup(width, height)
        renderTarget = BackendRenderTarget.makeGL(
            width,
            height,
            0,
            8,
            frameBuffer.framebufferName,
            FramebufferFormat.GR_GL_RGBA8
        )
        context = DirectContext.makeGL()
        surface = Surface.makeFromBackendRenderTarget(
            context!!,
            renderTarget!!,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.sRGB
        )
        canvas = surface!!.canvas
        Log.d("ldldldl", "setUp: ")
    }
    private val fontCollection = FontCollection()
        .setDefaultFontManager(FontMgr.default)
    private val style = ParagraphStyle()
    val renderInfo = ParagraphBuilder(style, fontCollection)
        .pushStyle(TextStyle().setColor(0xFFFF0000.toInt()))
        .addText("Graphics API:")
        .popStyle()
        .build()


    fun draw() {

        mRenderTarget?.let { mRenderTarget ->
            if (context == null){
                setUp(mRenderTarget.surfaceWidth,mRenderTarget.surfaceHeight)
            }
            if (mDrawer == null) {
                mDrawer = BaseImageDrawer()
            }
            mRenderTarget.bindOffScreenFrameBuffer()
            mRenderTarget.attachOffScreenTexture(mRenderTarget.outputTextureId)
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            renderInfo.layout(Float.POSITIVE_INFINITY)
            renderInfo.paint(canvas, 100f, 100f)
            context?.flush()

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
            mDrawer?.draw(
                frameBuffer.texName,
                0,
                0,
                mRenderTarget.surfaceWidth,
                mRenderTarget.surfaceHeight,
                false,
                1f,
                1f,
                1f, // KO scale
                0f
            )

            mRenderTarget.swapTexture()
        }
    }

}