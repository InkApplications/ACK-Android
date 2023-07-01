package com.inkapplications.ack.android.settings.transformer

/**
 * Transform a string to an integer.
 */
object IntFieldTransformer: Transformer<String, Int> {
    override fun toStorage(data: String): Int = data.toInt()
    override fun toData(storage: Int): String = storage.toString()
}
