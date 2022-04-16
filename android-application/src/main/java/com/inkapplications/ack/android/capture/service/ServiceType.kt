package com.inkapplications.ack.android.capture.service

import androidx.annotation.StringRes
import com.inkapplications.ack.android.R

enum class ServiceType(
    @StringRes
    val nameResource: Int,
    val id: Int,
) {
    Afsk(R.string.capture_service_type_afsk, 44),
    Internet(R.string.capture_service_type_internet, 2405),
}
