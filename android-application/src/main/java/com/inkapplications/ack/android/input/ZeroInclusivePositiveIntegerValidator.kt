package com.inkapplications.ack.android.input

import com.inkapplications.ack.android.R
import com.inkapplications.android.extensions.StringResources
import javax.inject.Inject

/**
 * Ensures that an integer is greater than or equal to zero.
 */
class ZeroInclusivePositiveIntegerValidator @Inject constructor(
    private val stringResources: StringResources,
): Validator<Int> {
    override fun validate(input: Int): ValidationResult {
        return if (input >= 0) {
            ValidationResult.Valid
        } else {
            ValidationResult.Error(
                stringResources.getString(R.string.input_validator_positive_integer_error)
            )
        }
    }
}
