package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;
import com.sun.jna.Platform;

import java.util.ArrayList;
import java.util.List;

// TODO: 18/02/2021 Switch to singleton?
public class RtMidi {

    private RtMidi() {}

    public static boolean supportsVirtualPorts() { return !Platform.isWindows(); }

    public static List<RtMidiApi> getAvailableApis() throws RtMidiException {
        int[] arr = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int written = RtMidiLibrary.getInstance().rtmidi_get_compiled_api(arr, arr.length);

        if (written < 0) throw new RtMidiException("Error trying to get compiled apis");
        else {
            List<RtMidiApi> result = new ArrayList<>(written);
            for (int i = 0; i < written; i++)
                result.add(i, RtMidiApi.fromInt(arr[i]));
            return result;
        }
    }

    public static List<MidiPort.Info> readableMidiPorts() throws RtMidiException {
        RtMidiInPtr ptr = RtMidiLibrary.getInstance().rtmidi_in_create_default();
        int portCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(ptr);
        if (ptr != null && !ptr.ok)
            throw new RtMidiException("An error occurred in the native code of RtMidi\n" + ptr.msg);
        List<MidiPort.Info> result = new ArrayList<>(portCount);
        for (int i = 0; i < portCount; i++) {
            result.add(i,
                    new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(ptr, i), i, MidiPort.Info.Type.READABLE));
        }
        RtMidiLibrary.getInstance().rtmidi_in_free(ptr);
        return result;
    }

    public static List<MidiPort.Info> writableMidiPorts() throws RtMidiException {
        RtMidiOutPtr ptr = RtMidiLibrary.getInstance().rtmidi_out_create_default();
        int portCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(ptr);
        if (ptr != null && !ptr.ok)
            throw new RtMidiException("An error occurred in the native code of RtMidi\n" + ptr.msg);
        List<MidiPort.Info> result = new ArrayList<>(portCount);
        for (int i = 0; i < portCount; i++) {
            result.add(i,
                    new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(ptr, i), i, MidiPort.Info.Type.WRITABLE));
        }
        RtMidiLibrary.getInstance().rtmidi_out_free(ptr);
        return result;
    }

}
