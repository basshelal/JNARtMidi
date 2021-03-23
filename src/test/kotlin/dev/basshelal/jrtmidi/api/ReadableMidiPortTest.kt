package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.allShouldNotThrow
import dev.basshelal.jrtmidi.allShouldThrow
import dev.basshelal.jrtmidi.defaultBeforeAll
import dev.basshelal.jrtmidi.lib.RtMidiBuild
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random

private fun supportsVirtualPorts(testCase: TestCase): Boolean {
    return RtMidi.supportsVirtualPorts().also {
        if (!it) System.err.println("Platform ${RtMidiBuild.platformName} does not support virtual ports\n" +
                "Cannot run test: ${testCase.source.fileName} ${testCase.displayName}")
    }
}

private val randomNumber: Int get() = Random.nextInt(from = 0, until = 1000)
private val emptyMidiMessageCallback: MidiMessageCallback = MidiMessageCallback { _ -> }

private val readableMidiPortInfos: List<MidiPort.Info> get() = RtMidi.readableMidiPorts()
private val writableMidiPortInfos: List<MidiPort.Info> get() = RtMidi.writableMidiPorts()

private val testWritablePortClientName = "Test Client"
private val testWritablePortName = "Test virtual port: $randomNumber"

private lateinit var testWritablePort: WritableMidiPort
private lateinit var compiledApis: List<RtMidiApi>

/** Tests [ReadableMidiPort] including its supertype [MidiPort] */
internal class ReadableMidiPortTest : StringSpec({

    beforeSpec {
        defaultBeforeAll()
        if (RtMidi.supportsVirtualPorts()) {
            testWritablePort = WritableMidiPort(clientName = testWritablePortClientName)
            testWritablePort.openVirtual(testWritablePortName)
            compiledApis = RtMidi.compiledApis()
        } else if (!RtMidi.supportsVirtualPorts() && RtMidi.writableMidiPorts().isNotEmpty()) {
            testWritablePort = WritableMidiPort(clientName = testWritablePortClientName, portInfo = RtMidi.writableMidiPorts().first())
            testWritablePort.open(testWritablePortName)
            compiledApis = RtMidi.compiledApis()
        } else throw RuntimeException("""Unable to run tests!
            |Platform: ${RtMidiBuild.platformName} does not support virtual ports AND no writable Midi Ports were found
            |To test this platform, please connect some physical Midi devices""".trimMargin())
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
        readableMidiPortInfos.isEmpty() shouldBe false
        val portInfo = readableMidiPortInfos.first()
        ReadableMidiPort(portInfo).apply {
            info shouldBe portInfo
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
        readableMidiPortInfos.isEmpty() shouldBe false
        val portInfo = readableMidiPortInfos.first()
        val testClientName = "Test Client $randomNumber"
        ReadableMidiPort(portInfo = portInfo, clientName = testClientName).apply {
            info shouldBe portInfo
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
        readableMidiPortInfos.isEmpty() shouldBe false
        val portInfo = readableMidiPortInfos.first()
        compiledApis.isEmpty() shouldBe false
        val testApi = compiledApis.first()
        val testClientName = "Test Client $randomNumber"
        ReadableMidiPort(portInfo = portInfo, clientName = testClientName, api = testApi).apply {
            info shouldBe portInfo
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

    }

    "Open With PortInfo" {

    }

    "Open Virtual".config(enabledIf = { supportsVirtualPorts(it) }) {

    }

    "Open Port After Info Index Change" {

    }

    "Close" {
        readableMidiPortInfos.isEmpty() shouldBe false
        val portInfo = readableMidiPortInfos.first()
        ReadableMidiPort(portInfo = portInfo).apply {
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
        readableMidiPortInfos.isEmpty() shouldBe false
        val portInfo = readableMidiPortInfos.first()
        ReadableMidiPort(portInfo = portInfo).apply {
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

    }

    "Set Callback On Invalid Port" {

    }

    "Ignore Types" {

    }

})