package com.inkapplications.ack.android.input

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntegerValidatorTest {
    @Test
    fun positiveValid() {
        val validator = IntegerValidator(
            error = "error",
        )

        val result = validator.validate(1)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun negativeInvalid() {
        val validator = IntegerValidator(
            error = "error",
        )

        val result = validator.validate(-5)

        assertTrue(result is ValidationResult.Error)
        assertEquals("error", result.message)
    }

    @Test
    fun zeroValid() {
        val validator = IntegerValidator(
            error = "error",
        )

        val result = validator.validate(0)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun zeroInvalid() {
        val validator = IntegerValidator(
            error = "error",
            zeroInclusive = false,
        )

        val result = validator.validate(0)

        assertTrue(result is ValidationResult.Error)
        assertEquals("error", result.message)
    }

    @Test
    fun sentinelValid() {
        val validator = IntegerValidator(
            error = "error",
            allowSentinel = -1,
        )

        val result = validator.validate(-1)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun invalidSentinel() {
        val validator = IntegerValidator(
            error = "error",
            allowSentinel = -1,
        )

        val result = validator.validate(-4)

        assertTrue(result is ValidationResult.Error)
        assertEquals("error", result.message)
    }
}
