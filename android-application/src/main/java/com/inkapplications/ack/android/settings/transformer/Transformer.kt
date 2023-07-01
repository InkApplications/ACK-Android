package com.inkapplications.ack.android.settings.transformer

/**
 * Transformer to change settings to/from their storage type.
 *
 * Note: Data is expected to be validated before being passed to the
 * transformer.
 */
interface Transformer<DATA, STORAGE> {
    /**
     * Change a data structure into a type appropriate for storing.
     */
    fun toStorage(data: DATA): STORAGE

    /**
     * Change a data structure into a type appropriate for application use.
     */
    fun toData(storage: STORAGE): DATA
}

/**
 * Combines two transformers, allowing chaining.
 */
private class CompositeTransformer<ORIGIN, MIDDLE, END>(
    val first: Transformer<ORIGIN, MIDDLE>,
    val second: Transformer<MIDDLE, END>
): Transformer<ORIGIN, END> {
    override fun toStorage(data: ORIGIN): END {
        return second.toStorage(first.toStorage(data))
    }

    override fun toData(storage: END): ORIGIN {
        return first.toData(second.toData(storage))
    }
}

/**
 * Chain two transformers together.
 */
fun <ORIGIN, MIDDLE, END> Transformer<ORIGIN, MIDDLE>.then(second: Transformer<MIDDLE, END>): Transformer<ORIGIN, END> {
    return CompositeTransformer(this, second)
}

/**
 * Chain two transformers together.
 */
operator fun <ORIGIN, FIRST, SECOND> Transformer<ORIGIN, FIRST>.plus(second: Transformer<FIRST, SECOND>): Transformer<ORIGIN, SECOND> {
    return this.then(second)
}
