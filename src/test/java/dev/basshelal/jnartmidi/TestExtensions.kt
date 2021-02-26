@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jnartmidi

import org.junit.jupiter.api.Assertions

internal infix fun Any?.mustBe(expected: Any?) = Assertions.assertEquals(expected, this)

internal infix fun Any?.mustNotBe(expected: Any?) = Assertions.assertNotEquals(expected, this)

internal inline infix fun Any?.mustEqual(expected: Any?) = Assertions.assertEquals(expected, this)

internal inline infix fun Array<*>?.mustEqual(expected: Array<*>?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun Any?.mustNotEqual(expected: Any?) = Assertions.assertNotEquals(expected, this)

internal inline infix fun Any?.mustBeSameAs(expected: Any?) = Assertions.assertSame(expected, this)

internal inline infix fun Any?.mustNotBeSameAs(expected: Any?) = Assertions.assertNotSame(expected, this)

internal inline infix fun <T : Any> Comparable<T>.mustBeLessThan(other: T) = (this < other).mustBeTrue()

internal inline infix fun <T : Any> Comparable<T>.mustBeLessThanOrEqualTo(other: T) = (this <= other).mustBeTrue()

internal inline infix fun <T : Any> Comparable<T>.mustBeGreaterThan(other: T) = (this > other).mustBeTrue()

internal inline infix fun <T : Any> Comparable<T>.mustBeGreaterThanOrEqualTo(other: T) = (this >= other).mustBeTrue()

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

internal inline fun wait(millis: Number) = Thread.sleep(millis.toLong())

internal inline fun Any?.log() = println(this)

// Below for myItem mustBe anyOf("", "example", "") etc

internal class AnyOf(vararg val items: Any? = emptyArray())

internal class AllOf(vararg val items: Any? = emptyArray())

internal inline fun anyOf(vararg expected: Any?) = AnyOf(expected)

internal inline fun allOf(vararg expected: Any?) = AllOf(expected)

// TODO: 26/02/2021 Good idea but needs more thought!
internal infix fun Any?.mustBe(expected: AllOf) = expected.items.forEach { this mustBe it }

// TODO: 26/02/2021 Good idea but needs more thought!
internal infix fun Any?.mustNotBe(expected: AnyOf) = expected.items.forEach { this mustNotBe it }