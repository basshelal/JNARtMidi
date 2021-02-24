package dev.basshelal.jnartmidi.api;

import com.sun.jna.Pointer;

import dev.basshelal.jnartmidi.api.exceptions.RtMidiException;
import dev.basshelal.jnartmidi.api.exceptions.RtMidiNativeException;
import dev.basshelal.jnartmidi.lib.RtMidiLibrary;
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;

import static java.util.Objects.requireNonNull;

// TODO: 23/02/2021 More specific exceptions!

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

    public /* constructor */ ReadableMidiPort(Info portInfo) throws NullPointerException {
        // TODO: 23/02/2021 Require type of portInfo to be READABLE??
        super(portInfo);
        this.createPtr();
    }

    public /* constructor */ ReadableMidiPort(Info portInfo, RtMidiApi api, String clientName) throws NullPointerException {
        super(portInfo, api, clientName);
        this.createPtr();
    }

    //endregion Constructors

    /**
     * @inheritDoc
     */
    @Override
    public void destroy() throws RtMidiException {
        // TODO: 23/02/2021 do nothing if is already destroyed
        this.checkIsDestroyed();
        this.removeCallback();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_in_free(this.ptr);
        this.checkErrors();
        this.isDestroyed = true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public RtMidiApi getApi() throws RtMidiNativeException {
        this.checkIsDestroyed();
        this.preventSegfault();
        int result = RtMidiLibrary.getInstance().rtmidi_in_get_current_api(this.ptr);
        this.checkErrors();
        return RtMidiApi.fromInt(result);
    }

    @Override
    protected void createPtr() throws RtMidiNativeException {
        if (this.api != null && this.clientName != null)
            this.ptr = RtMidiLibrary.getInstance().rtmidi_in_create(this.api.getNumber(), this.clientName, DEFAULT_QUEUE_SIZE_LIMIT);
        else this.ptr = RtMidiLibrary.getInstance().rtmidi_in_create_default();
        this.checkErrors();
        this.isDestroyed = false;
    }

    /**
     * Set this {@link ReadableMidiPort}'s callback which will be triggered when a new {@link MidiMessage} is sent to
     * this port. Ensure that the message you expect is not being ignored by calling {@link #ignoreTypes} before
     * setting the callback.
     *
     * @param callback the callback which will be triggered when a new {@link MidiMessage} is sent to this port
     * @throws NullPointerException if {@code callback} is {@code null}
     * @throws RtMidiException      if a callback already exists for this port which must be removed using {@link #removeCallback}
     */
    public void setCallback(MidiMessageCallback callback) throws NullPointerException, RtMidiException {
        requireNonNull(callback);
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

    /**
     * Removes this {@link ReadableMidiPort}'s {@link MidiMessageCallback} if it exists, otherwise does nothing.
     * You must call this before calling {@link #setCallback} if one already exists.
     */
    public void removeCallback() {
        this.checkIsDestroyed();
        if (this.midiMessageCallback == null) return;
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

    /**
     * @return the last received {@link MidiMessage} from the callback or {@code null} if none exists
     */
    public MidiMessage getLastMessage() { return this.midiMessage; }

}
