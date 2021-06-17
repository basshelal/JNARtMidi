package dev.basshelal.jrtmidi.lib

import dev.basshelal.jrtmidi.api.RtMidi
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.ALSA_AARCH64
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.ALSA_ARM
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.ALSA_JACK_AARCH64
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.ALSA_JACK_ARM
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.ALSA_JACK_X86_64
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.ALSA_X86_64
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.CORE_JACK_X86_64
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.CORE_X86_64
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.UNKNOWN
import dev.basshelal.jrtmidi.lib.RtMidiBuild.Type.WINMM_X86_64
import jnr.ffi.Platform
import java.io.File

/** `true` if *any* of the passed in [paths] refers to an existing file, `false` otherwise */
@Suppress("NOTHING_TO_INLINE")
private inline fun anyFileExists(paths: Iterable<String>): Boolean = paths.any { path: String ->
    File(path).let { it.exists() && it.isFile }
}

internal object RtMidiBuild {

    internal val platform: Platform = Platform.getNativePlatform()
    internal val platformName: String = platform.run { "$os-$cpu" }

    enum class Type {
        ALSA_X86_64, ALSA_JACK_X86_64, CORE_X86_64, CORE_JACK_X86_64, WINMM_X86_64,
        ALSA_ARM, ALSA_JACK_ARM, ALSA_AARCH64, ALSA_JACK_AARCH64,
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
                    ALSA in apis -> if (JACK in apis) ALSA_JACK_X86_64 else ALSA_X86_64
                    CORE in apis -> if (JACK in apis) CORE_JACK_X86_64 else CORE_X86_64
                    WINMM in apis -> WINMM_X86_64
                    else -> UNKNOWN
                }
            }
            Platform.CPU.ARM -> {
                when {
                    ALSA in apis -> if (JACK in apis) ALSA_JACK_ARM else ALSA_ARM
                    else -> UNKNOWN
                }
            }
            Platform.CPU.AARCH64 -> {
                when {
                    ALSA in apis -> if (JACK in apis) ALSA_JACK_AARCH64 else ALSA_AARCH64
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
            ALSA_X86_64 -> "alsa-x86_64"
            ALSA_JACK_X86_64 -> "alsa-jack-x86_64"
            CORE_X86_64 -> "core-x86_64"
            CORE_JACK_X86_64 -> "core-jack-x86_64"
            WINMM_X86_64 -> "winmm-x86_64"
            ALSA_ARM -> "alsa-arm"
            ALSA_JACK_ARM -> "alsa-jack-arm"
            ALSA_AARCH64 -> "alsa-aarch64"
            ALSA_JACK_AARCH64 -> "alsa-jack-aarch64"
            UNKNOWN -> throw IllegalStateException("Unknown/unsupported build type!\nPlatform: $platformName")
        }
    }

    internal val supportsVirtualPorts: Boolean = WINMM !in installedApis
}