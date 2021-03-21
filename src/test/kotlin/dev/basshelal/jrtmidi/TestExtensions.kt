@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jrtmidi

import dev.basshelal.jrtmidi.api.RtMidi
import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import jnr.ffi.Platform
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

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
    shouldNotThrowAny { RtMidi.compiledApis() }
}

internal val platform = Platform.getNativePlatform()

internal fun isLinux() = platform.os == Platform.OS.LINUX
internal fun isMacOs() = platform.os == Platform.OS.DARWIN
internal fun isWindows() = platform.os == Platform.OS.WINDOWS

internal object Reflect {
    val originalOS = platform.os
    val originalCpu = platform.cpu

    fun setOS(os: Platform.OS) = Platform::class.java.run {
        val osField = getDeclaredField("os")
        osField.isAccessible = true
        osField.set(platform, os)
    }

    fun setCPU(cpu: Platform.CPU) = Platform::class.java.run {
        val cpuField = getDeclaredField("cpu")
        cpuField.isAccessible = true
        cpuField.set(platform, cpu)
    }

    fun setPlatform(os: Platform.OS, cpu: Platform.CPU) = run {
        setOS(os)
        setCPU(cpu)
    }

    fun resetPlatform() = run { setPlatform(originalOS, originalCpu) }

    fun setLibrary(func: () -> RtMidiLibrary) = RtMidiLibrary::class.companionObject?.run {
        memberProperties.find { it.name == "instance" }?.javaField?.also {
            it.isAccessible = true
            it.set(RtMidiLibrary.instance, func())
        }
    } ?: Unit
}