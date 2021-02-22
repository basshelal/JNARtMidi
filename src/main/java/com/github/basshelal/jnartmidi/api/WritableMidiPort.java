package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;

public class WritableMidiPort extends MidiPort<RtMidiOutPtr> {

    private byte[] messageBuffer;

    public WritableMidiPort(Info info) {
        super(info);
        this.ptr = RtMidiLibrary.getInstance().rtmidi_out_create_default();
    }

    public WritableMidiPort(RtMidiApi api, String name, Info info) {
        super(info);
        this.ptr = RtMidiLibrary.getInstance().rtmidi_out_create(api.getNumber(), name);
    }

    @Override
    public void open(Info info) { this.open(this.ptr, info); }

    @Override
    public void close() {
        // TODO: 22/02/2021 Implement!
        RtMidiLibrary.getInstance().rtmidi_close_port(this.ptr);
        this.isOpen = false;
        this.isVirtual = false;
    }

    @Override
    public void destroy() {
        RtMidiLibrary.getInstance().rtmidi_out_free(ptr);
    }

    @Override
    public RtMidiApi getApi() {
        int result = RtMidiLibrary.getInstance().rtmidi_out_get_current_api(ptr);
        return RtMidiApi.fromInt(result);
    }

    // TODO: 21/02/2021 Check!
    public int sendMessage(int[] message) {
        if (this.messageBuffer == null || this.messageBuffer.length < message.length)
            this.messageBuffer = new byte[message.length];
        for (int i = 0; i < message.length; i++)
            this.messageBuffer[i] = (byte) message[i];
        return RtMidiLibrary.getInstance().rtmidi_out_send_message(this.ptr, this.messageBuffer, 3);
    }

}
