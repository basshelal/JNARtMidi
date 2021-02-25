package dev.basshelal.jnartmidi

import org.junit.jupiter.api.Assertions

internal infix fun Any?.mustEqual(actual: Any?) = Assertions.assertEquals(this, actual)

internal infix fun Array<*>?.mustEqual(actual: Array<*>?) = Assertions.assertArrayEquals(this, actual)

internal infix fun Any?.mustNotEqual(actual: Any?) = Assertions.assertNotEquals(this, actual)

internal infix fun Any?.mustBeSameAs(actual: Any?) = Assertions.assertSame(this, actual)

internal infix fun Any?.mustNotBeSameAs(actual: Any?) = Assertions.assertNotSame(this, actual)

internal fun Boolean.mustBeTrue() = Assertions.assertTrue(this)

internal fun (() -> Boolean).mustBeTrue() = Assertions.assertTrue(this)

internal fun Boolean.mustBeFalse() = Assertions.assertFalse(this)

internal fun (() -> Boolean).mustBeFalse() = Assertions.assertFalse(this)

internal fun Any?.mustBeNull() = Assertions.assertNull(this)

internal fun Any?.mustNotBeNull() = Assertions.assertNotNull(this)
