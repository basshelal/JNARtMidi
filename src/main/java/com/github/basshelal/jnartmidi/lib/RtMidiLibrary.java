package com.github.basshelal.jnartmidi.lib;

import com.sun.jna.Callback;
import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

import java.nio.ByteBuffer;

// JNAerator command used:
// java -jar jnaerator.jar -library rtmidi rtmidi_c.h librtmidi.so -o . -v -noJar -noComp -f -runtime JNA

/**
 * Wraps RtMidi's C interface, autogenerated by JNAerator with manual adjustments
 * <a href= "https://www.music.mcgill.ca/~gary/rtmidi/group__C-interface.html">RtMidi C interface documentation</a>.
 *
 * Implementation found in {@link RtMidiLibraryNative} where the methods are "implemented"
 * using the {@code native} keyword.
 *
 * To use the library use {@link RtMidiLibrary#getInstance()}.
 *
 * @author Bassam Helal
 */
@SuppressWarnings({"UnnecessaryInterfaceModifier", "unused"})
public interface RtMidiLibrary extends Library {

    public static final String LIBRARY_NAME = "rtmidi";

    /**
     * @return A usable {@link RtMidiLibrary} instance
     */
    public static RtMidiLibrary getInstance() { return RtMidiLibraryNative.getInstance(); }

    //=============================================================================================
    //====================================     Types     ==========================================
    //=============================================================================================

    /**
     * ! \brief MIDI API specifier arguments.  See \ref RtMidi::Api.<br>
     * <i>native declaration : rtmidi_c.h:23</i><br>
     * enum values
     */
    public static interface RtMidiApi {
        /**
         * < Search for a working compiled API.<br>
         * <i>native declaration : rtmidi_c.h:16</i>
         */
        public static final int RTMIDI_API_UNSPECIFIED = 0;
        /**
         * < Macintosh OS-X CoreMIDI API.<br>
         * <i>native declaration : rtmidi_c.h:17</i>
         */
        public static final int RTMIDI_API_MACOSX_CORE = 1;
        /**
         * < The Advanced Linux Sound Architecture API.<br>
         * <i>native declaration : rtmidi_c.h:18</i>
         */
        public static final int RTMIDI_API_LINUX_ALSA = 2;
        /**
         * < The Jack Low-Latency MIDI Server API.<br>
         * <i>native declaration : rtmidi_c.h:19</i>
         */
        public static final int RTMIDI_API_UNIX_JACK = 3;
        /**
         * < The Microsoft Multimedia MIDI API.<br>
         * <i>native declaration : rtmidi_c.h:20</i>
         */
        public static final int RTMIDI_API_WINDOWS_MM = 4;
        /**
         * < A compilable but non-functional API.<br>
         * <i>native declaration : rtmidi_c.h:21</i>
         */
        public static final int RTMIDI_API_RTMIDI_DUMMY = 5;
        /**
         * < Number of values in this enum.<br>
         * <i>native declaration : rtmidi_c.h:22</i>
         */
        public static final int RTMIDI_API_NUM = 6;
    }

    /**
     * ! \brief Defined RtMidiError types. See \ref RtMidiError::Type.<br>
     * <i>native declaration : rtmidi_c.h:37</i><br>
     * enum values
     */
    public static interface RtMidiErrorType {
        /**
         * < A non-critical error.<br>
         * <i>native declaration : rtmidi_c.h:26</i>
         */
        public static final int RTMIDI_ERROR_WARNING = 0;
        /**
         * < A non-critical error which might be useful for debugging.<br>
         * <i>native declaration : rtmidi_c.h:27</i>
         */
        public static final int RTMIDI_ERROR_DEBUG_WARNING = 1;
        /**
         * < The default, unspecified error type.<br>
         * <i>native declaration : rtmidi_c.h:28</i>
         */
        public static final int RTMIDI_ERROR_UNSPECIFIED = 2;
        /**
         * < No devices found on system.<br>
         * <i>native declaration : rtmidi_c.h:29</i>
         */
        public static final int RTMIDI_ERROR_NO_DEVICES_FOUND = 3;
        /**
         * < An invalid device ID was specified.<br>
         * <i>native declaration : rtmidi_c.h:30</i>
         */
        public static final int RTMIDI_ERROR_INVALID_DEVICE = 4;
        /**
         * < An error occured during memory allocation.<br>
         * <i>native declaration : rtmidi_c.h:31</i>
         */
        public static final int RTMIDI_ERROR_MEMORY_ERROR = 5;
        /**
         * < An invalid parameter was specified to a function.<br>
         * <i>native declaration : rtmidi_c.h:32</i>
         */
        public static final int RTMIDI_ERROR_INVALID_PARAMETER = 6;
        /**
         * < The function was called incorrectly.<br>
         * <i>native declaration : rtmidi_c.h:33</i>
         */
        public static final int RTMIDI_ERROR_INVALID_USE = 7;
        /**
         * < A system driver error occured.<br>
         * <i>native declaration : rtmidi_c.h:34</i>
         */
        public static final int RTMIDI_ERROR_DRIVER_ERROR = 8;
        /**
         * < A system error occured.<br>
         * <i>native declaration : rtmidi_c.h:35</i>
         */
        public static final int RTMIDI_ERROR_SYSTEM_ERROR = 9;
        /**
         * < A thread error occured.<br>
         * <i>native declaration : rtmidi_c.h:36</i>
         */
        public static final int RTMIDI_ERROR_THREAD_ERROR = 10;
    }

    /**
     * typedef void(* RtMidiCCallback) (double timeStamp, const unsigned char* message, size_t messageSize, void* userData);
     * <i>native declaration : rtmidi_c.h:45</i>
     */
    public interface RtMidiCCallback extends Callback {
        // RealTimeCritical
        public void invoke(final double timeStamp, final Pointer message, final NativeSize messageSize, final Pointer userData);
    }

    /**
     * 'size_t' C type (32 bits on 32 bits platforms, 64 bits on 64 bits platforms).
     * Can be also used to model the 'long' C type for libraries known to be compiled with GCC or LLVM even on Windows.
     * (NativeLong on Windows is only okay with MSVC++ libraries, as 'long' on Windows 64 bits will be 32 bits with MSVC++ and 64 bits with GCC/mingw)
     *
     * @author ochafik
     */
    public class NativeSize extends IntegerType {

        /**
         * Size of a size_t integer, in bytes.
         */
        public static int SIZE = Native.SIZE_T_SIZE; // Platform.is64Bit() ? 8 : 4;

        /**
         * Create a zero-valued Size.
         */
        public NativeSize() { this(0); }

        /**
         * Create a Size with the given value.
         */
        public NativeSize(long value) { super(SIZE, value); }
    }

    /**
     * Like {@link NativeSize} but passed by reference, ie in C code size_t *
     */
    public class NativeSizeByReference extends ByReference {
        public NativeSizeByReference() { this(0); }

        public NativeSizeByReference(int value) { this(new NativeSize(value)); }

        public NativeSizeByReference(NativeSize value) {
            super(NativeSize.SIZE);
            setValue(value);
        }

        public NativeSize getValue() {
            if (NativeSize.SIZE == 4) return new NativeSize(getPointer().getInt(0));
            else if (NativeSize.SIZE == 8) return new NativeSize(getPointer().getLong(0));
            else throw new RuntimeException("GCCLong has to be either 4 or 8 bytes.");
        }

        public void setValue(NativeSize value) {
            if (NativeSize.SIZE == 4)
                getPointer().setInt(0, value.intValue());
            else if (NativeSize.SIZE == 8)
                getPointer().setLong(0, value.longValue());
            else
                throw new RuntimeException("GCCLong has to be either 4 or 8 bytes.");
        }
    }

    public class RtMidiInPtr extends RtMidiPtr {}

    public class RtMidiOutPtr extends RtMidiPtr {}

    //=============================================================================================
    //=================================     RtMidi API     ========================================
    //=============================================================================================

    /**
     * Determine the available compiled MIDI APIs.
     * If the given {@code apis} parameter is {@code null}, returns the number of available APIs.
     * Otherwise, fill the given {@code apis} array with the {@link RtMidiApi} values.<br>
     *
     * Original signature : <code>int rtmidi_get_compiled_api(RtMidiApi*, unsigned int)</code>
     *
     * @param apis      An array to be filled or a null, ensure the array is at least
     *                  {@link RtMidiApi#RTMIDI_API_NUM} in size.
     * @param apis_size size of the passed in array {@code apis}, ignored if {@code apis} is {@code null}
     * @return number of items needed for {@code apis} array if {@code apis == null}, or
     *         number of items written to {@code apis} array otherwise.
     *         A negative return value indicates an error.
     */
    public int rtmidi_get_compiled_api(int[] apis, int apis_size);

    /**
     * ! See \ref RtMidi::getApiName().<br>
     * Original signature : <code>char* rtmidi_api_name(RtMidiApi)</code><br>
     * <i>native declaration : rtmidi_c.h:63</i>
     */
    public String rtmidi_api_name(int api);

    /**
     * ! See \ref RtMidi::getApiDisplayName().<br>
     * Original signature : <code>char* rtmidi_api_display_name(RtMidiApi)</code><br>
     * <i>native declaration : rtmidi_c.h:68</i>
     */
    public String rtmidi_api_display_name(int api);

    /**
     * ! See \ref RtMidi::getCompiledApiByName().<br>
     * Original signature : <code>RtMidiApi rtmidi_compiled_api_by_name(const char*)</code><br>
     * <i>native declaration : rtmidi_c.h:73</i>
     */
    public int rtmidi_compiled_api_by_name(String name);

    /**
     * \brief Open a MIDI port.<br>
     * \param port      Must be greater than 0<br>
     * \param portName  Name for the application port.<br>
     * See RtMidi::openPort().<br>
     * Original signature : <code>void rtmidi_open_port(RtMidiPtr, unsigned int, const char*)</code><br>
     * <i>native declaration : rtmidi_c.h:86</i>
     */
    public void rtmidi_open_port(RtMidiPtr device, int portNumber, String portName);

    /**
     * \brief Creates a virtual MIDI port to which other software applications can <br>
     * connect.  <br>
     * \param portName  Name for the application port.<br>
     * See RtMidi::openVirtualPort().<br>
     * Original signature : <code>void rtmidi_open_virtual_port(RtMidiPtr, const char*)</code><br>
     * <i>native declaration : rtmidi_c.h:94</i>
     */
    public void rtmidi_open_virtual_port(RtMidiPtr device, String portName);

    /**
     * \brief Close a MIDI connection.<br>
     * See RtMidi::closePort().<br>
     * Original signature : <code>void rtmidi_close_port(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:100</i>
     */
    public void rtmidi_close_port(RtMidiPtr device);

    /**
     * \brief Return the number of available MIDI ports.<br>
     * See RtMidi::getPortCount().<br>
     * Original signature : <code>int rtmidi_get_port_count(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:106</i>
     */
    public int rtmidi_get_port_count(RtMidiPtr device);

    /**
     * \brief Return a string identifier for the specified MIDI input port number.<br>
     * See RtMidi::getPortName().<br>
     * Original signature : <code>char* rtmidi_get_port_name(RtMidiPtr, unsigned int)</code><br>
     * <i>native declaration : rtmidi_c.h:112</i>
     */
    public String rtmidi_get_port_name(RtMidiPtr device, int portNumber);

    //=============================================================================================
    //===============================     RtMidiIn API     ========================================
    //=============================================================================================

    /**
     * Create a default {@link RtMidiInPtr} value, with no initialization, RtMidi will choose its own API and
     * client name, to set these yourself use {@link #rtmidi_in_create}
     * Original signature : <code>RtMidiInPtr rtmidi_in_create_default()</code>
     */
    public RtMidiInPtr rtmidi_in_create_default();

    /**
     * Create a {@link RtMidiInPtr} value, with given api, clientName and queueSizeLimit.
     * Original signature : <code>RtMidiInPtr rtmidi_in_create(RtMidiApi, const char*, unsigned int)</code><br>
     *
     * @param api            An {@link RtMidiApi} to use or 0 ie {@link RtMidiApi#RTMIDI_API_UNSPECIFIED}
     *                       to let RtMidi choose the first suitable API
     * @param clientName     Non null client name, this will be used to group the ports that are created by the
     *                       application.
     * @param queueSizeLimit Size of the MIDI input queue, negative values are not allowed and 0 may cause segfaults
     *                       later on, this allocates a queue of this size so keep it reasonable.
     */
    // TODO: 22/02/2021 queueSizeLimit is bad...
    //  basically, it cannot be null (even if using Pointer), cannot be 0 because segfault later and
    //  if a small number, the message queue limit will be reached which floods our output with error messages saying
    //  MidiInAlsa: message queue limit reached!!
    //  using rtmidi_in_create_default() will give a queueSizeLimit of 100
    //  this is regardless of anything because there is a midi message handler created by the library that pushes
    //  to the queue and if the queue is full, will output errors,
    //  Using a large number to avoid flooding is a bad idea because that memory is allocated
    //  The only thing we can do is allow it to flood our output, a solution would be to have the error logging be
    //  conditional in the C++ code so that a version can be built where this will be silent
    // TODO: 22/02/2021 Create a GitHub issue about this
    public RtMidiInPtr rtmidi_in_create(int api, String clientName, int queueSizeLimit);

    /**
     * Free the given {@link RtMidiInPtr}.
     * After this operation using the {@code RtMidiInPtr} in any way within the RtMidi library
     * (ie {@link #rtmidi_close_port}) will cause a fatal VM error as a result of a segfault in the native code.
     * It is only safe to query the struct's data such as {@link RtMidiPtr#ok} or {@link RtMidiPtr#getPointer()} etc.
     * Original signature : <code>void rtmidi_in_free(RtMidiInPtr)</code><br>
     *
     * @param device the {@link RtMidiInPtr} to free
     */
    public void rtmidi_in_free(RtMidiInPtr device);

    /**
     * ! See \ref RtMidiIn::getCurrentApi().<br>
     * Original signature : <code>RtMidiApi rtmidi_in_get_current_api(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:139</i>
     */
    public int rtmidi_in_get_current_api(RtMidiInPtr device);

    /**
     * ! See \ref RtMidiIn::setCallback().<br>
     * Original signature : <code>void rtmidi_in_set_callback(RtMidiInPtr, RtMidiCCallback, void*)</code><br>
     * <i>native declaration : rtmidi_c.h:144</i>
     */
    public void rtmidi_in_set_callback(RtMidiInPtr device, RtMidiLibrary.RtMidiCCallback callback, Pointer userData);

    /**
     * ! See \ref RtMidiIn::cancelCallback().<br>
     * Original signature : <code>void rtmidi_in_cancel_callback(RtMidiInPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:149</i>
     */
    public void rtmidi_in_cancel_callback(RtMidiInPtr device);

    /**
     * ! See \ref RtMidiIn::ignoreTypes().<br>
     * Original signature : <code>void rtmidi_in_ignore_types(RtMidiInPtr, bool, bool, bool)</code><br>
     * <i>native declaration : rtmidi_c.h:154</i>
     */
    public void rtmidi_in_ignore_types(RtMidiInPtr device, boolean midiSysex, boolean midiTime, boolean midiSense);

    /**
     * Fill the user-provided array with the data bytes for the next available<br>
     * MIDI message in the input queue and return the event delta-time in seconds.<br>
     * \param message   Must point to a char* that is already allocated.<br>
     * SYSEX messages maximum size being 1024, a statically<br>
     * allocated array could<br>
     * be sufficient. <br>
     * \param size      Is used to return the size of the message obtained. <br>
     * See RtMidiIn::getMessage().<br>
     * Original signature : <code>double rtmidi_in_get_message(RtMidiInPtr, unsigned char*, size_t*)</code><br>
     * <i>native declaration : rtmidi_c.h:166</i>
     */
    public double rtmidi_in_get_message(RtMidiInPtr device, ByteBuffer message, NativeSizeByReference size);

    //=============================================================================================
    //================================     RtMidiOut API     ======================================
    //=============================================================================================

    /**
     * ! \brief Create a default RtMidiInPtr value, with no initialization.<br>
     * Original signature : <code>RtMidiOutPtr rtmidi_out_create_default()</code><br>
     * <i>native declaration : rtmidi_c.h:171</i>
     */
    public RtMidiOutPtr rtmidi_out_create_default();

    /**
     * \brief Create a RtMidiOutPtr value, with given and clientName.<br>
     * \param api            An optional API id can be specified.<br>
     * \param clientName     An optional client name can be specified. This<br>
     * will be used to group the ports that are created<br>
     * by the application.<br>
     * See RtMidiOut::RtMidiOut().<br>
     * Original signature : <code>RtMidiOutPtr rtmidi_out_create(RtMidiApi, const char*)</code><br>
     * <i>native declaration : rtmidi_c.h:181</i>
     */
    public RtMidiOutPtr rtmidi_out_create(int api, String clientName);

    /**
     * Free the given {@link RtMidiOutPtr}.
     * After this operation using the {@code RtMidiOutPtr} in any way within the RtMidi library
     * (ie {@link #rtmidi_close_port}) will cause a fatal VM error as a result of a segfault in the native code.
     * It is only safe to query the struct's data such as {@link RtMidiPtr#ok} or {@link RtMidiPtr#getPointer()} etc.
     * Original signature : <code>void rtmidi_out_free(RtMidiOutPtr)</code><br>
     *
     * @param device the {@link RtMidiOutPtr} to free
     */
    public void rtmidi_out_free(RtMidiOutPtr device);

    /**
     * ! See \ref RtMidiOut::getCurrentApi().<br>
     * Original signature : <code>RtMidiApi rtmidi_out_get_current_api(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:191</i>
     */
    public int rtmidi_out_get_current_api(RtMidiOutPtr device);

    /**
     * ! See \ref RtMidiOut::sendMessage().<br>
     * Original signature : <code>int rtmidi_out_send_message(RtMidiOutPtr, const unsigned char*, int)</code><br>
     * <i>native declaration : rtmidi_c.h:196</i>
     */
    public int rtmidi_out_send_message(RtMidiOutPtr device, byte[] message, int length);

}
