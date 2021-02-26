@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jnartmidi

import com.sun.jna.Platform
import dev.basshelal.jnartmidi.api.RtMidi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.assertDoesNotThrow
import org.opentest4j.AssertionFailedError
import kotlin.reflect.KClass

internal infix fun Any?.mustBe(expected: Any?) = Assertions.assertEquals(expected, this)

internal infix fun Any?.mustNotBe(expected: Any?) = Assertions.assertNotEquals(expected, this)

internal inline infix fun Any?.mustEqual(expected: Any?) = Assertions.assertEquals(expected, this)

internal inline infix fun Any?.mustNotEqual(expected: Any?) = Assertions.assertNotEquals(expected, this)

internal inline infix fun Any?.mustBeSameAs(expected: Any?) = Assertions.assertSame(expected, this)

internal inline infix fun Any?.mustNotBeSameAs(expected: Any?) = Assertions.assertNotSame(expected, this)

@Suppress("UNCHECKED_CAST")
internal inline infix fun <reified T : Throwable> (() -> Any?).mustThrow(exception: Class<T>) =
        Assertions.assertThrows(exception, this as () -> Unit)

internal inline infix fun <reified T : Throwable> (() -> Any?).mustNotThrow(exception: Class<T>) {
    try {
        this()
    } catch (e: Throwable) {
        if (e is T) throw AssertionFailedError("Expected would not throw ${e.javaClass.simpleName} but actually did throw")
    }
}

@Suppress("UNCHECKED_CAST")
internal inline infix fun <reified T : Throwable> (() -> Any?).mustThrow(exception: KClass<T>) =
        Assertions.assertThrows(exception.java, this as () -> Unit)

internal inline infix fun <reified T : Throwable> (() -> Any?).mustNotThrow(exception: KClass<T>) {
    try {
        this()
    } catch (e: Throwable) {
        if (e is T) throw AssertionFailedError("Expected would not throw ${e.javaClass.simpleName} but actually did throw")
    }
}

// Array Overloads

internal inline infix fun BooleanArray?.mustBe(expected: BooleanArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun CharArray?.mustBe(expected: CharArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun ByteArray?.mustBe(expected: ByteArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun ShortArray?.mustBe(expected: ShortArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun IntArray?.mustBe(expected: IntArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun LongArray?.mustBe(expected: LongArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun FloatArray?.mustBe(expected: FloatArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun DoubleArray?.mustBe(expected: DoubleArray?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun Array<*>?.mustBe(expected: Array<*>?) = Assertions.assertArrayEquals(expected, this)

internal inline infix fun Iterable<*>?.mustBe(expected: Iterable<*>?) = Assertions.assertIterableEquals(expected, this)

// Comparable

internal inline infix fun <T : Any> Comparable<T>.mustBeLessThan(other: T) = (this < other) mustBe true

internal inline infix fun <T : Any> Comparable<T>.mustBeLessThanOrEqualTo(other: T) = (this <= other) mustBe true

internal inline infix fun <T : Any> Comparable<T>.mustBeGreaterThan(other: T) = (this > other) mustBe true

internal inline infix fun <T : Any> Comparable<T>.mustBeGreaterThanOrEqualTo(other: T) = (this >= other) mustBe true

internal inline fun assume(condition: Boolean, message: String = "") = Assumptions.assumeTrue(condition, message)

internal inline fun assume(predicate: () -> Boolean, message: String = "") = assume(predicate(), message)

internal inline fun ignoreExceptions(printStackTrace: Boolean = false, func: () -> Unit) = ignoreException<Throwable>(printStackTrace, func)

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

// privates

private inline infix fun Any?.isEqualTo(other: Any?) = if (this == null) other == null else (this == other)

private inline infix fun Any?.isNotEqualTo(other: Any?) = !(this isEqualTo other)

private fun fail(message: String = "", expected: Any?, actual: Any?): Nothing = throw AssertionFailedError()

// Utils

internal fun defaultBeforeAll() {
    RtMidi.addLibrarySearchPath("bin/${Platform.RESOURCE_PREFIX}")
    assertDoesNotThrow { RtMidi.availableApis() }
}