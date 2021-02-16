package com.github.basshelal.jnartmidi.lib;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class RtMidiLibraryNative implements RtMidiLibrary {

    static {
        Native.register(RtMidiLibrary.LIBRARY_NAME);
    }

    @Override
    public native int rtmidi_get_compiled_api(IntBuffer apis, int apis_size);

    @Override
    public native String rtmidi_api_name(int api);

    @Override
    public native String rtmidi_api_display_name(int api);

    @Override
    public native int rtmidi_compiled_api_by_name(Pointer name);

    @Override
    public native int rtmidi_compiled_api_by_name(String name);

    @Override
    public native void rtmidi_error(int type, Pointer errorString);

    @Override
    public native void rtmidi_error(int type, String errorString);

    @Override
    public native void rtmidi_open_port(RtMidiWrapper device, int portNumber, String portName);

    @Override
    public native void rtmidi_open_virtual_port(RtMidiWrapper device, String portName);

    @Override
    public native void rtmidi_close_port(RtMidiWrapper device);

    @Override
    public native int rtmidi_get_port_count(RtMidiWrapper device);

    @Override
    public native String rtmidi_get_port_name(RtMidiWrapper device, int portNumber);

    @Override
    public native RtMidiWrapper rtmidi_in_create_default();

    @Override
    public native RtMidiWrapper rtmidi_in_create(int api, String clientName, int queueSizeLimit);

    @Override
    public native void rtmidi_in_free(RtMidiWrapper device);

    @Override
    public native int rtmidi_in_get_current_api(RtMidiWrapper device);

    @Override
    public native void rtmidi_in_set_callback(RtMidiWrapper device, RtMidiCCallback callback, Pointer userData);

    @Override
    public native void rtmidi_in_cancel_callback(RtMidiWrapper device);

    @Override
    public native void rtmidi_in_ignore_types(RtMidiWrapper device, byte midiSysex, byte midiTime, byte midiSense);

    @Override
    public native double rtmidi_in_get_message(RtMidiWrapper device, ByteBuffer message, NativeSizeByReference size);

    @Override
    public native RtMidiWrapper rtmidi_out_create_default();

    @Override
    public native RtMidiWrapper rtmidi_out_create(int api, String clientName);

    @Override
    public native void rtmidi_out_free(RtMidiWrapper device);

    @Override
    public native int rtmidi_out_get_current_api(RtMidiWrapper device);

    @Override
    public native int rtmidi_out_send_message(RtMidiWrapper device, byte[] message, int length);

}
