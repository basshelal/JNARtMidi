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

    // TODO: 21/03/2021 To be 0 physical port safe we need to create virtual ports to test on
    //  this will mean though that testing on Windows will require some physical ports :/

    val randomNumber: Int = Random.nextInt(from = 0, until = 100)

    val virtualPortName = "Test virtual port: ${randomNumber}"

    lateinit var virtualPort: WritableMidiPort

    beforeSpec {
        defaultBeforeAll()
        virtualPort = WritableMidiPort(clientName = "Test Client")
        virtualPort.openVirtual(virtualPortName)
    }

    afterSpec {
        virtualPort.destroy()
    }

    "Empty Constructor" {
        ReadableMidiPort().apply {
            info shouldBe null
            api shouldNotBe RtMidiApi.UNSPECIFIED
            clientName shouldBe null
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            (api in RtMidi.compiledApis()) shouldBe true
        }.destroy()
    }

    "Info only Constructor" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isEmpty() shouldBe false
        val portInfo = allReadableInfos.first()
        ReadableMidiPort(portInfo).apply {
            info shouldBe portInfo
            api shouldNotBe RtMidiApi.UNSPECIFIED
            clientName shouldBe null
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            (api in RtMidi.compiledApis()) shouldBe true
        }.destroy()
    }

    "Client Name only Constructor" {
        val testClientName = "Test Client ${randomNumber}"
        ReadableMidiPort(clientName = testClientName).apply {
            info shouldBe null
            api shouldNotBe RtMidiApi.UNSPECIFIED
            clientName shouldBe testClientName
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            (api in RtMidi.compiledApis()) shouldBe true
        }.destroy()
    }

    "API only Constructor" {
        val apis = RtMidi.compiledApis()
        apis.isEmpty() shouldBe false
        val testApi = apis.first()
        ReadableMidiPort(api = testApi).apply {
            info shouldBe null
            api shouldNotBe RtMidiApi.UNSPECIFIED
            api shouldBe testApi
            clientName shouldBe null
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            (api in RtMidi.compiledApis()) shouldBe true
        }.destroy()
    }

    "Info & Client Name Constructor" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isEmpty() shouldBe false
        val portInfo = allReadableInfos.first()
        val testClientName = "Test Client ${randomNumber}"
        ReadableMidiPort(portInfo = portInfo, clientName = testClientName).apply {
            info shouldBe portInfo
            api shouldNotBe RtMidiApi.UNSPECIFIED
            clientName shouldBe testClientName
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            (api in RtMidi.compiledApis()) shouldBe true
        }.destroy()
    }

    "Full Constructor" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isEmpty() shouldBe false
        val portInfo = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isEmpty() shouldBe false
        val testApi = allApis.first()
        val testClientName = "Test Client ${randomNumber}"
        ReadableMidiPort(portInfo = portInfo, clientName = testClientName, api = testApi).apply {
            info shouldBe portInfo
            api shouldBe testApi
            clientName shouldBe testClientName
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
        }.destroy()
    }

    "Open Without Info" {
        ReadableMidiPort(portInfo = null).apply {
            info shouldBe null
            shouldThrow<RtMidiPortException> { open("My test port") }
        }
    }

    // TODO: 21/03/2021 Continue below

    "Open With Info" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val portInfo = allReadableInfos.first()
        val allApis = RtMidi.compiledApis()
        allApis.isNotEmpty() shouldBe true

        val port = ReadableMidiPort(portInfo)

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