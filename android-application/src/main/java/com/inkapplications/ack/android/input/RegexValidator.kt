package com.inkapplications.ack.android.input

/**
 * Validator that checks if a string matches a regex.
 *
 * @param regex The regex to consider valid input.
 * @param error The error message to return if the regex does not match.
 */
class RegexValidator(
    private val regex: Regex,
    private val error: String,
    private val trim: Boolean = true,
    private val allowEmpty: Boolean = false,
): Validator<String> {
    override fun validate(input: String): ValidationResult {
        if (input.isBlank() && allowEmpty) return ValidationResult.Valid
        return if (regex.matches(input.let { if(trim) it.trim() else it })) {
            ValidationResult.Valid
        } else {
            ValidationResult.Error(error)
        }
    }
}

