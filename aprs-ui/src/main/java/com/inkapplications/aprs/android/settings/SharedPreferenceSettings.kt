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

    override fun observeStringState(setting: StringSetting): Flow<String?> {
        return updates
            .filter { it == setting.key }
            .onStart { emit(setting.key) }
            .requireKey()
            .map { preferences.getString(setting.key, null) }
    }

    override fun setString(setting: StringSetting, value: String) = preferences.apply { putString(setting.key, value) }

    override fun observeIntState(setting: IntSetting): Flow<Int?> {
        return updates
            .filter { it == setting.key }
            .onStart { emit(setting.key) }
            .map { preferences.getOptionalInt(setting.key) }
    }

    override fun observeBooleanState(setting: BooleanSetting): Flow<Boolean?> {
        return updates
            .filter { it == setting.key }
            .onStart { emit(setting.key) }
            .map { preferences.getOptionalBoolean(setting.key) }
    }

    private fun SharedPreferences.getOptionalInt(key: String): Int? {
        return ifContains(key) { getInt(key, -1) }
    }

    private fun SharedPreferences.getOptionalBoolean(key: String): Boolean? {
        return ifContains(key) { getBoolean(key, false) }
    }

    private fun <T> SharedPreferences.ifContains(key: String, action: () -> T): T? {
        return if (contains(key)) action() else null
    }

    override fun setInt(setting: IntSetting, value: Int) = preferences.apply { putInt(setting.key, value) }

    override fun setBoolean(setting: BooleanSetting, value: Boolean) = preferences.apply { putBoolean(setting.key, value) }

    private fun Flow<String>.requireKey(): Flow<Result<String>> = map { key ->
        if (preferences.contains(key)) Result.success(key)
        else Result.failure(IllegalArgumentException("No preference contained for: $key"))
    }

    private inline fun SharedPreferences.apply(modifications: SharedPreferences.Editor.() -> Unit) {
        edit().apply { modifications() }.apply()
    }
}
