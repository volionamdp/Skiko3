package com.volio.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle


fun Fragment.isFragmentResumed(block: () -> Unit) {
    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
        block.invoke()
    }
}