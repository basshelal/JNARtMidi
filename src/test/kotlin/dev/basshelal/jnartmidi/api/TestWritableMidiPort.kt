package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.defaultBeforeAll
import dev.basshelal.jnartmidi.mustBe
import dev.basshelal.jnartmidi.mustBeGreaterThan
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
        port.isDestroyed mustBe false
        port.isOpen mustBe false
        port.isVirtual mustBe false
        (port.api in RtMidi.availableApis()) mustBe true

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
        port.isDestroyed mustBe false
        port.isOpen mustBe false
        port.isVirtual mustBe false

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
        port.isDestroyed mustBe false
        port.isOpen mustBe false
        port.isVirtual mustBe false
        (port.api in RtMidi.availableApis()) mustBe true

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

        port.isOpen mustBe true
        port.isVirtual mustBe false

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize mustNotBe newReadableSize
        oldReadableSize + 1 mustBe newReadableSize

        val foundReadable = RtMidi.readableMidiPorts().find { it.name.contains(portName) }

        foundReadable mustNotBe null

        // Calling open again should do nothing

        { port.open(portName) } mustNotThrow Throwable::class
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

        port.isOpen mustBe true
        port.isVirtual mustBe true

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize mustNotBe newReadableSize
        oldReadableSize + 1 mustBe newReadableSize

        val foundReadable = RtMidi.readableMidiPorts().find { it.name.contains(portName) }

        foundReadable mustNotBe null

        // Calling openVirtual again should do nothing

        { port.openVirtual(portName) } mustNotThrow Throwable::class
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

        port.isOpen mustBe true

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize mustNotBe newReadableSize
        oldReadableSize + 1 mustBe newReadableSize

        RtMidi.readableMidiPorts().find { it.name.contains(portName) } mustNotBe null

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

        val portName = "Test Writable Port ${Random.nextInt()}"

        val port = WritableMidiPort(info)

        port.isDestroyed mustBe false

        port.destroy()

        port.isDestroyed mustBe true

        // Destroying again should do nothing
        { port.destroy() } mustNotThrow Throwable::class
        port.isDestroyed mustBe true

        // doing anything else will throw RtMidiPortExceptions
        listOf({ port.open(portName) },
                { port.openVirtual(portName) },
                { port.close() },
                { port.sendMessage(MidiMessage()) }) mustThrow RtMidiPortException::class

        // accessing variables though will not
        listOf({ port.isOpen },
                { port.isDestroyed },
                { port.clientName },
                { port.api },
                { port.info },
                { port.isVirtual },
                { port.toString() }) mustNotThrow Throwable::class
    }

    @Test
    fun `Send Message`() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true

        val writablePort = WritableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        writablePort.open(writablePortName)

        val foundReadableInfo = RtMidi.readableMidiPorts().find { it.name.contains(writablePortName) }

        foundReadableInfo mustNotBe null
        require(foundReadableInfo != null) // for smart cast

        val readablePortName = "Test Readable Port ${Random.nextInt()}"

        val readablePort = ReadableMidiPort(foundReadableInfo)
        readablePort.open(readablePortName)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        writablePort.sendMessage(midiMessage)

        wait(200)

        receivedMessage.data mustBe midiMessage.data
        readablePort.midiMessage mustBe midiMessage
        writablePort.midiMessage mustBe readablePort.midiMessage

        readablePort.destroy()
        writablePort.destroy()
    }

    @Test
    fun `Send Message To Invalid Port`() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true

        val writablePort = WritableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        // sending without opening should do nothing
        { writablePort.sendMessage(midiMessage) } mustNotThrow Throwable::class
        writablePort.midiMessage mustBe null

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        writablePort.open(writablePortName)

        val foundReadableInfo = RtMidi.readableMidiPorts().find { it.name.contains(writablePortName) }

        foundReadableInfo mustNotBe null
        require(foundReadableInfo != null) // for smart cast

        val readablePortName = "Test Readable Port ${Random.nextInt()}"

        val readablePort = ReadableMidiPort(foundReadableInfo)
        readablePort.open(readablePortName)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        // close and try to send
        writablePort.close()
        val newMessage = MidiMessage(0)
        writablePort.sendMessage(newMessage)

        wait(200)

        writablePort.midiMessage?.data mustNotBe newMessage.data
        receivedMessage.data mustNotBe newMessage.data

        // try to send to a destroyed readable port
        writablePort.open(writablePortName)
        readablePort.destroy()

        newMessage.setData(byteArrayOf(1, 2, 3))

        writablePort.sendMessage(newMessage)

        wait(200)

        readablePort.midiMessage?.data mustNotBe newMessage.data

        // even though the receiving end does not exist anymore, we can't know that so the message gets
        // "sent", so port gets a new midiMessage because sending sent nowhere but was not unsuccessful
        writablePort.midiMessage?.data mustBe newMessage.data

        readablePort.destroy()
        writablePort.destroy()
    }

    @Test
    fun `Open Port After Info Index Change`() {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() mustBe true
        val writableInfo = allWritableInfos.first()
        val allApis = RtMidi.availableApis()
        allApis.isNotEmpty() mustBe true

        val writablePorts = (0..10).map { WritableMidiPort(writableInfo) }
        writablePorts.onEach { it.open("Port: ${Random.nextInt()}") }


        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.size mustBeGreaterThan 10

        val readableInfo = allReadableInfos.last() // The very last writablePort we made above
        val readablePorts = (0..10).map { ReadableMidiPort(readableInfo) }
        readablePorts.onEach {
            { it.open("Port: ${Random.nextInt()}") } mustNotThrow Throwable::class
        }

        val lastWritablePort = writablePorts.last()

        writablePorts.filterNot { it == lastWritablePort }.onEach { it.destroy() }

        val portName = "Test Writable Port ${Random.nextInt()}"

        readablePorts.onEach {
            { it.close() } mustNotThrow Throwable::class
            { it.open(portName) } mustNotThrow Throwable::class
        }

        lastWritablePort.destroy()

        readablePorts.onEach {
            { it.close() } mustNotThrow Throwable::class
            { it.open(portName) } mustThrow RtMidiPortException::class
        }

        readablePorts.onEach { it.destroy() }
        writablePorts.onEach { it.destroy() }
    }

}