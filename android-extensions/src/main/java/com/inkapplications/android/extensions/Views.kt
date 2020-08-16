package com.inkapplications.android.extensions

import android.view.View

fun View.setVisibility(visible: Boolean, hiddenState: Int = View.GONE) {
    visibility = if (visible) View.VISIBLE else hiddenState
}