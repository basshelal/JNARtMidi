package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import dev.basshelal.jrtmidi.lib.library
import java.util.Objects

/**
 * Represents a compiled API that RtMidi uses to interact with MIDI in the system.
 *
 * You can get all available [RtMidiApi]s on a machine by calling [RtMidi.compiledApis]
 *
 * [MidiPort]s have a constructor that allow you to choose which [RtMidiApi] the port will use.
 *
 * @author Bassam Helal
 */
class RtMidiApi private constructor(api: Int) {

    companion object {

        /**
         * An unspecified API, when used in a [MidiPort] constructor,
         * this tells RtMidi to choose the first working API it finds
         */
        @JvmField
        val UNSPECIFIED = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED)

        /** Macintosh OS-X CoreMIDI API */
        @JvmField
        val MACOSX_CORE = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE)

        /** The Advanced Linux Sound Architecture API */
        @JvmField
        val LINUX_ALSA = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA)

        /** The Jack Low-Latency MIDI Server API */
        @JvmField
        val UNIX_JACK = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK)

        /** The Microsoft Multimedia MIDI API */
        @JvmField
        val WINDOWS_MM = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM)

        /** A compilable but non-functional API */
        @JvmField
        val RTMIDI_DUMMY = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY)

        /**
         * @return the [RtMidiApi] corresponding to the given [api] such that its number will equal [api],
         * otherwise if none was found [RtMidiApi.UNSPECIFIED]
         */
        @JvmStatic
        fun fromInt(api: Int): RtMidiApi = when (api) {
            RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE -> MACOSX_CORE
            RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA -> LINUX_ALSA
            RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK -> UNIX_JACK
            RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM -> WINDOWS_MM
            RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY -> RTMIDI_DUMMY
            else -> UNSPECIFIED
        }
    }

    /**
     * The number of this [RtMidiApi] as used internally by RtMidi, see [RtMidiLibrary.RtMidiApi]
     */
    val number: Int = api

    /**
     * The name of this [RtMidiApi] as specified by RtMidi, this is different from this [RtMidiApi]'s [displayName]
     */
    val name: String = library.rtmidi_api_name(api)

    /**
     * The display name of this [RtMidiApi] as specified by RtMidi, this is different from this [RtMidiApi]'s [name]
     */
    val displayName: String = library.rtmidi_api_display_name(api)

    override fun equals(other: Any?): Boolean =
            other is RtMidiApi && this.number == other.number &&
                    this.name == other.name && this.displayName == other.displayName

    override fun hashCode(): Int = Objects.hash(number, name, displayName)

    override fun toString(): String = "RtMidiApi{ number=$number, name='$name', displayName='$displayName' }"
}