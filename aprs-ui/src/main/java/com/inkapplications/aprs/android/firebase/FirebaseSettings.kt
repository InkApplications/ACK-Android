package com.inkapplications.aprs.android.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.inkapplications.aprs.android.settings.BooleanSetting
import com.inkapplications.aprs.android.settings.IntSetting
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.StringSetting
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Reusable
class FirebaseSettings @Inject constructor(): SettingsReadAccess {
    override fun observeStringState(setting: StringSetting): Flow<String?> {
        return flowOf(Firebase.remoteConfig.getString(setting.key))
    }

    override fun observeIntState(setting: IntSetting): Flow<Int?> {
        return flowOf(Firebase.remoteConfig.getLong(setting.key).toInt())
    }

    override fun observeBooleanState(setting: BooleanSetting): Flow<Boolean?> {
        return flowOf(Firebase.remoteConfig.getBoolean(setting.key))
    }
}
