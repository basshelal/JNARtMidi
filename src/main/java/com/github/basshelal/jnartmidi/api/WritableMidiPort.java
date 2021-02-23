package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;

public class WritableMidiPort extends MidiPort<RtMidiOutPtr> {

    private byte[] messageBuffer;

    //region Constructors

    public /* constructor */ WritableMidiPort(Info info) {
        super(info);
        this.createPtr();
    }

    public /* constructor */ WritableMidiPort(Info info, RtMidiApi api, String name) {
        super(info, api, name);
        this.createPtr();
    }

    //endregion Constructors

    @Override
    public void destroy() {
        this.checkIsDestroyed();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_out_free(this.ptr);
        this.checkErrors();
        this.isDestroyed = true;
    }

    @Override
    public RtMidiApi getApi() {
        this.checkIsDestroyed();
        this.preventSegfault();
        int result = RtMidiLibrary.getInstance().rtmidi_out_get_current_api(ptr);
        this.checkErrors();
        return RtMidiApi.fromInt(result);
    }

    @Override
    protected void createPtr() {
        if (this.api != null && this.clientName != null)
            this.ptr = RtMidiLibrary.getInstance().rtmidi_out_create(this.api.getNumber(), this.clientName);
        else this.ptr = RtMidiLibrary.getInstance().rtmidi_out_create_default();
        this.checkErrors();
        this.isDestroyed = false;
    }

    // TODO: 21/02/2021 Check!
    public int sendMessage(int[] message) {
        this.checkIsDestroyed();
        this.preventSegfault();
        if (this.messageBuffer == null || this.messageBuffer.length < message.length)
            this.messageBuffer = new byte[message.length];
        for (int i = 0; i < message.length; i++)
            this.messageBuffer[i] = (byte) message[i];
        int result = RtMidiLibrary.getInstance().rtmidi_out_send_message(this.ptr, this.messageBuffer, 3);
        this.checkErrors();
        return result;
    }

    public void sendMessage(MidiMessage midiMessage) {
        this.sendMessage(midiMessage.getData());
    }

}
