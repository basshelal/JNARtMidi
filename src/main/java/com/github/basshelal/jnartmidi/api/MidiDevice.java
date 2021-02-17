package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiWrapper;

public abstract class MidiDevice {

    protected RtMidiWrapper wrapper;

    public int portCount() {
        return RtMidiLibrary.getInstance().rtmidi_get_port_count(this.wrapper);
    }

    public void openPort(int number, String name) {
        RtMidiLibrary.getInstance().rtmidi_open_port(wrapper, number, name);
    }

    public void openVirtualPort(String name) {
        RtMidiLibrary.getInstance().rtmidi_open_virtual_port(wrapper, name);
    }

    public void closePort() {
        RtMidiLibrary.getInstance().rtmidi_close_port(wrapper);
    }

    public String portName(int number) {
        return RtMidiLibrary.getInstance().rtmidi_get_port_name(wrapper, number);
    }

    public abstract void destroy();

    public abstract RtMidiApi getApi();


    public class Port {
        private String name;
        private boolean isOpen;
    }

}
