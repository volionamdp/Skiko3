package com.createchance.imageeditor.framerender

import android.opengl.GLES20
import android.util.Log
import com.createchance.imageeditor.IEManager
import com.createchance.imageeditor.IHistogramGenerateListener
import com.createchance.imageeditor.IRenderTarget
import com.createchance.imageeditor.RenderContext
import com.createchance.imageeditor.drawers.BaseImageDrawer
import com.createchance.imageeditor.framerender.inputeditor.InputRender
import com.createchance.imageeditor.ops.AbstractOperator
import com.createchance.imageeditor.transitions.AbstractTransition
import com.createchance.imageeditor.transitions.PagOverlayTransition
import com.createchance.imageeditor.transitions.PagTransition
import com.createchance.imageeditor.utils.TransUtils
import com.volio.vn.models.frame.FrameEditModel
import com.volio.vn.models.frame.FrameModel
import com.volio.vn.models.photo.PhotoEditModel
import com.volio.vn.models.transition.TransitionEditModel
import com.volio.vn.models.transition.TransitionGLSLEditModel
import com.volio.vn.models.transition.TransitionPagOverlayEditModel

/**
 * Image clip.
 *
 * @author createchance
 * @date 2018/12/24
 */
class IEClip internal constructor(
    var photoEditModel: PhotoEditModel,
    var startTime: Long,
    var endTime: Long,
    var position: Int
) : RenderContext {
    private var mOriginWidth = 0
    private var mOriginHeight = 0
    val duration: Long
        get() = endTime - startTime
    val transitionDuration: Long
        get() = photoEditModel.transition?.duration ?: 0L
    private var lastTransTime: Long = 0
    var baseTextureId = -1
    private val mOpList: MutableList<AbstractOperator> = ArrayList()
    private var mTransition: AbstractTransition? = null
    private var mRenderTarget: IRenderTarget? = null
    private val inputRender: InputRender = InputRender(this)

    private var mIEFrame: IEFrame? = null


    override fun getScaleFactor(): Float {
        return 1f
    }

    override fun getSurfaceWidth(): Int {
        return mRenderTarget?.surfaceWidth ?: 0
    }

    override fun getSurfaceHeight(): Int {
        return mRenderTarget?.surfaceHeight ?: 0
    }

    override fun getRenderWidth(): Int {
        return mRenderTarget?.surfaceWidth ?: 0
    }

    override fun getRenderHeight(): Int {
        return mRenderTarget?.surfaceHeight ?: 0
    }

    override fun getNextAspectRatio(): Float {
        var aspectRatio = 1.0f
        val iterator = IEManager.getInstance().clipList
        while (iterator.hasNext()){
            val clip = iterator.next()
            if (clip == this@IEClip&&iterator.hasNext()){
                val nexClip = iterator.next()
                aspectRatio = nexClip.originWidth * 1.0f / nexClip.originHeight
                break
            }
        }
        return aspectRatio
    }

    override fun getRenderLeft(): Int {
        return 0
    }

    override fun getRenderTop(): Int {
        return mRenderTarget?.surfaceHeight ?: 0
    }

    override fun getRenderRight(): Int {
        return mRenderTarget?.surfaceWidth ?: 0
    }

    override fun getRenderBottom(): Int {
        return 0
    }

    override fun getScissorX(): Float {
        return 0f
    }

    override fun getScissorY(): Float {
        return 0f
    }

    override fun getScissorWidth(): Float {
        return 1f
    }

    override fun getScissorHeight(): Float {
        return 1f
    }

    override fun getInputTextureId(): Int {
        return mRenderTarget?.inputTextureId ?: -1
    }

    override fun getOutputTextureId(): Int {
        return mRenderTarget?.outputTextureId ?: -1
    }

    override fun getFromTextureId(): Int {
        return baseTextureId
    }

    override fun getToTextureId(): Int {
        var textureId = -1
        val iterator = IEManager.getInstance().clipList
        while (iterator.hasNext()){
            val clip = iterator.next()
            if (clip == this@IEClip&&iterator.hasNext()){
                val nextClip = iterator.next()
                nextClip.setLastTransTime(transitionDuration)
                nextClip.renderBaseTexture(
                    currentLocalTime - (duration - transitionDuration),
                    false
                )
                textureId = nextClip.baseTextureId
                break
            }
        }
        return textureId
    }

    override fun bindOffScreenFrameBuffer() {
        mRenderTarget?.bindOffScreenFrameBuffer()
    }

    override fun attachOffScreenTexture(textureId: Int) {
        mRenderTarget?.attachOffScreenTexture(textureId)
    }

    override fun bindDefaultFrameBuffer() {
        mRenderTarget?.bindDefaultFrameBuffer()
    }

    override fun swapTexture() {
        mRenderTarget?.swapTexture()
    }

    override fun isPreview(): Boolean {
        return mRenderTarget?.isPreview == true
    }

    fun setRenderTarget(target: IRenderTarget?) {
        mRenderTarget = target

        mIEFrame?.mRenderTarget = target

        getOriginSize()
    }

    private fun setLastTransTime(time: Long) {
        lastTransTime = time
    }

    fun loadImage(isCurrentItem: Boolean) {
        inputRender.loadImage()

        // Load image frame
//        mIEFrame?.loadImage()

        if (isCurrentItem && mTransition is PagTransition) {
            (mTransition as PagTransition).loadPag()
        }
    }

    fun loadTexture() {
//        inputRender.loadTexture()
    }

    val originWidth: Int
        get() = mRenderTarget?.surfaceWidth ?: mOriginWidth

    val originHeight: Int
        get() = mRenderTarget?.surfaceHeight ?: mOriginHeight

    fun generatorHistogram(listener: IHistogramGenerateListener?) {
    }

    /**
     * Render all operators
     */
    private fun renderBaseTexture(localTime: Long, isCurrent: Boolean) {
        mRenderTarget?.let { mRenderTarget ->
            mRenderTarget.bindOffScreenFrameBuffer()
            baseTextureId = inputRender.renderBaseTexture(
                mRenderTarget.surfaceWidth,
                mRenderTarget.surfaceHeight,
                localTime.toFloat() / (duration + lastTransTime),
                isCurrent
            )
        }
    }

    private var currentLocalTime: Long = 0
    private var lastTime = 0L
    fun render(localTime: Long) {
        mRenderTarget?.let { mRenderTarget ->
            currentLocalTime = localTime
            if (mDrawer == null) {
                mDrawer = BaseImageDrawer()
            }
            renderBaseTexture(localTime + lastTransTime, true)
            if (System.currentTimeMillis() - lastTime > 100) {
                lastTime = System.currentTimeMillis()
            }
            mRenderTarget.bindOffScreenFrameBuffer()
            mRenderTarget.attachOffScreenTexture(mRenderTarget.inputTextureId)
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            mRenderTarget.attachOffScreenTexture(mRenderTarget.outputTextureId)
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            // scissor for output
//        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
//        GLES20.glScissor((int) (mScissorX * getRenderWidth()) + mRenderLeft,
//                (int) (mScissorY * getRenderHeight()) + mRenderBottom,
//                (int) (mScissorWidth * getRenderWidth()),
//                (int) (mScissorHeight * getRenderHeight()));
            mRenderTarget.attachOffScreenTexture(mRenderTarget.inputTextureId)
            mDrawer?.draw(
                baseTextureId,
                0,
                0,
                renderWidth,
                renderHeight,
                true,
                1.0f,
                1.0f, 0f, 0f
            )

            mRenderTarget.attachOffScreenTexture(mRenderTarget.outputTextureId)
            mDrawer?.draw(
                baseTextureId,
                0,
                0,
                renderWidth,
                renderHeight,
                true,
                1.0f,
                1.0f, 0f, 0f
            )
            for (operator in mOpList) {
                operator.exec()
            }
            kotlin.runCatching {
                if (mTransition != null && duration - localTime <= transitionDuration && mTransition?.isReady == true) {
                    mTransition?.setProgress(1.0f - (duration - localTime) * 1.0f / transitionDuration)
                    mTransition?.exec()
                }
            }

//        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
            mRenderTarget.bindOffScreenFrameBuffer()
            mRenderTarget.attachOffScreenTexture(mRenderTarget.outputTextureId)

            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            mDrawer?.draw(
                mRenderTarget.inputTextureId,
                0,
                0,
                mRenderTarget.surfaceWidth,
                mRenderTarget.surfaceHeight,
                false,
                1f,
                1f,
                0f,
                0f
            )
            mRenderTarget.swapTexture()
//            mIEFrame?.drawToSurface(mRenderTarget.inputTextureId)
        }
    }

    //    BlurFactor blurFactor = new BlurFactor();
    fun releaseImage() {
        inputRender.releaseImage()
    }

    fun releaseTexture() {
        inputRender.releaseTexture()
        mTransition?.release()
    }

    private fun getOriginSize() {
        mRenderTarget?.let { mRenderTarget ->
            mOriginWidth = mRenderTarget.surfaceWidth
            mOriginHeight = mRenderTarget.surfaceHeight
        }
    }

    fun setData(photo: PhotoEditModel) {
        setImagePath(photo)

//        if (photo.frameEditModel == null) {
//            mIEFrame = null
//        } else {
//            mIEFrame = IEFrame(photo.frameEditModel!!)
//            mIEFrame?.mRenderTarget = mRenderTarget
//        }

        photo.transition?.let {
            setTransition(it)
        }
        photoEditModel = photo

    }

//    fun setFrame(frameModel: FrameModel?) {
//        if (frameModel == null) {
//            mIEFrame = null
//        } else {
//            mIEFrame = IEFrame(FrameEditModel.invoke(frameModel, 0f))
//            mIEFrame?.mRenderTarget = mRenderTarget
//        }
//    }

    private fun setTransition(transitionEditModel: TransitionEditModel) {
        Log.d(TAG, "setTransition: ${transitionEditModel.duration} $position")
        IEManager.getInstance().runRenderThread(Runnable {
            if (transitionEditModel.duration > 0) {
                mTransition?.release()
                when (transitionEditModel) {
                    is TransitionGLSLEditModel -> {
                        Log.d(TAG, "setTransition: ${transitionEditModel.localPath} $position")
                        mTransition = TransUtils.getTransitionById(transitionEditModel.transId)
                    }
                    is TransitionPagOverlayEditModel -> {
                        Log.d(TAG, "setTransition: 2---")
                        mTransition = PagOverlayTransition(transitionEditModel)
//                        mTransition = PagReplaceTransition(transitionEditModel)
                    }
                }
                mTransition?.setRenderContext(this)
            } else {
                mTransition?.release()
                mTransition = null
            }
        })
//        Log.d(TAG, "setTransition: ${mTransition == null}")
    }

    private fun setImagePath(photoEditModel: PhotoEditModel) {
        Log.d(TAG, "setImagePath: $position ${photoEditModel.uuid} ${photoEditModel.path}")
        inputRender.setImagePath(photoEditModel.uuid, photoEditModel.path, photoEditModel.crop)
        inputRender.setBackground(photoEditModel.background)
    }

    enum class MoveType {
        NONE, ZOOM_IN, ZOOM_OUT, LEFT, RIGHT, TOP, BOTTOM, LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM,
    }

    companion object {
        private var mDrawer: BaseImageDrawer? = null
        private const val TAG = "IEClip"

    }

    init {
        setData(photoEditModel)
    }
}