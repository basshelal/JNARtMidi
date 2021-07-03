package dev.basshelal.jrtmidi.lib

import dev.basshelal.jrtmidi.api.RtMidi
import dev.basshelal.jrtmidi.lib.Build.Type.AARCH64_ALSA
import dev.basshelal.jrtmidi.lib.Build.Type.AARCH64_ALSA_JACK
import dev.basshelal.jrtmidi.lib.Build.Type.ARM_ALSA
import dev.basshelal.jrtmidi.lib.Build.Type.ARM_ALSA_JACK
import dev.basshelal.jrtmidi.lib.Build.Type.UNKNOWN
import dev.basshelal.jrtmidi.lib.Build.Type.X86_64_ALSA
import dev.basshelal.jrtmidi.lib.Build.Type.X86_64_ALSA_JACK
import dev.basshelal.jrtmidi.lib.Build.Type.X86_64_CORE
import dev.basshelal.jrtmidi.lib.Build.Type.X86_64_CORE_JACK
import dev.basshelal.jrtmidi.lib.Build.Type.X86_64_WINMM
import jnr.ffi.Platform

// TODO: 30-Jun-2021 @basshelal: Test this using reflection for platform, library doesn't need to load
//  may need to convert these to dynamic vals (use get()) instead of statics, this is useful for helping with unload
//  and reload in RtMidi file as well
internal object Build {

    internal val platform: Platform = Platform.getNativePlatform()
    internal val platformName: String = platform.run { "$cpu-$os" }

    // TODO: 30-Jun-2021 @basshelal: Maybe allow users to query their build type at runtime
    enum class Type {
        X86_64_ALSA, X86_64_ALSA_JACK, X86_64_CORE, X86_64_CORE_JACK, X86_64_WINMM,
        ARM_ALSA, ARM_ALSA_JACK,
        AARCH64_ALSA, AARCH64_ALSA_JACK,
        UNKNOWN;
    }

    private const val CORE = 1
    private const val ALSA = 2
    private const val JACK = 3
    private const val WINMM = 4

    internal val isPlatformSupported: Boolean = platform.run {
        when (cpu) {
            Platform.CPU.ARM, Platform.CPU.AARCH64 -> os == Platform.OS.LINUX
            Platform.CPU.X86_64 -> os == Platform.OS.LINUX || os == Platform.OS.DARWIN || os == Platform.OS.WINDOWS
            else -> false
        }
    }

    internal val isJackInstalled: Boolean = platform.libraryLocations("jack", null).isNotEmpty()

    /**
     * Dynamic way of finding what APIs are installed on the system by attempting to load each library.
     * Using this, we can determine which build of RtMidi to use depending on the available APIs. This
     * should work even when APIs are added or removed later on, such as JACK on Linux and MacOS.
     */
    internal val installedApis: List<Int> = mutableListOf<Int>().also {
        when (platform.os) {
            Platform.OS.LINUX -> it += ALSA // Safe to assume, ALSA is part of the kernel
            Platform.OS.DARWIN -> it += CORE // Safe to assume
            Platform.OS.WINDOWS -> it += WINMM // Safe to assume??
            else -> Unit
        }
        if (!RtMidi.Config.disallowJACK && isJackInstalled) it += JACK
    }


    internal val buildType: Type = installedApis.let { apis: List<Int> ->
        when (platform.cpu) {
            Platform.CPU.X86_64 -> {
                when {
                    ALSA in apis -> if (JACK in apis) X86_64_ALSA_JACK else X86_64_ALSA
                    CORE in apis -> if (JACK in apis) X86_64_CORE_JACK else X86_64_CORE
                    WINMM in apis -> X86_64_WINMM
                    else -> UNKNOWN
                }
            }
            Platform.CPU.ARM -> {
                when {
                    ALSA in apis -> if (JACK in apis) ARM_ALSA_JACK else ARM_ALSA
                    else -> UNKNOWN
                }
            }
            Platform.CPU.AARCH64 -> {
                when {
                    ALSA in apis -> if (JACK in apis) AARCH64_ALSA_JACK else AARCH64_ALSA
                    else -> UNKNOWN
                }
            }
            else -> UNKNOWN
        }
    }

    /**
     * Get the path corresponding to the current build type
     */
    internal val buildPath: String = buildType.let {
        when (it) {
            X86_64_ALSA -> "x86_64-alsa"
            X86_64_ALSA_JACK -> "x86_64-alsa-jack"
            X86_64_CORE -> "x86_64-core"
            X86_64_CORE_JACK -> "x86_64-core-jack"
            X86_64_WINMM -> "x86_64-winmm"
            ARM_ALSA -> "arm-alsa"
            ARM_ALSA_JACK -> "arm-alsa-jack"
            AARCH64_ALSA -> "aarch64-alsa"
            AARCH64_ALSA_JACK -> "aarch64-alsa-jack"
            UNKNOWN -> throw IllegalStateException("Unknown/unsupported build type!\nPlatform: $platformName")
        }
    }

    internal val supportsVirtualPorts: Boolean = WINMM !in installedApis
}