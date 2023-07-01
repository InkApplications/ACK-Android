package com.inkapplications.ack.android.settings.transformer

/**
 * Trim whitespace from a string in both transformation directions.
 */
object TrimmingTransformer: Transformer<String, String> {
    override fun toStorage(data: String): String {
        return data.trim()
    }

    override fun toData(storage: String): String {
        return storage.trim()
    }
}
