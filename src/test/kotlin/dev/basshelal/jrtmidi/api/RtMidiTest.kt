package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.isWindows
import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import dev.basshelal.jrtmidi.mustBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Tests [RtMidi]
 * @author Bassam Helal
 */
internal class RtMidiTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun `Before All`() = defaultBeforeAll()

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }

    @Test
    fun `Supports Virtual Ports`() {
        val supportsVirtualPorts = !isWindows()
        RtMidi.supportsVirtualPorts() mustBe supportsVirtualPorts
    }

    @Test
    fun `Available APIs`() {
        val apis = RtMidi.compiledApis()
        apis.isNotEmpty() mustBe true
        val expectedCount = RtMidiLibrary.instance.rtmidi_get_compiled_api(null, -1)
        apis.size mustBe expectedCount
        apis.contains(RtMidiApi.UNSPECIFIED) mustBe false
    }

    @Test
    fun `Readable MIDI Ports`() {
        val ports = RtMidi.readableMidiPorts()
        ports.isNotEmpty() mustBe true
        val ptr = RtMidiLibrary.instance.rtmidi_in_create_default()
        val expectedCount = RtMidiLibrary.instance.rtmidi_get_port_count(ptr)
        ports.size mustBe expectedCount
        val distinctTypes = ports.map { it.type }.distinct()
        distinctTypes.size mustBe 1
        distinctTypes.first() mustBe MidiPort.Info.Type.READABLE
        RtMidiLibrary.instance.rtmidi_in_free(ptr)
    }

    @Test
    fun `Writable MIDI Ports`() {
        val ports = RtMidi.writableMidiPorts()
        ports.isNotEmpty() mustBe true
        val ptr = RtMidiLibrary.instance.rtmidi_out_create_default()
        val expectedCount = RtMidiLibrary.instance.rtmidi_get_port_count(ptr)
        ports.size mustBe expectedCount
        val distinctTypes = ports.map { it.type }.distinct()
        distinctTypes.size mustBe 1
        distinctTypes.first() mustBe MidiPort.Info.Type.WRITABLE
        RtMidiLibrary.instance.rtmidi_out_free(ptr)
    }

}