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
import kotlin.random.Random

/** Tests [WritableMidiPort] including its supertype [MidiPort] */
internal class WritableMidiPortTest : StringSpec({

    beforeSpec { defaultBeforeAll() }

    afterSpec { }

    "Info Constructor" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val port = WritableMidiPort(info)
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
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true
        val api = allApis.first()
        val clientName = "Test Client ${Random.nextInt()}"
        val port = WritableMidiPort(info, clientName, api)
        port.info shouldBe info
        port.api shouldBe api
        port.clientName shouldBe clientName
        port.isDestroyed shouldBe false
        port.isOpen shouldBe false
        port.isVirtual shouldBe false

        port.destroy()
    }

    "No API Constructor" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()

        val clientName = "Test Client ${Random.nextInt()}"
        val port = WritableMidiPort(portInfo = info, clientName = clientName)
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
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val port = WritableMidiPort(info)

        val oldReadableSize = RtMidi.readableMidiPorts().size

        val portName = "Test Writable Port ${Random.nextInt()}"
        port.open(portName)

        port.isOpen shouldBe true
        port.isVirtual shouldBe false

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize shouldNotBe newReadableSize
        oldReadableSize + 1 shouldBe newReadableSize

        val foundReadable = RtMidi.readableMidiPorts().find { it.name.contains(portName) }

        foundReadable shouldNotBe null

        // Calling open again should do nothing

        shouldNotThrowAny { port.open(portName) }
        RtMidi.readableMidiPorts().size shouldBe newReadableSize

        port.destroy()
    }

    "Open Virtual" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val port = WritableMidiPort(info)

        val oldReadableSize = RtMidi.readableMidiPorts().size

        val portName = "Test Writable Port ${Random.nextInt()}"
        port.openVirtual(portName)

        port.isOpen shouldBe true
        port.isVirtual shouldBe true

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize shouldNotBe newReadableSize
        oldReadableSize + 1 shouldBe newReadableSize

        val foundReadable = RtMidi.readableMidiPorts().find { it.name.contains(portName) }

        foundReadable shouldNotBe null

        // Calling openVirtual again should do nothing

        shouldNotThrowAny { port.openVirtual(portName) }
        RtMidi.readableMidiPorts().size shouldBe newReadableSize

        port.destroy()
    }

    "Close" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val port = WritableMidiPort(info)

        val oldReadableSize = RtMidi.readableMidiPorts().size

        val portName = "Test Writable Port ${Random.nextInt()}"
        port.open(portName)

        port.isOpen shouldBe true

        val newReadableSize = RtMidi.readableMidiPorts().size

        oldReadableSize shouldNotBe newReadableSize
        oldReadableSize + 1 shouldBe newReadableSize

        RtMidi.readableMidiPorts().find { it.name.contains(portName) } shouldNotBe null

        port.close()

        port.isOpen shouldBe false
        port.isVirtual shouldBe false

        RtMidi.readableMidiPorts().size shouldNotBe newReadableSize
        RtMidi.readableMidiPorts().size shouldBe oldReadableSize
        RtMidi.readableMidiPorts().find { it.name.contains(portName) } shouldBe null

        // Calling close again should do nothing
        shouldNotThrowAny { port.close() }

        // Reopening should still work
        port.open(portName)

        port.isOpen shouldBe true

        RtMidi.readableMidiPorts().size shouldBe newReadableSize
        RtMidi.readableMidiPorts().find { it.name.contains(portName) } shouldNotBe null

        port.destroy()
    }

    "Destroy" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val portName = "Test Writable Port ${Random.nextInt()}"

        val port = WritableMidiPort(info)

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
                        { port.sendMessage(MidiMessage()) })
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

    "Send Message" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val writablePort = WritableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        writablePort.open(writablePortName)

        val foundReadableInfo = RtMidi.readableMidiPorts().find { it.name.contains(writablePortName) }

        foundReadableInfo shouldNotBe null
        require(foundReadableInfo != null) // for smart cast

        val readablePortName = "Test Readable Port ${Random.nextInt()}"

        val readablePort = ReadableMidiPort(foundReadableInfo)
        readablePort.open(readablePortName)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setData(message)
        })

        writablePort.sendMessage(midiMessage)

        wait(200)

        receivedMessage.data shouldBe midiMessage.data
        readablePort.midiMessage shouldBe midiMessage
        writablePort.midiMessage shouldBe readablePort.midiMessage

        readablePort.destroy()
        writablePort.destroy()
    }

    "Send Message To Invalid Port" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val info = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val writablePort = WritableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69));

        // sending without opening should do nothing
        shouldNotThrowAny { writablePort.sendMessage(midiMessage) }
        writablePort.midiMessage shouldBe null

        val writablePortName = "Test Writable Port ${Random.nextInt()}"

        writablePort.open(writablePortName)

        val foundReadableInfo = RtMidi.readableMidiPorts().find { it.name.contains(writablePortName) }

        foundReadableInfo shouldNotBe null
        require(foundReadableInfo != null) // for smart cast

        val readablePortName = "Test Readable Port ${Random.nextInt()}"

        val readablePort = ReadableMidiPort(foundReadableInfo)
        readablePort.open(readablePortName)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setData(message)
        })

        // close and try to send
        writablePort.close()
        val newMessage = MidiMessage(0)
        writablePort.sendMessage(newMessage)

        wait(200)

        writablePort.midiMessage?.data shouldNotBe newMessage.data
        receivedMessage.data shouldNotBe newMessage.data

        // try to send to a destroyed readable port
        writablePort.open(writablePortName)
        readablePort.destroy()

        newMessage.setData(byteArrayOf(1, 2, 3))

        writablePort.sendMessage(newMessage)

        wait(200)

        readablePort.midiMessage?.data shouldNotBe newMessage.data

        // even though the receiving end does not exist anymore, we can't know that so the message gets
        // "sent", so port gets a new midiMessage because sending sent nowhere but was not unsuccessful
        writablePort.midiMessage?.data shouldBe newMessage.data

        readablePort.destroy()
        writablePort.destroy()
    }

    "Open Port After Info Index Change" {
        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.isNotEmpty() shouldBe true
        val writableInfo = allWritableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val writablePorts = (0..10).map { WritableMidiPort(writableInfo) }
        writablePorts.onEach { it.open("Port: ${Random.nextInt()}") }


        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.size shouldBeGreaterThan 10

        val readableInfo = allReadableInfos.last() // The very last writablePort we made above
        val readablePorts = (0..10).map { ReadableMidiPort(readableInfo) }
        readablePorts.onEach {
            shouldNotThrowAny { it.open("Port: ${Random.nextInt()}") }
        }

        val lastWritablePort = writablePorts.last()

        writablePorts.filterNot { it == lastWritablePort }.onEach { it.destroy() }

        val portName = "Test Writable Port ${Random.nextInt()}"

        readablePorts.onEach {
            shouldNotThrowAny { it.close() }
            shouldNotThrowAny { it.open(portName) }
        }

        lastWritablePort.destroy()

        readablePorts.onEach {
            shouldNotThrowAny { it.close() }
            shouldThrow<RtMidiPortException> { it.open(portName) }
        }

        readablePorts.onEach { it.destroy() }
        writablePorts.onEach { it.destroy() }
    }

}) 