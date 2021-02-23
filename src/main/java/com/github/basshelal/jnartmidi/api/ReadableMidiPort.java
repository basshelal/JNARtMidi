package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import com.sun.jna.Pointer;

public class ReadableMidiPort extends MidiPort<RtMidiInPtr> {

    /**
     * This is the default queue size RtMidi uses if no size is passed in
     */
    private static final int DEFAULT_QUEUE_SIZE_LIMIT = 100;

    // TODO: 23/02/2021 Test idea! Make a Port and set its callback then make it unreachable and thus ready for GC,
    //  then force a GC and see what happens.

    private RtMidiLibrary.RtMidiCCallback cCallback;
    private MidiMessageCallback midiMessageCallback;
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

    public void setCallback(MidiMessageCallback callback) {
        this.checkIsDestroyed();
        if (this.midiMessageCallback != null)
            throw new RtMidiException("Cannot set callback there is an existing callback registered, " +
                    "call removeCallback() to remove.");
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
        this.cCallback = null;
        this.midiMessageCallback = null;
        this.midiMessage = null;
    }

    public void ignoreTypes(boolean midiSysex, boolean midiTime, boolean midiSense) {
        this.checkIsDestroyed();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_in_ignore_types(this.ptr, midiSysex, midiTime, midiSense);
        this.checkErrors();
    }

    public MidiMessage getLastMessage() { return this.midiMessage; }

}
