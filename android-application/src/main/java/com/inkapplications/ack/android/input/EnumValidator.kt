package com.inkapplications.ack.android.input

/**
 * Validates an input string, asserting that its value is a valid enum name.
 */
inline fun <reified T: Enum<T>> enumInputValidator(
    error: String,
): Validator<String> {
    return object: Validator<String> {
        override fun validate(input: String): ValidationResult {
            return if (input in enumValues<T>().map { it.name }) {
                ValidationResult.Valid
            } else {
                ValidationResult.Error(error)
            }
        }
    }
}
