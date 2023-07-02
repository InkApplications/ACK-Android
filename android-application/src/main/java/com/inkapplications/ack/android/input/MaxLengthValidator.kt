package com.inkapplications.ack.android.input

class MaxLengthValidator(
    private val error: String,
    private val maxLength: Int,
): Validator<String> {
    override fun validate(input: String): ValidationResult {
        return if (input.length <= maxLength) {
            ValidationResult.Valid
        } else {
            ValidationResult.Error(error)
        }
    }
}
