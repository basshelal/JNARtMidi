package dev.basshelal.jrtmidi.lib.jnr

import jnr.ffi.LibraryLoader
import jnr.ffi.LibraryOption
import jnr.ffi.Pointer
import jnr.ffi.annotations.Delegate
import jnr.ffi.annotations.IgnoreError
import jnr.ffi.byref.NumberByReference
import jnr.ffi.types.size_t
import java.nio.ByteBuffer

@IgnoreError
interface RtMidiLibraryJNR {

    companion object {
        /** The name of the native shared library */
        const val LIBRARY_NAME = "rtmidi"

        internal val libPaths = mutableListOf<String>()

        @JvmStatic
        val library: RtMidiLibraryJNR by lazy {
            LibraryLoader.loadLibrary(
                    RtMidiLibraryJNR::class.java,
                    mapOf(LibraryOption.LoadNow to true, LibraryOption.IgnoreError to true),
                    mapOf(LIBRARY_NAME to libPaths),
                    LIBRARY_NAME
            )
        }
    }

    //=============================================================================================
    //====================================     Types     ==========================================
    //=============================================================================================

    /** MIDI API specifier arguments. See RtMidi::Api */
    interface RtMidiApi {
        companion object {
            /** Search for a working compiled API. */
            const val RTMIDI_API_UNSPECIFIED = 0

            /** Macintosh OS-X CoreMIDI API */
            const val RTMIDI_API_MACOSX_CORE = 1

            /** The Advanced Linux Sound Architecture API */
            const val RTMIDI_API_LINUX_ALSA = 2

            /** The Jack Low-Latency MIDI Server API */
            const val RTMIDI_API_UNIX_JACK = 3

            /** The Microsoft Multimedia MIDI API */
            const val RTMIDI_API_WINDOWS_MM = 4

            /** A compilable but non-functional API */
            const val RTMIDI_API_RTMIDI_DUMMY = 5

            /** < Number of values in this enum */
            const val RTMIDI_API_NUM = 6
        }
    }

    interface RtMidiCCallback {
        @Delegate
        operator fun invoke(timeStamp: Double, message: Pointer?, @size_t messageSize: Long?, userData: Pointer?)
    }

    //=============================================================================================
    //=================================     RtMidi API     ========================================
    //=============================================================================================

    /**
     * Determine the available compiled MIDI APIs.
     * If the given `apis` parameter is `null`, returns the number of available APIs.
     * Otherwise, fill the given `apis` array with the [RtMidiApi] values.
     *
     * Original signature : `int rtmidi_get_compiled_api(RtMidiApi*, unsigned int)`
     *
     * @param apis An array to be filled or `null`,
     * ensure the array is at least [RtMidiApi.RTMIDI_API_NUM] in size.
     * @param apis_size size of the passed in array `apis`, ignored if `apis` is `null`
     * @return number of items needed for `apis` array if `apis == null`, or
     * number of items written to `apis` array otherwise.
     * A negative return value indicates an error.
     */
    fun rtmidi_get_compiled_api(apis: IntArray?, apis_size: Int): Int

    /**
     * See RtMidi::getApiName()
     * Original signature : `char* rtmidi_api_name(RtMidiApi)`
     */
    fun rtmidi_api_name(api: Int): String

    /**
     * See RtMidi::getApiDisplayName()
     * Original signature : `char* rtmidi_api_display_name(RtMidiApi)`
     */
    fun rtmidi_api_display_name(api: Int): String

    /**
     * See RtMidi::getCompiledApiByName()
     * Original signature : `RtMidiApi rtmidi_compiled_api_by_name(const char*)`
     */
    fun rtmidi_compiled_api_by_name(name: String): Int

    /**
     * Open a MIDI port
     * @param port Must be greater than 0
     * @param portName Name for the application port
     * See RtMidi::openPort()
     * Original signature : `void rtmidi_open_port(RtMidiPtr, unsigned int, const char*)`
     */
    fun rtmidi_open_port(device: RtMidiPtr, portNumber: Int, portName: String)

    /**
     * Creates a virtual MIDI port to which other software applications can connect
     * @param portName Name for the application port.
     * See RtMidi::openVirtualPort()
     * Original signature : `void rtmidi_open_virtual_port(RtMidiPtr, const char*)`
     */
    fun rtmidi_open_virtual_port(device: RtMidiPtr, portName: String)

    /**
     * Close a MIDI connection
     * See RtMidi::closePort()
     * Original signature : `void rtmidi_close_port(RtMidiPtr)`
     */
    fun rtmidi_close_port(device: RtMidiPtr)

    /**
     * Return the number of available MIDI ports
     * See RtMidi::getPortCount()
     * Original signature : `int rtmidi_get_port_count(RtMidiPtr)`
     */
    fun rtmidi_get_port_count(device: RtMidiPtr): Int

    /**
     * Return a string identifier for the specified MIDI input port number
     * See RtMidi::getPortName()
     * Original signature : `char* rtmidi_get_port_name(RtMidiPtr, unsigned int)`
     */
    fun rtmidi_get_port_name(device: RtMidiPtr, portNumber: Int): String

    //=============================================================================================
    //===============================     RtMidiIn API     ========================================
    //=============================================================================================

    /**
     * Create a default [RtMidiInPtr] value, with no initialization, RtMidi will choose its own API and
     * client name, to set these yourself use [rtmidi_in_create]
     * Original signature : `RtMidiInPtr rtmidi_in_create_default()`
     */
    fun rtmidi_in_create_default(): RtMidiInPtr

    /**
     * Create a [RtMidiInPtr] value, with given api, clientName and queueSizeLimit.
     * Original signature : `RtMidiInPtr rtmidi_in_create(RtMidiApi, const char*, unsigned int)`<br></br>
     *
     * @param api An [RtMidiApi] to use or 0 ie [RtMidiApi.RTMIDI_API_UNSPECIFIED] to let RtMidi choose the first
     * suitable API
     * @param clientName Non null client name, this will be used to group the ports that are created by the
     * application
     * @param queueSizeLimit Size of the MIDI input queue, negative values are not allowed and 0 may cause segfaults
     * later on, this allocates a queue of this size so keep it reasonable.
     */
    // NOTE: Bassam Helal 22-Feb-2021 queueSizeLimit is bad...
    //  basically, it cannot be null (even if using Pointer), cannot be 0 because segfault later and
    //  if a small number, the message queue limit will be reached which floods our output with error messages saying
    //  MidiInAlsa: message queue limit reached!!
    //  using rtmidi_in_create_default() will give a queueSizeLimit of 100
    //  this is regardless of anything because there is a midi message handler created by the library that pushes
    //  to the queue and if the queue is full, will output errors,
    //  Using a large number to avoid flooding is a bad idea because that memory is allocated
    //  The only thing we can do is allow it to flood our output, a solution would be to have the error logging be
    //  conditional in the C++ code so that a version can be built where this will be silent
    //  I have created an issue on RtMidi's GitHub about this
    fun rtmidi_in_create(api: Int, clientName: String, queueSizeLimit: Int): RtMidiInPtr

    /**
     * Free the given [RtMidiInPtr].
     * After this operation using the [RtMidiInPtr] in any way within the RtMidi library
     * (ie [rtmidi_close_port]) will cause a fatal VM error as a result of a segfault in the native code.
     * It is only safe to query the struct's data such as [RtMidiPtr.ok] or [RtMidiPtr.getPointer] etc.
     * Original signature : `void rtmidi_in_free(RtMidiInPtr)`
     *
     * @param device the [RtMidiInPtr] to free
     */
    fun rtmidi_in_free(device: RtMidiInPtr)

    /**
     * See RtMidiIn::getCurrentApi()
     * Original signature : `RtMidiApi rtmidi_in_get_current_api(RtMidiPtr)`
     */
    fun rtmidi_in_get_current_api(device: RtMidiInPtr): Int

    /**
     * See RtMidiIn::setCallback()
     * Original signature : `void rtmidi_in_set_callback(RtMidiInPtr, RtMidiCCallback, void*)`
     */
    fun rtmidi_in_set_callback(device: RtMidiInPtr, callback: RtMidiCCallback, userData: Pointer?)

    /**
     * See RtMidiIn::cancelCallback()
     * Original signature : `void rtmidi_in_cancel_callback(RtMidiInPtr)`
     */
    fun rtmidi_in_cancel_callback(device: RtMidiInPtr)

    /**
     * See RtMidiIn::ignoreTypes()
     * Original signature : `void rtmidi_in_ignore_types(RtMidiInPtr, bool, bool, bool)`
     */
    fun rtmidi_in_ignore_types(device: RtMidiInPtr, midiSysex: Boolean, midiTime: Boolean, midiSense: Boolean)

    /**
     * Fill the user-provided array with the data bytes for the next available
     * MIDI message in the input queue and return the event delta-time in seconds.
     * @param message Must point to a char* that is already allocated.
     * SYSEX messages maximum size being 1024, a statically allocated array could be sufficient
     * @param size Is used to return the size of the message obtained.
     * See RtMidiIn::getMessage()
     * Original signature : `double rtmidi_in_get_message(RtMidiInPtr, unsigned char*, size_t*)`
     */
    fun rtmidi_in_get_message(device: RtMidiInPtr, message: ByteBuffer, @size_t size: NumberByReference): Double

    //=============================================================================================
    //================================     RtMidiOut API     ======================================
    //=============================================================================================

    /**
     * ! \brief Create a default RtMidiInPtr value, with no initialization.<br></br>
     * Original signature : `RtMidiOutPtr rtmidi_out_create_default()`<br></br>
     * *native declaration : rtmidi_c.h:171*
     */
    fun rtmidi_out_create_default(): RtMidiOutPtr

    /**
     * Create a RtMidiOutPtr value, with given and clientName.
     * @param api An optional API id can be specified.
     * @param clientName An optional client name can be specified.
     * This will be used to group the ports that are created by the application.
     * See RtMidiOut::RtMidiOut()
     * Original signature : `RtMidiOutPtr rtmidi_out_create(RtMidiApi, const char*)`
     */
    fun rtmidi_out_create(api: Int, clientName: String): RtMidiOutPtr

    /**
     * Free the given [RtMidiOutPtr].
     * After this operation using the `RtMidiOutPtr` in any way within the RtMidi library
     * (ie [rtmidi_close_port]) will cause a fatal VM error as a result of a segfault in the native code.
     * It is only safe to query the struct's data such as [RtMidiPtr.ok] or [RtMidiPtr.getPointer] etc.
     * Original signature : `void rtmidi_out_free(RtMidiOutPtr)`<br></br>
     *
     * @param device the [RtMidiOutPtr] to free
     */
    fun rtmidi_out_free(device: RtMidiOutPtr)

    /**
     * See RtMidiOut::getCurrentApi().
     * Original signature : `RtMidiApi rtmidi_out_get_current_api(RtMidiPtr)`
     */
    fun rtmidi_out_get_current_api(device: RtMidiOutPtr): Int

    /**
     * See RtMidiOut::sendMessage().
     * Original signature : `int rtmidi_out_send_message(RtMidiOutPtr, const unsigned char*, int)`
     */
    fun rtmidi_out_send_message(device: RtMidiOutPtr, message: ByteArray, length: Int): Int
}