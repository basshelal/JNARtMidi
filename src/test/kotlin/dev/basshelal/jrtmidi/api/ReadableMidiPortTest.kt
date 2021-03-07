package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.mustBe
import dev.basshelal.jrtmidi.mustBeGreaterThan
import dev.basshelal.jrtmidi.mustNotBe
import dev.basshelal.jrtmidi.mustNotThrow
import dev.basshelal.jrtmidi.mustThrow
import dev.basshelal.jrtmidi.wait
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.Arrays
import kotlin.math.min
import kotlin.random.Random

/** Tests [ReadableMidiPort] including its supertype [MidiPort] */
internal class ReadableMidiPortTest {

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
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val port = ReadableMidiPort(info)
        port.info mustBe info
        port.api mustNotBe RtMidiApi.UNSPECIFIED
        port.clientName mustBe null
        port.isDestroyed mustBe false
        port.isOpen mustBe false
        port.isVirtual mustBe false
        (port.api in RtMidi.compiledApis()) mustBe true

        port.destroy()
    }

    @Test
    fun `Full Constructor`() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true
        val api = allApis.first()
        val clientName = "Test Client ${Random.nextInt()}"
        val port = ReadableMidiPort(info, clientName, api)
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
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()

        val clientName = "Test Client ${Random.nextInt()}"
        val port = ReadableMidiPort(portInfo = info, clientName = clientName)
        port.info mustBe info
        port.api mustNotBe RtMidiApi.UNSPECIFIED
        port.clientName mustBe clientName
        port.isDestroyed mustBe false
        port.isOpen mustBe false
        port.isVirtual mustBe false
        (port.api in RtMidi.compiledApis()) mustBe true

        port.destroy()
    }

    @Test
    fun Open() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val port = ReadableMidiPort(info)

        val oldWritableSize = RtMidi.writableMidiPorts().size

        val portName = "Test Readable Port ${Random.nextInt()}"
        port.open(portName)

        port.isOpen mustBe true
        port.isVirtual mustBe false

        val newWritableSize = RtMidi.writableMidiPorts().size

        oldWritableSize mustNotBe newWritableSize
        oldWritableSize + 1 mustBe newWritableSize

        val foundWritable = RtMidi.writableMidiPorts().find { it.name.contains(portName) }

        foundWritable mustNotBe null

        // Calling open again should do nothing

        { port.open(portName) } mustNotThrow Throwable::class
        RtMidi.writableMidiPorts().size mustBe newWritableSize

        port.destroy()
    }

    @Test
    fun `Open Virtual`() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val port = ReadableMidiPort(info)

        val oldWritableSize = RtMidi.writableMidiPorts().size

        val portName = "Test Readable Port ${Random.nextInt()}"
        port.openVirtual(portName)

        port.isOpen mustBe true
        port.isVirtual mustBe true

        val newWritableSize = RtMidi.writableMidiPorts().size

        oldWritableSize mustNotBe newWritableSize
        oldWritableSize + 1 mustBe newWritableSize

        val foundWritable = RtMidi.writableMidiPorts().find { it.name.contains(portName) }

        foundWritable mustNotBe null

        // Calling open again should do nothing

        { port.openVirtual(portName) } mustNotThrow Throwable::class
        RtMidi.writableMidiPorts().size mustBe newWritableSize

        port.destroy()
    }

    @Test
    fun Close() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val port = ReadableMidiPort(info)

        val oldWritableSize = RtMidi.writableMidiPorts().size

        val portName = "Test Readable Port ${Random.nextInt()}"
        port.open(portName)

        port.isOpen mustBe true

        val newWritableSize = RtMidi.writableMidiPorts().size

        oldWritableSize mustNotBe newWritableSize
        oldWritableSize + 1 mustBe newWritableSize

        RtMidi.writableMidiPorts().find { it.name.contains(portName) } mustNotBe null

        port.close()

        port.isOpen mustBe false
        port.isVirtual mustBe false

        RtMidi.writableMidiPorts().size mustNotBe newWritableSize
        RtMidi.writableMidiPorts().size mustBe oldWritableSize
        RtMidi.writableMidiPorts().find { it.name.contains(portName) } mustBe null

        // Calling close again should do nothing
        { port.close() } mustNotThrow Throwable::class

        // Reopening should still work
        port.open(portName)

        port.isOpen mustBe true

        RtMidi.writableMidiPorts().size mustBe newWritableSize
        RtMidi.writableMidiPorts().find { it.name.contains(portName) } mustNotBe null

        port.destroy()
    }

    @Test
    fun Destroy() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val portName = "Test Readable Port ${Random.nextInt()}"

        val port = ReadableMidiPort(info)

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
                { port.ignoreTypes(false, false, false) }) mustThrow RtMidiPortException::class

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
    fun `Set Callback`() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val readablePort = ReadableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        val readablePortName = "Test Writable Port ${Random.nextInt()}"

        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }

        foundWritableInfo mustNotBe null
        require(foundWritableInfo != null) // for smart cast

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        val writablePort = WritableMidiPort(foundWritableInfo)
        writablePort.open(writablePortName)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        writablePort.sendMessage(midiMessage)

        wait(200)

        receivedMessage.data mustBe midiMessage.data
        writablePort.midiMessage mustBe midiMessage
        readablePort.midiMessage mustBe writablePort.midiMessage

        writablePort.destroy()
        readablePort.destroy()
    }

    @Test
    fun `Set Callback On Invalid Port`() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val readablePort = ReadableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        val readablePortName = "Test Writable Port ${Random.nextInt()}"

        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }

        foundWritableInfo mustNotBe null
        require(foundWritableInfo != null) // for smart cast

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        val writablePort = WritableMidiPort(foundWritableInfo)
        writablePort.open(writablePortName)
        writablePort.destroy()
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        wait(200)

        receivedMessage.data mustNotBe midiMessage.data
        writablePort.midiMessage mustNotBe midiMessage
        readablePort.midiMessage mustNotBe writablePort.midiMessage

        readablePort.destroy()
    }

    @Test
    fun `Ignore Types`() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val readablePort = ReadableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.TIMING_CLOCK))

        val readablePortName = "Test Writable Port ${Random.nextInt()}"

        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }

        foundWritableInfo mustNotBe null
        require(foundWritableInfo != null) // for smart cast

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        val writablePort = WritableMidiPort(foundWritableInfo)
        writablePort.open(writablePortName)

        readablePort.ignoreTypes(false, false, false)

        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        writablePort.sendMessage(midiMessage)

        wait(200)

        // Messages get resized, just ensure they are equal until the smallest one of them

        var size = min(receivedMessage.size, midiMessage.size)
        Arrays.equals(receivedMessage.data, 0, size,
                midiMessage.data, 0, size) mustBe true

        readablePort.midiMessage mustNotBe null
        writablePort.midiMessage mustNotBe null

        size = min(readablePort.midiMessage!!.size, writablePort.midiMessage!!.size)
        Arrays.equals(readablePort.midiMessage!!.data, 0, size,
                writablePort.midiMessage!!.data, 0, size) mustBe true

        readablePort.ignoreTypes(true, true, true)

        receivedMessage.size = 1
        receivedMessage.setData(byteArrayOf(0))

        writablePort.sendMessage(midiMessage)

        wait(200)

        size = min(receivedMessage.size, midiMessage.size)
        Arrays.equals(receivedMessage.data, 0, size,
                midiMessage.data, 0, size) mustBe false

        receivedMessage.data mustBe byteArrayOf(0)

        writablePort.destroy()
        readablePort.destroy()
    }

    @Test
    fun `Open Port After Info Index Change`() {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() mustBe true
        val readableInfo = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() mustBe true

        val readablePorts = (0..10).map { ReadableMidiPort(readableInfo) }
        readablePorts.onEach { it.open("Port: ${Random.nextInt()}") }


        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.size mustBeGreaterThan 10

        val writableInfo = allWritableInfos.last() // The very last readablePort we made above
        val writablePorts = (0..10).map { WritableMidiPort(writableInfo) }
        writablePorts.onEach {
            { it.open("Port: ${Random.nextInt()}") } mustNotThrow Throwable::class
        }

        val lastReadablePort = readablePorts.last()

        readablePorts.filterNot { it == lastReadablePort }.onEach { it.destroy() }

        val portName = "Test Readable Port ${Random.nextInt()}"

        writablePorts.onEach {
            { it.close() } mustNotThrow Throwable::class
            { it.open(portName) } mustNotThrow Throwable::class
        }

        lastReadablePort.destroy()

        writablePorts.onEach {
            { it.close() } mustNotThrow Throwable::class
            { it.open(portName) } mustThrow RtMidiPortException::class
        }

        writablePorts.onEach { it.destroy() }
        readablePorts.onEach { it.destroy() }
    }

}