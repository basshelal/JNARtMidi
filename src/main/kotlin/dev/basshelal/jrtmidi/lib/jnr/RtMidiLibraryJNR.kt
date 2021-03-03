package dev.basshelal.jrtmidi.lib.jnr

import jnr.ffi.LibraryLoader
import jnr.ffi.LibraryOption
import jnr.ffi.Pointer
import jnr.ffi.annotations.Delegate
import jnr.ffi.byref.NumberByReference
import jnr.ffi.types.size_t
import java.nio.ByteBuffer

interface RtMidiLibraryJNR {

    companion object {
        /** The name of the native shared library */
        const val LIBRARY_NAME = "rtmidi"

        val library: RtMidiLibraryJNR by lazy {
            LibraryLoader.loadLibrary(
                    RtMidiLibraryJNR::class.java,
                    mapOf<LibraryOption, Any>(LibraryOption.LoadNow to true),
                    mapOf(LIBRARY_NAME to listOf("bin/linux-x86-64")),
                    LIBRARY_NAME
            )
        }
    }

    interface RtMidiCCallback {
        @Delegate
        operator fun invoke(timeStamp: Double, message: Pointer?, @size_t messageSize: Long?, userData: Pointer?)
    }

    //=============================================================================================
    //=================================     RtMidi API     ========================================
    //=============================================================================================

    fun rtmidi_get_compiled_api(apis: IntArray?, apis_size: Int): Int

    fun rtmidi_api_name(api: Int): String

    fun rtmidi_api_display_name(api: Int): String

    fun rtmidi_compiled_api_by_name(name: String): Int

    fun rtmidi_open_port(device: RtMidiPtr, portNumber: Int, portName: String)

    fun rtmidi_open_virtual_port(device: RtMidiPtr, portName: String)

    fun rtmidi_close_port(device: RtMidiPtr)

    fun rtmidi_get_port_count(device: RtMidiPtr): Int

    fun rtmidi_get_port_name(device: RtMidiPtr, portNumber: Int): String

    //=============================================================================================
    //===============================     RtMidiIn API     ========================================
    //=============================================================================================

    fun rtmidi_in_create_default(): RtMidiInPtr

    fun rtmidi_in_create(api: Int, clientName: String, queueSizeLimit: Int): RtMidiInPtr

    fun rtmidi_in_free(device: RtMidiInPtr)

    fun rtmidi_in_get_current_api(device: RtMidiInPtr): Int

    fun rtmidi_in_set_callback(device: RtMidiInPtr, callback: RtMidiCCallback, userData: Pointer?)

    fun rtmidi_in_cancel_callback(device: RtMidiInPtr)

    fun rtmidi_in_ignore_types(device: RtMidiInPtr, midiSysex: Boolean, midiTime: Boolean, midiSense: Boolean)

    fun rtmidi_in_get_message(device: RtMidiInPtr, message: ByteBuffer, @size_t size: NumberByReference): Double

    //=============================================================================================
    //================================     RtMidiOut API     ======================================
    //=============================================================================================

    fun rtmidi_out_create_default(): RtMidiOutPtr

    fun rtmidi_out_create(api: Int, clientName: String): RtMidiOutPtr

    fun rtmidi_out_free(device: RtMidiOutPtr)

    fun rtmidi_out_get_current_api(device: RtMidiOutPtr): Int

    fun rtmidi_out_send_message(device: RtMidiOutPtr, message: ByteArray, length: Int): Int
}