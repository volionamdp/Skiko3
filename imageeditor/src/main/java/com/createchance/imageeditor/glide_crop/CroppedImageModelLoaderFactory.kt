package com.createchance.imageeditor.glide_crop

import android.content.res.Resources
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.createchance.imageeditor.glide_crop.CroppedImage
import com.createchance.imageeditor.glide_crop.CroppedImageDecoderInput
import com.createchance.imageeditor.glide_crop.CroppedImageModelLoader

class CroppedImageModelLoaderFactory(private val resources: Resources) :
    ModelLoaderFactory<CroppedImage, CroppedImageDecoderInput> {

    override fun build(multiFactory: MultiModelLoaderFactory):
            ModelLoader<CroppedImage, CroppedImageDecoderInput> = CroppedImageModelLoader(resources)

    override fun teardown() { }
}