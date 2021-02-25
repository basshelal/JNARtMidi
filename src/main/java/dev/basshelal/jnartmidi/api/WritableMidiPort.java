package dev.basshelal.jnartmidi.api;

import dev.basshelal.jnartmidi.lib.RtMidiLibrary;
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;

import static java.util.Objects.requireNonNull;

public final class WritableMidiPort extends MidiPort<RtMidiOutPtr> {

    private byte[] messageBuffer;

    //region Constructors

    public /* constructor */ WritableMidiPort(Info info) throws NullPointerException {
        super(info);
        this.createPtr();
    }

    public /* constructor */ WritableMidiPort(Info info, RtMidiApi api, String name) throws NullPointerException {
        super(info, api, name);
        this.createPtr();
    }

    //endregion Constructors

    @Override
    public void destroy() throws RtMidiException {
        this.checkIsDestroyed();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_out_free(this.ptr);
        this.checkErrors();
        this.isDestroyed = true;
    }

    @Override
    public RtMidiApi getApi() throws RtMidiException {
        this.checkIsDestroyed();
        this.preventSegfault();
        int result = RtMidiLibrary.getInstance().rtmidi_out_get_current_api(ptr);
        this.checkErrors();
        return RtMidiApi.fromInt(result);
    }

    @Override
    protected void createPtr() throws RtMidiException {
        if (this.api != null && this.clientName != null)
            this.ptr = RtMidiLibrary.getInstance().rtmidi_out_create(this.api.getNumber(), this.clientName);
        else this.ptr = RtMidiLibrary.getInstance().rtmidi_out_create_default();
        this.checkErrors();
        this.isDestroyed = false;
    }

    /**
     * Sends the passed in {@code message} to this port.
     *
     * @param message the message to send, this array will not be modified
     * @throws NullPointerException if {@code message} is null
     * @throws RtMidiException      if an error occurred in RtMidi's native code
     */
    public void sendMessage(int[] message) throws NullPointerException, RtMidiException {
        requireNonNull(message);
        this.checkIsDestroyed();
        this.preventSegfault();
        if (this.messageBuffer == null || this.messageBuffer.length < message.length)
            this.messageBuffer = new byte[message.length];
        for (int i = 0; i < message.length; i++)
            this.messageBuffer[i] = (byte) message[i];
        RtMidiLibrary.getInstance().rtmidi_out_send_message(this.ptr, this.messageBuffer, this.messageBuffer.length);
        this.checkErrors();
    }

    /**
     * Sends the passed in {@code midiMessage} to this port.
     *
     * @param midiMessage the message to send, the data of the message will not be modified
     * @throws NullPointerException if {@code midiMessage} is null
     * @throws RtMidiException      if an error occurred in RtMidi's native code
     */
    public void sendMessage(MidiMessage midiMessage) throws NullPointerException, RtMidiException {
        requireNonNull(midiMessage);
        this.sendMessage(midiMessage.getData());
    }

}
