package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.lib.RtMidiBuild
import dev.basshelal.jrtmidi.lib.library
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Tests [RtMidi]
 * @author Bassam Helal
 */
internal class RtMidiTest : StringSpec({
    beforeSpec { defaultBeforeAll() }

    afterSpec { }

    "Is Platform Supported" {
        RtMidi.isPlatformSupported() shouldBe RtMidiBuild.isPlatformSupported
    }

    "Supports Virtual Ports" {
        RtMidi.supportsVirtualPorts() shouldBe RtMidiBuild.supportsVirtualPorts
    }

    "Compiled APIs" {
        val apis = RtMidi.compiledApis()
        apis.isNotEmpty() shouldBe true
        val expectedCount = library.rtmidi_get_compiled_api(null, -1)
        apis.size shouldBe expectedCount
        apis.contains(RtMidiApi.UNSPECIFIED) shouldBe false
    }

    "Readable MIDI Ports" {
        val ports = RtMidi.readableMidiPorts()
        ports.isNotEmpty() shouldBe true
        val ptr = library.rtmidi_in_create_default()
        val expectedCount = library.rtmidi_get_port_count(ptr)
        ports.size shouldBe expectedCount
        val distinctTypes = ports.map { it.type }.distinct()
        distinctTypes.size shouldBe 1
        distinctTypes.first() shouldBe MidiPort.Info.Type.READABLE
        library.rtmidi_in_free(ptr)
    }

    "Writable MIDI Ports" {
        val ports = RtMidi.writableMidiPorts()
        ports.isNotEmpty() shouldBe true
        val ptr = library.rtmidi_out_create_default()
        val expectedCount = library.rtmidi_get_port_count(ptr)
        ports.size shouldBe expectedCount
        val distinctTypes = ports.map { it.type }.distinct()
        distinctTypes.size shouldBe 1
        distinctTypes.first() shouldBe MidiPort.Info.Type.WRITABLE
        library.rtmidi_out_free(ptr)
    }

})