package com.inkapplications.aprs.android.settings

/**
 * A setting with a transformer to use for going between primitives and complex types.
 */
sealed interface TransformableSetting<DATA, STORAGE> {
    /**
     * Transformer used to store the typed data structure into settings.
     */
    val transformer: Transformer<DATA, STORAGE>
}
