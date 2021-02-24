package dev.basshelal.jnartmidi.api;

import com.sun.jna.Platform;

import java.util.ArrayList;
import java.util.List;

import dev.basshelal.jnartmidi.api.exceptions.RtMidiNativeException;
import dev.basshelal.jnartmidi.lib.RtMidiLibrary;
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;

/**
 * The entry point to the JNARtMidi Library.
 * <br>
 * Note: Before using anything in the library be sure to read {@link RtMidiLibraryLoader}
 * first to ensure the RtMidi native library is loaded correctly.
 * <br>
 * Getting Started:<br>
 * Create a {@link ReadableMidiPort} by getting a {@link MidiPort.Info} from {@link #readableMidiPorts()},
 * similarly for {@link WritableMidiPort}.
 * <br>
 * {@link MidiPort}s have a {@link MidiPort#MidiPort(MidiPort.Info, RtMidiApi, String) constructor}
 * which allows you to choose the {@link RtMidiApi} to use on the port. The {@link RtMidiApi}s available to use can
 * be retrieved using {@link #getAvailableApis()}.
 *
 * @author Bassam Helal
 */
public /* static */ class RtMidi {

    private RtMidi() { /* No instances */ }

    /**
     * @return true if this platform supports virtual ports, false otherwise,
     *         currently only Windows does not support virtual ports
     */
    public static boolean supportsVirtualPorts() { return !Platform.isWindows(); }

    /**
     * @return the list of all available {@link RtMidiApi}s usable on this machine,
     *         this should be at most 2, ie on Unix (ALSA and JACK)
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public static List<RtMidiApi> getAvailableApis() throws RtMidiNativeException {
        int[] arr = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int written = RtMidiLibrary.getInstance().rtmidi_get_compiled_api(arr, arr.length);

        if (written < 0) throw new RtMidiNativeException("Error trying to get compiled apis");
        else {
            List<RtMidiApi> result = new ArrayList<>(written);
            for (int i = 0; i < written; i++)
                result.add(i, RtMidiApi.fromInt(arr[i]));
            return result;
        }
    }

    /**
     * @return a list of {@link MidiPort.Info}s for all {@link ReadableMidiPort}s on the system
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public static List<MidiPort.Info> readableMidiPorts() throws RtMidiNativeException {
        RtMidiInPtr ptr = RtMidiLibrary.getInstance().rtmidi_in_create_default();
        int portCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(ptr);
        if (ptr != null && !ptr.ok) throw new RtMidiNativeException(ptr);
        List<MidiPort.Info> result = new ArrayList<>(portCount);
        for (int i = 0; i < portCount; i++) {
            result.add(i,
                    new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(ptr, i), i, MidiPort.Info.Type.READABLE));
        }
        RtMidiLibrary.getInstance().rtmidi_in_free(ptr);
        return result;
    }

    /**
     * @return a list of {@link MidiPort.Info}s for all {@link WritableMidiPort}s on the system
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public static List<MidiPort.Info> writableMidiPorts() throws RtMidiNativeException {
        RtMidiOutPtr ptr = RtMidiLibrary.getInstance().rtmidi_out_create_default();
        int portCount = RtMidiLibrary.getInstance().rtmidi_get_port_count(ptr);
        if (ptr != null && !ptr.ok) throw new RtMidiNativeException(ptr);
        List<MidiPort.Info> result = new ArrayList<>(portCount);
        for (int i = 0; i < portCount; i++) {
            result.add(i,
                    new MidiPort.Info(RtMidiLibrary.getInstance().rtmidi_get_port_name(ptr, i), i, MidiPort.Info.Type.WRITABLE));
        }
        RtMidiLibrary.getInstance().rtmidi_out_free(ptr);
        return result;
    }

}
