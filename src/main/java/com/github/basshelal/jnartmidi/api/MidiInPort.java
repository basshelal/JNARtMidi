package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;

public class MidiInPort extends MidiPort {

    private RtMidiLibrary.RtMidiCCallback libCallback;
    private Callback callback;
    private int[] messageBuffer;

    public MidiInPort() {
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_in_create_default();
    }

    public MidiInPort(RtMidiApi api, String name, int queueSizeLimit) {
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_in_create(api.getNumber(), name, queueSizeLimit);
    }

    public MidiInPort(String name, int number) {
        super(name, number);
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

    public void setCallback(Callback callback) {
        this.callback = callback;
        this.messageBuffer = new int[3]; // TODO: 17/02/2021 Resize buffer if too small??
        this.libCallback = (double timeStamp, StringArray message, RtMidiLibrary.NativeSize messageSize, Pointer userData) -> {
            for (int i = 0; i < messageSize.intValue(); i++) {
                if (i == 0) this.messageBuffer[i] = message.getByte(i) & 0xF0;
                else this.messageBuffer[i] = message.getByte(i);
            }
            this.callback.invoke(this.messageBuffer, timeStamp);
        };
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.wrapper, this.libCallback, null);
    }

    // TODO: 17/02/2021 setCallback with MidiMessage type for more high level programming

    public void removeCallback() {
        RtMidiLibrary.getInstance().rtmidi_in_cancel_callback(this.wrapper);
        this.messageBuffer = null;
        this.libCallback = null;
        this.callback = null;
    }

    public void ignoreTypes(boolean midiSysex, boolean midiTime, boolean midiSense) {
        RtMidiLibrary.getInstance().rtmidi_in_ignore_types(this.wrapper, midiSysex, midiTime, midiSense);
    }

    public double getMessage() {
        // TODO: 17/02/2021 Implement!
        return 0.0;
    }

    public interface Callback {
        public void invoke(int[] message, double deltaTime);
    }

}
