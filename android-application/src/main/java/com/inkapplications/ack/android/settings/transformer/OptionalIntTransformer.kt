package com.inkapplications.ack.android.settings.transformer

/**
 * Convert a string to an integer, using a default value if the string is blank.
 *
 * @param nullValue The value to use if the string is blank.
 */
class OptionalIntTransformer(val nullValue: Int = -1): Transformer<String, Int> {
    override fun toStorage(data: String): Int = data.takeIf { it.isNotBlank() }?.toInt() ?: nullValue
    override fun toData(storage: Int): String = storage.takeIf { it != nullValue }?.toString().orEmpty()
}
