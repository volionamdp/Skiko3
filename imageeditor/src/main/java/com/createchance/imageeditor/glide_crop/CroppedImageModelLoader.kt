package com.createchance.imageeditor.glide_crop

import android.content.res.Resources
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.createchance.imageeditor.glide_crop.CroppedImage
import com.createchance.imageeditor.glide_crop.CroppedImageDataFetcher
import com.createchance.imageeditor.glide_crop.CroppedImageDecoderInput

class CroppedImageModelLoader(val resources: Resources) :
    ModelLoader<CroppedImage, CroppedImageDecoderInput> {

    override fun handles(model: CroppedImage) = true
    override fun buildLoadData(
        model: CroppedImage,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<CroppedImageDecoderInput>? {

        return ModelLoader.LoadData<CroppedImageDecoderInput>(
                ObjectKey(model),
                CroppedImageDataFetcher(resources, model)
        )
    }
}