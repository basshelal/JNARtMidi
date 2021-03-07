package dev.basshelal.jrtmidi.lib.jnr

import dev.basshelal.jrtmidi.api.RtMidiApi
import jnr.ffi.LibraryLoader
import jnr.ffi.LibraryOption
import jnr.ffi.byref.PointerByReference

private inline fun <reified T> loadLibrary(name: String) =
        LibraryLoader.loadLibrary(T::class.java, mapOf(LibraryOption.LoadNow to true), name)

/**
 * Dynamic way of finding what APIs are installed on the system by attempting to load each library.
 * Using this, we can determine which build of RtMidi to use depending on the available APIs. This
 * should work even when APIs are added or removed later on, such as JACK on Linux and MacOS.
 */
fun getApis(): List<RtMidiApi> {
    val result = mutableListOf<RtMidiApi>()

    if (runCatching { loadLibrary<Alsa>("asound") }.isSuccess) result.add(RtMidiApi.LINUX_ALSA)
    // TODO: 07/03/2021 Unsure about coreMidi name
    if (runCatching { loadLibrary<Core>("CoreMIDI") }.isSuccess) result.add(RtMidiApi.MACOSX_CORE)
    if (runCatching { loadLibrary<Jack>("jack") }.isSuccess) result.add(RtMidiApi.UNIX_JACK)
    if (runCatching { loadLibrary<WinMM>("Winmm") }.isSuccess) result.add(RtMidiApi.WINDOWS_MM)

    return result
}

/**
 * Possible RtMidi Build combinations
 */
internal enum class RtMidiBuildType {
    ALSA, ALSA_JACK, CORE, CORE_JACK, WIN_MM, UNKNOWN;

    companion object {
        @JvmStatic
        fun get(): RtMidiBuildType {
            val apis = getApis()
            return when {
                RtMidiApi.LINUX_ALSA in apis -> when {
                    RtMidiApi.UNIX_JACK in apis -> ALSA_JACK
                    else -> ALSA
                }
                RtMidiApi.MACOSX_CORE in apis -> when {
                    RtMidiApi.UNIX_JACK in apis -> CORE_JACK
                    else -> CORE
                }
                RtMidiApi.WINDOWS_MM in apis -> WIN_MM
                else -> UNKNOWN
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