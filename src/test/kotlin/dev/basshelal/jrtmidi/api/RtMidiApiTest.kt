package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import dev.basshelal.jrtmidi.mustBe
import dev.basshelal.jrtmidi.mustBeSameAs
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class RtMidiApiTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun `Before All`() = defaultBeforeAll()

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }

    @Test
    fun Unspecified() {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED
        val api = RtMidiApi.UNSPECIFIED
        api mustBeSameAs RtMidiApi.fromInt(number)
        api.number mustBe number
        api.name mustBe RtMidiLibrary.instance.rtmidi_api_name(number)
        api.displayName mustBe RtMidiLibrary.instance.rtmidi_api_display_name(number)
    }

    @Test
    fun `MacOSX Core`() {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE
        val api = RtMidiApi.MACOSX_CORE
        api mustBeSameAs RtMidiApi.fromInt(number)
        api.number mustBe number
        api.name mustBe RtMidiLibrary.instance.rtmidi_api_name(number)
        api.displayName mustBe RtMidiLibrary.instance.rtmidi_api_display_name(number)
    }

    @Test
    fun `Linux ALSA`() {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA
        val api = RtMidiApi.LINUX_ALSA
        api mustBeSameAs RtMidiApi.fromInt(number)
        api.number mustBe number
        api.name mustBe RtMidiLibrary.instance.rtmidi_api_name(number)
        api.displayName mustBe RtMidiLibrary.instance.rtmidi_api_display_name(number)
    }

    @Test
    fun `Unix JACK`() {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK
        val api = RtMidiApi.UNIX_JACK
        api mustBeSameAs RtMidiApi.fromInt(number)
        api.number mustBe number
        api.name mustBe RtMidiLibrary.instance.rtmidi_api_name(number)
        api.displayName mustBe RtMidiLibrary.instance.rtmidi_api_display_name(number)
    }

    @Test
    fun `Windows MM`() {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM
        val api = RtMidiApi.WINDOWS_MM
        api mustBeSameAs RtMidiApi.fromInt(number)
        api.number mustBe number
        api.name mustBe RtMidiLibrary.instance.rtmidi_api_name(number)
        api.displayName mustBe RtMidiLibrary.instance.rtmidi_api_display_name(number)
    }

    @Test
    fun `RTMidi Dummy`() {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY
        val api = RtMidiApi.RTMIDI_DUMMY
        api mustBeSameAs RtMidiApi.fromInt(number)
        api.number mustBe number
        api.name mustBe RtMidiLibrary.instance.rtmidi_api_name(number)
        api.displayName mustBe RtMidiLibrary.instance.rtmidi_api_display_name(number)
    }

    @Test
    fun `From Int`() {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY
        val api = RtMidiApi.fromInt(number)
        api mustBeSameAs RtMidiApi.RTMIDI_DUMMY
        api.number mustBe number
        api.name mustBe RtMidiLibrary.instance.rtmidi_api_name(number)
        api.displayName mustBe RtMidiLibrary.instance.rtmidi_api_display_name(number)
    }
}