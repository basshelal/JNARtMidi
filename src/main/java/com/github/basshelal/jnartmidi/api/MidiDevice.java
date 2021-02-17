package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiWrapper;

import java.util.Objects;

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
        private final String name;
        private final int number;
        private boolean isOpen;

        public Port(String name, int number, boolean isOpen) {
            this.name = name;
            this.number = number;
            this.isOpen = isOpen;
        }

        public String getName() { return name; }

        public int getNumber() { return number; }

        public void setOpen(boolean open) { isOpen = open; }

        public boolean isOpen() { return isOpen; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Port)) return false;
            Port port = (Port) o;
            return getNumber() == port.getNumber() && isOpen() == port.isOpen() && getName().equals(port.getName());
        }

        @Override
        public int hashCode() { return Objects.hash(getName(), getNumber(), isOpen()); }

        @Override
        public String toString() {
            return "Port{" +
                    "name='" + name + '\'' +
                    ", number=" + number +
                    ", isOpen=" + isOpen +
                    '}';
        }
    }

}
