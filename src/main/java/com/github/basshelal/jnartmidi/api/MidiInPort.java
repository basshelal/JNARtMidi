package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;

public class MidiInPort extends MidiPort {

    private RtMidiLibrary.RtMidiCCallback cCallback;
    private ArrayCallback arrayCallback;
    private MidiMessageCallback midiMessageCallback;
    private int[] messageBuffer;
    private MidiMessage midiMessage;

    public MidiInPort(Info info) {
        super(info);
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_in_create_default();
    }

    public MidiInPort(RtMidiApi api, String name, int queueSizeLimit, Info info) {
        super(info);
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_in_create(api.getNumber(), name, queueSizeLimit);
    }

    @Override
    public void destroy() {
        this.removeCallback();
        RtMidiLibrary.getInstance().rtmidi_in_free(this.wrapper);
    }

    @Override
    public RtMidiApi getApi() {
        int result = RtMidiLibrary.getInstance().rtmidi_in_get_current_api(this.wrapper);
        return RtMidiApi.fromInt(result);
    }

    public void setCallback(RtMidiLibrary.RtMidiCCallback callback) {
        // TODO: 18/02/2021 Implement!
    }

    public void setCallback(ArrayCallback callback) {
        this.arrayCallback = callback;
        this.messageBuffer = new int[3]; // TODO: 17/02/2021 Resize buffer if too small??
        this.cCallback = (double timeStamp, StringArray message, RtMidiLibrary.NativeSize messageSize, Pointer userData) -> {
            for (int i = 0; i < messageSize.intValue(); i++) {
                if (i == 0) this.messageBuffer[i] = message.getByte(i) & 0xF0;
                else this.messageBuffer[i] = message.getByte(i);
            }
            this.arrayCallback.invoke(this.messageBuffer, timeStamp);
        };
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.wrapper, this.cCallback, null);
    }

    public void setCallback(MidiMessageCallback callback) {
        // TODO: 18/02/2021 Implement!
    }

    public void removeCallback() {
        RtMidiLibrary.getInstance().rtmidi_in_cancel_callback(this.wrapper);
        this.messageBuffer = null;
        this.cCallback = null;
        this.arrayCallback = null;
    }

    public void ignoreTypes(boolean midiSysex, boolean midiTime, boolean midiSense) {
        RtMidiLibrary.getInstance().rtmidi_in_ignore_types(this.wrapper, midiSysex, midiTime, midiSense);
    }

    public double getMessage() {
        // TODO: 17/02/2021 Implement!
        return 0.0;
    }

    public interface ArrayCallback {
        public void invoke(int[] message, double deltaTime);
    }

    public interface MidiMessageCallback {
        public void invoke(MidiMessage message);
    }

}
