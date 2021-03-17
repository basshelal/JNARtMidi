package dev.basshelal.jrtmidi.lib

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
import jnr.ffi.LibraryLoader
import jnr.ffi.LibraryOption
import jnr.ffi.Platform
import jnr.ffi.byref.PointerByReference

private inline fun <reified T> loadLibrary(name: String) =
        LibraryLoader.loadLibrary(T::class.java, mapOf(LibraryOption.LoadNow to true), name)

/**
 * Possible RtMidi Build combinations.
 * We support (or plan to support):
 * * ALSA, ALSA+JACK, Core, Core+JACK, WinMM on x86_64
 * * ALSA, ALSA+JACK on arm (32 bit arm)
 * * ALSA, ALSA+JACK on aarch64 (64 bit arm)
 */
internal object RtMidiBuild {

    internal val platform: Platform = Platform.getNativePlatform()
    internal val platformName: String = platform.run { "$os-$cpu" }

    enum class Type {
        ALSA_X86_64, ALSA_JACK_X86_64, CORE_X86_64, CORE_JACK_X86_64, WINMM_X86_64,
        ALSA_ARM, ALSA_JACK_ARM, ALSA_AARCH64, ALSA_JACK_AARCH64,
        UNKNOWN;
    }

    const val CORE = 1
    const val ALSA = 2
    const val JACK = 3
    const val WINMM = 4

    internal fun isPlatformSupported(): Boolean {
        return platform.run {
            when (cpu) {
                Platform.CPU.ARM, Platform.CPU.AARCH64 -> os == Platform.OS.LINUX
                Platform.CPU.X86_64 -> os == Platform.OS.LINUX || os == Platform.OS.DARWIN || os == Platform.OS.WINDOWS
                else -> false
            }
        }
    }

    /**
     * Dynamic way of finding what APIs are installed on the system by attempting to load each library.
     * Using this, we can determine which build of RtMidi to use depending on the available APIs. This
     * should work even when APIs are added or removed later on, such as JACK on Linux and MacOS.
     */
    internal fun getInstalledApis(): List<Int> {
        return mutableListOf<Int>().also {
            // TODO: 13/03/2021 We can do file checks instead of library loading although it would mean
            //  having to use default lib paths to determine if a library exists or not,
            //  the code would be long and ugly
            if (runCatching { loadLibrary<Alsa>("asound") }.isSuccess) it += ALSA
            if (runCatching { loadLibrary<Jack>("jack") }.isSuccess) it += JACK
            if (runCatching { loadLibrary<WinMM>("winmm") }.isSuccess) it += WINMM
            // TODO: 13/03/2021 Is there a better way to ensure Darwin has CoreMIDI?
            if (platform.os == Platform.OS.DARWIN) it += CORE
        }
    }

    internal fun getBuildType(): Type {
        return getInstalledApis().let { apis: List<Int> ->
            when (platform.cpu) {
                Platform.CPU.X86_64 -> {
                    when {
                        ALSA in apis -> when {
                            JACK in apis -> ALSA_JACK_X86_64
                            else -> ALSA_X86_64
                        }
                        CORE in apis -> when {
                            JACK in apis -> CORE_JACK_X86_64
                            else -> CORE_X86_64
                        }
                        WINMM in apis -> WINMM_X86_64
                        else -> UNKNOWN
                    }
                }
                Platform.CPU.ARM -> {
                    when {
                        ALSA in apis -> when {
                            JACK in apis -> ALSA_JACK_ARM
                            else -> ALSA_ARM
                        }
                        else -> UNKNOWN
                    }
                }
                Platform.CPU.AARCH64 -> {
                    when {
                        ALSA in apis -> when {
                            JACK in apis -> ALSA_JACK_AARCH64
                            else -> ALSA_AARCH64
                        }
                        else -> UNKNOWN
                    }
                }
                else -> UNKNOWN
            }
        }
    }

    /**
     * Get the path corresponding to the current build type
     */
    internal fun getBuildPath(): String {
        return getBuildType().let {
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
                else -> throw IllegalStateException("Unknown/unsupported build type: $it\n$platformName")
            }
        }
    }
}

// Below are minimal mappings of each API, I picked the simplest functions I could find quickly
// I have tested ALSA and JACK, need to test the proprietary OSes
// 07-Mar-2021 Bassam Helal

internal interface Alsa {
    fun snd_asoundlib_version(): String
}

internal interface Core {
    fun MIDIGetNumberOfDevices(): Int
}

internal interface Jack {
    fun jack_activate(client: PointerByReference): Int
}

internal interface WinMM {
    fun midiInGetNumDevs(): Int
}