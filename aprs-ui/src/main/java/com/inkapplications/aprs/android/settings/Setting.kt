package com.inkapplications.aprs.android.settings

import com.inkapplications.aprs.android.input.NoValidation
import com.inkapplications.aprs.android.input.ValidationResult
import com.inkapplications.aprs.android.input.Validator

/**
 * Definition of an individual configuration aspect of the application.
 */
sealed interface Setting {
    /**
     * A Unique Identifier for the setting, used for storage.
     */
    val key: String

    /**
     * A user-readable name for the setting.
     */
    val name: String

    /**
     * A user-readable name for the category of the setting.
     */
    val categoryName: String

    /**
     * The value that should be used if no provider has overridden it.
     */
    val defaultValue: Any

    /**
     * Identifies a setting that should be hidden from the user.
     */
    val advanced: Boolean

    /**
     * An object to check whether an input value can be allowed for this setting.
     */
    val validator: Validator<*>
}

/**
 * Plain string setting.
 */
open class StringSetting(
    override val key: String,
    override val name: String,
    override val categoryName: String,
    override val defaultValue: String,
    override val advanced: Boolean = false,
    override val validator: Validator<String> = NoValidation,
): Setting

/**
 * A plain number setting.
 */
open class IntSetting(
    override val key: String,
    override val name: String,
    override val categoryName: String,
    override val defaultValue: Int,
    override val advanced: Boolean = false,
    override val validator: Validator<Int> = NoValidation,
): Setting

/**
 * A plain boolean setting.
 */
data class BooleanSetting(
    override val key: String,
    override val name: String,
    override val categoryName: String,
    override val defaultValue: Boolean,
    override val advanced: Boolean = false,
    override val validator: Validator<Boolean> = NoValidation,
): Setting

/**
 * Complex data type that is based on a [StringSetting] for storage.
 *
 * @param transformer Used to convert to/from the data structure and a string.
 * @param inputValidator Used to validate plain input from the user.
 * @param dataValidator Used to validate input from the user, but after it has been transformed by the [transformer].
 */
class StringBackedSetting<T>(
    key: String,
    name: String,
    categoryName: String,
    defaultValue: T,
    advanced: Boolean = false,
    override val transformer: Transformer<T, String>,
    val inputValidator: Validator<String> = NoValidation,
    val dataValidator: Validator<T> = NoValidation,
): StringSetting(
    key = key,
    name = name,
    categoryName = categoryName,
    defaultValue = transformer.toStorage(defaultValue),
    advanced = advanced,
    validator = object: Validator<String> {
        override fun validate(input: String): ValidationResult {
            val inputResult = inputValidator.validate(input)
            if (inputResult !is ValidationResult.Valid) return inputResult
            return transformer.toData(input).run(dataValidator::validate)
        }
    }
), TransformableSetting<T, String>

/**
 * Complex data type that is based on a [IntSetting] for storage.
 *
 * @param transformer Used to convert to/from the data structure and an integer.
 * @param inputValidator Used to validate plain input from the user.
 * @param dataValidator Used to validate input from the user, but after it has been transformed by the [transformer].
 */
class IntBackedSetting<T>(
    key: String,
    name: String,
    categoryName: String,
    defaultValue: T,
    advanced: Boolean = false,
    override val transformer: Transformer<T, Int>,
    val inputValidator: Validator<Int> = NoValidation,
    val dataValidator: Validator<T> = NoValidation,
): IntSetting(
    key = key,
    name = name,
    categoryName = categoryName,
    defaultValue = transformer.toStorage(defaultValue),
    advanced = advanced,
    validator = object: Validator<Int> {
        override fun validate(input: Int): ValidationResult {
            val inputResult = inputValidator.validate(input)
            if (inputResult !is ValidationResult.Valid) return inputResult
            return transformer.toData(input).run(dataValidator::validate)
        }
    }
), TransformableSetting<T, Int>
