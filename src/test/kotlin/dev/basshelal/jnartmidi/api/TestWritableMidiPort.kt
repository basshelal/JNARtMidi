package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.defaultBeforeAll
import dev.basshelal.jnartmidi.mustBe
import dev.basshelal.jnartmidi.mustNotBe
import dev.basshelal.jnartmidi.mustNotThrow
import dev.basshelal.jnartmidi.mustThrow
import dev.basshelal.jnartmidi.wait
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.random.Random

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
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val port = WritableMidiPort(info)
        port.info mustBe info
        port.api mustNotBe RtMidiApi.UNSPECIFIED
        port.clientName mustBe null

        port.destroy()
    }

    @Test
    fun `Full Constructor`() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true
        val api = allApis.first()
        val clientName = "Test Client ${Random.nextInt()}"
        val port = WritableMidiPort(info, clientName, api)
        port.info mustBe info
        port.api mustBe api
        port.clientName mustBe clientName

        port.destroy()
    }

    @Test
    fun `No API Constructor`() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()

        val clientName = "Test Client ${Random.nextInt()}"
        val port = WritableMidiPort(portInfo = info, clientName = clientName)
        port.info mustBe info
        port.api mustNotBe RtMidiApi.UNSPECIFIED
        port.clientName mustBe clientName

        port.destroy()
    }

    @Test
    fun Open() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true
        val port = WritableMidiPort(info)

        val oldReadableSize = RtMidi.readableMidiPorts().size

        val portName = "Test Writable Port ${Random.nextInt()}"
        port.open(portName)

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize mustNotBe newReadableSize
        oldReadableSize + 1 mustBe newReadableSize

        RtMidi.readableMidiPorts().find { it.name.contains(portName) } mustNotBe null

        port.isOpen mustBe true
        port.isVirtual mustBe false

        // Calling open again should do nothing

        { port.open() } mustNotThrow Throwable::class
        RtMidi.readableMidiPorts().size mustBe newReadableSize

        port.destroy()
    }

    @Test
    fun `Open Virtual`() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true
        val port = WritableMidiPort(info)

        val oldReadableSize = RtMidi.readableMidiPorts().size

        val portName = "Test Writable Port ${Random.nextInt()}"
        port.openVirtual(portName)

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize mustNotBe newReadableSize
        oldReadableSize + 1 mustBe newReadableSize

        RtMidi.readableMidiPorts().find { it.name.contains(portName) } mustNotBe null

        port.isOpen mustBe true
        port.isVirtual mustBe true

        // Calling open again should do nothing

        { port.openVirtual() } mustNotThrow Throwable::class
        RtMidi.readableMidiPorts().size mustBe newReadableSize

        port.destroy()
    }

    @Test
    fun Close() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true
        val port = WritableMidiPort(info)

        val oldReadableSize = RtMidi.readableMidiPorts().size

        val portName = "Test Writable Port ${Random.nextInt()}"
        port.open(portName)

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize mustNotBe newReadableSize
        oldReadableSize + 1 mustBe newReadableSize

        RtMidi.readableMidiPorts().find { it.name.contains(portName) } mustNotBe null

        port.isOpen mustBe true

        port.close()

        port.isOpen mustBe false
        port.isVirtual mustBe false

        RtMidi.readableMidiPorts().size mustNotBe newReadableSize
        RtMidi.readableMidiPorts().size mustBe oldReadableSize
        RtMidi.readableMidiPorts().find { it.name.contains(portName) } mustBe null

        // Calling close again should do nothing
        { port.close() } mustNotThrow Throwable::class

        // Reopening should still work
        port.open(portName)

        port.isOpen mustBe true

        RtMidi.readableMidiPorts().size mustBe newReadableSize
        RtMidi.readableMidiPorts().find { it.name.contains(portName) } mustNotBe null

        port.destroy()
    }

    @Test
    fun Destroy() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true
        val port = WritableMidiPort(info)

        port.isDestroyed mustBe false

        port.destroy()
        port.isDestroyed mustBe true

        // Destroying again should do nothing
        { port.destroy() } mustNotThrow Throwable::class
        port.isDestroyed mustBe true

        // doing anything else will throw RtMidiPortExceptions
        { port.open() } mustThrow RtMidiPortException::class
        { port.openVirtual() } mustThrow RtMidiPortException::class
        { port.close() } mustThrow RtMidiPortException::class
        { port.sendMessage(MidiMessage()) } mustThrow RtMidiPortException::class
    }

    @Test
    fun `Send Message`() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true
        val port = WritableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(intArrayOf(MidiMessage.NOTE_ON, 69, 69));

        // sending without opening should do nothing
        { port.sendMessage(midiMessage) } mustNotThrow Throwable::class
        port.midiMessage mustBe null

        val portName = "Test Writable Port ${Random.nextInt()}"

        port.open(portName)

        val foundReadableInfo = RtMidi.readableMidiPorts().find { it.name.contains(portName) }

        foundReadableInfo mustNotBe null
        require(foundReadableInfo != null) // for smart cast

        val readablePort = ReadableMidiPort(foundReadableInfo)
        readablePort.open()
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        port.sendMessage(midiMessage)

        wait(200)

        receivedMessage.data mustBe midiMessage.data
        readablePort.midiMessage mustBe midiMessage
        port.midiMessage mustBe readablePort.midiMessage
    }

}