package dev.basshelal.jrtmidi.lib.jnr

import dev.basshelal.jrtmidi.api.RtMidiApi
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
    X86_64_ALSA, X86_64_ALSA_JACK, X86_64_CORE, X86_64_CORE_JACK, X86_64_WIN_MM,
    AARCH64_ALSA, AARCH64_ALSA_JACK,
    UNKNOWN;

    internal companion object {
        internal val platform: Platform by lazy { Platform.getNativePlatform() }

        /**
         * Dynamic way of finding what APIs are installed on the system by attempting to load each library.
         * Using this, we can determine which build of RtMidi to use depending on the available APIs. This
         * should work even when APIs are added or removed later on, such as JACK on Linux and MacOS.
         */
        internal fun getInstalledApis(): List<RtMidiApi> {
            return mutableListOf<RtMidiApi>().also {
                if (runCatching { loadLibrary<Alsa>("asound") }.isSuccess) it += RtMidiApi.LINUX_ALSA
                // TODO: 07/03/2021 Unsure about coreMidi name
                if (runCatching { loadLibrary<Core>("CoreMIDI") }.isSuccess) it += RtMidiApi.MACOSX_CORE
                if (runCatching { loadLibrary<Jack>("jack") }.isSuccess) it += RtMidiApi.UNIX_JACK
                if (runCatching { loadLibrary<WinMM>("Winmm") }.isSuccess) it += RtMidiApi.WINDOWS_MM
            }
        }

        internal fun getBuildType(): RtMidiBuildType {
            return getInstalledApis().let { apis: List<RtMidiApi> ->
                val cpu = platform.cpu
                when (cpu) {
                    Platform.CPU.X86_64 -> {
                        when {
                            RtMidiApi.LINUX_ALSA in apis -> when {
                                RtMidiApi.UNIX_JACK in apis -> X86_64_ALSA_JACK
                                else -> X86_64_ALSA
                            }
                            RtMidiApi.MACOSX_CORE in apis -> when {
                                RtMidiApi.UNIX_JACK in apis -> X86_64_CORE_JACK
                                else -> X86_64_CORE
                            }
                            RtMidiApi.WINDOWS_MM in apis -> X86_64_WIN_MM
                            else -> UNKNOWN
                        }
                    }
                    Platform.CPU.AARCH64 -> {
                        when {
                            RtMidiApi.LINUX_ALSA in apis -> when {
                                RtMidiApi.UNIX_JACK in apis -> AARCH64_ALSA_JACK
                                else -> AARCH64_ALSA
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
                X86_64_ALSA -> "x86-64/alsa"
                X86_64_ALSA_JACK -> "x86-64/alsa-jack"
                X86_64_CORE -> "x86-64/core"
                X86_64_CORE_JACK -> "x86-64/core-jack"
                X86_64_WIN_MM -> "x86-64/winmm"
                AARCH64_ALSA -> "aarch64/alsa"
                AARCH64_ALSA_JACK -> "aarch64/alsa-jack"
                else -> throw IllegalStateException("Unknown build type: ${getBuildType()}\n${platform}")
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