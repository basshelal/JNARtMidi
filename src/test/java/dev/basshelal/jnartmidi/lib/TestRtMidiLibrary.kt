@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jnartmidi.lib

import com.sun.jna.Platform
import dev.basshelal.jnartmidi.api.RtMidi
import dev.basshelal.jnartmidi.api.RtMidi.readableMidiPorts
import dev.basshelal.jnartmidi.api.RtMidi.writableMidiPorts
import dev.basshelal.jnartmidi.api.RtMidiApi
import dev.basshelal.jnartmidi.mustBe
import dev.basshelal.jnartmidi.mustBeLessThanOrEqualTo
import dev.basshelal.jnartmidi.mustEqual
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
        written mustEqual writtenNull
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
        val apiNumber = when {
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

}