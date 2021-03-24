package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import dev.basshelal.jrtmidi.lib.library
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

/**
 * Tests [RtMidiApi]
 * @author Bassam Helal
 */
internal class RtMidiApiTest : StringSpec({

    beforeSpec { defaultBeforeAll() }

    afterSpec { }

    "Unspecified" {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED
        val api = RtMidiApi.UNSPECIFIED
        api shouldBeSameInstanceAs RtMidiApi.fromInt(number)
        api.number shouldBe number
        api.name shouldBe library.rtmidi_api_name(number)
        api.displayName shouldBe library.rtmidi_api_display_name(number)
    }

    "MacOSX Core" {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE
        val api = RtMidiApi.MACOSX_CORE
        api shouldBeSameInstanceAs RtMidiApi.fromInt(number)
        api.number shouldBe number
        api.name shouldBe library.rtmidi_api_name(number)
        api.displayName shouldBe library.rtmidi_api_display_name(number)
    }

    "Linux ALSA" {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA
        val api = RtMidiApi.LINUX_ALSA
        api shouldBeSameInstanceAs RtMidiApi.fromInt(number)
        api.number shouldBe number
        api.name shouldBe library.rtmidi_api_name(number)
        api.displayName shouldBe library.rtmidi_api_display_name(number)
    }

    "Unix JACK" {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK
        val api = RtMidiApi.UNIX_JACK
        api shouldBeSameInstanceAs RtMidiApi.fromInt(number)
        api.number shouldBe number
        api.name shouldBe library.rtmidi_api_name(number)
        api.displayName shouldBe library.rtmidi_api_display_name(number)
    }

    "Windows MM" {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM
        val api = RtMidiApi.WINDOWS_MM
        api shouldBeSameInstanceAs RtMidiApi.fromInt(number)
        api.number shouldBe number
        api.name shouldBe library.rtmidi_api_name(number)
        api.displayName shouldBe library.rtmidi_api_display_name(number)
    }

    "RTMidi Dummy"{
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY
        val api = RtMidiApi.RTMIDI_DUMMY
        api shouldBeSameInstanceAs RtMidiApi.fromInt(number)
        api.number shouldBe number
        api.name shouldBe library.rtmidi_api_name(number)
        api.displayName shouldBe library.rtmidi_api_display_name(number)
    }

    "From Int" {
        val number = RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY
        val api = RtMidiApi.fromInt(number)
        api shouldBeSameInstanceAs RtMidiApi.RTMIDI_DUMMY
        api.number shouldBe number
        api.name shouldBe library.rtmidi_api_name(number)
        api.displayName shouldBe library.rtmidi_api_display_name(number)
    }

})