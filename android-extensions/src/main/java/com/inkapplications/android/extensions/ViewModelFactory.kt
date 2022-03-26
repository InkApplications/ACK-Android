package com.inkapplications.android.extensions

/**
 * Generic interface for creating a viewmodel type from data.
 */
interface ViewModelFactory<DATA, VIEWMODEL> {
    /**
     * Create a new instance of the viewmodel from some data.
     */
    fun create(data: DATA): VIEWMODEL
}
