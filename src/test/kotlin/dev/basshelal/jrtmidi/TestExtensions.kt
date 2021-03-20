@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jrtmidi

import dev.basshelal.jrtmidi.api.RtMidi
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import jnr.ffi.Platform
import org.junit.jupiter.api.assertDoesNotThrow

internal inline fun Any?.log() = println(this)

internal inline fun wait(millis: Number) = Thread.sleep(millis.toLong())

internal inline fun <reified T : Throwable> allShouldThrow(funcs: Iterable<() -> Any?>) {
    funcs.forEach { shouldThrow<T>(it) }
}

internal inline fun <reified T : Throwable> allShouldNotThrow(funcs: Iterable<() -> Any?>) {
    funcs.forEach { shouldNotThrow<T>(it) }
}

// Utils

internal fun defaultBeforeAll() {
    RtMidi.Config.useBundledLibraries(true).load()
    assertDoesNotThrow { RtMidi.compiledApis() }
}

internal val platform = Platform.getNativePlatform()

internal fun isLinux() = platform.os == Platform.OS.LINUX
internal fun isMacOs() = platform.os == Platform.OS.DARWIN
internal fun isWindows() = platform.os == Platform.OS.WINDOWS