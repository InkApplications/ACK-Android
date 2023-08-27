package com.inkapplications.ack.android.settings.transformer

/**
 * Creates a transformer for an enum type, stored by enm name.
 */
inline fun <reified T: Enum<T>> enumTransformer(): Transformer<T, String> {
    return object: Transformer<T, String> {
        override fun toStorage(data: T): String = data.name
        override fun toData(storage: String): T = enumValueOf(storage)
    }
}
