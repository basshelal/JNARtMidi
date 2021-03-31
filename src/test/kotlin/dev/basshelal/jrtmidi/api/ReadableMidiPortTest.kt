package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.allShouldNotThrow
import dev.basshelal.jrtmidi.allShouldThrow
import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.lib.RtMidiBuild
import dev.basshelal.jrtmidi.wait
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random

private fun supportsVirtualPorts(testCase: TestCase): Boolean {
    return RtMidi.supportsVirtualPorts().also {
        if (!it) System.err.println("Platform ${RtMidiBuild.platformName} does not support virtual ports\n" +
                "Cannot run test: ${testCase.source.fileName} ${testCase.displayName}")
    }
}

private fun waitToReceiveMessage() = wait(15)

private val randomNumber: Int get() = Random.nextInt(from = 0, until = 1000)
private val emptyMidiMessageCallback: MidiMessageCallback = MidiMessageCallback { _ -> }

private val readableMidiPortInfos: List<MidiPort.Info> get() = RtMidi.readableMidiPorts()
private val writableMidiPortInfos: List<MidiPort.Info> get() = RtMidi.writableMidiPorts()

private const val testWritablePortClientName = "Test Client"
private val testWritablePortName = "Test virtual port: $randomNumber"

private lateinit var testWritablePort: WritableMidiPort
private lateinit var compiledApis: List<RtMidiApi>

private val testPortInfo: MidiPort.Info? get() = readableMidiPortInfos.find { it.name.contains(testWritablePortName) }

/** Tests [ReadableMidiPort] including its supertype [MidiPort] */
internal class ReadableMidiPortTest : StringSpec({

    beforeSpec {
        defaultBeforeAll()
        when {
            RtMidi.writableMidiPorts().isNotEmpty() -> {
                testWritablePort = WritableMidiPort(clientName = testWritablePortClientName,
                        portInfo = RtMidi.writableMidiPorts().first())
                testWritablePort.open(testWritablePortName)
                compiledApis = RtMidi.compiledApis()
            }
            RtMidi.supportsVirtualPorts() -> {
                testWritablePort = WritableMidiPort(clientName = testWritablePortClientName)
                testWritablePort.openVirtual(testWritablePortName)
                compiledApis = RtMidi.compiledApis()
            }
            else -> throw RuntimeException("""Unable to run tests!
            |No Writable Midi Ports were found AND Platform: ${RtMidiBuild.platformName} 
            |does not support virtual ports!
            |To test this platform, please connect some physical Midi (In and Out) devices""".trimMargin())
        }
    }

    afterSpec {
        testWritablePort.destroy()
    }

    "Empty Constructor" {
        ReadableMidiPort().apply {
            info shouldBe null
            api shouldNotBe RtMidiApi.UNSPECIFIED
            api shouldBeIn compiledApis
            clientName shouldBe null
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            midiMessage shouldBe null
            hasCallback shouldBe false
        }.destroy()
    }

    "PortInfo only Constructor" {
        testPortInfo.shouldNotBeNull()
        ReadableMidiPort(testPortInfo).apply {
            info shouldBe testPortInfo
            api shouldNotBe RtMidiApi.UNSPECIFIED
            api shouldBeIn compiledApis
            clientName shouldBe null
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            midiMessage shouldBe null
            hasCallback shouldBe false
        }.destroy()
    }

    "Client Name only Constructor" {
        val testClientName = "Test Client $randomNumber"
        ReadableMidiPort(clientName = testClientName).apply {
            info shouldBe null
            api shouldNotBe RtMidiApi.UNSPECIFIED
            api shouldBeIn compiledApis
            clientName shouldBe testClientName
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            midiMessage shouldBe null
            hasCallback shouldBe false
        }.destroy()
    }

    "API only Constructor" {
        compiledApis.isEmpty() shouldBe false
        val testApi = compiledApis.first()
        ReadableMidiPort(api = testApi).apply {
            info shouldBe null
            api shouldNotBe RtMidiApi.UNSPECIFIED
            api shouldBe testApi
            api shouldBeIn compiledApis
            clientName shouldBe null
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            midiMessage shouldBe null
            hasCallback shouldBe false
        }.destroy()
    }

    "PortInfo & Client Name Constructor" {
        testPortInfo.shouldNotBeNull()
        val testClientName = "Test Client $randomNumber"
        ReadableMidiPort(portInfo = testPortInfo, clientName = testClientName).apply {
            info shouldBe testPortInfo
            api shouldNotBe RtMidiApi.UNSPECIFIED
            api shouldBeIn compiledApis
            clientName shouldBe testClientName
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            midiMessage shouldBe null
            hasCallback shouldBe false
        }.destroy()
    }

    "Full Constructor" {
        testPortInfo.shouldNotBeNull()
        compiledApis.isEmpty() shouldBe false
        val testApi = compiledApis.first()
        val testClientName = "Test Client $randomNumber"
        ReadableMidiPort(portInfo = testPortInfo, clientName = testClientName, api = testApi).apply {
            info shouldBe testPortInfo
            api shouldBe testApi
            api shouldBeIn compiledApis
            clientName shouldBe testClientName
            isDestroyed shouldBe false
            isOpen shouldBe false
            isVirtual shouldBe false
            midiMessage shouldBe null
            hasCallback shouldBe false
        }.destroy()
    }

    "Open Without PortInfo" {
        ReadableMidiPort().apply {
            info shouldBe null
            isOpen shouldBe false
            shouldThrow<RtMidiPortException> { open("Test Port $randomNumber") }
            isOpen shouldBe false
        }.destroy()
    }

    "Open With PortInfo" {
        val portName = "Test Port $randomNumber"
        val foundPortNameAsWritablePort = { writableMidiPortInfos.find { it.name.contains(portName) } }
        testPortInfo.shouldNotBeNull()
        ReadableMidiPort(testPortInfo).apply {
            info shouldBe testPortInfo
            isOpen shouldBe false

            // should not find it as a writable port
            foundPortNameAsWritablePort().shouldBeNull()

            shouldNotThrow<RtMidiPortException> { open(portName) }
            isOpen shouldBe true

            if (!testWritablePort.isVirtual) {
                // should find it as a writable port now
                foundPortNameAsWritablePort().shouldNotBeNull()
            }
        }.destroy()
    }

    "Open Virtual".config(enabledIf = ::supportsVirtualPorts) {

    }

    "Open Port After Info Index Change".config(enabledIf = ::supportsVirtualPorts) {

    }

    "Close" {
        testPortInfo.shouldNotBeNull()
        ReadableMidiPort(portInfo = testPortInfo).apply {
            isOpen shouldBe false
            open("Test Port name $randomNumber")
            isOpen shouldBe true
            close()
            isOpen shouldBe false
            isDestroyed shouldBe false
            shouldNotThrowAny { close() }
            isOpen shouldBe false
            isDestroyed shouldBe false
        }.destroy()
    }

    "Destroy" {
        testPortInfo.shouldNotBeNull()
        ReadableMidiPort(portInfo = testPortInfo).apply {
            isDestroyed shouldBe false
            open("Test Port name $randomNumber")
            isOpen shouldBe true
            setCallback(emptyMidiMessageCallback)
            hasCallback shouldBe true
            destroy()
            isDestroyed shouldBe true
            isOpen shouldBe false
            hasCallback shouldBe false
            shouldNotThrowAny { destroy() }

            // functions should throw exceptions
            allShouldThrow<RtMidiPortException>(listOf(
                    { open("Test Port $randomNumber") },
                    { openVirtual("Test Port $randomNumber") },
                    { close() },
                    { setCallback(emptyMidiMessageCallback) },
                    { removeCallback() },
                    { ignoreTypes(midiSysex = false, midiTime = false, midiSense = false) }
            ))
            // querying data shouldn't throw exceptions
            allShouldNotThrow<Throwable>(listOf(
                    { api }, { info }, { midiMessage }, { clientName },
                    { isOpen }, { isVirtual }, { isDestroyed }, { hasCallback },
                    // kotlin.Any functions shouldn't throw exceptions either
                    { hashCode() }, { equals(null) }, { toString() }
            ))
        }
    }

    "Set Callback & Remove Callback" {
        testPortInfo.shouldNotBeNull()
        ReadableMidiPort(portInfo = testPortInfo).apply {
            hasCallback shouldBe false
            val messageToSend = MidiMessage(byteArrayOf(MidiMessage.NOTE_ON, 69, 69))
            val receivedMessage = MidiMessage(size = 0)
            setCallback(MidiMessageCallback { message -> receivedMessage.setData(message) })
            hasCallback shouldBe true
            open("Readable Port $randomNumber")
            isOpen shouldBe true
            testWritablePort.sendMessage(messageToSend)
            waitToReceiveMessage()
            receivedMessage shouldBe messageToSend
            midiMessage shouldBe messageToSend

            // Time to remove callback!
            receivedMessage.size = 0
            receivedMessage shouldNotBe messageToSend
            removeCallback()
            hasCallback shouldBe false
            midiMessage shouldBe null
            isOpen shouldBe true
            testWritablePort.sendMessage(messageToSend)
            waitToReceiveMessage()
            receivedMessage shouldNotBe messageToSend
            midiMessage shouldNotBe messageToSend
        }.destroy()
    }

    "Set Callback On Invalid Port" {

    }

    "Ignore Types" {
        testPortInfo.shouldNotBeNull()
        ReadableMidiPort(portInfo = testPortInfo).apply {
            val messageToSend = MidiMessage(byteArrayOf(MidiMessage.TIMING_CLOCK))
            val receivedMessage = MidiMessage(size = 0)
            ignoreTypes(midiSysex = true, midiTime = true, midiSense = true)
            setCallback(MidiMessageCallback { message -> receivedMessage.setData(message) })
            open("Readable Port $randomNumber")
            testWritablePort.sendMessage(messageToSend)
            waitToReceiveMessage()
            receivedMessage shouldNotBe messageToSend
            midiMessage shouldNotBe messageToSend
            midiMessage shouldBe null

            // don't ignore
            ignoreTypes(midiSysex = false, midiTime = false, midiSense = false)
            testWritablePort.sendMessage(messageToSend)
            waitToReceiveMessage()
            receivedMessage shouldBe messageToSend
            midiMessage shouldBe messageToSend
        }.destroy()
    }

})