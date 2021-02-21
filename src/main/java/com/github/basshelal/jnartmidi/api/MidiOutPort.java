package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;

public class MidiOutPort extends MidiPort {

    private byte[] messageBuffer;
    private final RtMidiOutPtr wrapper;

    public MidiOutPort(Info info) {
        super(info);
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_out_create_default();
    }

    public MidiOutPort(RtMidiApi api, String name, Info info) {
        super(info);
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_out_create(api.getNumber(), name);
    }

    @Override
    public void destroy() {
        RtMidiLibrary.getInstance().rtmidi_out_free(wrapper);
    }

    @Override
    public RtMidiApi getApi() {
        int result = RtMidiLibrary.getInstance().rtmidi_out_get_current_api(wrapper);
        return RtMidiApi.fromInt(result);
    }

    // TODO: 21/02/2021 Check!
    public int sendMessage(int[] message) {
        if (this.messageBuffer == null || this.messageBuffer.length < message.length)
            this.messageBuffer = new byte[message.length];
        for (int i = 0; i < message.length; i++)
            this.messageBuffer[i] = (byte) message[i];
        return RtMidiLibrary.getInstance().rtmidi_out_send_message(this.wrapper, null, 3);
    }

}
