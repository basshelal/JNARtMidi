@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jnartmidi

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.opentest4j.AssertionFailedError

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

internal inline fun assume(condition: Boolean, message: String = "") = Assumptions.assumeTrue(condition, message)

internal inline fun assume(predicate: () -> Boolean, message: String = "") = assume(predicate(), message)

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

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
internal inline class AnyOf(val items: Array<Any?> = emptyArray()) {

    inline infix fun mustBe(actual: Any?) {
        for (it in this.items) {
            if (actual isEqualTo it) return
            else if (actual isNotEqualTo it) fail(expected = it, actual = actual)
        }
    }

    inline infix fun mustNotBe(actual: Any?) {
        for (it in this.items) {
            if (actual isNotEqualTo it) return
            else if (actual isEqualTo it) fail(expected = it, actual = actual)
        }
    }
}

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
internal inline class AllOf(val items: Array<Any?> = emptyArray()) {

    inline infix fun mustBe(actual: Any?) {
        this.items.forEach {
            if (actual isNotEqualTo it) fail(expected = it, actual = actual)
        }
    }

    inline infix fun mustNotBe(actual: Any?) {
        this.items.forEach {
            if (actual isEqualTo it) fail(expected = it, actual = actual)
        }
    }
}

internal inline fun anyOf(vararg items: Any?) = AnyOf(arrayOf(items))

internal inline fun allOf(vararg items: Any?) = AllOf(arrayOf(items))

// someOf and noneOf

internal infix fun Any?.mustBe(expected: AllOf) = expected mustBe this

internal infix fun Any?.mustNotBe(expected: AnyOf) = expected mustNotBe this

private inline infix fun Any?.isEqualTo(other: Any?) = if (this == null) other == null else (this == other)

private inline infix fun Any?.isNotEqualTo(other: Any?) = !(this isEqualTo other)

private fun fail(message: String = "", expected: Any?, actual: Any?): Nothing = throw AssertionFailedError()