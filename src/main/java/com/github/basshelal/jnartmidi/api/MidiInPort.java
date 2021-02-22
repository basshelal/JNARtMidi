package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import com.sun.jna.Pointer;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MidiInPort extends MidiPort {

    private RtMidiLibrary.RtMidiCCallback cCallback;
    private ArrayCallback arrayCallback;
    private MidiMessageCallback midiMessageCallback;
    private int[] messageBuffer;
    private MidiMessage midiMessage;
    private final RtMidiInPtr ptr;

    public MidiInPort(Info info) {
        super(info);
        this.ptr = RtMidiLibrary.getInstance().rtmidi_in_create_default();
    }

    public MidiInPort(RtMidiApi api, String name, Info info) {
        super(info);
        this.ptr = RtMidiLibrary.getInstance().rtmidi_in_create(api.getNumber(), name, 0);
    }

    @Override
    public void open(Info info) { this.open(this.ptr, info); }

    @Override
    public void destroy() {
        this.removeCallback();
        RtMidiLibrary.getInstance().rtmidi_in_free(this.ptr);
    }

    @Override
    public RtMidiApi getApi() {
        int result = RtMidiLibrary.getInstance().rtmidi_in_get_current_api(this.ptr);
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
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.ptr, this.cCallback, null);
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
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.ptr, this.cCallback, null);
    }

    public void setCallback(MidiMessageCallback callback) {
        this.checkHasCallback();
        this.midiMessageCallback = callback;
        this.midiMessage = new MidiMessage();
        this.cCallback = (final double timeStamp, final Pointer message,
                          final RtMidiLibrary.NativeSize messageSize, final Pointer userData) -> {
            // TODO: 18/02/2021 Implement!
        };
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.ptr, this.cCallback, null);
    }

    public void removeCallback() {
        RtMidiLibrary.getInstance().rtmidi_in_cancel_callback(this.ptr);
        this.messageBuffer = null;
        this.cCallback = null;
        this.arrayCallback = null;
        this.midiMessageCallback = null;
        this.midiMessage = null;
    }

    public void ignoreTypes(boolean midiSysex, boolean midiTime, boolean midiSense) {
        RtMidiLibrary.getInstance().rtmidi_in_ignore_types(this.ptr, midiSysex, midiTime, midiSense);
    }

    // TODO: 18/02/2021 Check!
    public double getMessage(byte[] buffer) {
        ByteBuffer buff = ByteBuffer.wrap(buffer);
        double result = RtMidiLibrary.getInstance().rtmidi_in_get_message(this.ptr, buff,
                new RtMidiLibrary.NativeSizeByReference(buffer.length));
        System.out.println(Arrays.toString(buffer));

        /*
         * The behavior of getMessage is weird,
         * Essentially, it gets the messages from the queue in FIFO order
         * that is if I do 6 then 9 then 0, getMessage will return 6, then if
         * called again 9 then if called again 0, if there is no remaining message the buffer
         * is left unchanged.
         *
         * This is... weird and not very useful at all, but the current signature for
         * native getMessage works at least
         */

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
