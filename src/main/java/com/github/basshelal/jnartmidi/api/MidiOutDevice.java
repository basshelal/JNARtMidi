package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;

public class MidiOutDevice extends MidiDevice {

    public MidiOutDevice() {
        super();
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
        // TODO: 17/02/2021 Implement!
        return 0;
    }

}
