package com.volio.vn.models.font

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextEditModel(
    val id: Long,
    val name: String,
    val remotePath: String,
    val localPath: String,
    val textContent: String,
    val color: Long,
    val height: Float,
    val with: Float,
    var matrix: FloatArray,
) : Parcelable {
    companion object {
        operator fun invoke(
            fontModel: FontModel,
            textContent: String,
            color: Long,
            height: Float,
            with: Float,
            matrix: FloatArray,
        ): TextEditModel {
            return TextEditModel(
                id = fontModel.id,
                name = fontModel.name,
                remotePath = fontModel.remotePath,
                localPath = fontModel.localPath,
                textContent = textContent,
                color = color,
                matrix = matrix,
                height = height,
                with = with
            )
        }
    }
}
