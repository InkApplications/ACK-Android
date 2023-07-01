package com.inkapplications.ack.android.input

import com.inkapplications.ack.android.R
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import javax.inject.Inject

/**
 * Ensures that a string can be successfully converted to an integer, or is blank.
 */
@Reusable
class OptionalIntValidator @Inject constructor(
    private val stringResources: StringResources,
) : Validator<String> {
    override fun validate(input: String): ValidationResult {
        val result = runCatching { input.toInt() }
        return when {
            input.isBlank() -> ValidationResult.Valid
            result.isSuccess -> ValidationResult.Valid
            else -> ValidationResult.Error(
                stringResources.getString(R.string.prompt_int_invalid_error)
            )
        }
    }
}
