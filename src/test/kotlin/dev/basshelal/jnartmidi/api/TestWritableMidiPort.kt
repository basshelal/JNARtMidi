package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.defaultBeforeAll
import dev.basshelal.jnartmidi.mustBe
import dev.basshelal.jnartmidi.mustNotBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/** Tests [WritableMidiPort] including its supertype [MidiPort] */
internal class TestWritableMidiPort {
    companion object {
        @BeforeAll
        @JvmStatic
        fun `Before All`() = defaultBeforeAll()

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }

    @Test
    fun `Info Constructor`() {
        val allInfos = RtMidi.writableMidiPorts()
        allInfos.isNotEmpty() mustBe true
        val info = allInfos.first()
        val port = WritableMidiPort(info)
        port.info mustBe info
    }

    @Test
    fun `Custom API Constructor`() {
        val allInfos = RtMidi.writableMidiPorts()
        allInfos.isNotEmpty() mustBe true
        val info = allInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true
        val api = allApis.first()
        val name = "My Port name"
        var port = WritableMidiPort(info, name, api)
        port.info mustBe info
        port.api mustBe api
        port.clientName mustBe name

        // Let RtMidi choose the api
        port = WritableMidiPort(portInfo = info, clientName = name)
        port.api mustNotBe RtMidiApi.UNSPECIFIED
    }
}