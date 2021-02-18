package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiWrapper;

// TODO: 18/02/2021 Switch to singleton?
public class RtMidi {

    private RtMidi() {}

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
        RtMidiWrapper midiInWrapper = RtMidiLibrary.getInstance().rtmidi_in_create_default();
        int deviceCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(midiInWrapper);
        MidiPort.Info[] result = new MidiInPort.Info[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            result[i] = new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(midiInWrapper, i), i, MidiPort.Info.Type.IN);
        }
        RtMidiLibrary.getInstance().rtmidi_in_free(midiInWrapper);
        return result;
    }

    public static MidiPort.Info[] midiOutPorts() {
        RtMidiWrapper midiOutWrapper = RtMidiLibrary.getInstance().rtmidi_out_create_default();
        int deviceCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(midiOutWrapper);
        MidiPort.Info[] result = new MidiPort.Info[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            result[i] = new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(midiOutWrapper, i), i, MidiPort.Info.Type.OUT);
        }
        RtMidiLibrary.getInstance().rtmidi_out_free(midiOutWrapper);
        return result;
    }

}
