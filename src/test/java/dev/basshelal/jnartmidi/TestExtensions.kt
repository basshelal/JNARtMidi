@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jnartmidi

import org.junit.jupiter.api.Assertions

internal inline infix fun Any?.mustEqual(expected: Any?) = Assertions.assertEquals(expected, this)

internal inline infix fun Array<*>?.mustEqual(expected: Array<*>?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun Any?.mustNotEqual(expected: Any?) = Assertions.assertNotEquals(expected, this)

internal inline infix fun Any?.mustBeSameAs(expected: Any?) = Assertions.assertSame(expected, this)

internal inline infix fun Any?.mustNotBeSameAs(expected: Any?) = Assertions.assertNotSame(expected, this)

internal inline fun Boolean.mustBeTrue() = Assertions.assertTrue(this)

internal inline fun (() -> Boolean).mustBeTrue() = Assertions.assertTrue(this)

internal inline fun Boolean.mustBeFalse() = Assertions.assertFalse(this)

internal inline fun (() -> Boolean).mustBeFalse() = Assertions.assertFalse(this)

internal inline fun Any?.mustBeNull() = Assertions.assertNull(this)

internal inline fun Any?.mustNotBeNull() = Assertions.assertNotNull(this)

internal inline fun ignoreExceptions(printStackTrace: Boolean = false, func: () -> Unit) =
        ignoreException<Throwable>(printStackTrace, func)

internal inline fun <reified T : Throwable> ignoreException(printStackTrace: Boolean = false, func: () -> Unit) {
    try {
        func()
    } catch (e: Throwable) {
        if (e !is T) throw e
        else if (printStackTrace) e.printStackTrace()
    }
}
