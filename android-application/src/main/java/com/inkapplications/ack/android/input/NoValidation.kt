package com.inkapplications.ack.android.input

/**
 * Validator that always passes regardless of the input
 */
object NoValidation: Validator<Any?> {
    override fun validate(input: Any?) = ValidationResult.Valid
}
