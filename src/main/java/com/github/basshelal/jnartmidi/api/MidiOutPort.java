package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;

public class MidiOutPort extends MidiPort {

    public MidiOutPort() {
        super(null);
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_out_create_default();
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

    public int sendMessage() {
        return RtMidiLibrary.getInstance().rtmidi_out_send_message(this.wrapper, new byte[3], 3);
    }

}
