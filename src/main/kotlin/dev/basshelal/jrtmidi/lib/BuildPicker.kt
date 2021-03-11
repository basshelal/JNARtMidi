package dev.basshelal.jrtmidi.lib

import jnr.ffi.LibraryLoader
import jnr.ffi.LibraryOption
import jnr.ffi.Platform
import jnr.ffi.byref.PointerByReference

private inline fun <reified T> loadLibrary(name: String) =
        LibraryLoader.loadLibrary(T::class.java, mapOf(LibraryOption.LoadNow to true), name)

/**
 * Possible RtMidi Build combinations
 */
internal enum class RtMidiBuildType {
    ALSA_X86_64, ALSA_JACK_X86_64, CORE_X86_64, CORE_JACK_X86_64, WIN_MM_X86_64,
    ALSA_AARCH64, ALSA_JACK_AARCH64,
    UNKNOWN;

    internal companion object {
        const val CORE = 1
        const val ALSA = 2
        const val JACK = 3
        const val WINMM = 4

        internal val platform: Platform by lazy { Platform.getNativePlatform() }

        /**
         * Dynamic way of finding what APIs are installed on the system by attempting to load each library.
         * Using this, we can determine which build of RtMidi to use depending on the available APIs. This
         * should work even when APIs are added or removed later on, such as JACK on Linux and MacOS.
         */
        internal fun getInstalledApis(): List<Int> {
            return mutableListOf<Int>().also {
                if (runCatching { loadLibrary<Alsa>("asound") }.isSuccess) it += ALSA
                // TODO: 07/03/2021 Unsure about coreMidi name
                if (runCatching { loadLibrary<Core>("CoreMIDI") }.isSuccess) it += CORE
                if (runCatching { loadLibrary<Jack>("jack") }.isSuccess) it += JACK
                if (runCatching { loadLibrary<WinMM>("winmm") }.isSuccess) it += WINMM
            }
        }

        internal fun getBuildType(): RtMidiBuildType {
            return getInstalledApis().let { apis: List<Int> ->
                val cpu = platform.cpu
                when (cpu) {
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
                            WINMM in apis -> WIN_MM_X86_64
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
            return when (getBuildType()) {
                ALSA_X86_64 -> "alsa-x86-64"
                ALSA_JACK_X86_64 -> "alsa-jack-x86-64"
                CORE_X86_64 -> "core-x86-64"
                CORE_JACK_X86_64 -> "core-jack-x86-64"
                WIN_MM_X86_64 -> "winmm-x86-64"
                ALSA_AARCH64 -> "alsa-aarch64"
                ALSA_JACK_AARCH64 -> "alsa-jack-aarch64"
                else -> throw IllegalStateException("Unknown build type: ${getBuildType()}\n${platform.name}")
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