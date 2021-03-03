@file:Suppress("NOTHING_TO_INLINE")

package dev.basshelal.jnartmidi.lib

import com.sun.jna.Platform
import com.sun.jna.Pointer
import dev.basshelal.jnartmidi.api.MidiMessage
import dev.basshelal.jnartmidi.api.RtMidi
import dev.basshelal.jnartmidi.lib.jnr.RtMidiLibraryJNR
import dev.basshelal.jnartmidi.mustBe
import dev.basshelal.jnartmidi.wait
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class LatencyTest {

    companion object {
        lateinit var jna: RtMidiLibrary
        lateinit var jnr: RtMidiLibraryJNR

        @BeforeAll
        @JvmStatic
        fun `Before All`() {
            RtMidi.addLibrarySearchPath("bin/${Platform.RESOURCE_PREFIX}")
            jna = RtMidiLibrary.instance
            jnr = RtMidiLibraryJNR.library
            ::jna.isInitialized mustBe true
            ::jnr.isInitialized mustBe true
        }

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }

    private inline fun RtMidiPtr.free() = when (this) {
        is RtMidiInPtr -> jna.rtmidi_in_free(this)
        is RtMidiOutPtr -> jna.rtmidi_out_free(this)
        else -> Unit
    }

    private inline fun free(vararg ptrs: RtMidiPtr) = ptrs.forEach { it.free() }

    private fun inPortName(): String = "Test JNARtMidi In Port at ${Random.nextInt()}"

    private fun outPortName(): String = "Test JNARtMidi Out Port at ${Random.nextInt()}"

    @Test
    fun JNA() {
        val readable = jna.rtmidi_in_create_default()
        val writable = jna.rtmidi_out_create_default()
        val writableName = outPortName()
        jna.rtmidi_open_port(writable, 0, writableName)
        val readableName = inPortName()
        jna.rtmidi_open_port(readable, jna.rtmidi_get_port_count(readable) - 1, readableName)
        val sentMessage = byteArrayOf(MidiMessage.NOTE_ON, 69, 69)
        val deltas = mutableListOf<Double>()
        var dT: Long = 0
        val callback = object : RtMidiLibrary.RtMidiCCallback {
            override fun invoke(timeStamp: Double, message: Pointer?,
                                messageSize: RtMidiLibrary.NativeSize?, userData: Pointer?) {
                val time = System.nanoTime()
                //  println("dT: ${dT}")
                //  println("time: ${time}")
                val delta = (time - dT).toDouble() / 1000.0
                // println("diff is: ${delta}")
                deltas.add(delta)
            }
        }
        jna.rtmidi_in_set_callback(readable, callback, null)

        (0..10_000).forEach {
            wait(10)
            dT = System.nanoTime()
            jna.rtmidi_out_send_message(writable, sentMessage, sentMessage.size)
        }

        wait(1000)

        println("JNA:")
        println("Size: ${deltas.size}")
        println("Min: ${deltas.min()}")
        println("Max: ${deltas.max()}")
        println("Median: ${deltas.sorted().get(deltas.size / 2)}")
        println("Mean: ${deltas.average()}")


        jna.rtmidi_in_free(readable)
        jna.rtmidi_out_free(writable)
    }

    @Test
    fun JNR() {
        val readable = jnr.rtmidi_in_create_default()
        val writable = jnr.rtmidi_out_create_default()
        val writableName = outPortName()
        jnr.rtmidi_open_port(writable, 0, writableName)
        val readableName = inPortName()
        jnr.rtmidi_open_port(readable, jnr.rtmidi_get_port_count(readable) - 1, readableName)
        val sentMessage = byteArrayOf(MidiMessage.NOTE_ON, 69, 69)
        val deltas = mutableListOf<Double>()
        var dT: Long = 0
        val callback = object : RtMidiLibraryJNR.RtMidiCCallback {
            override fun invoke(timeStamp: Double, message: jnr.ffi.Pointer?,
                                messageSize: Long?, userData: jnr.ffi.Pointer?) {
                val time = System.nanoTime()
                //  println("dT: ${dT}")
                //  println("time: ${time}")
                val delta = (time - dT).toDouble() / 1000.0
                // println("diff is: ${delta}")
                deltas.add(delta)
            }
        }
        jnr.rtmidi_in_set_callback(readable, callback, null)

        (0..10_000).forEach {
            wait(10)
            dT = System.nanoTime()
            jnr.rtmidi_out_send_message(writable, sentMessage, sentMessage.size)
        }

        wait(1000)

        println("JNR:")
        println("Size: ${deltas.size}")
        println("Min: ${deltas.min()}")
        println("Max: ${deltas.max()}")
        println("Median: ${deltas.sorted().get(deltas.size / 2)}")
        println("Mean: ${deltas.average()}")


        jnr.rtmidi_in_free(readable)
        jnr.rtmidi_out_free(writable)
    }
}