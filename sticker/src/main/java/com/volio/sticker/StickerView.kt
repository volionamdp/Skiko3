package com.volio.sticker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.SizeF
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.volio.sticker.icon.IconButton
import com.volio.sticker.icon.IconButtonBorder
import com.volio.sticker.icon.IconButtonEdit
import com.volio.sticker.icon.IconButtonScaleRotate
import com.volio.sticker.model.StickerModel
import com.volio.sticker.model.TextModel
import com.volio.sticker.sticker.Sticker
import com.volio.sticker.sticker.TextSticker
import kotlin.math.atan2
import kotlin.math.sqrt


open class StickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), StickerUpdate {
    private var listSticker: MutableList<Sticker> = mutableListOf()
    private var oldTouchX = 0f
    private var oldTouchY = 0f
    private var oldDistance = 0f
    private var oldRotation = 0f
    private var midPoint = PointF()
    private var currentIcon: IconButton? = null
    private var handlingSticker: Sticker? = null
    private var currentMode: ActionMode = ActionMode.NONE
    private val listIcon: MutableList<IconButton> = mutableListOf()
    private val paintBorder: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCenterLine: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isHideSticker: Boolean = false
    private var isAllowTouch: Boolean = true
    private var isShowLineCenterX: Boolean = false
    private var isShowLineCenterY: Boolean = false
    private var centerLineSize = 60.dp
    private val vibration: Vibration by lazy {
        Vibration(context)
    }
    var callback: StickerCallback? = null

    init {
        paintBorder.pathEffect = DashPathEffect(floatArrayOf(10.dp, 10.dp), 0f)
        paintBorder.color = Color.RED
        paintBorder.strokeWidth = 1.dp
        paintCenterLine.strokeWidth = 1.dp
        paintCenterLine.color = Color.parseColor("#FEBB40")

    }

    fun setIcon(list: List<IconButton>) {
        listIcon.clear()
        listIcon.addAll(list)
        postInvalidate()
    }

//    fun setDefaultIcons() {
//        listIcon.add(
//            IconButtonEdit(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.ic_sticker_edit_text,
//                    null
//                ), CornerGravity.LEFT_TOP
//            ) {
//                isHideSticker = !isHideSticker
//                postInvalidate()
//            }
//        )
//        listIcon.add(
//            IconButton(
//                ResourcesCompat.getDrawable(resources, R.drawable.ic_sticker_delete, null),
//                CornerGravity.RIGHT_TOP
//            )
//        )
//        listIcon.add(
//            IconButtonScaleRotate(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.ic_sticker_rotate_scale,
//                    null
//                ), CornerGravity.RIGHT_BOTTOM
//            )
//        )
//        listIcon.add(
//            IconButtonBorder(
//                ResourcesCompat.getDrawable(resources, R.drawable.ic_sticker_duration_time, null),
//                CornerGravity.LEFT_BOTTOM,
//                SizeF(18.dp, 18.dp)
//            ).apply {
//                setText("Adjust Time")
//            }
//        )
//    }

    fun setAllowTouch(isTouch: Boolean) {
        isAllowTouch = isTouch
        handlingSticker = null
        postInvalidate()
    }

    fun setHideSticker(isHide: Boolean) {
        isHideSticker = isHide
        postInvalidate()
    }

    fun isHideSticker() = isHideSticker
    fun addSticker(sticker: Sticker) {
        sticker.stickerUpdate = this
        sticker.context = context
        sticker.updateScreenSize(width.toFloat(), height.toFloat())
        listSticker.add(sticker)
        postInvalidate()
    }

    fun setStickers(list: List<StickerModel>) {
        val lastList: MutableList<Sticker> = mutableListOf()
        lastList.addAll(listSticker)
        listSticker.clear()
        list.forEach { stickerModel ->
            var check = false
            for (sticker in lastList) {
                if (stickerModel.uuid == sticker.getStickerData().uuid) {
                    addSticker(sticker)
                    if (sticker is TextSticker && stickerModel is TextModel) {
                        sticker.dataText = stickerModel
                    }
                    check = true
                    break
                }
            }
            if (!check) {
                when (stickerModel) {
                    is TextModel -> {
                        val textSticker = TextSticker()
                        textSticker.dataText = stickerModel
                        addSticker(textSticker)
                    }
                }
            }

        }
        if (handlingSticker != null) {
            handlingSticker =
                listSticker.find { it.getStickerData().uuid == handlingSticker?.getStickerData()?.uuid }
        }
        postInvalidate()
    }

    fun setHandlingSticker(uuid:String){
        listSticker.forEach {
            if (it.getStickerData().uuid == uuid){
                handlingSticker = it
                postInvalidate()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        listSticker.forEach {
            it.updateScreenSize(width.toFloat(), height.toFloat())
        }
    }

    override fun onUpdateView() {
        postInvalidate()
    }

    override fun onShowLineCenterX(isShow: Boolean) {
        if (isShow != isShowLineCenterX && isShow) {
            vibration.startRingPhone()
        }
        isShowLineCenterX = isShow
    }

    override fun onShowLineCenterY(isShow: Boolean) {
        if (isShow != isShowLineCenterY && isShow) {
            vibration.startRingPhone()
        }
        isShowLineCenterY = isShow
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            if (!isHideSticker) {
                listSticker.forEach {
                    it.onDraw(canvas)
                }
            }
            drawIcon(canvas)
            drawCenterLine(canvas)
        }

    }

    private fun drawCenterLine(canvas: Canvas) {
        if (currentMode == ActionMode.DRAG && handlingSticker != null) {
            if (isShowLineCenterY) {
                canvas.drawLine(0f, height / 2f, centerLineSize, height / 2f, paintCenterLine)
                canvas.drawLine(
                    width - centerLineSize,
                    height / 2f,
                    width.toFloat(),
                    height / 2f,
                    paintCenterLine
                )
            }
            if (isShowLineCenterX) {
                canvas.drawLine(width / 2f, 0f, width / 2f, centerLineSize, paintCenterLine)
                canvas.drawLine(
                    width / 2f,
                    height - centerLineSize,
                    width / 2f,
                    height.toFloat(),
                    paintCenterLine
                )
            }
        }
    }

    private fun drawIcon(canvas: Canvas) {
        handlingSticker?.let { sticker ->
            paintBorder.color = if (isHideSticker) Color.RED else Color.WHITE
            val leftTop = sticker.getCornerPoint(CornerGravity.LEFT_TOP)
            val rightTop = sticker.getCornerPoint(CornerGravity.RIGHT_TOP)
            val rightBottom = sticker.getCornerPoint(CornerGravity.RIGHT_BOTTOM)
            val leftBottom = sticker.getCornerPoint(CornerGravity.LEFT_BOTTOM)
            canvas.drawLine(leftTop.x, leftTop.y, rightTop.x, rightTop.y, paintBorder)
            canvas.drawLine(rightBottom.x, rightBottom.y, rightTop.x, rightTop.y, paintBorder)
            canvas.drawLine(leftBottom.x, leftBottom.y, rightBottom.x, rightBottom.y, paintBorder)
            canvas.drawLine(leftBottom.x, leftBottom.y, leftTop.x, leftTop.y, paintBorder)
            listIcon.forEach {
                it.setRotate(sticker.getStickerRotate())
                it.setCenterPoint(sticker.getCornerPoint(it.cornerGravity))
                it.onDraw(canvas)
            }
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isAllowTouch) {
            return false
        }
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> if (!onTouchDown(event)) {
                oldTouchX = event.x
                oldTouchY = event.y
                return false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDistance = calculateDistance(event)
                oldRotation = calculateRotation(event)
                midPoint = calculateMidPoint(event)
                if (handlingSticker != null && handlingSticker?.checkContains(
                        event.x,
                        event.y
                    ) == true
                    && findCurrentIconTouched() == null
                ) {
                    currentMode = ActionMode.ZOOM_WITH_TWO_FINGER
                }
            }
            MotionEvent.ACTION_MOVE -> {
                handleCurrentMode(event)
                invalidate()
                handlingSticker?.let {
                    callback?.updateData(it)
                }
            }
            MotionEvent.ACTION_UP -> {
                currentMode = ActionMode.NONE
                onTouchUp(event)
                invalidate()

            }
            MotionEvent.ACTION_POINTER_UP -> {
                currentMode = ActionMode.NONE
                invalidate()

            }
        }
        return true
    }

    private fun onTouchDown(event: MotionEvent): Boolean {
        currentMode = ActionMode.DRAG
        oldTouchX = event.x
        oldTouchY = event.y
        handlingSticker?.let {
            midPoint.set(it.getCenterX(), it.getCenterY())
            oldDistance = calculateDistance(midPoint.x, midPoint.y, oldTouchX, oldTouchY)
            oldRotation = calculateRotation(midPoint.x, midPoint.y, oldTouchX, oldTouchY)
        }
        currentIcon = findCurrentIconTouched()
        if (currentIcon != null) {
            currentMode = ActionMode.ICON
            currentIcon?.onActionDown(this, event)
        } else {
            handlingSticker = findHandlingSticker(event.x, event.y)
        }
        return true
    }

    protected open fun onTouchUp(event: MotionEvent) {
        if (currentMode === ActionMode.ICON && currentIcon != null && handlingSticker != null) {
            currentIcon?.onActionUp(this, event)
        }
        currentMode = ActionMode.NONE
    }


    private fun findCurrentIconTouched(): IconButton? {
        if (handlingSticker != null) {
            for (index in listIcon.size - 1 downTo 0) {
                if (listIcon[index].contains(oldTouchX, oldTouchY, 0f)) {
                    return listIcon[index]
                }
            }
        }
        return null
    }

    private fun findHandlingSticker(x: Float, y: Float): Sticker? {
        for (index in listSticker.size - 1 downTo 0) {
            if (listSticker[index].checkContains(x, y)) {
                return listSticker[index]
            }
        }
        return null
    }

    private fun handleCurrentMode(event: MotionEvent) {
        when (currentMode) {
            ActionMode.NONE, ActionMode.CLICK -> {}
            ActionMode.DRAG -> {
                handlingSticker?.postTranslate(event.x - oldTouchX, event.y - oldTouchY)
                oldTouchX = event.x
                oldTouchY = event.y
            }
            ActionMode.ZOOM_WITH_TWO_FINGER -> {
                val newDistance = calculateDistance(event)
                val newRotation = calculateRotation(event)
                handlingSticker?.postScale(
                    newDistance / oldDistance
                )
                handlingSticker?.postRotate(newRotation - oldRotation)
                oldDistance = newDistance
                oldRotation = newRotation
            }
            ActionMode.ICON -> if (handlingSticker != null) {
                currentIcon?.onActionMove(this, event)
            }
        }
    }

    fun zoomAndRotateCurrentSticker(event: MotionEvent) {
        zoomAndRotateSticker(handlingSticker, event)
    }

    private fun zoomAndRotateSticker(sticker: Sticker?, event: MotionEvent) {
        if (sticker != null) {
            val newDistance = calculateDistance(midPoint.x, midPoint.y, event.x, event.y)
            val newRotation = calculateRotation(midPoint.x, midPoint.y, event.x, event.y)
            handlingSticker?.postScale(
                newDistance / oldDistance
            )
            handlingSticker?.postRotate(newRotation - oldRotation)
            oldDistance = newDistance
            oldRotation = newRotation
        }
    }

    private fun calculateDistance(event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()
        return sqrt(x * x + y * y).toFloat()
    }

    private fun calculateRotation(event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    private fun calculateRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = (x1 - x2).toDouble()
        val y = (y1 - y2).toDouble()
        val radians = atan2(y, x)
        return Math.toDegrees(radians).toFloat()
    }

    private fun calculateMidPoint(event: MotionEvent?): PointF {
        if (event == null || event.pointerCount < 2) {
            midPoint.set(0f, 0f)
            return midPoint
        }
        val x = (event.getX(0) + event.getX(1)) / 2
        val y = (event.getY(0) + event.getY(1)) / 2
        midPoint.set(x, y)
        return midPoint
    }
    fun getCurrentSticker() = handlingSticker


}


