package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.allShouldNotThrow
import dev.basshelal.jrtmidi.allShouldThrow
import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.wait
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.Arrays
import kotlin.math.min
import kotlin.random.Random

/** Tests [ReadableMidiPort] including its supertype [MidiPort] */
internal class ReadableMidiPortTest : StringSpec({

    beforeSpec { defaultBeforeAll() }

    afterSpec { }

    "Info Constructor" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val port = ReadableMidiPort(info)
        port.info shouldBe info
        port.api shouldNotBe RtMidiApi.UNSPECIFIED
        port.clientName shouldBe null
        port.isDestroyed shouldBe false
        port.isOpen shouldBe false
        port.isVirtual shouldBe false
        (port.api in RtMidi.compiledApis()) shouldBe true

        port.destroy()
    }

    "Full Constructor" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true
        val api = allApis.first()
        val clientName = "Test Client ${Random.nextInt()}"
        val port = ReadableMidiPort(info, clientName, api)
        port.info shouldBe info
        port.api shouldBe api
        port.clientName shouldBe clientName
        port.isDestroyed shouldBe false
        port.isOpen shouldBe false
        port.isVirtual shouldBe false

        port.destroy()
    }

    "No API Constructor" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()

        val clientName = "Test Client ${Random.nextInt()}"
        val port = ReadableMidiPort(portInfo = info, clientName = clientName)
        port.info shouldBe info
        port.api shouldNotBe RtMidiApi.UNSPECIFIED
        port.clientName shouldBe clientName
        port.isDestroyed shouldBe false
        port.isOpen shouldBe false
        port.isVirtual shouldBe false
        (port.api in RtMidi.compiledApis()) shouldBe true

        port.destroy()
    }

    "Open" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val port = ReadableMidiPort(info)

        val oldWritableSize = RtMidi.writableMidiPorts().size

        val portName = "Test Readable Port ${Random.nextInt()}"
        port.open(portName)

        port.isOpen shouldBe true
        port.isVirtual shouldBe false

        val newWritableSize = RtMidi.writableMidiPorts().size

        oldWritableSize shouldNotBe newWritableSize
        oldWritableSize + 1 shouldBe newWritableSize

        val foundWritable = RtMidi.writableMidiPorts().find { it.name.contains(portName) }

        foundWritable shouldNotBe null

        // Calling open again should do nothing

        shouldNotThrowAny { port.open(portName) }
        RtMidi.writableMidiPorts().size shouldBe newWritableSize

        port.destroy()
    }

    "Open Virtual" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val port = ReadableMidiPort(info)

        val oldWritableSize = RtMidi.writableMidiPorts().size

        val portName = "Test Readable Port ${Random.nextInt()}"
        port.openVirtual(portName)

        port.isOpen shouldBe true
        port.isVirtual shouldBe true

        val newWritableSize = RtMidi.writableMidiPorts().size

        oldWritableSize shouldNotBe newWritableSize
        oldWritableSize + 1 shouldBe newWritableSize

        val foundWritable = RtMidi.writableMidiPorts().find { it.name.contains(portName) }

        foundWritable shouldNotBe null

        // Calling open again should do nothing

        shouldNotThrowAny {
            port.openVirtual(portName)
        }
        RtMidi.writableMidiPorts().size shouldBe newWritableSize

        port.destroy()
    }

    "Close" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val port = ReadableMidiPort(info)

        val oldWritableSize = RtMidi.writableMidiPorts().size

        val portName = "Test Readable Port ${Random.nextInt()}"
        port.open(portName)

        port.isOpen shouldBe true

        val newWritableSize = RtMidi.writableMidiPorts().size

        oldWritableSize shouldNotBe newWritableSize
        oldWritableSize + 1 shouldBe newWritableSize

        RtMidi.writableMidiPorts().find { it.name.contains(portName) } shouldNotBe null

        port.close()

        port.isOpen shouldBe false
        port.isVirtual shouldBe false

        RtMidi.writableMidiPorts().size shouldNotBe newWritableSize
        RtMidi.writableMidiPorts().size shouldBe oldWritableSize
        RtMidi.writableMidiPorts().find { it.name.contains(portName) } shouldBe null

        // Calling close again should do nothing
        shouldNotThrowAny { port.close() }

        // Reopening should still work
        port.open(portName)

        port.isOpen shouldBe true

        RtMidi.writableMidiPorts().size shouldBe newWritableSize
        RtMidi.writableMidiPorts().find { it.name.contains(portName) } shouldNotBe null

        port.destroy()
    }

    "Destroy" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val portName = "Test Readable Port ${Random.nextInt()}"

        val port = ReadableMidiPort(info)

        port.isDestroyed shouldBe false

        port.destroy()

        port.isDestroyed shouldBe true

        // Destroying again should do nothing
        shouldNotThrowAny { port.destroy() }
        port.isDestroyed shouldBe true

        // doing anything else will throw RtMidiPortExceptions
        allShouldThrow<RtMidiPortException>(
                listOf({ port.open(portName) },
                        { port.openVirtual(portName) },
                        { port.close() },
                        { port.ignoreTypes(false, false, false) })
        )

        // accessing variables though will not
        allShouldNotThrow<Throwable>(
                listOf({ port.isOpen },
                        { port.isDestroyed },
                        { port.clientName },
                        { port.api },
                        { port.info },
                        { port.isVirtual },
                        { port.toString() })
        )
    }

    "Set Callback" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val readablePort = ReadableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        val readablePortName = "Test Writable Port ${Random.nextInt()}"

        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }

        foundWritableInfo shouldNotBe null
        require(foundWritableInfo != null) // for smart cast

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        val writablePort = WritableMidiPort(foundWritableInfo)
        writablePort.open(writablePortName)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        writablePort.sendMessage(midiMessage)

        wait(200)

        receivedMessage.data shouldBe midiMessage.data
        writablePort.midiMessage shouldBe midiMessage
        readablePort.midiMessage shouldBe writablePort.midiMessage

        writablePort.destroy()
        readablePort.destroy()
    }

    "Set Callback On Invalid Port" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val readablePort = ReadableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        val readablePortName = "Test Writable Port ${Random.nextInt()}"

        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }

        foundWritableInfo shouldNotBe null
        require(foundWritableInfo != null) // for smart cast

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        val writablePort = WritableMidiPort(foundWritableInfo)
        writablePort.open(writablePortName)
        writablePort.destroy()
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        wait(200)

        receivedMessage.data shouldNotBe midiMessage.data
        writablePort.midiMessage shouldNotBe midiMessage
        readablePort.midiMessage shouldNotBe writablePort.midiMessage

        readablePort.destroy()
    }

    "Ignore Types" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val readablePort = ReadableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.TIMING_CLOCK))

        val readablePortName = "Test Writable Port ${Random.nextInt()}"

        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }

        foundWritableInfo shouldNotBe null
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
                midiMessage.data, 0, size) shouldBe true

        readablePort.midiMessage shouldNotBe null
        writablePort.midiMessage shouldNotBe null

        size = min(readablePort.midiMessage!!.size, writablePort.midiMessage!!.size)
        Arrays.equals(readablePort.midiMessage!!.data, 0, size,
                writablePort.midiMessage!!.data, 0, size) shouldBe true

        readablePort.ignoreTypes(true, true, true)

        receivedMessage.size = 1
        receivedMessage.setData(byteArrayOf(0))

        writablePort.sendMessage(midiMessage)

        wait(200)

        size = min(receivedMessage.size, midiMessage.size)
        Arrays.equals(receivedMessage.data, 0, size,
                midiMessage.data, 0, size) shouldBe false

        receivedMessage.data shouldBe byteArrayOf(0)

        writablePort.destroy()
        readablePort.destroy()
    }

    "Open Port After Info Index Change" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val readableInfo = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val readablePorts = (0..10).map { ReadableMidiPort(readableInfo) }
        readablePorts.onEach { it.open("Port: ${Random.nextInt()}") }


        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.size shouldBeGreaterThan 10

        val writableInfo = allWritableInfos.last() // The very last readablePort we made above
        val writablePorts = (0..10).map { WritableMidiPort(writableInfo) }
        writablePorts.onEach {
            shouldNotThrowAny { it.open("Port: ${Random.nextInt()}") }
        }

        val lastReadablePort = readablePorts.last()

        readablePorts.filterNot { it == lastReadablePort }.onEach { it.destroy() }

        val portName = "Test Readable Port ${Random.nextInt()}"

        writablePorts.onEach {
            shouldNotThrowAny { it.close() }
            shouldNotThrowAny { it.open(portName) }
        }

        lastReadablePort.destroy()

        writablePorts.onEach {
            shouldNotThrowAny { it.close() }
            shouldThrow<RtMidiPortException> {
                it.open(portName)
            }
        }

        writablePorts.onEach { it.destroy() }
        readablePorts.onEach { it.destroy() }
    }

})