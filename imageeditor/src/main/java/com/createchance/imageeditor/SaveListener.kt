package com.createchance.imageeditor

import java.io.File

open class SaveListener {
    open fun onSaveFailed() {}

    open fun onSaveProgress(progress: Float) {}

    open fun onSaved(target: File?) {}

    open fun onCancel(target: File?) {}
}