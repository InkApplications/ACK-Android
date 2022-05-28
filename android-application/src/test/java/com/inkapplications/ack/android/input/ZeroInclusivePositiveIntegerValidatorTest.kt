package com.inkapplications.ack.android.input

import com.inkapplications.ack.android.ParrotStringResources
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZeroInclusivePositiveIntegerValidatorTest {
    val validator = ZeroInclusivePositiveIntegerValidator(ParrotStringResources)

    @Test
    fun positive() {
        assertEquals(ValidationResult.Valid, validator.validate(1))
        assertEquals(ValidationResult.Valid, validator.validate(0))
    }

    @Test
    fun negative() {
        val result = validator.validate(-1)
        assertTrue(result is ValidationResult.Error)
    }
}
