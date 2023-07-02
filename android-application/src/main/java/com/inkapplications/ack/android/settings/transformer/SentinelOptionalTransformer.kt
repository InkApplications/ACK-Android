package com.inkapplications.ack.android.settings.transformer

/**
 * Wrap a transformer, making it optional if the stored value matches a key.
 */
class SentinelOptionalTransformer<DATA, STORAGE>(
    private val sentinelValue: STORAGE,
    private val delegate: Transformer<DATA, STORAGE>,
): Transformer<DATA?, STORAGE> {
    override fun toStorage(data: DATA?): STORAGE {
        return data?.let(delegate::toStorage) ?: sentinelValue
    }

    override fun toData(storage: STORAGE): DATA? {
        return storage?.takeIf { it != sentinelValue }?.let(delegate::toData)
    }
}
