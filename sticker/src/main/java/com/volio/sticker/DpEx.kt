package com.volio.sticker

import android.content.res.Resources
import android.util.TypedValue
//dp to px
val Number.dp get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)