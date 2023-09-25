package com.volio.vn.models.frame

import android.content.Context
import android.util.Log
import com.volio.vn.ConstantsFolder.FOLDER_FRAME
import com.volio.vn.models.RemoteData
import java.io.File
import kotlin.math.roundToInt

data class FrameEditModel(
    override val id: Long,
    override val idCategory: Long,
    override val name: String,
    override val remotePath: String, // remote file zip
    override val localPath: String,
    override val thumb: String
) : RemoteData {
    companion object {
        operator fun invoke(frameModel: FrameModel, intensity: Float): FrameEditModel {
            return FrameEditModel(
                id = frameModel.id,
                idCategory = frameModel.idCategory,
                name = frameModel.name,
                remotePath = frameModel.remotePath,
                localPath = frameModel.localPath,
                thumb = frameModel.thumb
            )
        }
    }


    // Todo
    fun getPhotoPathByRatio(context: Context, width: Int, height: Int): String? {
        val zipFileName =
            remotePath.substringAfterLast("/").substringBeforeLast(".zip").substringBeforeLast("_")
        val file =
            context.filesDir.absolutePath.plus(FOLDER_FRAME).plus("${zipFileName}/${zipFileName}")

        val realFilePath = when (((width * 100f) / height).roundToInt()) {
            in 95..110 -> file.plus("_11.png")
            in 50..60 -> file.plus("_916.png")
            in 70..85 -> file.plus("_45.png")
            in 170..182 -> file.plus("_169.png")
            in 128..140 -> file.plus("_43.png")
            else -> ""
        }

        return if (File(realFilePath).exists()) {
            realFilePath
        } else {
           null
        }
    }
}
