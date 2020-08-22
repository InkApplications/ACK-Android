package com.inkapplications.aprs.android.settings

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Provides read + write access to settings via Android's SharedPreferences.
 */
class SharedPreferenceSettings @Inject constructor(
    private val preferences: SharedPreferences
): SettingsReadAccess, SettingsWriteAccess {
    private val updates = callbackFlow<String> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            sendBlocking(key)
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)

        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun observeString(setting: StringSetting): Flow<Result<String>> {
        return updates
            .filter { it == setting.key }
            .onStart { emit(setting.key) }
            .requireKey()
            .map { Result.success(preferences.getString(setting.key, null)!!) }
    }

    override fun setString(setting: StringSetting, value: String) = preferences.apply { putString(setting.key, value) }

    override fun observeInt(setting: IntSetting): Flow<Result<Int>> {
        return updates
            .filter { it == setting.key }
            .onStart { emit(setting.key) }
            .requireKey()
            .map { Result.success(preferences.getInt(setting.key, 0)) }
    }

    override fun setInt(setting: IntSetting, value: Int) = preferences.apply { putInt(setting.key, value) }

    private fun Flow<String>.requireKey(): Flow<Result<String>> = map { key ->
        if (preferences.contains(key)) Result.success(key)
        else Result.failure(IllegalArgumentException("No preference contained for: $key"))
    }

    private inline fun SharedPreferences.apply(modifications: SharedPreferences.Editor.() -> Unit) {
        edit().apply { modifications() }.apply()
    }
}