package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;

public class MidiInDevice extends MidiDevice {

    private RtMidiLibrary.RtMidiCCallback libCallback;
    private Callback callback;
    private int[] messageBuffer;

    public MidiInDevice() {
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_in_create_default();
    }

    @Override
    public void destroy() {
        this.removeCallback();
        RtMidiLibrary.getInstance().rtmidi_in_free(wrapper);
    }

    @Override
    public RtMidiApi getApi() {
        int result = RtMidiLibrary.getInstance().rtmidi_in_get_current_api(wrapper);
        return RtMidiApi.fromInt(result);
    }

    public Port[] getPorts() {
        int count = this.portCount();
        Port[] result = new Port[count];
        for (int i = 0; i < count; i++) {
            result[i] = new Port(this.portName(i), i, false);
        }
        return result;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
        this.messageBuffer = new int[3];
        this.libCallback = (double timeStamp, StringArray message, RtMidiLibrary.NativeSize messageSize, Pointer userData) -> {
            for (int i = 0; i < messageSize.intValue(); i++) {
                if (i == 0) this.messageBuffer[i] = message.getByte(i) & 0xF0;
                else this.messageBuffer[i] = message.getByte(i);
            }
            this.callback.invoke(this.messageBuffer, timeStamp);
        };
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(wrapper, this.libCallback, null);
    }

    public void removeCallback() {
        RtMidiLibrary.getInstance().rtmidi_in_cancel_callback(wrapper);
        this.messageBuffer = null;
        this.libCallback = null;
        this.callback = null;
    }

    public void ignoreTypes() {
        // TODO: 17/02/2021 Implement!
    }

    public double getMessage() {
        // TODO: 17/02/2021 Implement!
        return 0.0;
    }

    public interface Callback {
        public void invoke(int[] message, double deltaTime);
    }

}
