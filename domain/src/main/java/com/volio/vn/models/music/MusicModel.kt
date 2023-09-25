package com.volio.vn.models.music

import com.volio.vn.models.MediaData
import java.io.File
import kotlinx.android.parcel.Parcelize
import kotlin.math.abs

@Parcelize
data class MusicModel(
    override val id: Long,
    override val name: String,
    override val uri: String,
    override var path: String,
    override val timeCreated: Long,
    override val idFolder: Long,
    val duration: Long? = null,
    val idCategory: Long?= null,
    val author: String?= null,
    val remotePath: String?= null,
    val highlight: List<Long>?= null,
    val license: String?= null,
    val thumb: String?= null,
    override var uuid: String = ""
) : MediaData {

    fun isMusicDownloaded(): Boolean {
        if (path.isBlank()) {
            return false
        }
        val file = File(path)
        if (!file.exists() || file.length() <= 0) {
            return false
        }
        return true
    }

    fun hasHighlight(): Boolean {
        return getFirstHighlight() != 0L
    }

    fun getFirstHighlight() : Long {
        if (highlight.isNullOrEmpty()){
            return 0
        }

        if (highlight[0] < 0){
            return 0
        }
        
        if (duration != null && duration < highlight[0]){
            return 0
        }

        return highlight[0]
    }

    fun nearHighlight(choosePosition : Long) : Boolean{
        if (getFirstHighlight() == 0L){
            return false
        }
        return abs(choosePosition - getFirstHighlight()) <= 1500
    }
}