package com.createchance.imageeditor.framerender

import android.content.Context
import android.graphics.*
import android.opengl.GLES20
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.createchance.imageeditor.IEManager
import com.createchance.imageeditor.IRenderTarget
import com.createchance.imageeditor.R
import com.createchance.imageeditor.drawers.BaseImageDrawer
import com.createchance.imageeditor.drawers.MixImageDrawer
import com.createchance.imageeditor.framerender.inputeditor.TextureModel
import com.volio.vn.models.frame.FrameEditModel

class IEFrame(
    private val frameEditModel: FrameEditModel
) {

    private var isFrameLoading = false

    private var keySrc: String = ""

    // Lưu trữ callback
    var mRenderTarget: IRenderTarget? = null

    // Bitmap load tử frame ra để vẽ
    private var mBitmapFrame: Bitmap? = null


    // Lấy bitmap của frame ra
    fun loadImage() {
        IEManager.getInstance().context?.let { context ->
            if (keySrc != generatorKeySrc() && !isFrameLoading && mRenderTarget != null) {
                isFrameLoading = true

                val width = mRenderTarget?.surfaceWidth ?: 1
                val height = mRenderTarget?.surfaceHeight ?: 1

                val isPreview = mRenderTarget?.isPreview ?: false
                val framePath = frameEditModel.getPhotoPathByRatio(context, width, height)


                keySrc = generatorKeySrc()

                if (framePath == null) {
                    mBitmapFrame = textAsBitmap(context, "Error frame!", width, height)
                    isFrameLoading = false
                    return
                }

                mBitmapFrame = if (isPreview) {
                    // Nếu đang preview thì lấy từ cache cho nhanh
                    Glide.with(context).asBitmap().load(framePath)
                        .error(R.drawable.ic_error_frame)
                        .override(width, height)
                        .submit()
                        .get()
                } else {
                    // Nếu ko phải preview thì xóa cached để lấy ảnh nét nhất
                    Glide.with(context).asBitmap().load(framePath)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.ic_error_frame)
                        .override(width, height)
                        .submit()
                        .get()
                }
                isFrameLoading = false
            }
        }
    }

    fun textAsBitmap(context: Context, text: String, width: Int, height: Int): Bitmap? {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = Typeface.createFromAsset(context.assets, "sarabun_semibold_600.ttf")
        paint.setTextSize(50f)
        paint.setColor(ContextCompat.getColor(context, R.color.white))
        paint.setTextAlign(Paint.Align.LEFT)
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length - 1, rect)
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawColor(ContextCompat.getColor(context, R.color.bg))
        val xCenter = width / 2 - rect.width() / 2
        canvas.drawText(text, xCenter.toFloat(), height / 2f, paint)
        return image
    }

    // Lấy bitmap gán vào cho texture
    fun loadTexture() {
        mBitmapFrame?.let { bmpFrame ->
            // Vì chuyển tiếp sẽ nhìn thấy cả frame đằng trc và frame đằng sau,
            // Nên cần biến isCurrent để biết đang vẽ frame trc hay frame sau
            val isCurrent = true // todo ThoNH -> fake tạm
            textureModel.loadTexture(isCurrent, bmpFrame, keySrc)
        }
    }


    fun drawToSurface(idTexture: Int) {
        mRenderTarget?.let { mRenderTarget ->
            if (mDrawer == null) {
                mDrawer = BaseImageDrawer()
            }
            loadTexture()

            // Vì chuyển tiếp sẽ nhìn thấy cả frame đằng trc và frame đằng sau,
            // Nên cần biến isCurrent để biết đang vẽ frame trc hay frame sau
            val isCurrent = true // todo ThoNH -> fake tạm

            val inputTexture = textureModel.getImageTextureId(isCurrent)

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

            mDrawer?.draw(
                inputTexture,
                0,
                0, // Vẽ ở góc 0;0
                mRenderTarget.surfaceWidth,
                mRenderTarget.surfaceHeight, // Vẽ đủ chiều rông và dài
                true,
                1f,
                1f, // KO scale
                0f,
                0f
            )
            mRenderTarget.swapTexture()
        }
    }


    // Key này dùng để xác định là view dirty, cần vẽ lại
    private fun generatorKeySrc(): String {
        IEManager.getInstance().context?.let { context ->
            val width = mRenderTarget?.surfaceWidth ?: 1
            val height = mRenderTarget?.surfaceHeight ?: 1

            val path = frameEditModel.getPhotoPathByRatio(context, width, height)
            val key = if (path == null) {
                "${frameEditModel.hashCode()}"
            } else {
                frameEditModel.getPhotoPathByRatio(context, width, height)
            }

            var keySrc = "${key}|"
            keySrc += "${mRenderTarget?.surfaceWidth}|"
            keySrc += "${mRenderTarget?.surfaceHeight}|"
            return keySrc
        }
        return ""
    }

    fun releaseTexture() {
        textureModel.deleteTexture()
    }

    fun releaseImage() {
        mBitmapFrame = null
        keySrc = ""
    }

    companion object {

        // Dùng để vẽ lên Surface
        private var mDrawer: BaseImageDrawer? = null


        // Đây chính texture frame để vẽ lên view
        private val textureModel: TextureModel = TextureModel()
    }

}