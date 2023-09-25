package com.createchance.imageeditor

import android.app.Application
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.SurfaceHolder
import android.view.TextureView.SurfaceTextureListener
import com.createchance.imageeditor.framerender.IEClip
import com.createchance.imageeditor.framerender.IERender
import com.createchance.imageeditor.framerender.TextureManage
import com.createchance.imageeditor.utils.Logger
import com.volio.vn.models.ProjectModel
import com.volio.vn.models.photo.PhotoEditModel
import kotlinx.coroutines.*
import java.io.File
import kotlin.math.abs

open class IEManager {
    private var mAppContext: Context? = null
    private var mPreviewTarget: IRenderTarget? = null
    private var mSaveTarget: VideoSaver? = null
    private var jobPlayer: Job? = null
    private var currentTimeNs = 0L
    private var lastRenderTimeNs = -1L
    private var currentRenderFrameStartTimeMs: Long = 0L
    private val timeDelayUpdateFrame = 16L
    private var mRenderThread: GLRenderThread? = null
    private var playListener: PlayListener? = null
    private val ieRender = IERender()

    //    private var currentPhotoIndex: Int = -1
    private var currentItemUUID: String = ""
    private var timeDelayCallbackMs: Long = 30
    private var isPlay: Boolean = false
    private var startTimeRepeatNs: Long = -1
    private var endTimeRepeatNs: Long = -1
    private var isUpdateTime: Boolean = true
    val totalDurationMs: Long
        get() = ieRender.totalDuration

    val clipList: Iterator<IEClip>
        get() = ieRender.clipList
    val context: Context?
        get() = mAppContext
    var isCancelSave: Boolean = false
    var isRenderSaved: Boolean = false
    private var currentProject: ProjectModel? = null
    private var lastTimeRender: Long = 0L


    interface PlayListener {
        fun onUpdateTime(currentTimeMs: Long, durationMs: Long)
        fun onChangePlaying(isPlaying: Boolean)
        fun onRenderItem(uuid: String)
        fun onSeekTo(currentTimeMs: Long, durationMs: Long, isUser: Boolean)
    }

    fun init(context: Application) {
        mAppContext = context.applicationContext
    }

    fun setPlayListener(listener: PlayListener?) {
        playListener = listener
        listener?.onChangePlaying(isPlay)
        listener?.onUpdateTime(currentTimeNs.nanoToMilli(), totalDurationMs)
        listener?.onRenderItem(currentItemUUID)
    }

    fun setData(projectModel: ProjectModel) {
        currentProject = projectModel
        ieRender.setData(projectModel)
        renderCurrentFrame()
    }

    fun getCurrentProjectData() = currentProject


    fun startEngine() {
        if (mRenderThread == null) {
            mRenderThread = GLRenderThread()
            mRenderThread?.start()
        }
    }

    fun stopEngine() {
        if (mRenderThread == null) {
            return
        }
        // release resources.
        mRenderThread?.post {
            ieRender.release()
            ieRender.clearData()
            TextureManage.deleteTexture()
            mRenderThread?.stopThread()
            mRenderThread = null
        }
    }


    fun attachPreview(previewView: IEPreviewView) {
        mPreviewTarget = previewView
        previewView.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                Logger.d(
                    TAG,
                    "onSurfaceTextureAvailable, width: $width, height: $height"
                )
                mRenderThread?.post {
                    mPreviewTarget?.init(mRenderThread?.eglCore)
                    mPreviewTarget?.makeCurrent()
                    ieRender.setTarget(mPreviewTarget)
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                Logger.d(TAG, "onSurfaceTextureDestroyed: ")
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                Logger.d(TAG, "onSurfaceTextureUpdated: ")
            }
        }
    }

    fun detachPreview(previewView: IEPreviewView?) {
        mRenderThread?.post {
            mPreviewTarget?.release()
            mPreviewTarget = null
        }
    }

    private var surfaceHolderCallback: SurfaceHolder.Callback? = null
    fun attachPreview(previewView: IESurfaceView) {
        if (previewView != mPreviewTarget) {
            mPreviewTarget = previewView
            surfaceHolderCallback = object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    mRenderThread?.post {
                        Log.d(TAG, "surfaceCreated: ")
                        mPreviewTarget?.init(mRenderThread?.eglCore)
                        mPreviewTarget?.makeCurrent()
                        ieRender.setTarget(mPreviewTarget)
                        ieRender.release()
                        TextureManage.resetTexture()
                        if (isRenderSaved) currentTimeNs = 0
                        isRenderSaved = false
                    }
                    renderCurrentFrame()
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    renderCurrentFrame()
                    TextureManage.resetTexture()
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mRenderThread?.post {
                        TextureManage.resetTexture()
                        ieRender.release()
                        mPreviewTarget?.release()
                        mPreviewTarget = null
                    }
                    previewView.holder.removeCallback(this)
                }
            }
            previewView.holder.addCallback(surfaceHolderCallback)
        }
    }

    fun detachPreview(previewView: IESurfaceView?) {
        previewView?.holder?.removeCallback(surfaceHolderCallback)
        mRenderThread?.post {
            mPreviewTarget?.release()
            mPreviewTarget = null
        }
    }


    fun getClip(clipIndex: Int): IEClip {
        return ieRender.getClip(clipIndex)
    }

    fun saveAsVideo(
        width: Int,
        height: Int,
        frameRate: Int,
        target: File?,
        bgmFile: File?,
        tempFolder: File,
        startTimeMs: Long,
        endTimeMs: Long,
        saveListener: SaveListener?
    ): Boolean {
        if (width <= 0 || height <= 0) {
            Logger.e(
                TAG,
                "Output size invalid, width: $width, height: $height"
            )
        }
        if (target == null) {
            Logger.e(TAG, "Target file can not be null!")
            return false
        }
        Log.d(TAG, "saveAsVideo: ${bgmFile?.path}")
        pause()
        val totalDuration = ieRender.totalDuration
        val videoSaver = VideoSaver(
            width,
            height,
            frameRate,
            totalDuration,
            target,
            bgmFile,
            tempFolder,
            startTimeMs,
            endTimeMs,
            saveListener
        )
        isCancelSave = false
        mSaveTarget = videoSaver
        ieRender.setTarget(mSaveTarget)
        mRenderThread?.post {
            ieRender.release()
            TextureManage.resetTexture()
            mSaveTarget?.init(mRenderThread?.eglCore)
            mSaveTarget?.makeCurrent()
            isRenderSaved = true
            var curTime: Double = 0.0
            var count = 0
            while (curTime < totalDuration) {
                if (isCancelSave) {
                    break
                }
                if (!videoSaver.isReadySave.get()) {
                    count++
                    Thread.sleep(0, 100000)
                    continue
                }
                Log.d(TAG, "saveAsVideo: $count")
                count = 0
                videoSaver.isReadySave.set(false)
                ieRender.renderAtPosition(curTime.toLong(), true)
                curTime += 1000.0 / frameRate
            }
            mSaveTarget?.release()
            TextureManage.resetTexture()
            mSaveTarget = null
            ieRender.release()
            mPreviewTarget?.makeCurrent()
//            ieRender.setTarget(mPreviewTarget)
        }
        return true
    }

    fun runRenderThread(runnable: Runnable?) {
        mRenderThread?.post(runnable)
    }

    private fun renderAtPosition(
        position: Long, onSuccess: (processingTime: Long) -> Unit = {}
    ): String {
        return ieRender.renderAtPosition(position, false, onSuccess)
    }

    protected fun renderCurrentFrame() {
        lastRenderTimeNs = -1
        currentRenderFrameStartTimeMs = System.currentTimeMillis()
        seekTo(currentTimeNs, false)
    }

    fun play() {
        isPlay = true
        isUpdateTime = true
        playListener?.onChangePlaying(isPlay)
        runUpdateFrameAsync()
    }

    fun pause() {
        isPlay = false
        playListener?.onChangePlaying(isPlay)
    }

    fun stop() {
        pause()
        jobPlayer?.cancel()
        jobPlayer = null
    }

    fun seek(positionMs: Long) {
        currentTimeNs = positionMs.milliToNano()
        playListener?.onSeekTo(positionMs, totalDurationMs, true)
        runUpdateFrameAsync()
    }

    private fun seekTo(positionNs: Long, isUser: Boolean) {
        playListener?.onSeekTo(positionNs.nanoToMilli(), totalDurationMs, isUser)
        currentTimeNs = positionNs
        runUpdateFrameAsync()
    }

    fun seekTo(photoEditModel: PhotoEditModel) {
        clipList.forEach { ieClip ->
            if (ieClip.photoEditModel.uuid == photoEditModel.uuid) {
                seek(ieClip.startTime + 1)
                currentItemUUID = photoEditModel.uuid
                playListener?.onRenderItem(currentItemUUID)
            }
        }
    }

    fun setUpdateTime(isUpdate: Boolean) {
        isUpdateTime = isUpdate
    }

    private fun runUpdateFrameAsync() {
        if (jobPlayer == null && mPreviewTarget != null) {
            jobPlayer = CoroutineScope(Dispatchers.Default).launch {
                launch {
                    var lastTimeCallback: Long = 0
                    while (true) {
                        val time = System.nanoTime()
                        val duration = totalDurationMs
                        if (currentTimeNs > duration.milliToNano()) {
                            seekTo(0, false)
                        }

                        withContext(Dispatchers.Main) {
                            if (abs(System.currentTimeMillis() - lastTimeCallback) > timeDelayCallbackMs) {
                                lastTimeCallback = System.currentTimeMillis()
                                playListener?.onUpdateTime(currentTimeNs.nanoToMilli(), duration)
                            }
                        }
                        delay(timeDelayUpdateFrame)
                        if (isPlay && isUpdateTime) {
                            currentTimeNs += (System.nanoTime() - time)
                        }
                        if (endTimeRepeatNs > 0 && startTimeRepeatNs >= 0
                            && (currentTimeNs < startTimeRepeatNs || currentTimeNs >= endTimeRepeatNs)
                        ) {
                            seekTo(startTimeRepeatNs, false)
                        }
                    }
                }
                launch {
                    var isRender = false
                    while (true) {
                        if (!isRender && (lastRenderTimeNs != currentTimeNs ||
                                    System.currentTimeMillis() - currentRenderFrameStartTimeMs < 100
                                    || (!isPlay && System.currentTimeMillis() - lastTimeRender > 100))
                            && mPreviewTarget?.isPreview == true
                        ) {
                            isRender = true
                            lastTimeRender = System.currentTimeMillis()
                            lastRenderTimeNs = currentTimeNs
                            val timeRender = currentTimeNs.nanoToMilli()
                            val uuid = renderAtPosition(timeRender) {
                                isRender = false
                            }
                            if (currentItemUUID != uuid) {
                                playListener?.onRenderItem(uuid)
                                currentItemUUID = uuid
                            }
                        }
                        delay(4)
                    }
                }

            }

        }
    }


    fun setRepeat(photoEditModel: PhotoEditModel, isOnlyRepeatTransitions: Boolean = false) {
        for (it in clipList) {
            if (it.photoEditModel.uuid == photoEditModel.uuid) {
                if (isOnlyRepeatTransitions) {
                    if (it.transitionDuration > 0) {
                        endTimeRepeatNs = (it.endTime - timeDelayUpdateFrame).milliToNano()
                        startTimeRepeatNs =
                            (it.endTime - (it.photoEditModel.transition?.duration
                                ?: 0L)).milliToNano()
                    } else {
                        val startMs = it.startTime + it.duration / 2
                        val endTimeMs = startMs + it.duration
                        startTimeRepeatNs = startMs.milliToNano()
                        endTimeRepeatNs = endTimeMs.milliToNano()
                    }
                } else {
                    endTimeRepeatNs = (it.endTime - timeDelayUpdateFrame).milliToNano()
                    startTimeRepeatNs = it.startTime.milliToNano()
                }
                currentTimeNs = startTimeRepeatNs
            }
        }

    }

    fun clearRepeat() {
        startTimeRepeatNs = -1
        endTimeRepeatNs = -1
    }

    fun cancelSave() {
        isCancelSave = true
        mSaveTarget?.cancel()
    }

    fun isPlaying() = isPlay

    fun release() {
        ieRender.release()
        ieRender.clearData()
        TextureManage.resetTexture()
    }


    private fun Long.milliToNano() = this * 1000000
    private fun Long.nanoToMilli() = this / 1000000

    private object Holder {
        val INSTANCE = IEManager()
    }

    companion object {
        private const val TAG = "IEManager"
        fun getInstance(): IEManager {
            return Holder.INSTANCE
        }
    }
}