package com.createchance.imageeditor.glide_crop

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.theartofdev.edmodo.cropper.GetBitmapCrop
import com.volio.vn.models.photo.CropModel

object CroppedLoadImage {
    fun loadImageCrop(
        context: Context,
        path: String,
        cropModel: CropModel,
        onSuccess: (Bitmap) -> Unit
    ) {
        val rect = GetBitmapCrop.getRectFromPoint(cropModel)
        GlideApp.with(context).asBitmap().load(
            CroppedImage(
                path,
                rect.width(),
                rect.height(),
                rect.left,
                rect.top
            )
        ).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                onSuccess(resource)
                onSuccess(GetBitmapCrop.getBitmapRegionCrop(resource, cropModel))
            }
        })
    }

}