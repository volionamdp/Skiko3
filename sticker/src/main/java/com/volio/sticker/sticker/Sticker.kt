package com.volio.sticker.sticker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import com.volio.sticker.CornerGravity
import com.volio.sticker.StickerUpdate
import com.volio.sticker.dp
import com.volio.sticker.model.StickerModel
import java.util.*
import kotlin.math.abs

/*cornerPoint
        0,1             2,3
        ----------------
        |               |
        |               |
        |               |
        _________________
        6,7             4,5


 */
open class Sticker() {
    //    protected var standardSize = 1000f
//    protected var scale: Float = 1f
//    protected var rotate: Float = 0f
//    protected val centerPoint: PointF = PointF(0.5f, 0.5f)
    protected open var data: StickerModel =
        StickerModel(UUID.randomUUID().toString(), 1000f, floatArrayOf(0.5f, 0.5f), 0f, 1f)
    protected var padding: Float = 20.dp
    protected var scWidth: Float = -1f
    protected var scHeight: Float = -1f
    protected var rectSrc: RectF = RectF()
    protected var cornerPoint: FloatArray = FloatArray(8)
    protected var cornerPointSrc: FloatArray = FloatArray(8)
    protected var minWidth = 60.dp
    protected var matrix: Matrix = Matrix()
    protected var matrixInvert: Matrix = Matrix()
    var stickerUpdate: StickerUpdate? = null
    var context:Context? = null
    fun getStickerData() = data
    open fun updateScreenSize(width: Float, height: Float) {
        scWidth = width
        scHeight = height
        updateMatrix()
    }

    fun getCenterX() = data.getCenterX() * scWidth
    fun getCenterY() = data.getCenterY() * scHeight
    protected fun updateMatrix() {
        matrix.reset()
        matrix.postTranslate(-rectSrc.width() / 2, -rectSrc.height() / 2)
        matrix.postScale(data.scale, data.scale)
        matrix.postRotate(data.rotate)
        matrix.postTranslate(
            getCenterX(),
            getCenterY()
        )
        updateCorner()
        stickerUpdate?.onUpdateView()
    }

    private fun updateCorner() {
        val paddingBorder = getSrcPaddingBorder()
        var space = 0f
        if (data.scale * getContentWidth() < minWidth) {
            space = minWidth / data.scale - getContentWidth()
        }
        val srcLeft = rectSrc.left - paddingBorder - space / 2f
        val srcTop = rectSrc.top - paddingBorder
        val srcRight = rectSrc.right + paddingBorder + space / 2f
        val srcBottom = rectSrc.bottom + paddingBorder
        //left-top
        cornerPointSrc[0] = srcLeft
        cornerPointSrc[1] = srcTop

        //right-top
        cornerPointSrc[2] = srcRight
        cornerPointSrc[3] = srcTop

        //right-bottom
        cornerPointSrc[4] = srcRight
        cornerPointSrc[5] = srcBottom

        //left-bottom
        cornerPointSrc[6] = srcLeft
        cornerPointSrc[7] = srcBottom
        matrix.mapPoints(cornerPoint, cornerPointSrc)
    }

    open fun onDraw(canvas: Canvas) {

    }

    open fun getContentWidth(): Float {
        return 100f
    }

    fun postTranslate(x: Float, y: Float) {
        val percentX = x / scWidth
        val percentY = y / scHeight
        var newPercentX = data.getCenterX() + percentX
        var newPercentY = data.getCenterY() + percentY
        if (abs(newPercentX-0.5f)<0.003){
            newPercentX = 0.5f
            stickerUpdate?.onShowLineCenterX(true)
        }else{
            stickerUpdate?.onShowLineCenterX(false)
        }
        if (abs(newPercentY-0.5f)<0.003){
            newPercentY = 0.5f
            stickerUpdate?.onShowLineCenterY(true)
        }else{
            stickerUpdate?.onShowLineCenterY(false)
        }
        data.setCenterPoint(newPercentX,newPercentY )
        updateMatrix()
    }

    fun postScale(scaleValue: Float) {
        data.scale *= if (scaleValue > 0) scaleValue else 1f
        updateMatrix()
    }

    fun postRotate(rotateValue: Float) {
        data.rotate += rotateValue
        updateMatrix()
    }

    fun checkContains(x: Float, y: Float): Boolean {
        val floatArray = floatArrayOf(x, y)
        matrix.invert(matrixInvert)
        matrixInvert.mapPoints(floatArray)
        return rectSrc.contains(floatArray[0], floatArray[1], getSrcPaddingBorder())
    }

    protected open fun getSrcPaddingBorder() = padding / data.scale
    fun getCornerPoint(cornerGravity: CornerGravity): PointF {
        return when (cornerGravity) {
            CornerGravity.LEFT_TOP -> {
                PointF(cornerPoint[0], cornerPoint[1])
            }
            CornerGravity.RIGHT_TOP -> {
                PointF(cornerPoint[2], cornerPoint[3])
            }
            CornerGravity.RIGHT_BOTTOM -> {
                PointF(cornerPoint[4], cornerPoint[5])
            }
            CornerGravity.LEFT_BOTTOM -> {
                PointF(cornerPoint[6], cornerPoint[7])
            }
        }
    }

    fun getStickerRotate() = data.rotate
    fun getStickerScale() = data.scale
    fun RectF.contains(x: Float, y: Float, padding: Float): Boolean {
        return left < right && top < bottom
                && x >= left - padding && x < right + padding && y >= top - padding && y < bottom + padding
    }
}