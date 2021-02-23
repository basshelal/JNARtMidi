package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.api.callbacks.ArrayCallback;
import com.github.basshelal.jnartmidi.api.callbacks.MidiMessageCallback;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import com.sun.jna.Pointer;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ReadableMidiPort extends MidiPort<RtMidiInPtr> {

    /**
     * This is the default queue size RtMidi will use if no size is passed in
     */
    private static final int DEFAULT_QUEUE_SIZE_LIMIT = 100;

    private RtMidiLibrary.RtMidiCCallback cCallback;
    private ArrayCallback arrayCallback;
    private MidiMessageCallback midiMessageCallback;
    private int[] messageBuffer;
    private MidiMessage midiMessage;

    //region Constructors

    public /* constructor */ ReadableMidiPort(Info portInfo) {
        super(portInfo);
        this.createPtr();
    }

    public /* constructor */ ReadableMidiPort(Info portInfo, RtMidiApi api, String clientName) {
        super(portInfo, api, clientName);
        this.createPtr();
    }

    //endregion Constructors

    @Override
    public void destroy() {
        this.checkIsDestroyed();
        this.removeCallback();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_in_free(this.ptr);
        this.checkErrors();
        this.isDestroyed = true;
    }

    @Override
    public RtMidiApi getApi() {
        this.checkIsDestroyed();
        this.preventSegfault();
        int result = RtMidiLibrary.getInstance().rtmidi_in_get_current_api(this.ptr);
        this.checkErrors();
        return RtMidiApi.fromInt(result);
    }

    @Override
    protected void createPtr() {
        if (this.api != null && this.clientName != null)
            this.ptr = RtMidiLibrary.getInstance().rtmidi_in_create(this.api.getNumber(), this.clientName, DEFAULT_QUEUE_SIZE_LIMIT);
        else this.ptr = RtMidiLibrary.getInstance().rtmidi_in_create_default();
        this.checkErrors();
        this.isDestroyed = false;
    }


    // TODO: 23/02/2021 Because of the status byte problem, I think ArrayCallback might be a bad idea...
    //  otherwise we can keep it for a "raw" values but really it's not useful
    public void setCallback(ArrayCallback callback) {
        this.checkIsDestroyed();
        this.checkHasCallback();
        this.arrayCallback = callback;
        this.messageBuffer = new int[3]; // prevent memalloc in callback by guessing buffer size
        this.cCallback = (final double timeStamp, final Pointer message,
                          final RtMidiLibrary.NativeSize messageSize, final Pointer userData) -> {
            if (message == null || messageSize == null) return; // prevent NPE or worse segfault
            // memalloc in realtime code! Dangerous but necessary and extremely rare
            if (this.messageBuffer == null || this.messageBuffer.length < messageSize.intValue())
                this.messageBuffer = new int[messageSize.intValue()];
            for (int i = 0; i < messageSize.intValue(); i++) {
                if (i == 0) this.messageBuffer[i] = message.getByte(i) & 0xF0; // fix status byte
                else this.messageBuffer[i] = message.getByte(i); // data bytes are correct
            }
            this.arrayCallback.onMessage(this.messageBuffer, timeStamp);
        };
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.ptr, this.cCallback, null);
        this.checkErrors();
    }

    public void setCallback(MidiMessageCallback callback) {
        this.checkIsDestroyed();
        this.checkHasCallback();
        this.midiMessageCallback = callback;
        this.midiMessage = new MidiMessage();
        this.cCallback = (final double timeStamp, final Pointer message,
                          final RtMidiLibrary.NativeSize messageSize, final Pointer userData) -> {
            if (message == null || messageSize == null) return; // prevent NPE or worse segfault
            int size = messageSize.intValue();
            // memalloc in realtime code! Dangerous but necessary and extremely rare
            if (this.midiMessage.size() < size) this.midiMessage.setSize(size);
            for (int i = 0; i < size; i++) this.midiMessage.set(i, message.getByte(i));
            this.midiMessageCallback.onMessage(this.midiMessage, timeStamp);
        };
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(this.ptr, this.cCallback, null);
        this.checkErrors();
    }

    public void removeCallback() {
        this.checkIsDestroyed();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_in_cancel_callback(this.ptr);
        this.checkErrors();
        this.messageBuffer = null;
        this.cCallback = null;
        this.arrayCallback = null;
        this.midiMessageCallback = null;
        this.midiMessage = null;
    }

    public void ignoreTypes(boolean midiSysex, boolean midiTime, boolean midiSense) {
        this.checkIsDestroyed();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_in_ignore_types(this.ptr, midiSysex, midiTime, midiSense);
        this.checkErrors();
    }

    // TODO: 18/02/2021 Check! Possibly remove in favor of callbacks!
    //  if we keep this make another with a MidiMessage type
    public double getMessage(byte[] buffer) {
        this.checkIsDestroyed();
        this.preventSegfault();
        ByteBuffer buff = ByteBuffer.wrap(buffer);
        double result = RtMidiLibrary.getInstance().rtmidi_in_get_message(this.ptr, buff,
                new RtMidiLibrary.NativeSizeByReference(buffer.length));
        System.out.println(Arrays.toString(buffer));
        this.checkErrors();

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

    private void checkHasCallback() throws RtMidiException {
        if (this.cCallback != null || this.arrayCallback != null || this.midiMessageCallback != null)
            throw new RtMidiException("Cannot set callback there is an existing callback registered, " +
                    "call removeCallback() to remove.");
    }

}
