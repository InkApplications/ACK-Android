package com.inkapplications.aprs.android.settings

/**
 * Transformer to change settings to/from their storage type.
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
