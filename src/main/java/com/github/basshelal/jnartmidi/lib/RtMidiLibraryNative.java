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

    public native void rtmidi_open_port(RtMidiPtr device, int portNumber, String portName);

    public native void rtmidi_open_virtual_port(RtMidiPtr device, String portName);

    public native void rtmidi_close_port(RtMidiPtr device);

    public native int rtmidi_get_port_count(RtMidiPtr device);

    public native String rtmidi_get_port_name(RtMidiPtr device, int portNumber);

    public native RtMidiInPtr rtmidi_in_create_default();

    public native RtMidiInPtr rtmidi_in_create(int api, String clientName, int queueSizeLimit);

    public native void rtmidi_in_free(RtMidiInPtr device);

    public native int rtmidi_in_get_current_api(RtMidiInPtr device);

    public native void rtmidi_in_set_callback(RtMidiInPtr device, RtMidiCCallback callback, Pointer userData);

    public native void rtmidi_in_cancel_callback(RtMidiInPtr device);

    public native void rtmidi_in_ignore_types(RtMidiInPtr device, boolean midiSysex, boolean midiTime, boolean midiSense);

    public native double rtmidi_in_get_message(RtMidiInPtr device, byte[] message, int size);

    public native RtMidiOutPtr rtmidi_out_create_default();

    public native RtMidiOutPtr rtmidi_out_create(int api, String clientName);

    public native void rtmidi_out_free(RtMidiOutPtr device);

    public native int rtmidi_out_get_current_api(RtMidiOutPtr device);

    public native int rtmidi_out_send_message(RtMidiOutPtr device, byte[] message, int length);

}
