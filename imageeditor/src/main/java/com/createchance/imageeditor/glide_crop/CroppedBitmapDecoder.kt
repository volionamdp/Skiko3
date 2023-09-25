package com.createchance.imageeditor.glide_crop

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class CroppedBitmapDecoder(val resources: Resources) :
    ResourceDecoder<CroppedImageDecoderInput, Bitmap> {

    override fun handles(source: CroppedImageDecoderInput, options: Options): Boolean {
        return true
    }

    override fun decode(
        source: CroppedImageDecoderInput,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Bitmap> {
        var bitmap: Bitmap
        var decoder: BitmapRegionDecoder? = null

        val bitmapFactoryOptions = BitmapFactory.Options().apply {
            // Decode image dimensions only, not content
            inJustDecodeBounds = true
        }
        if (source.resId != null) {
            try {
                BitmapFactory.decodeFile(source.resId, bitmapFactoryOptions)
                val imageHeight = bitmapFactoryOptions.outHeight
                val imageWidth = bitmapFactoryOptions.outWidth
                decoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    BitmapRegionDecoder.newInstance(source.resId)
                } else {
                    BitmapRegionDecoder.newInstance(source.resId, false)
                }

                // Ensure the cropping and translation region doesn't exceed the image dimensions
                val region = Rect(
                    source.horizontalOffset,
                    source.verticalOffset,
                    Math.min(source.viewWidth + source.horizontalOffset, imageWidth),
                    Math.min(source.viewHeight + source.verticalOffset, imageHeight)
                )

                // Decode image content within the cropping region
                bitmapFactoryOptions.inJustDecodeBounds = false
                bitmap = decoder.decodeRegion(region, bitmapFactoryOptions)
            }catch (e:Exception){
                e.printStackTrace()
                bitmap = Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888)
            } finally {
                decoder?.recycle()
            }
        }else{
            bitmap = Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888)
        }
        return SimpleResource<Bitmap>(bitmap)
    }
}