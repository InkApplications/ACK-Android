package com.inkapplications.aprs.android.input

/**
 * Results of a [Validator]'s validation check.
 */
sealed interface ValidationResult {
    /**
     * Indicates that the given input was valid.
     */
    object Valid: ValidationResult

    /**
     * Indicates that the given input was not valid, and explains why with a [message]
     *
     * @param message A user-readable message to explain why the input was not valid.
     */
    data class Error(val message: String): ValidationResult
}
