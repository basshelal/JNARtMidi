package com.github.basshelal.jnartmidi.lib;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class RtMidiLibraryNative implements RtMidiLibrary {

    private static RtMidiLibrary INSTANCE = null;

    static {
        Native.register(RtMidiLibrary.LIBRARY_NAME);
    }

    public static RtMidiLibrary getInstance() {
        if (INSTANCE == null) INSTANCE = new RtMidiLibraryNative();
        return INSTANCE;
    }

    public native int rtmidi_get_compiled_api(int[] apis, int apis_size);

    public native String rtmidi_api_name(int api);

    public native String rtmidi_api_display_name(int api);

    public native int rtmidi_compiled_api_by_name(String name);

    public native void rtmidi_open_port(RtMidiWrapper device, int portNumber, String portName);

    public native void rtmidi_open_virtual_port(RtMidiWrapper device, String portName);

    public native void rtmidi_close_port(RtMidiWrapper device);

    public native int rtmidi_get_port_count(RtMidiWrapper device);

    public native String rtmidi_get_port_name(RtMidiWrapper device, int portNumber);

    public native RtMidiWrapper rtmidi_in_create_default();

    public native RtMidiWrapper rtmidi_in_create(int api, String clientName, int queueSizeLimit);

    public native void rtmidi_in_free(RtMidiWrapper device);

    public native int rtmidi_in_get_current_api(RtMidiWrapper device);

    public native void rtmidi_in_set_callback(RtMidiWrapper device, RtMidiCCallback callback, Pointer userData);

    public native void rtmidi_in_cancel_callback(RtMidiWrapper device);

    public native void rtmidi_in_ignore_types(RtMidiWrapper device, boolean midiSysex, boolean midiTime, boolean midiSense);

    public native double rtmidi_in_get_message(RtMidiWrapper device, byte[] message, int size);

    public native RtMidiWrapper rtmidi_out_create_default();

    public native RtMidiWrapper rtmidi_out_create(int api, String clientName);

    public native void rtmidi_out_free(RtMidiWrapper device);

    public native int rtmidi_out_get_current_api(RtMidiWrapper device);

    public native int rtmidi_out_send_message(RtMidiWrapper device, byte[] message, int length);

}
