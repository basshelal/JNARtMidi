@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jrtmidi

import dev.basshelal.jrtmidi.api.RtMidi
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import jnr.ffi.Platform
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.assertDoesNotThrow
import org.opentest4j.AssertionFailedError

internal infix fun Any?.mustBe(expected: Any?) = Assertions.assertEquals(expected, this)

internal infix fun Any?.mustNotBe(expected: Any?) = Assertions.assertNotEquals(expected, this)

internal infix fun Number?.mustBe(expected: Number?) = Assertions.assertEquals(expected?.toInt(), this?.toInt())

// Array Overloads

internal inline infix fun ByteArray?.mustBe(expected: ByteArray?) = Assertions.assertArrayEquals(expected, this)

// Comparable

internal inline infix fun <T : Any> Comparable<T>.mustBeLessThanOrEqualTo(other: T) = (this <= other) mustBe true

internal inline infix fun <T : Any> Comparable<T>.mustBeGreaterThan(other: T) = (this > other) mustBe true

internal inline fun assume(condition: Boolean, message: String = "") = Assumptions.assumeTrue(condition, message)

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

internal inline fun anyOf(vararg items: Any?) = AnyOf(arrayOf(items))

internal infix fun Any?.mustNotBe(expected: AnyOf) = expected mustNotBe this

// privates

private inline infix fun Any?.isEqualTo(other: Any?) = if (this == null) other == null else (this == other)

private inline infix fun Any?.isNotEqualTo(other: Any?) = !(this isEqualTo other)

private fun fail(message: String = "", expected: Any?, actual: Any?): Nothing = throw AssertionFailedError()

internal inline fun <reified T : Throwable> allShouldThrow(funcs: Iterable<() -> Any?>) {
    funcs.forEach { shouldThrow<T>(it) }
}

internal inline fun <reified T : Throwable> allShouldNotThrow(funcs: Iterable<() -> Any?>) {
    funcs.forEach { shouldNotThrow<T>(it) }
}

// Utils

private var beforeTestSuiteInvoked = false
internal fun beforeTestSuite() {
    if (!beforeTestSuiteInvoked) {

        beforeTestSuiteInvoked = true
    }
}

internal fun defaultBeforeAll() {
    RtMidi.Config.useBundledLibraries(true).load()
    assertDoesNotThrow { RtMidi.compiledApis() }
}

internal val platform = Platform.getNativePlatform()

internal fun isLinux() = platform.os == Platform.OS.LINUX
internal fun isMacOs() = platform.os == Platform.OS.DARWIN
internal fun isWindows() = platform.os == Platform.OS.WINDOWS