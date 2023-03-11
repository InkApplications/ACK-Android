package com.inkapplications.android.extensions

/**
 * Generic interface for creating a view state type from data.
 */
interface ViewStateFactory<DATA, STATE> {
    /**
     * Create a new instance of the viewmodel from some data.
     */
    fun create(data: DATA): STATE
}
