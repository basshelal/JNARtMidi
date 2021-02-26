@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jnartmidi.lib

import com.sun.jna.Platform
import dev.basshelal.jnartmidi.api.RtMidi
import dev.basshelal.jnartmidi.api.RtMidi.readableMidiPorts
import dev.basshelal.jnartmidi.api.RtMidi.writableMidiPorts
import dev.basshelal.jnartmidi.api.RtMidiApi
import dev.basshelal.jnartmidi.mustBe
import dev.basshelal.jnartmidi.mustBeGreaterThan
import dev.basshelal.jnartmidi.mustBeLessThanOrEqualTo
import dev.basshelal.jnartmidi.mustNotBe
import dev.basshelal.jnartmidi.wait
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.random.Random

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestRtMidiLibrary {

    companion object {
        lateinit var lib: RtMidiLibrary

        @BeforeAll
        @JvmStatic
        fun `Before All`() {
            RtMidi.addLibrarySearchPath("bin/${Platform.RESOURCE_PREFIX}")
            assertDoesNotThrow { RtMidi.availableApis() }
            lib = RtMidiLibrary.instance
        }

        @AfterAll
        @JvmStatic
        fun `After All`() {
        }

    }

    private fun logPorts() {
        println("\nReadable Midi Ports:")
        readableMidiPorts().forEach(::println)
        println("\nWritable Midi Ports:")
        writableMidiPorts().forEach(::println)
    }

    private inline fun RtMidiPtr?.isOk() {
        this mustNotBe null
        this?.ok mustBe true
    }

    private inline fun RtMidiPtr.free() = when (this) {
        is RtMidiInPtr -> lib.rtmidi_in_free(this)
        is RtMidiOutPtr -> lib.rtmidi_out_free(this)
        else -> Unit
    }

    private inline fun free(vararg ptrs: RtMidiPtr) = ptrs.forEach { it.free() }

    private fun inCreateDefault(): RtMidiInPtr = lib.rtmidi_in_create_default().also { it.isOk() }

    private fun outCreateDefault(): RtMidiOutPtr = lib.rtmidi_out_create_default().also { it.isOk() }

    private fun inPortName(): String = "Test JNARtMidi In Port at ${Random.nextInt()}"

    private fun outPortName(): String = "Test JNARtMidi Out Port at ${Random.nextInt()}"

    // GC or JUnit causes something to go wong when running all tests in succession, a slight wait fixes it somehow
    @BeforeEach
    fun `Before Each`() {
        wait(50)
    }

    @Order(0)
    @Test
    fun `0 rtmidi_get_compiled_api`() {
        // Using array
        val arr = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
        val written = lib.rtmidi_get_compiled_api(arr, arr.size)
        written mustBeLessThanOrEqualTo RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM
        val apis = Array(written) { RtMidiApi.fromInt(arr[it]) }
        apis.isNotEmpty() mustBe true
        apis.forEach { it mustNotBe null }

        // using null
        val writtenNull = lib.rtmidi_get_compiled_api(null, -1)
        written mustBe writtenNull
    }

    @Order(1)
    @Test
    fun `1 rtmidi_api_name`() {
        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED) mustBe "unspecified"
        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE) mustBe "core"
        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA) mustBe "alsa"
        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK) mustBe "jack"
        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM) mustBe "winmm"
        lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY) mustBe "dummy"
    }

    @Order(2)
    @Test
    fun `2 rtmidi_api_display_name`() {
        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED) mustBe "Unknown"
        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE) mustBe "CoreMidi"
        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA) mustBe "ALSA"
        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK) mustBe "Jack"
        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM) mustBe "Windows MultiMedia"
        lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY) mustBe "Dummy"
    }

    @Order(3)
    @Test
    fun `3 rtmidi_compiled_api_by_name`() {
        val apiNumber: Int = when {
            Platform.isLinux() -> lib.rtmidi_compiled_api_by_name("alsa")
            Platform.isMac() -> lib.rtmidi_compiled_api_by_name("core")
            Platform.isWindows() -> lib.rtmidi_compiled_api_by_name("winmm")
            else -> RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED
        }
        apiNumber mustNotBe RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED
        val api = RtMidiApi.fromInt(apiNumber)
        api mustNotBe RtMidiApi.UNSPECIFIED
        apiNumber mustBe api.number
    }

    @Order(4)
    @Test
    fun `4 rtmidi_open_port`() {
        val `in` = inCreateDefault()
        val out = outCreateDefault()
        val inPortCount = lib.rtmidi_get_port_count(`in`)
        val outPortCount = lib.rtmidi_get_port_count(out)
        inPortCount mustBeGreaterThan 0
        outPortCount mustBeGreaterThan 0

        // Open an in port with a unique name!
        val inPortName = inPortName()
        lib.rtmidi_open_port(`in`, 0, inPortName)
        val newOutPortCount = lib.rtmidi_get_port_count(out)
        outPortCount mustNotBe newOutPortCount
        outPortCount + 1 mustBe newOutPortCount

        // out ports should contain our newly created in port
        val outPortNames = List(newOutPortCount) { lib.rtmidi_get_port_name(out, it) }
        val foundOut = outPortNames.any { it.contains(inPortName) }
        foundOut mustBe true

        // Open an out port with a unique name!
        val outPortName = outPortName()
        lib.rtmidi_open_port(out, 0, outPortName)
        val newInPortCount = lib.rtmidi_get_port_count(`in`)
        inPortCount mustNotBe newInPortCount
        inPortCount + 1 mustBe newInPortCount

        // in ports should contain our newly created out port
        val inPortNames = List(newInPortCount) { lib.rtmidi_get_port_name(`in`, it) }
        val foundIn = inPortNames.any { it.contains(outPortName) }
        foundIn mustBe true

        free(`in`, out)
    }

    @Order(7)
    @Test
    fun `7 rtmidi_get_port_count`() {
        val `in` = inCreateDefault()
        val out = outCreateDefault()
        val inCount = lib.rtmidi_get_port_count(`in`)
        val outCount = lib.rtmidi_get_port_count(out)
        inCount mustBeGreaterThan 0
        outCount mustBeGreaterThan 0

        free(`in`, out)
    }

    @Order(8)
    @Test
    fun `8 rtmidi_get_port_name`() {
        val `in` = inCreateDefault()
        val out = outCreateDefault()
        val inCount = lib.rtmidi_get_port_count(`in`)
        val outCount = lib.rtmidi_get_port_count(out)
        inCount mustBeGreaterThan 0
        outCount mustBeGreaterThan 0
        for (i in 0 until inCount) {
            lib.rtmidi_get_port_name(`in`, i).also {
                it mustNotBe null
                it mustNotBe ""
            }
        }
        for (i in 0 until outCount) {
            lib.rtmidi_get_port_name(out, i).also {
                it mustNotBe null
                it mustNotBe ""
            }
        }
        free(`in`, out)
    }

    @Order(9)
    @Test
    fun `9 rtmidi_in_create_default`() {
        lib.rtmidi_in_create_default().also {
            it.isOk()
        }.free()
    }

    @Order(10)
    @Test
    fun `10 rtmidi_in_create`() {
        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
        totalApis mustBeGreaterThan 0

        lib.rtmidi_in_create(apis.first(), "Test JNARtMidi Client", 1024).also {
            it.isOk()
        }.free()
    }

    @Order(10)
    @Test
    fun `10_1 rtmidi_in_create no args`() {
        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
        totalApis mustBeGreaterThan 0

        lib.rtmidi_in_create(0, "", 0).also {
            it.isOk()
        }.free()
    }

    @Order(11)
    @Test
    fun `11 rtmidi_in_free`() {
        val `in` = inCreateDefault()
        val copy = RtMidiPtr(`in`)
        copy.ptr mustBe `in`.ptr
        copy.data mustBe `in`.data
        lib.rtmidi_in_free(`in`)
        copy.ptr mustNotBe `in`.ptr
        copy.data mustNotBe `in`.data

        // using `in` should cause a fatal error SIGSEGV (ie segfault)
    }

    @Order(12)
    @Test
    fun `12 rtmidi_in_get_current_api`() {
        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
        totalApis mustBeGreaterThan 0
        val api = apis.first()
        val clientName = "Test JNARtMidi Client"
        val queueSizeLimit = 1024
        lib.rtmidi_in_create(api, clientName, queueSizeLimit).also {
            it.isOk()
            val usedApi = lib.rtmidi_in_get_current_api(it)
            api mustBe usedApi
        }.free()
    }

    @Order(17)
    @Test
    fun `17 rtmidi_out_create_default`() {
        lib.rtmidi_out_create_default().also {
            it.isOk()
        }.free()
    }

    @Order(18)
    @Test
    fun `18 rtmidi_out_create`() {
        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
        totalApis mustBeGreaterThan 0
        val clientName = "Test JNARtMidi Client"
        lib.rtmidi_out_create(apis.first(), clientName).also {
            it.isOk()
        }.free()
    }

    @Order(19)
    @Test
    fun `19 rtmidi_out_free`() {
        val out = outCreateDefault()
        val copy = RtMidiPtr(out)
        copy.ptr mustBe out.ptr
        copy.data mustBe out.data
        lib.rtmidi_out_free(out)
        copy.ptr mustNotBe out.ptr
        copy.data mustNotBe out.data

        /// using `out` should cause a fatal error SIGSEGV (ie segfault)
    }

    @Order(20)
    @Test
    fun `20 rtmidi_out_get_current_api`() {
        val apis = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
        val totalApis = lib.rtmidi_get_compiled_api(apis, apis.size)
        totalApis mustBeGreaterThan 0
        val api = apis.first()
        val clientName = "Test JNARtMidi Client"
        lib.rtmidi_out_create(api, clientName).also {
            it.isOk()
            val usedApi = lib.rtmidi_out_get_current_api(it)
            api mustBe usedApi
        }.free()
    }
}