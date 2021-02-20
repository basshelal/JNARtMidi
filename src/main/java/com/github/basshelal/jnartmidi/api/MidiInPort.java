package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import com.sun.jna.Pointer;

public class MidiInPort extends MidiPort {

    private RtMidiLibrary.RtMidiCCallback cCallback;
    private ArrayCallback arrayCallback;
    private MidiMessageCallback midiMessageCallback;
    private int[] messageBuffer;
    private MidiMessage midiMessage;
    private final RtMidiInPtr wrapper;

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

    private void checkHasCallback() throws RtMidiException {
        if (this.cCallback != null || this.arrayCallback != null || this.midiMessageCallback != null)
            throw new RtMidiException("Cannot set callback there is an existing callback registered, " +
                    "call removeCallback() to remove.");
    }

    public void setCallback(RtMidiLibrary.RtMidiCCallback callback) {
        this.checkHasCallback();
        this.cCallback = callback;
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.wrapper, this.cCallback, null);
    }

    public void setCallback(ArrayCallback callback) {
        this.checkHasCallback();
        this.arrayCallback = callback;
        // prevent memalloc in callback by guessing buffer size
        this.messageBuffer = new int[3];
        this.cCallback = (final double timeStamp, final Pointer message,
                          final RtMidiLibrary.NativeSize messageSize, final Pointer userData) -> {
            // memalloc in realtime code! Dangerous but necessary and extremely rare
            if (this.messageBuffer == null || this.messageBuffer.length < messageSize.intValue())
                this.messageBuffer = new int[messageSize.intValue()];
            for (int i = 0; i < messageSize.intValue(); i++) {
                if (i == 0) this.messageBuffer[i] = message.getByte(i) & 0xF0;
                else this.messageBuffer[i] = message.getByte(i);
            }
            this.arrayCallback.invoke(this.messageBuffer, timeStamp);
        };
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.wrapper, this.cCallback, null);
    }

    public void setCallback(MidiMessageCallback callback) {
        this.checkHasCallback();
        this.midiMessageCallback = callback;
        this.midiMessage = new MidiMessage();
        this.cCallback = (final double timeStamp, final Pointer message,
                          final RtMidiLibrary.NativeSize messageSize, final Pointer userData) -> {
            // TODO: 18/02/2021 Implement!
        };
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.wrapper, this.cCallback, null);
    }

    public void removeCallback() {
        RtMidiLibrary.getInstance().rtmidi_in_cancel_callback(this.wrapper);
        this.messageBuffer = null;
        this.cCallback = null;
        this.arrayCallback = null;
        this.midiMessageCallback = null;
        this.midiMessage = null;
    }

    public void ignoreTypes(boolean midiSysex, boolean midiTime, boolean midiSense) {
        RtMidiLibrary.getInstance().rtmidi_in_ignore_types(this.wrapper, midiSysex, midiTime, midiSense);
    }

    // TODO: 18/02/2021 Check!
    public double getMessage(byte[] buffer) {
        double result = RtMidiLibrary.getInstance().rtmidi_in_get_message(this.wrapper, buffer,
                new RtMidiLibrary.NativeSizeByReference(buffer.length));
        return result;
    }

    public interface ArrayCallback {
        // RealTimeCritical
        public void invoke(int[] message, double deltaTime);
    }

    public interface MidiMessageCallback {
        // RealTimeCritical
        public void invoke(MidiMessage message);
    }

}
