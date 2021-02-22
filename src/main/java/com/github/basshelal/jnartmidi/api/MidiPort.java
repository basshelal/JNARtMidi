package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiPtr;

import java.util.Objects;

public abstract class MidiPort {

    protected final Info info;
    protected boolean isOpen = false;
    protected boolean isVirtual = false;

    protected RtMidiPtr wrapper;

    public MidiPort(Info info) { this.info = info; }

    protected void open(RtMidiPtr ptr, Info info) {
        if (ptr == null) throw new IllegalArgumentException("RtMidiPtr cannot be null!");
        RtMidiLibrary.getInstance().rtmidi_open_port(ptr, info.getNumber(), info.getName());
        this.isOpen = true;
        this.isVirtual = false;
    }

    public void open() { this.open(this.getInfo()); }

    public void openVirtual(String name) {
        // TODO: 18/02/2021 Fail if unsupported ie Windows
        RtMidiLibrary.getInstance().rtmidi_open_virtual_port(wrapper, name);
        this.isOpen = true;
        this.isVirtual = true;
    }

    public void close() {
        // TODO: 22/02/2021 After close call free, the problem is the port is now unusable, so we need to recreate it
        //  after we freed it, so that we: close the port truly and yet can still do stuff with the port again
        //  without creating a new one
        RtMidiLibrary.getInstance().rtmidi_close_port(wrapper);
        this.isOpen = false;
        this.isVirtual = false;
    }

    public Info getInfo() { return info; }

    public boolean isOpen() { return isOpen; }

    public boolean isVirtual() { return isVirtual; }

    @Override
    public String toString() {
        return "MidiPort{" +
                "info=" + info +
                ", isOpen=" + isOpen +
                ", isVirtual=" + isVirtual +
                '}';
    }

    public abstract void open(Info info);

    public abstract void destroy();

    public abstract RtMidiApi getApi();

    public static class Info {
        protected final String name;
        protected final int number;
        protected final Type type;

        public Info(String name, int number, Type type) {
            this.name = name;
            this.number = number;
            this.type = type;
        }

        public String getName() { return name; }

        public int getNumber() { return number; }

        public Type getType() { return type; }

        @Override
        public int hashCode() { return Objects.hash(getName(), getNumber(), getType()); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Info)) return false;
            Info info = (Info) o;
            return getType() == info.getType() && getName().equals(info.getName()) && getNumber() == info.getNumber();
        }

        @Override
        public String toString() {
            return "MidiPort.Info{" +
                    "name='" + name + "'" +
                    ", number=" + number +
                    ", type=" + type +
                    "}";
        }

        public enum Type {IN, OUT, UNKNOWN}
    }

}
