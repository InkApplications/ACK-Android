package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.settings.transformer.Transformer

/**
 * A setting with a transformer to use for going between primitives and complex types.
 */
sealed interface TransformableSetting<DATA, STORAGE> {
    /**
     * Transformer used to store the typed data structure into settings.
     */
    val transformer: Transformer<DATA, STORAGE>

    /**
     * A default value for the setting, in its data format.
     */
    val defaultData: DATA
}
