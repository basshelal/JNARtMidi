package dev.basshelal.jnartmidi.lib

import com.sun.jna.Native
import com.sun.jna.Pointer
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.NativeSizeByReference
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiCCallback
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr
import java.nio.ByteBuffer

class RtMidiLibraryNative : RtMidiLibrary {

    companion object {
        private var INSTANCE: RtMidiLibrary? = null

        @JvmStatic
        val instance: RtMidiLibrary?
            get() {
                if (INSTANCE == null) INSTANCE = RtMidiLibraryNative()
                return INSTANCE
            }

        init {
            Native.register(RtMidiLibrary.LIBRARY_NAME)
        }
    }

    external override fun rtmidi_get_compiled_api(apis: IntArray, apis_size: Int): Int

    external override fun rtmidi_api_name(api: Int): String

    external override fun rtmidi_api_display_name(api: Int): String

    external override fun rtmidi_compiled_api_by_name(name: String): Int

    external override fun rtmidi_open_port(device: RtMidiPtr, portNumber: Int, portName: String)

    external override fun rtmidi_open_virtual_port(device: RtMidiPtr, portName: String)

    external override fun rtmidi_close_port(device: RtMidiPtr)

    external override fun rtmidi_get_port_count(device: RtMidiPtr): Int

    external override fun rtmidi_get_port_name(device: RtMidiPtr, portNumber: Int): String

    external override fun rtmidi_in_create_default(): RtMidiInPtr

    external override fun rtmidi_in_create(api: Int, clientName: String, queueSizeLimit: Int): RtMidiInPtr

    external override fun rtmidi_in_free(device: RtMidiInPtr)

    external override fun rtmidi_in_get_current_api(device: RtMidiInPtr): Int

    external override fun rtmidi_in_set_callback(device: RtMidiInPtr, callback: RtMidiCCallback, userData: Pointer)

    external override fun rtmidi_in_cancel_callback(device: RtMidiInPtr)

    external override fun rtmidi_in_ignore_types(device: RtMidiInPtr, midiSysex: Boolean, midiTime: Boolean, midiSense: Boolean)

    external override fun rtmidi_in_get_message(device: RtMidiInPtr, message: ByteBuffer, size: NativeSizeByReference): Double

    external override fun rtmidi_out_create_default(): RtMidiOutPtr

    external override fun rtmidi_out_create(api: Int, clientName: String): RtMidiOutPtr

    external override fun rtmidi_out_free(device: RtMidiOutPtr)

    external override fun rtmidi_out_get_current_api(device: RtMidiOutPtr): Int

    external override fun rtmidi_out_send_message(device: RtMidiOutPtr, message: ByteArray, length: Int): Int

}
