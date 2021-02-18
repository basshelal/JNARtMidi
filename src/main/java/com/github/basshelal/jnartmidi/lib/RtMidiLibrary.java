package com.github.basshelal.jnartmidi.lib;

import com.sun.jna.Callback;
import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

// java -jar jnaerator.jar -library rtmidi rtmidi_c.h librtmidi.so -o . -v -noJar -noComp -f -runtime JNA

/**
 * Wraps RtMidi's C interface, autogenerated by JNAerator with some manual adjustments
 * https://www.music.mcgill.ca/~gary/rtmidi/group__C-interface.html
 */
public interface RtMidiLibrary extends Library {

    public static final String LIBRARY_NAME = "rtmidi";

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
     * size_t *
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

    //=============================================================================================
    //=================================     RtMidi API     ========================================
    //=============================================================================================

    /**
     * \brief Determine the available compiled MIDI APIs.<br>
     * If the given `apis` parameter is null, returns the number of available APIs.<br>
     * Otherwise, fill the given apis array with the RtMidi::Api values.<br>
     * \param apis  An array or a null value.<br>
     * \param apis_size  Number of elements pointed to by apis<br>
     * \return number of items needed for apis array if apis==NULL, or<br>
     * number of items written to apis array otherwise.  A negative<br>
     * return value indicates an error.<br>
     * See \ref RtMidi::getCompiledApi().<br>
     * Original signature : <code>int rtmidi_get_compiled_api(RtMidiApi*, unsigned int)</code><br>
     * <i>native declaration : rtmidi_c.h:58</i>
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
    public void rtmidi_open_port(RtMidiWrapper device, int portNumber, String portName);

    /**
     * \brief Creates a virtual MIDI port to which other software applications can <br>
     * connect.  <br>
     * \param portName  Name for the application port.<br>
     * See RtMidi::openVirtualPort().<br>
     * Original signature : <code>void rtmidi_open_virtual_port(RtMidiPtr, const char*)</code><br>
     * <i>native declaration : rtmidi_c.h:94</i>
     */
    public void rtmidi_open_virtual_port(RtMidiWrapper device, String portName);

    /**
     * \brief Close a MIDI connection.<br>
     * See RtMidi::closePort().<br>
     * Original signature : <code>void rtmidi_close_port(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:100</i>
     */
    public void rtmidi_close_port(RtMidiWrapper device);

    /**
     * \brief Return the number of available MIDI ports.<br>
     * See RtMidi::getPortCount().<br>
     * Original signature : <code>int rtmidi_get_port_count(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:106</i>
     */
    public int rtmidi_get_port_count(RtMidiWrapper device);

    /**
     * \brief Return a string identifier for the specified MIDI input port number.<br>
     * See RtMidi::getPortName().<br>
     * Original signature : <code>char* rtmidi_get_port_name(RtMidiPtr, unsigned int)</code><br>
     * <i>native declaration : rtmidi_c.h:112</i>
     */
    public String rtmidi_get_port_name(RtMidiWrapper device, int portNumber);

    //=============================================================================================
    //===============================     RtMidiIn API     ========================================
    //=============================================================================================

    /**
     * ! \brief Create a default RtMidiInPtr value, with no initialization.<br>
     * Original signature : <code>RtMidiInPtr rtmidi_in_create_default()</code><br>
     * <i>native declaration : rtmidi_c.h:117</i>
     */
    public RtMidiWrapper rtmidi_in_create_default();

    /**
     * \brief Create a  RtMidiInPtr value, with given api, clientName and queueSizeLimit.<br>
     * \param api            An optional API id can be specified.<br>
     * \param clientName     An optional client name can be specified. This<br>
     * will be used to group the ports that are created<br>
     * by the application.<br>
     * \param queueSizeLimit An optional size of the MIDI input queue can be<br>
     * specified.<br>
     * See RtMidiIn::RtMidiIn().<br>
     * Original signature : <code>RtMidiInPtr rtmidi_in_create(RtMidiApi, const char*, unsigned int)</code><br>
     * <i>native declaration : rtmidi_c.h:129</i>
     */
    public RtMidiWrapper rtmidi_in_create(int api, String clientName, int queueSizeLimit);

    /**
     * ! \brief Free the given RtMidiInPtr.<br>
     * Original signature : <code>void rtmidi_in_free(RtMidiInPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:134</i>
     */
    public void rtmidi_in_free(RtMidiWrapper device);

    /**
     * ! See \ref RtMidiIn::getCurrentApi().<br>
     * Original signature : <code>RtMidiApi rtmidi_in_get_current_api(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:139</i>
     */
    public int rtmidi_in_get_current_api(RtMidiWrapper device);

    /**
     * ! See \ref RtMidiIn::setCallback().<br>
     * Original signature : <code>void rtmidi_in_set_callback(RtMidiInPtr, RtMidiCCallback, void*)</code><br>
     * <i>native declaration : rtmidi_c.h:144</i>
     */
    public void rtmidi_in_set_callback(RtMidiWrapper device, RtMidiLibrary.RtMidiCCallback callback, Pointer userData);

    /**
     * ! See \ref RtMidiIn::cancelCallback().<br>
     * Original signature : <code>void rtmidi_in_cancel_callback(RtMidiInPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:149</i>
     */
    public void rtmidi_in_cancel_callback(RtMidiWrapper device);

    /**
     * ! See \ref RtMidiIn::ignoreTypes().<br>
     * Original signature : <code>void rtmidi_in_ignore_types(RtMidiInPtr, bool, bool, bool)</code><br>
     * <i>native declaration : rtmidi_c.h:154</i>
     */
    public void rtmidi_in_ignore_types(RtMidiWrapper device, boolean midiSysex, boolean midiTime, boolean midiSense);

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
    public double rtmidi_in_get_message(RtMidiWrapper device, byte[] message, NativeSize size);

    //=============================================================================================
    //================================     RtMidiOut API     ======================================
    //=============================================================================================

    /**
     * ! \brief Create a default RtMidiInPtr value, with no initialization.<br>
     * Original signature : <code>RtMidiOutPtr rtmidi_out_create_default()</code><br>
     * <i>native declaration : rtmidi_c.h:171</i>
     */
    public RtMidiWrapper rtmidi_out_create_default();

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
    public RtMidiWrapper rtmidi_out_create(int api, String clientName);

    /**
     * ! \brief Free the given RtMidiOutPtr.<br>
     * Original signature : <code>void rtmidi_out_free(RtMidiOutPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:186</i>
     */
    public void rtmidi_out_free(RtMidiWrapper device);

    /**
     * ! See \ref RtMidiOut::getCurrentApi().<br>
     * Original signature : <code>RtMidiApi rtmidi_out_get_current_api(RtMidiPtr)</code><br>
     * <i>native declaration : rtmidi_c.h:191</i>
     */
    public int rtmidi_out_get_current_api(RtMidiWrapper device);

    /**
     * ! See \ref RtMidiOut::sendMessage().<br>
     * Original signature : <code>int rtmidi_out_send_message(RtMidiOutPtr, const unsigned char*, int)</code><br>
     * <i>native declaration : rtmidi_c.h:196</i>
     */
    public int rtmidi_out_send_message(RtMidiWrapper device, byte[] message, int length);

}
