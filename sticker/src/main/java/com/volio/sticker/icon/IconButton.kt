package com.volio.sticker.icon

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.SizeF
import android.view.MotionEvent
import com.volio.sticker.CornerGravity
import com.volio.sticker.StickerView
import com.volio.sticker.dp

open class IconButton(
    val drawable: Drawable?,
    val cornerGravity: CornerGravity,
    var size: SizeF = SizeF(32.dp, 24.dp)
)  {
    val centerPoint: PointF = PointF()
    private var rotate: Float = 0f
    protected val matrix = Matrix()
    protected val invertMatrix = Matrix()

    fun setCenterPoint(point: PointF) {
        centerPoint.set(point)
        updateMatrix()
    }

    fun setRotate(rotateDeg: Float) {
        rotate = rotateDeg
        updateMatrix()
    }

    private fun updateMatrix() {
        matrix.reset()
        matrix.postTranslate(centerPoint.x - size.width / 2f, centerPoint.y - size.height / 2f)
        matrix.postRotate(rotate, centerPoint.x, centerPoint.y)
        matrix.invert(invertMatrix)
    }

    fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.setMatrix(matrix)
        drawIcon(canvas)
        canvas.restore()
    }

    open fun drawIcon(canvas: Canvas) {
        drawable?.setBounds(0, 0, size.width.toInt(), size.height.toInt())
        drawable?.draw(canvas)
    }

    open fun contains(x: Float, y: Float, padding: Float): Boolean {
        val floatArray = floatArrayOf(x, y)
        invertMatrix.mapPoints(floatArray)
        return floatArray[0] >= -padding && floatArray[0] < size.width + padding &&
                floatArray[1] >= -padding && floatArray[1] < size.height + padding
    }

    open fun onActionDown(stickerView: StickerView?, event: MotionEvent) {

    }

    open fun onActionMove(stickerView: StickerView?, event: MotionEvent) {

    }

    open fun onActionUp(stickerView: StickerView?, event: MotionEvent) {

    }
}