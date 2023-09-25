package com.createchance.imageeditor

import android.annotation.SuppressLint
import android.media.*
import android.opengl.GLES20
import com.createchance.imageeditor.gles.EglCore
import com.createchance.imageeditor.gles.WindowSurface
import com.createchance.imageeditor.utils.Logger
import com.createchance.imageeditor.utils.UiThreadUtil
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min

/**
 * Video saver, save images as video.
 *
 * @author createchance
 * @date 2018/12/30
 */
class VideoSaver(
    private val mSurfaceWidth: Int,
    private val mSurfaceHeight: Int,
    private var mFrameRate: Int,
    videoDurationMs: Long,
    private val mOutputFile: File,
    private val mAudioInput: File?,
    private val mTempFolder: File,
    private val mAudioStartTimeMs: Long,
    private val mAudioEndTimeMs: Long,
    private val mListener: SaveListener?
) : IRenderTarget {
    private val mVideoFile: File = mOutputFile
    private val mAudioFile: File? = mAudioInput
    private val mOffScreenFrameBuffer = IntArray(1)
    private val mOffScreenTextureIds = IntArray(3)
    private var mInputTextureIndex = 0
    private var mOutputTextureIndex = 1
    private val mVideoDuration: Long = videoDurationMs * 1000
    private var mWindowSurface: WindowSurface? = null
    private var mVideoEncoder: MediaCodec? = null
    private var mRequestStop = false
    private var mCancel = false
    private var mBitRate = 10000000
    private var mSaveThread: SaverThread? = null
    private var videoProgressMax: Float = 0.98f
    private var timeDelayCallbackProgressMs: Long = 50
    private var initSuccess = false

    val isReadySave: AtomicBoolean = AtomicBoolean(false)
    override fun init(eglCore: EglCore?) {
        prepare(eglCore)
        mWindowSurface?.makeCurrent()
        createOffScreenFrameBuffer()
        createOffScreenTextures()
        initSuccess = true
    }

    override fun getInputTextureId(): Int {
        return mOffScreenTextureIds[mInputTextureIndex]
    }

    override fun getOutputTextureId(): Int {
        return mOffScreenTextureIds[mOutputTextureIndex]
    }

    override fun getTempleTextureId(): Int {
        return mOffScreenTextureIds[2]
    }

    override fun bindOffScreenFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mOffScreenFrameBuffer[0])
    }

    override fun attachOffScreenTexture(textureId: Int) {
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, textureId, 0
        )
    }

    override fun bindDefaultFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    override fun getSurfaceWidth(): Int {
        return mSurfaceWidth
    }

    override fun getSurfaceHeight(): Int {
        return mSurfaceHeight
    }

    override fun swapTexture() {
        val tmp = mInputTextureIndex
        mInputTextureIndex = mOutputTextureIndex
        mOutputTextureIndex = tmp
    }

    override fun makeCurrent() {
        if (mWindowSurface != null) {
            mWindowSurface?.makeCurrent()
        }
    }

    override fun swapBuffers() {
        if (mWindowSurface != null) {
            mWindowSurface?.swapBuffers()
        }
    }

    override fun release() {
        mRequestStop = true
        deleteOffScreenFrameBuffer()
        if (mWindowSurface != null) {
            mWindowSurface?.release()
        }
        initSuccess = false
    }

    override fun isPreview(): Boolean {
        return false
    }

    override fun isInitSuccess(): Boolean {
        return initSuccess
    }

    fun setBitrate(bitrate: Int) {
        mBitRate = bitrate
    }

    fun setFrameRate(frameRate: Int) {
        mFrameRate = frameRate
    }

    private fun estimateVideoBitRate(width: Int, height: Int, frameRate: Int): Int {
        return (0.07f * 2 * width * height * frameRate).toInt()
    }

    private fun calcBitRate(width: Int, height: Int): Int {
//        val min = min(width, height)
//        var max = max(width, height)
//        if (max < min * 16 / 9) {
//            max = min * 16 / 9
//        }
//        val bitrate = (BPP * mFrameRate * max * min).toInt()

        val bitrate = (BPP * mFrameRate * width * height).toInt()
        when (min(width, height)) {
            in 481..720 -> {
                return bitrate * 2
            }
            in 0..480 -> {
                return bitrate * 4
            }
        }
        return bitrate
    }

    private fun prepare(eglCore: EglCore?) {
        try {
            // init video format
            val videoFormat =
                MediaFormat.createVideoFormat("video/avc", mSurfaceWidth, mSurfaceHeight)
            videoFormat.setInteger(
                MediaFormat.KEY_BIT_RATE,
                calcBitRate(mSurfaceWidth, mSurfaceHeight)
            )
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate)
            videoFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 3)
            // init audio format
            val audioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", 44100, 2)
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000)
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 512 * 1024)
            audioFormat.setInteger(
                MediaFormat.KEY_AAC_PROFILE,
                MediaCodecInfo.CodecProfileLevel.AACObjectLC
            )

            // init video encoder
            mVideoEncoder = MediaCodec.createEncoderByType("video/avc")
            mVideoEncoder!!.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            val inputSurface = mVideoEncoder!!.createInputSurface()
            mWindowSurface = WindowSurface(eglCore, inputSurface, true)
            mVideoEncoder?.start()

            // init saver thread.
            mSaveThread = SaverThread()
            mSaveThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            if (mVideoEncoder != null) {
                kotlin.runCatching {
                    mVideoEncoder?.stop()
                }
            }
            UiThreadUtil.post {
                mListener?.onSaveFailed()
            }
        }
    }

    private fun createOffScreenFrameBuffer() {
        GLES20.glGenFramebuffers(mOffScreenFrameBuffer.size, mOffScreenFrameBuffer, 0)
    }

    private fun createOffScreenTextures() {
        GLES20.glGenTextures(mOffScreenTextureIds.size, mOffScreenTextureIds, 0)
        for (mTextureId in mOffScreenTextureIds) {
            // bind to fbo texture cause we are going to do setting.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mSurfaceWidth, mSurfaceHeight,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
            )
            // 设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )
            // 设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            // 设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            // 设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            // unbind fbo texture.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }

    private fun deleteOffScreenFrameBuffer() {
        GLES20.glDeleteFramebuffers(mOffScreenFrameBuffer.size, mOffScreenFrameBuffer, 0)
    }

    private inner class SaverThread : Thread() {
        private val TIME_OUT: Long = 5000
        override fun run() {
            mCancel = false
            if (mAudioInput != null) {
                doMuxVideoAndAudio(mAudioInput)
            } else {
                doMuxOnlyVideo()
            }
        }

        private fun doMuxOnlyVideo(): Boolean {
            var outputBufferId: Int
            val bufferInfo = MediaCodec.BufferInfo()
            var buffer: ByteBuffer?
            val framePts = (1000 * 1000 / mFrameRate).toLong()
            var nowPts: Long = 0
            var muxer: MediaMuxer? = null
            var tempVideoTrackId = -1
            var lastTime = 0L
            try {
                muxer =
                    MediaMuxer(mVideoFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                while (true) {
                    if (mRequestStop || mCancel) {
                        mRequestStop = false
                        Logger.d(TAG, "Request stop, so we are stopping.")
                        break
                    }
                    outputBufferId = mVideoEncoder!!.dequeueOutputBuffer(bufferInfo, TIME_OUT)
                    if (outputBufferId >= 0) {
                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            Logger.d(TAG, "Reach video eos.")
                            mVideoEncoder!!.signalEndOfInputStream()
                            break
                        }
                        buffer = mVideoEncoder!!.getOutputBuffer(outputBufferId)
                        bufferInfo.presentationTimeUs = nowPts
                        nowPts += framePts
                        Logger.i(
                            TAG,
                            "doMux..........., pts: " + outputBufferId + "   " + bufferInfo.presentationTimeUs + " "
                        )
                        if (mListener != null && System.currentTimeMillis() - lastTime > timeDelayCallbackProgressMs) {
                            lastTime = System.currentTimeMillis()
                            UiThreadUtil.post {
                                if (bufferInfo.presentationTimeUs <= mVideoDuration) {
                                    mListener.onSaveProgress(bufferInfo.presentationTimeUs * 1.0f / mVideoDuration)
                                }
                            }
                        }
                        muxer.writeSampleData(tempVideoTrackId, buffer!!, bufferInfo)
                        mVideoEncoder!!.releaseOutputBuffer(outputBufferId, false)
                        isReadySave.set(true)
                    } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        val videoFormat = mVideoEncoder!!.outputFormat
                        tempVideoTrackId = muxer.addTrack(videoFormat)
                        muxer.start()
                        isReadySave.set(true)
                    } else {
                        isReadySave.set(true)
                        Logger.d(
                            "VideoSaver",
                            "no data: $outputBufferId"
                        )
                    }
                }
                Logger.d(TAG, "Mux video done!")
                UiThreadUtil.post {
                    if (!mCancel) {
                        mListener?.onSaveProgress(1f)
                        mListener?.onSaved(mOutputFile)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.e(TAG, "Save worker failed.")
                UiThreadUtil.post { mListener?.onSaveFailed() }
                return false
            } finally {
                kotlin.runCatching {
                    if (muxer != null) {
                        muxer.stop()
                        muxer.release()
                    }
                    if (mVideoEncoder != null) {
                        mVideoEncoder!!.stop()
                        mVideoEncoder!!.release()
                    }
                }.onFailure {
                    it.printStackTrace()
                }
                if (mCancel) {
                    UiThreadUtil.post {
                        mListener?.onCancel(mOutputFile)
                    }
                }
            }
            return true
        }

        @SuppressLint("WrongConstant")
        private fun doMuxVideoAndAudio(mAudioInput: File): Boolean {
            var outputBufferId: Int
            val bufferInfo = MediaCodec.BufferInfo()
            var buffer: ByteBuffer?
            val framePts = (1000 * 1000 / mFrameRate).toLong()
            var nowPts: Long = 0
            var muxer: MediaMuxer? = null
            var videoTrackId = -1
            var audioTrackId = -1
            var audioExtractor: MediaExtractor? = null
            val buffer2 = ByteBuffer.allocate(512 * 1024)
            var lastTime = System.currentTimeMillis()
            try {
                muxer =
                    MediaMuxer(mVideoFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                while (true) {
                    if (mRequestStop || mCancel) {
                        mRequestStop = false
                        Logger.d(TAG, "Request stop, so we are stopping.")
                        break
                    }
                    outputBufferId = mVideoEncoder!!.dequeueOutputBuffer(bufferInfo, TIME_OUT)
                    if (outputBufferId >= 0) {
                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            Logger.d(TAG, "Reach video eos.")
                            mVideoEncoder!!.signalEndOfInputStream()
                            break
                        }
                        buffer = mVideoEncoder!!.getOutputBuffer(outputBufferId)
                        bufferInfo.presentationTimeUs = nowPts
                        nowPts += framePts
                        Logger.i(
                            TAG,
                            "doMux..........., pts: " + outputBufferId + "   " + bufferInfo.presentationTimeUs + " "
                        )
                        if (mListener != null && System.currentTimeMillis() - lastTime > timeDelayCallbackProgressMs) {
                            lastTime = System.currentTimeMillis()
                            UiThreadUtil.post {
                                if (bufferInfo.presentationTimeUs <= mVideoDuration) {
                                    mListener.onSaveProgress(bufferInfo.presentationTimeUs * videoProgressMax / mVideoDuration)
                                }
                            }
                        }
                        muxer.writeSampleData(videoTrackId, buffer!!, bufferInfo)
                        mVideoEncoder!!.releaseOutputBuffer(outputBufferId, false)
                        isReadySave.set(true)
                    } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        val videoFormat = mVideoEncoder!!.outputFormat
                        Logger.d(
                            TAG,
                            "Video encode output format: $videoFormat"
                        )
                        audioExtractor = MediaExtractor()
                        audioExtractor.setDataSource(mAudioInput.absolutePath)
                        audioTrackId = muxer.addTrack(audioExtractor.getTrackFormat(0))

                        videoTrackId = muxer.addTrack(videoFormat)
                        muxer.start()
                        isReadySave.set(true)

                    } else {
                        isReadySave.set(true)
                        Logger.d(
                            "VideoSaver",
                            "no data: $outputBufferId"
                        )

                    }
                }
                Logger.d(TAG, "Mux video done!")
                audioExtractor?.selectTrack(0)
                if (audioExtractor != null) {
                    while (true) {
                        val sampleSize = audioExtractor.readSampleData(buffer2, 0) ?: -1
                        if (sampleSize != -1) {
                            bufferInfo.size = sampleSize
                            bufferInfo.flags = audioExtractor.sampleFlags
                            bufferInfo.offset = 0
                            bufferInfo.presentationTimeUs = audioExtractor.sampleTime
                            if (bufferInfo.presentationTimeUs >= 0) {
                                muxer.writeSampleData(audioTrackId, buffer2, bufferInfo)
                            }
                            audioExtractor.advance()
                            if (mListener != null && System.currentTimeMillis() - lastTime > timeDelayCallbackProgressMs) {
                                lastTime = System.currentTimeMillis()
                                UiThreadUtil.post {
                                    if (bufferInfo.presentationTimeUs <= mVideoDuration) {
                                        mListener.onSaveProgress(videoProgressMax + bufferInfo.presentationTimeUs * (1f - videoProgressMax) / mVideoDuration)
                                    }
                                }
                            }
                        } else {
                            Logger.d(TAG, "Read video done.")
                            break
                        }
                    }
                }
                UiThreadUtil.post {
                    if (!mCancel) {
                        mListener?.onSaveProgress(1f)
                        mListener?.onSaved(mOutputFile)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.e(TAG, "Save worker failed.")
                UiThreadUtil.post { mListener?.onSaveFailed() }
                return false
            } finally {
                kotlin.runCatching {
                    if (muxer != null) {
                        audioExtractor?.release()
                        muxer.stop()
                        muxer.release()
                    }
                    if (mVideoEncoder != null) {
                        mVideoEncoder!!.stop()
                        mVideoEncoder!!.release()
                    }
                }
                if (mCancel) {
                    UiThreadUtil.post {
                        mListener?.onCancel(mOutputFile)
                    }
                }
            }
            return true
        }

    }

    fun cancel() {
        mCancel = true
    }


    companion object {
        private const val TAG = "VideoSaver"
        private const val BPP = 0.25f
    }

    init {
        //        mAudioFile = File(outputFile.parent, "audio_track_only.aac")
    }
}