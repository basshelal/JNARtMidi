package dev.basshelal.jrtmidi.lib

import jnr.ffi.Pointer
import jnr.ffi.annotations.Delegate
import jnr.ffi.types.size_t

typealias RtMidiApi = Int

/** MIDI API specifier arguments. See RtMidi::Api */
internal object RtMidiApis {
    /** Search for a working compiled API */
    const val RTMIDI_API_UNSPECIFIED: RtMidiApi = 0

    /** Macintosh OS-X CoreMIDI API */
    const val RTMIDI_API_MACOSX_CORE: RtMidiApi = 1

    /** The Advanced Linux Sound Architecture API */
    const val RTMIDI_API_LINUX_ALSA: RtMidiApi = 2

    /** The Jack Low-Latency MIDI Server API */
    const val RTMIDI_API_UNIX_JACK: RtMidiApi = 3

    /** The Microsoft Multimedia MIDI API */
    const val RTMIDI_API_WINDOWS_MM: RtMidiApi = 4

    /** A compilable but non-functional API */
    const val RTMIDI_API_RTMIDI_DUMMY: RtMidiApi = 5

    /** Number of values in this enum */
    const val RTMIDI_API_NUM: Int = 6
}

/**
 * typedef void(* RtMidiCCallback) (double timeStamp, const unsigned char* message, size_t messageSize, void* userData)
 */
@jnr.ffi.annotations.IgnoreError
internal interface RtMidiCCallback {
    @Delegate
    operator fun invoke(timeStamp: Double, message: Pointer?, @size_t messageSize: Long, userData: Pointer?)
}