package com.inkapplications.aprs.android.input

/**
 * Checks if an input type is allowable.
 */
interface Validator<in T> {
    /**
     * Check if the input is allowed, returning a specific result.
     */
    fun validate(input: T): ValidationResult
}
