package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.allShouldNotThrow
import dev.basshelal.jrtmidi.allShouldThrow
import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.lib.RtMidiBuild
import dev.basshelal.jrtmidi.wait
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.min
import kotlin.random.Random

/** Tests [ReadableMidiPort] including its supertype [MidiPort] */
internal class ReadableMidiPortTestOld : StringSpec({

    fun supportsVirtualPorts(testCase: TestCase): Boolean {
        return RtMidi.supportsVirtualPorts().also {
            if (!it) System.err.println("Platform ${RtMidiBuild.platformName} does not support virtual ports\n" +
                    "Cannot run test: ${testCase.source.fileName} ${testCase.displayName}")
        }
    }

    val randomNumber: Int = Random.nextInt(from = 0, until = 100)

    val virtualPortName = "Test virtual port: $randomNumber"

    lateinit var virtualPort: WritableMidiPort

    beforeSpec {
        defaultBeforeAll()
        if (RtMidi.supportsVirtualPorts()) {
            virtualPort = WritableMidiPort(clientName = "Test Client")
            virtualPort.openVirtual(virtualPortName)
        } else if (!RtMidi.supportsVirtualPorts() && RtMidi.writableMidiPorts().isNotEmpty()) {
            virtualPort = WritableMidiPort(clientName = "Test Client", portInfo = RtMidi.writableMidiPorts().first())
            virtualPort.open(virtualPortName)
        } else throw RuntimeException("""Unable to run tests!
            |Platform: ${RtMidiBuild.platformName} does not support virtual ports AND no writable Midi Ports were found
            |To test this platform, please connect some physical Midi devices""".trimMargin())
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
        val testClientName = "Test Client $randomNumber"
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
        val testClientName = "Test Client $randomNumber"
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
        val testClientName = "Test Client $randomNumber"
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
            isOpen shouldBe false
            val oldWritableSize = RtMidi.writableMidiPorts().size

            // Opening should throw exception and change no state
            shouldThrow<RtMidiPortException> { open("My test port") }
            isOpen shouldBe false
            RtMidi.writableMidiPorts().size shouldBe oldWritableSize
        }
    }

    "Open With Info" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val portInfo = allReadableInfos.first()
        val testClientName = "Test Client $randomNumber"

        ReadableMidiPort(clientName = testClientName, portInfo = portInfo).apply {
            val oldWritableSize = RtMidi.writableMidiPorts().size
            val portName = "Test Readable Port $randomNumber"

            open(portName)

            isOpen shouldBe true
            isVirtual shouldBe false

            val newWritableSize = RtMidi.writableMidiPorts().size

            newWritableSize shouldBe oldWritableSize + 1

            val foundWritable = RtMidi.writableMidiPorts().find {
                it.name.contains(testClientName) && it.name.contains(portName)
            }
            foundWritable shouldNotBe null

            // Calling open again should do nothing

            shouldNotThrowAny { open(portName) }
            RtMidi.writableMidiPorts().size shouldBe newWritableSize
        }.destroy()
    }

    "Open Virtual".config(enabledIf = { supportsVirtualPorts(it) }) {

        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val portInfo = allReadableInfos.first()
        val testClientName = "Test Client $randomNumber"

        ReadableMidiPort(clientName = testClientName, portInfo = portInfo).apply {
            val oldWritableSize = RtMidi.writableMidiPorts().size
            val portName = "Test Readable Port $randomNumber"

            openVirtual(portName)

            isOpen shouldBe true
            isVirtual shouldBe true

            val newWritableSize = RtMidi.writableMidiPorts().size

            newWritableSize shouldBe oldWritableSize + 1

            val foundWritable = RtMidi.writableMidiPorts().find {
                it.name.contains(testClientName) && it.name.contains(portName)
            }
            foundWritable shouldNotBe null

            // Calling open again should do nothing

            shouldNotThrowAny { open(portName) }
            isVirtual shouldBe true
            shouldNotThrowAny { openVirtual(portName) }
            RtMidi.writableMidiPorts().size shouldBe newWritableSize
        }.destroy()
    }

    "Close" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isEmpty() shouldBe false
        val portInfo = allReadableInfos.first()
        val testClientName = "Test Client $randomNumber"

        ReadableMidiPort(clientName = testClientName, portInfo = portInfo).apply {
            val oldWritableSize = RtMidi.writableMidiPorts().size
            val portName = "Test Readable Port $randomNumber"

            open(portName)

            isOpen shouldBe true

            val newWritableSize = RtMidi.writableMidiPorts().size

            newWritableSize shouldBe oldWritableSize + 1

            RtMidi.writableMidiPorts().find {
                it.name.contains(testClientName) && it.name.contains(portName)
            } shouldNotBe null

            close()

            isOpen shouldBe false
            isVirtual shouldBe false

            RtMidi.writableMidiPorts().size shouldNotBe newWritableSize
            RtMidi.writableMidiPorts().size shouldBe oldWritableSize
            RtMidi.writableMidiPorts().find {
                it.name.contains(testClientName) && it.name.contains(portName)
            } shouldBe null

            // Calling close again should do nothing
            shouldNotThrowAny { close() }

            // Reopening should still work
            open(portName)

            isOpen shouldBe true

            RtMidi.writableMidiPorts().size shouldBe newWritableSize
            RtMidi.writableMidiPorts().find {
                it.name.contains(testClientName) && it.name.contains(portName)
            } shouldNotBe null
        }.destroy()
    }

    "Destroy" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val portInfo = allReadableInfos.first()
        val testClientName = "Test Client $randomNumber"

        ReadableMidiPort(clientName = testClientName, portInfo = portInfo).apply {
            isDestroyed shouldBe false

            destroy()

            isDestroyed shouldBe true

            // Destroying again should do nothing
            shouldNotThrowAny { destroy() }
            isDestroyed shouldBe true

            val portName = "Test Readable Port $randomNumber"

            // doing anything else will throw RtMidiPortExceptions
            allShouldThrow<RtMidiPortException>(
                    listOf({ open(portName) },
                            { openVirtual(portName) },
                            { close() },
                            { ignoreTypes(midiSysex = false, midiTime = false, midiSense = false) })
            )

            // accessing variables though will not
            allShouldNotThrow<Throwable>(
                    listOf({ isOpen },
                            { isDestroyed },
                            { clientName },
                            { api },
                            { info },
                            { isVirtual },
                            { toString() })
            )
        }
    }

    "Set Callback" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val portInfo = allReadableInfos.first()

        val readablePort = ReadableMidiPort(portInfo = portInfo)

        val receivedMessage = MidiMessage()
        val messageToSend = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69))

        val readablePortName = "Test Readable Port $randomNumber"
        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }
        foundWritableInfo shouldNotBe null

        val writablePortName = "Test Writable Port $randomNumber"

        val writablePort = WritableMidiPort(portInfo = foundWritableInfo)
        writablePort.open(writablePortName)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        writablePort.sendMessage(messageToSend)

        wait(200) // give some time to receive message

        receivedMessage.data shouldBe messageToSend.data
        writablePort.midiMessage shouldBe messageToSend
        readablePort.midiMessage shouldBe writablePort.midiMessage

        writablePort.destroy()
        readablePort.destroy()
    }

    "Set Callback On Invalid Port" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val readablePortInfo = allReadableInfos.first()

        val readablePort = ReadableMidiPort(portInfo = readablePortInfo)

        val receivedMessage = MidiMessage()
        val messageToSend = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69))

        val readablePortName = "Test Writable Port $randomNumber"
        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }
        foundWritableInfo shouldNotBe null

        val writablePortName = "Test Writable Port $randomNumber"

        val writablePort = WritableMidiPort(foundWritableInfo)
        writablePort.open(writablePortName)
        writablePort.destroy()
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        wait(200) // give some time to receive message

        receivedMessage.data shouldNotBe messageToSend.data
        writablePort.midiMessage shouldNotBe messageToSend
        readablePort.midiMessage shouldNotBe writablePort.midiMessage

        readablePort.destroy()
    }

    "Ignore Types" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val info = allReadableInfos.first()

        val readablePort = ReadableMidiPort(info)

        val receivedMessage = MidiMessage()
        val midiMessage = MidiMessage(byteArrayOf(MidiMessage.TIMING_CLOCK))

        val readablePortName = "Test Writable Port $randomNumber"
        readablePort.open(readablePortName)

        val foundWritableInfo = RtMidi.writableMidiPorts().find { it.name.contains(readablePortName) }
        foundWritableInfo shouldNotBe null

        val writablePortName = "Test Writable Port $randomNumber"

        val writablePort = WritableMidiPort(foundWritableInfo)
        writablePort.open(writablePortName)

        readablePort.ignoreTypes(midiSysex = false, midiTime = false, midiSense = false)
        readablePort.setCallback(MidiMessageCallback { message: MidiMessage ->
            receivedMessage.setDataFrom(message)
        })

        writablePort.sendMessage(midiMessage)

        wait(200) // give some time to receive message

        // Messages get resized, just ensure they are equal until the smallest one of them

        var size = min(receivedMessage.size, midiMessage.size)
        receivedMessage.data.copyOf(size) shouldBe midiMessage.data.copyOf(size)

        readablePort.midiMessage shouldNotBe null
        writablePort.midiMessage shouldNotBe null

        size = min(readablePort.midiMessage!!.size, writablePort.midiMessage!!.size)
        readablePort.midiMessage!!.data.copyOf(size) shouldBe writablePort.midiMessage!!.data.copyOf(size)

        readablePort.ignoreTypes(midiSysex = true, midiTime = true, midiSense = true)

        receivedMessage.size = 1
        receivedMessage.setData(byteArrayOf(0))

        writablePort.sendMessage(midiMessage)

        wait(200) // give some time to receive message

        size = min(receivedMessage.size, midiMessage.size)
        receivedMessage.data.copyOf(size) shouldNotBe midiMessage.data.copyOf(size)

        receivedMessage.data shouldBe byteArrayOf(0)

        writablePort.destroy()
        readablePort.destroy()
    }

    "Open Port After Info Index Change" {
        val allReadableInfos = RtMidi.readableMidiPorts()
        allReadableInfos.isNotEmpty() shouldBe true
        val readableInfo = allReadableInfos.first()

        val readablePorts = (0..10).map { ReadableMidiPort(readableInfo) }
        readablePorts.onEach { it.open("Port: $randomNumber") }

        val allWritableInfos = RtMidi.writableMidiPorts()
        allWritableInfos.size shouldBeGreaterThan 10

        val writableInfo = allWritableInfos.last() // The very last readablePort we made above
        val writablePorts = (0..10).map { WritableMidiPort(writableInfo) }
        writablePorts.onEach {
            shouldNotThrowAny { it.open("Port: $randomNumber") }
        }

        val lastReadablePort = readablePorts.last()

        readablePorts.filterNot { it == lastReadablePort }.onEach { it.destroy() }

        val portName = "Test Readable Port $randomNumber"

        writablePorts.onEach {
            shouldNotThrowAny { it.close() }
            shouldNotThrowAny { it.open(portName) }
        }

        lastReadablePort.destroy()

        writablePorts.onEach {
            shouldNotThrowAny { it.close() }
            shouldThrow<RtMidiPortException> { it.open(portName) }
        }

        writablePorts.onEach { it.destroy() }
        readablePorts.onEach { it.destroy() }
    }

})