package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiWrapper;

public abstract class MidiPort {

    protected final String name;
    protected final int number;
    protected boolean isOpen = false;
    protected boolean isVirtual = false;

    protected RtMidiWrapper wrapper;

    protected MidiPort() {
        // TODO: 18/02/2021 Delete!
        this("", 0);
    }

    protected MidiPort(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public int portCount() {
        return RtMidiLibrary.getInstance().rtmidi_get_port_count(this.wrapper);
    }

    public void openPort(int number, String name) {
        RtMidiLibrary.getInstance().rtmidi_open_port(wrapper, number, name);
        this.isOpen = true;
        this.isVirtual = false;
    }

    public void openVirtualPort(String name) {
        // TODO: 18/02/2021 Fail if unsupported ie Windows
        RtMidiLibrary.getInstance().rtmidi_open_virtual_port(wrapper, name);
        this.isOpen = true;
        this.isVirtual = true;
    }

    public void closePort() {
        RtMidiLibrary.getInstance().rtmidi_close_port(wrapper);
        this.isOpen = false;
        this.isVirtual = false;
    }

    public String portName(int number) {
        return RtMidiLibrary.getInstance().rtmidi_get_port_name(wrapper, number);
    }

    public abstract void destroy();

    public abstract RtMidiApi getApi();

}
