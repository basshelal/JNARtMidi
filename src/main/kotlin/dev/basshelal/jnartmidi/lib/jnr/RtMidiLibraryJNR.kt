package dev.basshelal.jnartmidi.lib.jnr

import jnr.ffi.LibraryLoader
import jnr.ffi.LibraryOption

interface RtMidiLibraryJNR {

    companion object {
        /** The name of the native shared library */
        const val LIBRARY_NAME = "rtmidi"

        val library: RtMidiLibraryJNR by lazy {
            LibraryLoader.loadLibrary(
                    RtMidiLibraryJNR::class.java,
                    emptyMap<LibraryOption, Any>(),
                    mapOf(LIBRARY_NAME to listOf("bin/linux-x86-64")),
                    LIBRARY_NAME
            )
        }
    }

    fun rtmidi_get_compiled_api(apis: IntArray?, apis_size: Int): Int

    fun rtmidi_api_name(api: Int): String

    fun rtmidi_api_display_name(api: Int): String

    fun rtmidi_in_create_default(): RtMidiInPtr

    fun rtmidi_get_port_count(device: RtMidiPtr): Int
}