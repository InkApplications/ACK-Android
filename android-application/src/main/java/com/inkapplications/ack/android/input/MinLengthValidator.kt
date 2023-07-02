package com.inkapplications.ack.android.input

class MinLengthValidator(
    private val error: String,
    private val minLength: Int,
): Validator<String> {
    override fun validate(input: String): ValidationResult {
        return if (input.length >= minLength) {
            ValidationResult.Valid
        } else {
            ValidationResult.Error(error)
        }
    }
}
