package com.inkapplications.ack.android.input

/**
 * Validates common constraints on positive integer values.
 *
 * @param error The error message to return if the regex does not match.
 * @param requirePositive Whether the value must be greater than zero.
 * @param zeroInclusive Whether the value can be zero.
 * @param sentinelValue An arbitrary single value to consider valid, when using sentinel values.
 */
class IntegerValidator(
    private val error: String,
    private val zeroInclusive: Boolean = true,
    private val allowSentinel: Int? = null,
): Validator<Int> {
    override fun validate(input: Int): ValidationResult {
        return when {
            allowSentinel != null && input == allowSentinel -> ValidationResult.Valid
            input < 0 -> ValidationResult.Error(error)
            !zeroInclusive && input == 0 -> ValidationResult.Error(error)
            else -> ValidationResult.Valid
        }
    }
}
