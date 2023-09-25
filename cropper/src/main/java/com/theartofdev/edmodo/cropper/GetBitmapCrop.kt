package com.theartofdev.edmodo.cropper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import com.volio.vn.models.photo.CropModel
import kotlin.math.min

object GetBitmapCrop {

//    fun getBitmapCrop(bitmap: Bitmap, cropModel: CropModel): Bitmap {
//        return BitmapUtils.cropBitmapObjectHandleOOM(
//            bitmap,
//            cropModel.points,
//            cropModel.degreesRotated,
//            cropModel.fixAspectRatio,
//            cropModel.aspectRatioX,
//            cropModel.aspectRatioY,
//            cropModel.flipHorizontally,
//            cropModel.flipVertically
//        ).bitmap
//    }

    fun getBitmapCrop(bitmap: Bitmap, cropModel: CropModel): Bitmap {
        return BitmapUtils.cropBitmapObjectWithScale2(
            bitmap,
            cropModel.srcBitmapWidth,
            cropModel.srcBitmapHeight,
            cropModel.points,
            cropModel.degreesRotated,
            cropModel.fixAspectRatio,
            cropModel.aspectRatioX,
            cropModel.aspectRatioY,
            cropModel.flipHorizontally,
            cropModel.flipVertically
        )
    }

    fun getScaleCrop(cropModel: CropModel?, minRenderSize: Int): Float {
        var scale = 1f
        if (cropModel != null) {
            val rect = BitmapUtils.getRectFromPoints(
                cropModel.points,
                cropModel.srcBitmapWidth,
                cropModel.srcBitmapHeight,
                cropModel.fixAspectRatio,
                cropModel.aspectRatioX,
                cropModel.aspectRatioY
            )
            scale = cropModel.srcBitmapWidth.toFloat() / rect.width()
            val minSrcSize = min(cropModel.srcBitmapWidth, cropModel.srcBitmapHeight)
            if (minRenderSize * scale > minSrcSize) {
                scale = minSrcSize / minRenderSize.toFloat()
            }
        }
        return scale
    }

    fun getRectFromPoint(cropModel: CropModel) = BitmapUtils.getRectFromPoints(
        cropModel.points,
        cropModel.srcBitmapWidth,
        cropModel.srcBitmapHeight,
        false,
        1,
        1
    )

    fun getBitmapCrop2(bitmap: Bitmap, cropModel: CropModel): Bitmap {
        var newBitmap = bitmap
        if (cropModel.degreesRotated % 180 == 90) {
            newBitmap = Bitmap.createBitmap(bitmap.height, bitmap.width, Bitmap.Config.ARGB_8888)
        }
        Canvas(newBitmap).apply {
            val matrix = Matrix()
            matrix.postTranslate(
                (newBitmap.width - bitmap.width) / 2f,
                (newBitmap.height - bitmap.height) / 2f
            )
            matrix.postScale(
                if (cropModel.flipHorizontally) -1f else 1f,
                if (cropModel.flipVertically) -1f else 1f,
                newBitmap.width / 2f, newBitmap.height / 2f
            )
            matrix.postRotate(
                cropModel.degreesRotated.toFloat(),
                newBitmap.width / 2f, newBitmap.height / 2f
            )
            drawBitmap(bitmap, 0f, 0f, null)
        }
        return newBitmap
    }

    fun getBitmapRegionCrop(bitmap: Bitmap, cropModel: CropModel): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        if (cropModel.degreesRotated % 180 == 90) {
            width = bitmap.height
            height = bitmap.width
        }
        val newBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        Canvas(newBitmap).apply {
            val matrix = Matrix()
            matrix.postTranslate(
                (width - bitmap.width) / 2f,
                (height - bitmap.height) / 2f
            )
            matrix.postScale(
                if (cropModel.flipHorizontally) -1f else 1f,
                if (cropModel.flipVertically) -1f else 1f,
                width / 2f, height / 2f
            )
            matrix.postRotate(
                cropModel.degreesRotated.toFloat(),
                width / 2f, height / 2f
            )
            drawBitmap(bitmap,matrix,null)
        }

        return newBitmap
    }

}
