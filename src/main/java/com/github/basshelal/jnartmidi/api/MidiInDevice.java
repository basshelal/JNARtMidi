package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;

public class MidiInDevice extends MidiDevice {

    private RtMidiLibrary.RtMidiCCallback callback;

    public MidiInDevice() {
        this.wrapper = RtMidiLibrary.getInstance().rtmidi_in_create_default();
    }

    @Override
    public void destroy() {
        this.removeCallback();
        RtMidiLibrary.getInstance().rtmidi_in_free(wrapper);
    }

    @Override
    public RtMidiApi getApi() {
        int result = RtMidiLibrary.getInstance().rtmidi_in_get_current_api(wrapper);
        return RtMidiApi.fromInt(result);
    }

    public void setCallback(RtMidiLibrary.RtMidiCCallback callback) {
        this.callback = callback;
        RtMidiLibrary.getInstance().rtmidi_in_set_callback(wrapper, this.callback, null);
    }

    public void removeCallback() {
        RtMidiLibrary.getInstance().rtmidi_in_cancel_callback(wrapper);
        this.callback = null;
    }

    public void ignoreTypes() {
        // TODO: 17/02/2021 Implement!
    }

    public double getMessage() {
        // TODO: 17/02/2021 Implement!
        return 0.0;
    }

}
