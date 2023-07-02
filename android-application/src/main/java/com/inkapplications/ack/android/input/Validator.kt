package com.inkapplications.ack.android.input

/**
 * Checks if an input type is allowable.
 */
interface Validator<in T> {
    /**
     * Check if the input is allowed, returning a specific result.
     */
    fun validate(input: T): ValidationResult
}

private class CompositeValidator<T>(
    val validators: List<Validator<T>>,
): Validator<T> {
    override fun validate(input: T): ValidationResult {
        return validators
            .map { it.validate(input) }
            .firstOrNull { it is ValidationResult.Error }
            ?: ValidationResult.Valid
    }
}

operator fun <T> Validator<T>.plus(other: Validator<T>): Validator<T> {
    val validators = when (this) {
        is CompositeValidator -> this.validators + other
        else -> listOf(this, other)
    }
    
    return CompositeValidator(validators)
}
