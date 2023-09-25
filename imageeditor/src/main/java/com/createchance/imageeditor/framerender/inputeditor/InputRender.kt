package com.createchance.imageeditor.framerender.inputeditor

import android.graphics.Bitmap
import android.graphics.Rect
import android.opengl.GLES20
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.createchance.imageeditor.EglUtil
import com.createchance.imageeditor.IEManager
import com.createchance.imageeditor.R
import com.createchance.imageeditor.RenderContext
import com.createchance.imageeditor.drawers.BaseImageDrawer
import com.createchance.imageeditor.framerender.IEClip.MoveType
import com.createchance.imageeditor.glide_crop.CroppedImage
import com.createchance.imageeditor.glide_crop.GlideApp
import com.theartofdev.edmodo.cropper.GetBitmapCrop
import com.volio.vn.models.background.BackgroundEditImageModel
import com.volio.vn.models.background.BackgroundEditModel
import com.volio.vn.models.photo.CropModel
import java.io.File
import kotlin.math.min
import kotlin.random.Random

class InputRender(private val renderContext: RenderContext) {
    private var blur: Blur? = null
    private var srcWidth = 1
    private var srcHeight = 1
    private var backgroundWidth = 1
    private var backgroundHeight = 1
    private var scaleX = 1f
    private var scaleY = 1f
    private var transX = 0f
    private var transY = 0f
    private val scalePreview = 0.4f
    private val defaultScale = 1.04f
    private val moveType = randomMoveType()
    private var mInputTextureIndex = 0
    private var mOutputTextureIndex = 1
    private var scWidth = 1
    private var scHeight = 1
    private val mRenderLeft = 0
    private val mRenderBottom = 0
    private val mScissorX = 0f
    private val mScissorY = 0f
    private val mScissorWidth = 1.0f
    private val mScissorHeight = 1.0f
    private var mImageFilePath: String? = null
    private var isCurrent: Boolean = true
    private var background: BackgroundEditModel? = null
    private var srcBitmap: Bitmap? = null
    private var backgroundBitmap: Bitmap? = null
    private var isLoadSrcImage = false
    private var isLoadBackgroundImage = false
    private val rect = Rect()
    private var cropModelData: CropModel? = null
    private var keySrc: String = ""
    private var uuid: String = ""

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

    init {
        Log.d("zzzzetvz", ": $moveType")
    }

    fun setBackground(backgroundEditModel: BackgroundEditModel) {
        background = backgroundEditModel
    }

    fun setImagePath(uuid: String, imagePath: String?, cropModel: CropModel?) {
        this.uuid = uuid
        mImageFilePath = imagePath
        if (checkCropUpdate(cropModel, cropModelData)) {
            kotlin.runCatching {
                srcBitmap?.recycle()
            }.onFailure {
                it.printStackTrace()
            }
            cropModelData = cropModel?.copy()
            srcBitmap = null
            resetTexture()
        }
    }

    // thực hiện cắt ở trung tâm để full màn
    // đầu vào kết cấu texture
    // đầu ra kết cấu đã được sử lí
    private fun renderCenterCrop(texture: Int, width: Int, height: Int): Int {
        var sx = 1f
        var sy = 1f
        val textureRatio = backgroundWidth.toFloat() / backgroundHeight
        val screenRatio = width.toFloat() / height
        if (screenRatio > textureRatio) {
            sy = width / textureRatio / height
        } else {
            sx = height * textureRatio / width
        }
        renderContext.attachOffScreenTexture(outputTextureId)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawer!!.draw(
            texture,
            0,
            0,
            width,
            height,
            false,
            sx,
            sy,
            0f,
            0f
        )
        swapTexture()
        return inputTextureId
    }

    private fun renderTexture(texture: Int, width: Int, height: Int): Int {
        val textureRatio = srcWidth.toFloat() / srcHeight
        val screenRatio = width.toFloat() / height
        if (screenRatio > textureRatio) {
            val widthNew = (height * textureRatio).toInt()
            rect.left = (width - widthNew) / 2
            rect.right = rect.left + widthNew
            rect.top = 0
            rect.bottom = height
        } else {
            val heightNew = (width / textureRatio).toInt()
            rect.left = 0
            rect.right = width
            rect.top = (height - heightNew) / 2
            rect.bottom = rect.top + heightNew
        }
        renderContext.attachOffScreenTexture(outputTextureId)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST)
        GLES20.glScissor(
            (mScissorX * renderContext.renderWidth + mRenderLeft).toInt(),
            (mScissorY * renderContext.renderHeight + mRenderBottom).toInt(),
            (mScissorWidth * renderContext.renderWidth).toInt(),
            (mScissorHeight * renderContext.renderHeight).toInt()
        )
        mDrawer!!.draw(
            texture,
            rect.left,
            rect.top,
            rect.width(),
            rect.height(),
            false,
            1f,
            1f, 0f, 0f
        )
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST)
        swapTexture()
        return inputTextureId
    }

    // thực hiện gộp các kết cấu blur,và kết cấu đã được di chuyển
    // đầu vào kết cấu texture
    // đầu ra kết cấu đã được sử lí
    private fun renderAllTexture(texture: Int, width: Int, height: Int, isCurrent: Boolean): Int {
        if (blur == null) {
            blur = Blur(renderContext)
        }
        if (mDrawer == null) {
            mDrawer = BaseImageDrawer()
        }
        setupTexture(width, height)
        var backgroundTexture = texture
        if (background is BackgroundEditImageModel && backgroundTextureModel.getImageTextureId(
                isCurrent
            ) >= 0
        ) {
            backgroundTexture = backgroundTextureModel.getImageTextureId(isCurrent)
        }
        val cropTexture = renderCenterCrop(backgroundTexture, width, height)
        var blurTexture = cropTexture
        background?.let {
            if (it.blurLevel > 0) {
                blur?.setBlurSize(it.blurLevel)
                blurTexture = blur?.render(cropTexture, width, height, isCurrent) ?: -1
            }
        } ?: run {
            blur?.setBlurSize(30)
            blurTexture = blur?.render(cropTexture, width, height, isCurrent) ?: -1
        }

        val editTexture = renderTexture(texture, width, height)
//        Log.d("zzzet", "renderTexture: ${textureModel.getImageTextureId(isCurrent)}")
        renderContext.attachOffScreenTexture(outputTextureId)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawer?.draw(
            blurTexture,
            0,
            0,
            width,
            height,
            false,
            1f, 1f,
            0f,
            0f
        )

        mDrawer?.draw(
            editTexture,
            0,
            0,
            width,
            height,
            false,
            scaleX,
            scaleY,
            transX,
            -transY
        )
        swapTexture()
        return inputTextureId
    }

    private fun clearFrameBuffer() {
        renderContext.attachOffScreenTexture(outputTextureId)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        renderContext.attachOffScreenTexture(inputTextureId)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }

    // thực hiện tạo anim (zoom in,out..)
    // đầu vào kết cấu texture
    // đầu ra kết cấu đã được sử lí
    fun renderBaseTexture(width: Int, height: Int, progressMove: Float, isCurrent: Boolean): Int {
        clearFrameBuffer()
        this.isCurrent = isCurrent
        if (mDrawer == null) {
            mDrawer = BaseImageDrawer()
        }
        setupTexture(width, height)
        loadTexture()
        val texture =
            renderAllTexture(textureModel.getImageTextureId(isCurrent), width, height, isCurrent)
        val newScale = defaultScale
        val space = 1 * (newScale - 1f)
        val progress = (progressMove - 0.5f) * 2f
        var transX = 0f
        var transY = 0f
        var scale = newScale
        when (moveType) {
            MoveType.NONE -> {
                transX = 0f
                transY = 0f
                scale = 1f
            }
            MoveType.TOP -> {
                transY = -progress * space
            }
            MoveType.LEFT -> {
                transX = -progress * space
            }
            MoveType.RIGHT -> {
                transX = progress * space
            }
            MoveType.BOTTOM -> {
                transY = progress * space
            }
            MoveType.LEFT_TOP -> {
                transX = -progress * space
                transY = -progress * space
            }
            MoveType.RIGHT_TOP -> {
                transX = progress * space
                transY = -progress * space
            }
            MoveType.LEFT_BOTTOM -> {
                transX = -progress * space
                transY = progress * space
            }
            MoveType.RIGHT_BOTTOM -> {
                transX = progress * space
                transY = progress * space
            }
            MoveType.ZOOM_IN -> {
                transX = 0f
                transY = 0f
                scale = newScale - (newScale - 1f) * progressMove
            }
            MoveType.ZOOM_OUT -> {
                transX = 0f
                transY = 0f
                scale = 1f + (newScale - 1f) * progressMove
            }
        }
        renderContext.attachOffScreenTexture(outputTextureId)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawer!!.draw(
            texture,
            0,
            0,
            width,
            height,
            false,
            scale,
            scale,
            transX,
            transY
        )
        releaseTexture()
        swapTexture()
        return inputTextureId
    }

    fun releaseTexture() {
        blur?.release()
//        EglUtil.deleteTextures(textureImage)
//        textureImage=-1
    }

    fun releaseImage() {
//        srcBitmap?.recycle()
//        backgroundBitmap?.recycle()
        backgroundBitmap = null
        if (cropModelData != null && keySrc == generatorKeySrc()) {
            srcBitmap?.recycle()
        }
        srcBitmap = null
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


    fun loadImage() {
        if (!isLoadSrcImage && (srcBitmap == null || srcBitmap?.isRecycled == true || keySrc != generatorKeySrc())) {
            if (renderContext.surfaceWidth <= 0 || renderContext.surfaceHeight <= 0) {
                return
            }
            isLoadSrcImage = true
            var scale = 1f
            if (renderContext.isPreview) {
                scale = if (cropModelData == null) {
                    scalePreview
                } else {
                    val cropScale = GetBitmapCrop.getScaleCrop(
                        cropModelData,
                        min(renderContext.surfaceWidth, renderContext.surfaceWidth)
                    )
                    if (cropScale < scalePreview) {
                        cropScale
                    } else {
                        cropScale * scalePreview
                    }
                }
            }
//            else {
//                if (cropModelData != null) {
//                    scale = GetBitmapCrop.getScaleCrop(
//                        cropModelData,
//                        min(renderContext.surfaceWidth, renderContext.surfaceWidth)
//                    )
//                }
//            }
            if (scale > 3) scale = 3f
            kotlin.runCatching {
                val overrideSize = min(
                    (renderContext.surfaceWidth * scale).toInt(),
                    (renderContext.surfaceHeight * scale).toInt()
                )
                Log.d("vveeez", "loadImage: $mImageFilePath")
                val src =
                    IEManager.getInstance().context?.let {
                        if (mImageFilePath != null && (File(mImageFilePath!!).exists() || mImageFilePath?.contains(
                                "android_asset"
                            ) == true)
                        ) {
                            if (cropModelData == null || mImageFilePath?.contains("android_asset") == true) {
                                if (!renderContext.isPreview) {
                                    Glide.with(it).asBitmap().load(mImageFilePath)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .error(R.drawable.img_error)
                                        .override(overrideSize, overrideSize).submit().get()
                                } else {
                                    Glide.with(it).asBitmap().load(mImageFilePath)
                                        .error(R.drawable.img_error)
                                        .override(overrideSize, overrideSize).submit().get()
                                }
                            } else {
                                cropModelData?.let { crop ->
                                    val rect = GetBitmapCrop.getRectFromPoint(crop)
                                    val croppedImage = CroppedImage(
                                        mImageFilePath,
                                        rect.width(),
                                        rect.height(),
                                        rect.left,
                                        rect.top
                                    )
                                    if (!renderContext.isPreview) {
                                        GlideApp.with(it).asBitmap().load(croppedImage)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .error(R.drawable.img_error)
                                            .override(overrideSize, overrideSize).submit().get()
                                    } else {
                                        GlideApp.with(it).asBitmap().load(croppedImage)
                                            .error(R.drawable.img_error)
                                            .override(overrideSize, overrideSize).submit().get()
                                    }
                                }

//                                LoadBitmapUtils.decodeSampledBitmapFromResource(
//                                    mImageFilePath!!,
//                                    overrideSize,
//                                    overrideSize
//                                )
                            }
                        } else {
                            Glide.with(it).asBitmap().load(R.drawable.img_error).submit().get()
                        }
                    }

                if (src != null) {
                    cropModelData?.let {
                        srcBitmap = GetBitmapCrop.getBitmapRegionCrop(src, it)
                    } ?: run {
                        srcBitmap = src
                    }
                    keySrc = generatorKeySrc()
                    Log.d("kkkkkz", "loadImage: $keySrc")

                }
            }.onFailure {
                it.printStackTrace()
            }
            isLoadSrcImage = false
        }

        if (background is BackgroundEditImageModel && !isLoadBackgroundImage &&
            (backgroundBitmap == null || backgroundBitmap?.isRecycled == true)
        ) {
            if (renderContext.surfaceWidth <= 0 || renderContext.surfaceHeight <= 0) {
                return
            }
            isLoadBackgroundImage = true
            var scale = 1f
            if (renderContext.isPreview) {
                scale = scalePreview
            }
            kotlin.runCatching {
                val overrideSize = min(
                    (renderContext.surfaceWidth * scale).toInt(),
                    (renderContext.surfaceHeight * scale).toInt()
                )
                val background =
                    IEManager.getInstance().context?.let {
                        (background as BackgroundEditImageModel).localPath.let { it1 ->
                            if (!renderContext.isPreview) {
                                Glide.with(it).asBitmap().load(it1)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.img_error)
                                    .override(overrideSize, overrideSize).submit().get()

                            } else {
                                Glide.with(it).asBitmap().load(it1)
                                    .error(R.drawable.img_error)
                                    .override(overrideSize, overrideSize).submit().get()
                            }
                        }
                    }
                if (background != null) {
                    backgroundBitmap = background
                }
            }.onFailure {
                it.printStackTrace()
            }
            isLoadBackgroundImage = false
        }
    }

    fun loadTexture() {
        srcBitmap?.let {
            srcWidth = it.width
            srcHeight = it.height
            backgroundWidth = it.width
            backgroundHeight = it.height
            textureModel.loadTexture(isCurrent, it, keySrc)
//            if (textureImage <= 0) {
//                textureImage = EglUtil.loadTexture(srcBitmap, EglUtil.NO_TEXTURE, false)
//            }
        }
        if (background is BackgroundEditImageModel) {
            backgroundBitmap?.let {
                backgroundWidth = it.width
                backgroundHeight = it.height
                backgroundTextureModel.loadTexture(
                    isCurrent,
                    it,
                    (background as BackgroundEditImageModel).localPath
                )
            }
        }

    }

    private fun checkCropUpdate(cropModel1: CropModel?, cropModel2: CropModel?): Boolean {
        if (cropModel1 == null && cropModel2 == null) return false
        return (cropModel1?.equals(cropModel2) != true)
    }

    private fun generatorKeySrc(): String {
        var keySrc = "${mImageFilePath}|"
        keySrc += "${renderContext.renderWidth}|"
        keySrc += "${renderContext.renderHeight}|"
        keySrc += "${uuid}|"
        cropModelData?.let {
            keySrc += it.toString()
        }
        return keySrc
    }


    companion object {
        private var mDrawer: BaseImageDrawer? = null
        private val currentOffScreenIds = IntArray(2)
        private val nextOffScreenIds = IntArray(2)
        private val textureModel: TextureModel = TextureModel()
        private val backgroundTextureModel: TextureModel = TextureModel()
        private val random = Random(1)
        private fun randomMoveType(): MoveType {
            val directions: Array<MoveType> = MoveType.values()
            return directions[random.nextInt(1, directions.size)]
        }

        fun resetTexture() {
            textureModel.reset()
            backgroundTextureModel.reset()
        }

        fun deleteOffScreen() {
            currentOffScreenIds[0] = -1
            currentOffScreenIds[1] = -1
            nextOffScreenIds[0] = -1
            nextOffScreenIds[1] = -1
            textureModel.deleteTexture()
            backgroundTextureModel.deleteTexture()
        }
    }
}