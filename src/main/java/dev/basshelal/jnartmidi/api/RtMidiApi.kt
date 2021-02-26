package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import java.util.Objects

class RtMidiApi private constructor(api: Int) {

    companion object {
        @JvmField
        val UNSPECIFIED = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED)

        @JvmField
        val MACOSX_CORE = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE)

        @JvmField
        val LINUX_ALSA = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA)

        @JvmField
        val UNIX_JACK = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK)

        @JvmField
        val WINDOWS_MM = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM)

        @JvmField
        val RTMIDI_DUMMY = RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY)

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

    val number: Int = api
    val name: String = RtMidiLibrary.instance.rtmidi_api_name(api)
    val displayName: String = RtMidiLibrary.instance.rtmidi_api_display_name(api)

    override fun equals(other: Any?): Boolean =
            other is RtMidiApi && this.name == other.name && this.displayName == other.displayName

    override fun hashCode(): Int = Objects.hash(name, displayName)

    override fun toString(): String = "RtMidiApi{name='$name', displayName='$displayName'}"
}