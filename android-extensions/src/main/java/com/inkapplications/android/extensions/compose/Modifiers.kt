package com.inkapplications.android.extensions.compose

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

fun Modifier.longClickable(action: () -> Unit) = composed {
    val vibrator = ContextCompat.getSystemService(LocalContext.current, Vibrator::class.java)!!
    pointerInput(Unit) {
        detectTapGestures(
            onLongPress = {
                when {
                    Build.VERSION.SDK_INT >= 29 -> vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                    Build.VERSION.SDK_INT >= 26 -> vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    else -> vibrator.vibrate(50)
                }
                action()
            }
        )
    }
}
