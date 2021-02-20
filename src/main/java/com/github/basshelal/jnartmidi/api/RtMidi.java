package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;
import com.sun.jna.Platform;

// TODO: 18/02/2021 Switch to singleton?
public class RtMidi {

    private RtMidi() {}

    public static boolean supportsVirtualPorts() { return !Platform.isWindows(); }

    public static RtMidiApi[] getAvailableApis() throws RtMidiException {
        int[] arr = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int written = RtMidiLibrary.getInstance().rtmidi_get_compiled_api(arr, arr.length);

        if (written < 0) throw new RtMidiException("Error trying to get compiled apis");
        else {
            RtMidiApi[] result = new RtMidiApi[written];
            for (int i = 0; i < written; i++) {
                result[i] = RtMidiApi.fromInt(arr[i]);
            }
            return result;
        }
    }

    public static MidiPort.Info[] midiInPorts() {
        RtMidiInPtr midiInPtr = RtMidiLibrary.getInstance().rtmidi_in_create_default();
        int deviceCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(midiInPtr);
        MidiPort.Info[] result = new MidiInPort.Info[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            result[i] = new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(midiInPtr, i), i, MidiPort.Info.Type.IN);
        }
        RtMidiLibrary.getInstance().rtmidi_in_free(midiInPtr);
        return result;
    }

    public static MidiPort.Info[] midiOutPorts() {
        RtMidiOutPtr midiOutPtr = RtMidiLibrary.getInstance().rtmidi_out_create_default();
        int deviceCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(midiOutPtr);
        MidiPort.Info[] result = new MidiPort.Info[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            result[i] = new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(midiOutPtr, i), i, MidiPort.Info.Type.OUT);
        }
        RtMidiLibrary.getInstance().rtmidi_out_free(midiOutPtr);
        return result;
    }

}
