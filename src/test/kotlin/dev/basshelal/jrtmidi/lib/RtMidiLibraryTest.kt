@file:Suppress("NOTHING_TO_INLINE", "unused")

package dev.basshelal.jrtmidi.lib

//import dev.basshelal.jrtmidi.api.MidiMessage
//import dev.basshelal.jrtmidi.api.RtMidi
//import dev.basshelal.jrtmidi.isLinux
//import dev.basshelal.jrtmidi.isMacOs
//import dev.basshelal.jrtmidi.isWindows
//import dev.basshelal.jrtmidi.wait
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.matchers.comparables.shouldBeGreaterThan
//import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.shouldNotBe
//import jnr.ffi.Pointer
//import jnr.ffi.TypeAlias
//import jnr.ffi.byref.NumberByReference
//import java.nio.ByteBuffer
//import kotlin.random.Random
//
///**
// * Tests all 22 of the exported native C functions from the RtMidi library found in [RtMidiLibrary]
// */
//internal class RtMidiLibraryTest : StringSpec({
//
//    lateinit var lib: RtMidiLibrary
//
//    beforeSpec {
//        RtMidi.Config.useBundledLibraries(true).disallowJACK(false).load()
//        lib = RtMidiLibrary.instance
//    }
//
//    afterSpec { }
//
//    fun RtMidiPtr?.isOk() {
//        this shouldNotBe null
//        this?.Boolean().also {
//            it?.set(true)
//            this?.ok?.get() shouldBe it?.get()
//        }
//    }
//
//    fun RtMidiPtr.free() = when (this) {
//        is RtMidiInPtr -> lib.rtmidi_in_free(this)
//        is RtMidiOutPtr -> lib.rtmidi_out_free(this)
//        else -> Unit
//    }
//
//    fun free(vararg ptrs: RtMidiPtr) = ptrs.forEach { it.free() }
//
//    fun inCreateDefault(): RtMidiInPtr = lib.rtmidi_in_create_default().also { it.isOk() }
//
//    fun outCreateDefault(): RtMidiOutPtr = lib.rtmidi_out_create_default().also { it.isOk() }
//
//    fun inPortName(): String = "Test JRtMidi In Port at ${Random.nextInt()}"
//
//    fun outPortName(): String = "Test JRtMidi Out Port at ${Random.nextInt()}"
//
//    "rtmidi_get_compiled_api" {
//        // Using array
//        val arr = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
//        val written = lib.rtmidi_get_compiled_api(arr, arr.size)
//        written shouldBeLessThanOrEqualTo RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM
//
//        // using null
//        val writtenNull = lib.rtmidi_get_compiled_api(null, -1)
//        written shouldBe writtenNull
//    }
//
//    "rtmidi_api_name" {
//        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED) shouldBe "unspecified"
//        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE) shouldBe "core"
//        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA) shouldBe "alsa"
//        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK) shouldBe "jack"
//        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM) shouldBe "winmm"
//        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY) shouldBe "dummy"
//    }
//
//    "rtmidi_api_display_name" {
//        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED) shouldBe "Unknown"
//        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE) shouldBe "CoreMidi"
//        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA) shouldBe "ALSA"
//        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK) shouldBe "Jack"
//        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM) shouldBe "Windows MultiMedia"
//        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY) shouldBe "Dummy"
//    }
//
//    "rtmidi_compiled_api_by_name" {
//        val apiNumber: Int = when {
//            isLinux() -> lib.rtmidi_compiled_api_by_name("alsa")
//            isMacOs() -> lib.rtmidi_compiled_api_by_name("core")
//            isWindows() -> lib.rtmidi_compiled_api_by_name("winmm")
//            else -> RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED
//        }
//        apiNumber shouldNotBe RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED
//    }
//
//    "rtmidi_open_port" {
//        val `in` = inCreateDefault()
//        val out = outCreateDefault()
//        val inPortCount = lib.rtmidi_get_port_count(`in`)
//        val outPortCount = lib.rtmidi_get_port_count(out)
//        inPortCount shouldBeGreaterThan 0
//        outPortCount shouldBeGreaterThan 0
//
//        // Open an in port with a unique name!
//        val inPortName = inPortName()
//        lib.rtmidi_open_port(`in`, 0, inPortName)
//        val newOutPortCount = lib.rtmidi_get_port_count(out)
//        outPortCount shouldNotBe newOutPortCount
//        outPortCount + 1 shouldBe newOutPortCount
//
//        // out ports should contain our newly created in port
//        val outPortNames = List(newOutPortCount) { lib.rtmidi_get_port_name(out, it) }
//        val foundOut = outPortNames.filter { it.contains(inPortName) }
//        foundOut.isNotEmpty() shouldBe true
//        foundOut.size shouldBe 1
//
//        // Open an out port with a unique name!
//        val outPortName = outPortName()
//        lib.rtmidi_open_port(out, 0, outPortName)
//        val newInPortCount = lib.rtmidi_get_port_count(`in`)
//        inPortCount shouldNotBe newInPortCount
//        inPortCount + 1 shouldBe newInPortCount
//
//        // in ports should contain our newly created out port
//        val inPortNames = List(newInPortCount) { lib.rtmidi_get_port_name(`in`, it) }
//        val foundIn = inPortNames.filter { it.contains(outPortName) }
//        foundIn.isNotEmpty() shouldBe true
//        foundIn.size shouldBe 1
//
//        free(`in`, out)
//    }
//
//    "rtmidi_open_virtual_port" {
//        val `in` = inCreateDefault()
//        val out = outCreateDefault()
//        val inPortCount = lib.rtmidi_get_port_count(`in`)
//        val outPortCount = lib.rtmidi_get_port_count(out)
//        inPortCount shouldBeGreaterThan 0
//        outPortCount shouldBeGreaterThan 0
//
//        // Open an in port with a unique name!
//        val inPortName = inPortName()
//        lib.rtmidi_open_virtual_port(`in`, inPortName)
//        val newOutPortCount = lib.rtmidi_get_port_count(out)
//        outPortCount shouldNotBe newOutPortCount
//        outPortCount + 1 shouldBe newOutPortCount
//
//        // out ports should contain our newly created in port
//        val outPortNames = List(newOutPortCount) { lib.rtmidi_get_port_name(out, it) }
//        val foundOut = outPortNames.filter { it.contains(inPortName) }
//        foundOut.isNotEmpty() shouldBe true
//        foundOut.size shouldBe 1
//
//        // Open an out port with a unique name!
//        val outPortName = outPortName()
//        lib.rtmidi_open_virtual_port(out, outPortName)
//        val newInPortCount = lib.rtmidi_get_port_count(`in`)
//        inPortCount shouldNotBe newInPortCount
//        inPortCount + 1 shouldBe newInPortCount
//
//        // in ports should contain our newly created out port
//        val inPortNames = List(newInPortCount) { lib.rtmidi_get_port_name(`in`, it) }
//        val foundIn = inPortNames.filter { it.contains(outPortName) }
//        foundIn.isNotEmpty() shouldBe true
//        foundIn.size shouldBe 1
//
//        free(`in`, out)
//    }
//
//    "rtmidi_close_port" {
//        val `in` = inCreateDefault()
//        val out = outCreateDefault()
//        val inPortCount = lib.rtmidi_get_port_count(`in`)
//        val outPortCount = lib.rtmidi_get_port_count(out)
//        inPortCount shouldBeGreaterThan 0
//        outPortCount shouldBeGreaterThan 0
//
//        // Open an in port with a unique name!
//        val inPortName = inPortName()
//        lib.rtmidi_open_port(`in`, 0, inPortName)
//        val newOutPortCount = lib.rtmidi_get_port_count(out)
//        outPortCount shouldNotBe newOutPortCount
//        outPortCount + 1 shouldBe newOutPortCount
//        lib.rtmidi_close_port(`in`)
//        `in`.free() // necessary to truly close!
//        outPortCount shouldBe lib.rtmidi_get_port_count(out)
//        out.free()
//    }
//
//    "rtmidi_get_port_count" {
//        val `in` = inCreateDefault()
//        val out = outCreateDefault()
//        val inCount = lib.rtmidi_get_port_count(`in`)
//        val outCount = lib.rtmidi_get_port_count(out)
//        inCount shouldBeGreaterThan 0
//        outCount shouldBeGreaterThan 0
//
//        free(`in`, out)
//    }
//
//    "rtmidi_get_port_name" {
//        val `in` = inCreateDefault()
//        val out = outCreateDefault()
//        val inCount = lib.rtmidi_get_port_count(`in`)
//        val outCount = lib.rtmidi_get_port_count(out)
//        inCount shouldBeGreaterThan 0
//        outCount shouldBeGreaterThan 0
//        for (i in 0 until inCount) {
//            lib.rtmidi_get_port_name(`in`, i).also {
//                it shouldNotBe ""
//                it shouldNotBe null
//            }
//        }
//        for (i in 0 until outCount) {
//            lib.rtmidi_get_port_name(out, i).also {
//                it shouldNotBe ""
//                it shouldNotBe null
//            }
//        }
//        free(`in`, out)
//    }
//
//    "rtmidi_in_create_default" {
//        lib.rtmidi_in_create_default().also {
//            it.isOk()
//        }.free()
//    }
//
//    "rtmidi_in_create" {
//        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
//        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
//        totalApis shouldBeGreaterThan 0
//
//        lib.rtmidi_in_create(apis.first(), "Test JRtMidi Client", 1024).also {
//            it.isOk()
//        }.free()
//    }
//
//    "rtmidi_in_create no args" {
//        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
//        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
//        totalApis shouldBeGreaterThan 0
//
//        lib.rtmidi_in_create(0, "", 0).also {
//            it.isOk()
//        }.free()
//    }
//
//    "rtmidi_in_free" {
//        val `in` = inCreateDefault()
//        val ptr = `in`.ptr
//        `in`.ptr shouldNotBe null
//        lib.rtmidi_in_free(`in`)
//        // `in`.ptr shouldNotBe ptr
//
//        // using `in` should cause a fatal error SIGSEGV (ie segfault)
//    }
//
//    "rtmidi_in_get_current_api" {
//        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
//        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
//        totalApis shouldBeGreaterThan 0
//        val api = apis.first()
//        val clientName = "Test JRtMidi Client"
//        val queueSizeLimit = 1024
//        lib.rtmidi_in_create(api, clientName, queueSizeLimit).also {
//            it.isOk()
//            val usedApi = lib.rtmidi_in_get_current_api(it)
//            api shouldBe usedApi
//        }.free()
//    }
//
//    "rtmidi_in_set_callback" {
//        val readable = inCreateDefault()
//        val writable = outCreateDefault()
//        val writableName = outPortName()
//        lib.rtmidi_open_port(writable, 0, writableName)
//        val readableName = inPortName()
//        lib.rtmidi_open_port(readable, lib.rtmidi_get_port_count(readable) - 1, readableName)
//        val sentMessage = byteArrayOf(MidiMessage.NOTE_ON, 69, 69)
//        var messageReceived = false
//        val callback = object : RtMidiLibrary.RtMidiCCallback {
//            override fun invoke(timeStamp: Double, message: Pointer?, messageSize: Long?, userData: Pointer?) {
//                message shouldNotBe null
//                messageSize shouldNotBe null
//                require(message != null && messageSize != null) // for smart cast
//                sentMessage.size shouldBe messageSize.toInt()
//                for (i in 0 until (messageSize.toInt()))
//                    sentMessage[i] shouldBe message.getByte(i.toLong())
//                messageReceived = true
//            }
//        }
//        lib.rtmidi_in_set_callback(readable, callback, null)
//        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.size)
//        wait(200) // wait a little for flag to have changed
//        messageReceived shouldBe true
//        free(readable, writable)
//    }
//
//    "rtmidi_in_cancel_callback" {
//        val readable = inCreateDefault()
//        val writable = outCreateDefault()
//        val writableName = outPortName()
//        lib.rtmidi_open_port(writable, 0, writableName)
//        val readableName = inPortName()
//        lib.rtmidi_open_port(readable, lib.rtmidi_get_port_count(readable) - 1, readableName)
//        val sentMessage = byteArrayOf(MidiMessage.NOTE_ON, 69, 69)
//        var messageReceived = false
//        val callback = object : RtMidiLibrary.RtMidiCCallback {
//            override fun invoke(timeStamp: Double, message: Pointer?, messageSize: Long?, userData: Pointer?) {
//                message shouldNotBe null
//                messageSize shouldNotBe null
//                require(message != null && messageSize != null) // for smart cast
//                sentMessage.size shouldBe messageSize.toInt()
//                for (i in 0 until messageSize.toInt())
//                    sentMessage[i] shouldBe message.getByte(i.toLong())
//                messageReceived = true
//            }
//        }
//        lib.rtmidi_in_set_callback(readable, callback, null)
//        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.size)
//        wait(200) // wait a little for flag to have changed
//        messageReceived shouldBe true
//        messageReceived = false
//        lib.rtmidi_in_cancel_callback(readable)
//
//        // try to send some messages, if readable received them then flag will have changed
//        for (i in 0..20) lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.size)
//        wait(200) // wait a little for flag to have changed
//        messageReceived shouldBe false
//        lib.rtmidi_in_free(readable)
//        lib.rtmidi_out_free(writable)
//    }
//
//    "rtmidi_in_ignore_types" {
//        val readable = inCreateDefault()
//        val writable = outCreateDefault()
//        val writableName = outPortName()
//        lib.rtmidi_open_port(writable, 0, writableName)
//        val readableName = inPortName()
//        lib.rtmidi_open_port(readable, lib.rtmidi_get_port_count(readable) - 1, readableName)
//        var ignoring = false
//        lib.rtmidi_in_ignore_types(readable, midiSysex = ignoring, midiSense = ignoring, midiTime = ignoring)
//        val sentMessage = byteArrayOf(MidiMessage.TIMING_CLOCK)
//        var messageReceived = false
//        val callback = object : RtMidiLibrary.RtMidiCCallback {
//            override fun invoke(timeStamp: Double, message: Pointer?, messageSize: Long?, userData: Pointer?) {
//                messageReceived = true
//            }
//        }
//        lib.rtmidi_in_set_callback(readable, callback, null)
//        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.size)
//        wait(200) // wait a little for flag to have changed
//        messageReceived shouldBe true
//        ignoring = true
//        lib.rtmidi_in_ignore_types(readable, midiSysex = ignoring, midiSense = ignoring, midiTime = ignoring)
//        messageReceived = false
//        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.size)
//        wait(200) // wait a little for flag to have changed
//        messageReceived shouldBe false
//        free(readable, writable)
//    }
//
//    "rtmidi_out_create_default" {
//        lib.rtmidi_out_create_default().also {
//            it.isOk()
//        }.free()
//    }
//
//    "rtmidi_out_create" {
//        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
//        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
//        totalApis shouldBeGreaterThan 0
//        val clientName = "Test JRtMidi Client"
//        lib.rtmidi_out_create(apis.first(), clientName).also {
//            it.isOk()
//        }.free()
//    }
//
//    "rtmidi_out_free" {
//        val out = outCreateDefault()
//        val ptr = out.ptr
//        out.ptr shouldNotBe null
//        lib.rtmidi_out_free(out)
//        //  out.ptr shouldNotBe ptr
//
//        /// using `out` should cause a fatal error SIGSEGV (ie segfault)
//    }
//
//    "rtmidi_out_get_current_api" {
//        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
//        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
//        totalApis shouldBeGreaterThan 0
//        val api = apis.first()
//        val clientName = "Test JRtMidi Client"
//        lib.rtmidi_out_create(api, clientName).also {
//            it.isOk()
//            val usedApi = lib.rtmidi_out_get_current_api(it)
//            api shouldBe usedApi
//        }.free()
//    }
//
//    "rtmidi_out_send_message" {
//        val `in` = inCreateDefault()
//        val out = outCreateDefault()
//        val inPortCount = lib.rtmidi_get_port_count(`in`)
//        val outPortCount = lib.rtmidi_get_port_count(out)
//        inPortCount shouldBeGreaterThan 0
//        outPortCount shouldBeGreaterThan 0
//
//        // Open an out port with a unique name!
//        val outPortName = outPortName()
//
//        // open the port
//        lib.rtmidi_open_port(out, 0, outPortName)
//
//        // find it on the other side and open that
//        val newInPortCount = lib.rtmidi_get_port_count(`in`)
//        inPortCount shouldNotBe newInPortCount
//        inPortCount + 1 shouldBe newInPortCount
//
//        // in ports should contain our newly created out port
//        val inPortIndex = newInPortCount - 1
//        val inPortName = inPortName()
//        lib.rtmidi_open_port(`in`, inPortIndex, inPortName)
//
//        // send the out message
//        val message = byteArrayOf(MidiMessage.NOTE_ON, 69, 69)
//        val sent = lib.rtmidi_out_send_message(out, message, message.size)
//        sent shouldNotBe -1
//
//        // get the in message and assert they are equal
//        val receivedMessage = byteArrayOf(-1, -1, -1)
//        val got = lib.rtmidi_in_get_message(`in`, ByteBuffer.wrap(receivedMessage),
//                NumberByReference(TypeAlias.size_t, receivedMessage.size))
//        got shouldNotBe -1.0
//        message shouldBe receivedMessage
//        free(`in`, out)
//    }
//
//})