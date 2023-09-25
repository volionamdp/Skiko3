package com.createchance.imageeditor.framerender

import android.content.Context
import android.util.Log
import com.createchance.imageeditor.IEManager
import com.createchance.imageeditor.IRenderTarget
import com.volio.vn.models.ProjectModel
import com.volio.vn.models.frame.FrameEditModel
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class IERender {
    private val mClipList: CopyOnWriteArrayList<IEClip> = CopyOnWriteArrayList()
    private val mListTheme: CopyOnWriteArrayList<IETheme> = CopyOnWriteArrayList()
//    private val mListRender: MutableList<RenderData> = mutableListOf()
    private var mFrame: IEFrame? = null
    private var mRenderTarget: IRenderTarget? = null
    private var mRenderDefault:IERenderDefaultScreen = IERenderDefaultScreen()
    private var mStickers = IEStickers()
    private val text = Text()
    fun release() {
        mClipList.iterator().forEach { clip ->
            clip.releaseImage()
            clip.releaseTexture()
        }

        mListTheme.iterator().forEach { theme ->
            theme.releaseData()
            theme.releaseTexture()
        }

        mFrame?.releaseImage()
        mFrame?.releaseTexture()
    }

    fun clearData() {
        mClipList.clear()
    }

    fun setTarget(target: IRenderTarget?) {
        mListTheme.iterator().forEach { theme ->
            theme.setRenderTarget(target)
        }
        mClipList.iterator().forEach { clip ->
            clip.setRenderTarget(target)
        }
        mRenderDefault.setRenderTarget(target)
        mFrame?.mRenderTarget = target
        mStickers.target = target
        mRenderTarget = target
        text.setRenderTarget(target)
    }

    fun setData(projectModel: ProjectModel) {
        val listData = projectModel.listPhoto
        Log.d(TAG, "setData---0: ${listData.size}")
        val listClip: MutableList<IEClip> = mutableListOf()
        var startTime: Long = 0
        var endTime: Long
        var check: Boolean
        for ((index, photo) in listData.withIndex()) {
            check = false
            if (index == listData.size - 1) {
                photo.transition?.duration = 0L
            }
            endTime = startTime + photo.duration + (photo.transition?.duration ?: 0)
            val clipIterator = mClipList.iterator()
            Log.d(TAG, "setData: ${photo.uuid}")
            for (ieClip in clipIterator) {
                if (photo.uuid == ieClip.photoEditModel.uuid) {
                    ieClip.position = index
                    ieClip.setData(photo)
                    ieClip.startTime = startTime
                    ieClip.endTime = endTime
                    listClip.add(ieClip)
                    ieClip.setRenderTarget(mRenderTarget)
                    check = true
                    break
                }
            }
            if (!check) {
                val ieClip = IEClip(photo, startTime, endTime, index)
                ieClip.setRenderTarget(mRenderTarget)
                listClip.add(ieClip)
            }
            startTime = endTime
        }
        mClipList.clear()
        mClipList.addAll(listClip)



        if (projectModel.prjFrameModel == null) {
            mFrame = null
        } else {
            mFrame = IEFrame(FrameEditModel.invoke(projectModel.prjFrameModel!!, 0f))
            mFrame!!.mRenderTarget = mRenderTarget
        }

        if (projectModel.theme != null) {
            val lisTemp = mutableListOf<IETheme>()
            val fileName =
                projectModel.theme!!.localPath
            val context: Context? = IEManager.getInstance().context
            context?.let {
                val file = File(fileName)
                val files = file.listFiles()
                val listPagPath = files?.filter { it.name.endsWith(".pag") }
                    ?.map { fileName + "/" + it.name }
                listPagPath?.forEach {
                    val themeIterator = mListTheme.iterator()
                    var ieThem: IETheme? = null
                    for (theme in themeIterator) {
                        if (it == theme.mImageFilePath) {
                            ieThem = theme
                            ieThem.startTime = 0
                            ieThem.endTime = totalDuration
                        }
                    }
                    if (ieThem == null) {
                        ieThem = IETheme(it, 0, totalDuration)
                    }
                    ieThem.setRenderTarget(mRenderTarget)
                    lisTemp.add(ieThem)
                }
            }
            mListTheme.clear()
            mListTheme.addAll(lisTemp)
        } else {
            mListTheme.clear()
        }
        projectModel.listSticker?.let { mStickers.setData(it) }
    }

    val totalDuration: Long
        get() {
            var duration: Long = 0
            mClipList.iterator().forEach { clip ->
                duration += clip.duration
            }
            return duration
        }


    fun getClip(clipIndex: Int): IEClip {
        val iterator = mClipList.iterator()
        var item: IEClip? = null
        for ((index, value) in iterator.withIndex()) {
            item = value
            if (index == clipIndex) {
                return value
            }
        }
        return item!!

    }

    fun getSurfaceWidth(clipIndex: Int): Int {
        val iterator = mClipList.iterator()
        for ((index, value) in iterator.withIndex()) {
            if (index == clipIndex) {
                return value.surfaceWidth
            }
        }
        return -1
    }

    fun getSurfaceHeight(clipIndex: Int): Int {
        val iterator = mClipList.iterator()
        for ((index, value) in iterator.withIndex()) {
            if (index == clipIndex) {
                return value.surfaceHeight
            }
        }
        return -1
    }

    fun getRenderWidth(clipIndex: Int): Int {
        val iterator = mClipList.iterator()
        for ((index, value) in iterator.withIndex()) {
            if (index == clipIndex) {
                return value.renderWidth
            }
        }
        return -1
    }

    fun getRenderHeight(clipIndex: Int): Int {
        val iterator = mClipList.iterator()
        for ((index, value) in iterator.withIndex()) {
            if (index == clipIndex) {
                return value.renderHeight
            }
        }
        return -1
    }

    val clipList: Iterator<IEClip>
        get() = mClipList.iterator()

    private fun loadImageIEClip(clipIndex: Int, isCurrentItem: Boolean) {
        val iterator = mClipList.iterator()
        for ((index, value) in iterator.withIndex()) {
            if (index == clipIndex) {
                value.loadImage(isCurrentItem)
                break
            }
        }
    }

    private fun releaseUnnecessaryDataIEClip(clipIndex: Int) {
        val iterator = mClipList.iterator()
        for ((index, ieClip) in iterator.withIndex()) {
            if (index + 2 < clipIndex || index - 2 > clipIndex) {
                ieClip.releaseImage()
            }
        }
    }

    private fun releaseUnnecessaryTextureIEClip(currentIndex: Int) {
        val iterator = mClipList.iterator()
        for ((index, ieClip) in iterator.withIndex()) {
            if (index + 2 < currentIndex || index - 2 > currentIndex) {
                ieClip.releaseTexture()
            }
        }
    }

    private fun renderIEClip(
        clipIndex: Int,
        currentTime: Long
    ) {
        val iterator = mClipList.iterator()
        for ((index, clip) in iterator.withIndex()) {
            if (index == clipIndex) {
                clip.render(currentTime - clip.startTime)
                break
            }
        }
    }

    private fun loadDataIETheme(themeIndex: Int) {
        val iterator = mListTheme.iterator()
        for ((index, theme) in iterator.withIndex()) {
            if (index == themeIndex) {
                theme.loadPag()
                break
            }
        }
    }

    private fun loadDataFrame() {
        mFrame?.loadImage()
    }

    private fun renderFrame() {
        mFrame?.drawToSurface(0)
    }

    private fun renderTheme(
        themeIndex: Int,
        currentTheme: Long,
    ) {
        val iterator = mListTheme.iterator()
        for ((index, theme) in iterator.withIndex()) {
            if (index == themeIndex) {
                theme.render(currentTheme - theme.startTime)
                break
            }
        }
    }

    private fun loadCurrentDataAllLayer(listRender: List<RenderData>) {
        listRender.forEachIndexed { _, data ->
            when (data) {
                is RenderClipData -> {
                    loadImageIEClip(data.index, true)
                    loadImageIEClip(data.index + 1, false)
                }
                is RenderThemeData -> {
                    loadDataIETheme(data.index)
                }
                is RenderFrameData -> {
                    loadDataFrame()
                }
            }
        }
    }

    private fun loadNextDataAllLayer(listRender: List<RenderData>, position: Long) {
        listRender.forEachIndexed { _, data ->
            when (data) {
                is RenderClipData -> {
                    val iterator = mClipList.iterator()
                    for ((index, clip) in iterator.withIndex()) {
                        if (index == data.index && position - clip.startTime > 200) {
                            loadImageIEClip(data.index + 2, false)
                            break
                        }
                    }
                }
                is RenderFrameData -> {
                    loadDataFrame()
                }
                else -> {}
            }
        }
    }

    private fun renderAllLayer(listRender: List<RenderData>, position: Long) {
        listRender.forEachIndexed { _, data ->
            when (data) {
                is RenderClipData -> {
                    renderIEClip(data.index, position)
                }
                is RenderThemeData -> {
                    renderTheme(data.index, position)
                }
                is RenderFrameData -> {
                    renderFrame()
                }
                is RenderStickerData->{
                    renderSticker(data.currentClipId,data.nextClipId,position)
                }
            }
        }
        text.draw()
        mRenderDefault.draw()
    }

    private fun renderSticker(currentClipId: String, nextClipId: String, position: Long) {
        mStickers.draw(position,currentClipId,nextClipId)
    }


    private fun releaseUnnecessaryAllDataLayer(listRender: List<RenderData>) {
        listRender.forEachIndexed { _, data ->
            when (data) {
                is RenderClipData -> {
                    releaseUnnecessaryDataIEClip(data.index)
                }

                else -> {}
            }
        }
    }

    private fun releaseAllTextureLayer(listRender: List<RenderData>) {
        listRender.forEachIndexed { _, data ->
            when (data) {
                is RenderClipData -> {
                    releaseUnnecessaryTextureIEClip(data.index)
                }

                else -> {}
            }
        }
    }

    fun renderAtPosition(
        position: Long,
        isSynchronized: Boolean,
        onSuccess: (processingTime: Long) -> Unit = {}
    ): String {
        val time = System.currentTimeMillis()
        var currentUUID = ""
        var nextUUid = ""
        val mListRender: MutableList<RenderData> = mutableListOf()
        if (mRenderTarget?.isInitSuccess == true) {
            mListRender.clear()
            val clipIterator = mClipList.iterator()
            for ((index, clip) in clipIterator.withIndex()) {
                if (currentUUID.isNotEmpty()){
                    nextUUid = clip.photoEditModel.uuid
                    break
                }
                if (position >= clip.startTime && position < clip.endTime) {
                    mListRender.add(RenderClipData(index))
                    currentUUID = clip.photoEditModel.uuid
                }
            }
            val themeIterator = mListTheme.iterator()
            for ((index, theme) in themeIterator.withIndex()) {
                if (position >= theme.startTime && position < theme.endTime) {
                    mListRender.add(RenderThemeData(index))
                }
            }
            mFrame?.let {
                mListRender.add(RenderFrameData(0))
            }
            mListRender.add(RenderStickerData(currentUUID,nextUUid))
            if (isSynchronized) {
                loadCurrentDataAllLayer(mListRender)
                renderAllLayer(mListRender, position)
                releaseUnnecessaryAllDataLayer(mListRender)
                releaseAllTextureLayer(mListRender)
                mRenderTarget?.swapBuffers()
                onSuccess(System.currentTimeMillis() - time)
            } else {
                loadCurrentDataAllLayer(mListRender)
                IEManager.getInstance().runRenderThread {
                    renderAllLayer(mListRender, position)
                    releaseAllTextureLayer(mListRender)
                    mRenderTarget?.swapBuffers()
                    onSuccess(System.currentTimeMillis() - time)
                }
                releaseUnnecessaryAllDataLayer(mListRender)
                loadNextDataAllLayer(mListRender, position)
            }


        } else {
            onSuccess(System.currentTimeMillis() - time)
        }
        return currentUUID
    }


    private open class RenderData()
    private class RenderClipData(var index: Int): RenderData()
    private class RenderThemeData(var index: Int): RenderData()
    private class RenderFrameData(var index: Int): RenderData()
    private class RenderStickerData(var currentClipId:String,var nextClipId:String): RenderData()
    private enum class RenderType {
        IE_CLIP, IE_THEME, IE_FRAME
    }

    companion object {
        private const val TAG = "IERender"
    }
}