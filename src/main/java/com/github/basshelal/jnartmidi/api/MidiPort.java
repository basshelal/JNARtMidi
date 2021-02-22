package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiPtr;
import com.sun.jna.Platform;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class MidiPort<P extends RtMidiPtr> {

    protected final Info info;
    protected boolean isOpen = false;
    protected boolean isVirtual = false;

    protected P ptr;

    public MidiPort(Info info) {
        this.info = requireNonNull(info, "Constructor parameter info cannot be null!");
    }

    protected void open(RtMidiPtr ptr, Info info) {
        requireNonNull(ptr, "ptr cannot be null!");
        requireNonNull(info, "info cannot be null!");
        RtMidiLibrary.getInstance().rtmidi_open_port(ptr, info.getNumber(), info.getName());
        this.isOpen = true;
        this.isVirtual = false;
    }

    protected void preventSegfault() { requireNonNull(this.ptr, "ptr cannot be null!"); }

    public void open() { this.open(this.getInfo()); }

    public void openVirtual(String name) throws RtMidiException {
        if (!RtMidi.supportsVirtualPorts())
            throw new RtMidiException("Platform " + Platform.RESOURCE_PREFIX + " does not support virtual ports");
        requireNonNull(this.ptr);
        RtMidiLibrary.getInstance().rtmidi_open_virtual_port(this.ptr, name);
        this.isOpen = true;
        this.isVirtual = true;
    }

    public abstract void close();

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

        public enum Type {READABLE, WRITABLE, UNKNOWN}
    }

}
