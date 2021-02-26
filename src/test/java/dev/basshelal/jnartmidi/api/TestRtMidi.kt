package dev.basshelal.jnartmidi.api

import com.sun.jna.Platform
import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.mustBeFalse
import dev.basshelal.jnartmidi.mustBeTrue
import dev.basshelal.jnartmidi.mustEqual
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

/**
 * Tests [RtMidi]
 * @author Bassam Helal
 */
class TestRtMidi {

    companion object {
        @BeforeAll
        @JvmStatic
        fun `Before All`() {
            RtMidi.addLibrarySearchPath("bin/${Platform.RESOURCE_PREFIX}")
            assertDoesNotThrow { RtMidi.availableApis() }
        }

        @AfterAll
        @JvmStatic
        fun `After All`() {
        }

    }

    @Test
    fun `Supports Virtual Ports`() {
        val supportsVirtualPorts = !Platform.isWindows()
        RtMidi.supportsVirtualPorts() mustEqual supportsVirtualPorts
    }

    @Test
    fun `Available APIs`() {
        val apis = RtMidi.availableApis()
        apis.isNotEmpty().mustBeTrue()
        val expectedCount = RtMidiLibrary.instance.rtmidi_get_compiled_api(null, -1)
        apis.size mustEqual expectedCount
        apis.contains(RtMidiApi.UNSPECIFIED).mustBeFalse()
    }

    @Test
    fun `Readable MIDI Ports`() {
        val ports = RtMidi.readableMidiPorts()
        ports.isNotEmpty().mustBeTrue()
        val ptr = RtMidiLibrary.instance.rtmidi_in_create_default()
        val expectedCount = RtMidiLibrary.instance.rtmidi_get_port_count(ptr)
        ports.size mustEqual expectedCount
        val distinctTypes = ports.map { it.type }.distinct()
        distinctTypes.size mustEqual 1
        distinctTypes.first() mustEqual MidiPort.Info.Type.READABLE
        RtMidiLibrary.instance.rtmidi_in_free(ptr)
    }

    @Test
    fun `Writable MIDI Ports`() {
        val ports = RtMidi.writableMidiPorts()
        ports.isNotEmpty().mustBeTrue()
        val ptr = RtMidiLibrary.instance.rtmidi_out_create_default()
        val expectedCount = RtMidiLibrary.instance.rtmidi_get_port_count(ptr)
        ports.size mustEqual expectedCount
        val distinctTypes = ports.map { it.type }.distinct()
        distinctTypes.size mustEqual 1
        distinctTypes.first() mustEqual MidiPort.Info.Type.WRITABLE
        RtMidiLibrary.instance.rtmidi_out_free(ptr)
    }

}