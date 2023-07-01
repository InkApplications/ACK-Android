package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.input.NoValidation
import com.inkapplications.ack.android.input.ValidationResult
import com.inkapplications.ack.android.input.Validator
import com.inkapplications.ack.android.settings.transformer.IntFieldTransformer
import com.inkapplications.ack.android.settings.transformer.Transformer

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
    @Deprecated("Use visibility instead", ReplaceWith("visibility == Visibility.Advanced"))
    val advanced: Boolean get() { return visibility == SettingVisibility.Advanced }

    /**
     * An object to check whether an input value can be allowed for this setting.
     */
    val validator: Validator<*>

    /**
     * How/when to display the setting in the UI.
     */
    val visibility: SettingVisibility
}

/**
 * Plain string setting.
 */
open class StringSetting(
    override val key: String,
    override val name: String,
    override val categoryName: String,
    override val defaultValue: String,
    override val visibility: SettingVisibility = SettingVisibility.Visible,
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
    override val visibility: SettingVisibility = SettingVisibility.Visible,
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
    override val visibility: SettingVisibility = SettingVisibility.Visible,
    override val validator: Validator<Boolean> = NoValidation,
): Setting

/**
 * Complex data type that is based on a [StringSetting] for storage.
 *
 * @param storageTransformer Used to convert to/from the data structure and a string.
 * @param inputValidator Used to validate plain input from the user.
 * @param dataValidator Used to validate input from the user, but after it has been transformed by the [storageTransformer].
 */
class StringBackedSetting<T>(
    key: String,
    name: String,
    categoryName: String,
    override val defaultData: T,
    override val visibility: SettingVisibility = SettingVisibility.Visible,
    override val storageTransformer: Transformer<T, String>,
    val inputValidator: Validator<String> = NoValidation,
    val dataValidator: Validator<T> = NoValidation,
): StringSetting(
    key = key,
    name = name,
    categoryName = categoryName,
    defaultValue = storageTransformer.toStorage(defaultData),
    visibility = visibility,
    validator = object: Validator<String> {
        override fun validate(input: String): ValidationResult {
            val inputResult = inputValidator.validate(input)
            if (inputResult !is ValidationResult.Valid) return inputResult
            return storageTransformer.toData(input).run(dataValidator::validate)
        }
    }
), TransformableSetting<T, String>

/**
 * Complex data type that is based on a [IntSetting] for storage.
 *
 * @param storageTransformer Used to convert to/from the data structure and an integer.
 * @param inputValidator Used to validate plain input from the user.
 * @param dataValidator Used to validate input from the user, but after it has been transformed by the [storageTransformer].
 */
class IntBackedSetting<T>(
    key: String,
    name: String,
    categoryName: String,
    override val defaultData: T,
    override val visibility: SettingVisibility = SettingVisibility.Visible,
    override val storageTransformer: Transformer<T, Int>,
    val inputValidator: Validator<Int> = NoValidation,
    val dataValidator: Validator<T> = NoValidation,
    val fieldTransformer: Transformer<String, Int> = IntFieldTransformer,
): IntSetting(
    key = key,
    name = name,
    categoryName = categoryName,
    defaultValue = storageTransformer.toStorage(defaultData),
    visibility = visibility,
    validator = object: Validator<Int> {
        override fun validate(input: Int): ValidationResult {
            val inputResult = inputValidator.validate(input)
            if (inputResult !is ValidationResult.Valid) return inputResult
            return storageTransformer.toData(input).run(dataValidator::validate)
        }
    }
), TransformableSetting<T, Int>
