package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiPtr;
import com.sun.jna.Platform;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class MidiPort<P extends RtMidiPtr> {

    protected P ptr;

    protected final Info info;
    protected boolean isOpen = false;
    protected boolean isVirtual = false;
    protected boolean isDestroyed = false;

    protected RtMidiApi api = null;
    protected String clientName = null;

    //region Constructors

    protected  /* constructor */ MidiPort(Info portInfo) {
        this.info = requireNonNull(portInfo, "Constructor parameter portInfo cannot be null!");
    }

    protected  /* constructor */ MidiPort(Info portInfo, RtMidiApi api, String clientName) {
        this(portInfo);
        this.api = requireNonNull(api, "Constructor parameter api cannot be null!");
        this.clientName = requireNonNull(clientName, "Constructor parameter clientName cannot be null!");
    }

    //endregion Constructors

    //region Abstract Functions

    /**
     * Destroys this port such that it can and will no longer be used, attempting to use the port
     * after this should throw an {@link RtMidiException}, see {@link #checkIsDestroyed()}
     */
    public abstract void destroy();

    /**
     * @return the {@link RtMidiApi} that this port is using
     */
    public abstract RtMidiApi getApi();

    /**
     * Create {@link #ptr} to be used in this port.
     * Calls either the default create function like {@link RtMidiLibrary#rtmidi_in_create_default} or
     * the custom function {@link RtMidiLibrary#rtmidi_in_create} depending on how this was constructed
     */
    protected abstract void createPtr();

    //endregion Abstract Functions

    //region Concrete Functions

    public void open(Info info) {
        checkIsDestroyed();
        requireNonNull(info, "info cannot be null!");
        preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_open_port(this.ptr, info.getNumber(), info.getName());
        this.isOpen = true;
        this.isVirtual = false;
    }

    public void open() { this.open(this.getInfo()); }

    /**
     * TODO doc!
     *
     * @param name
     * @throws RtMidiException if this platform does not support virtual ports, see {@link RtMidi#supportsVirtualPorts()}
     */
    public void openVirtual(String name) throws RtMidiException {
        this.checkIsDestroyed();
        if (!RtMidi.supportsVirtualPorts())
            throw new RtMidiException("Platform " + Platform.RESOURCE_PREFIX + " does not support virtual ports");
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_open_virtual_port(this.ptr, name);
        this.isOpen = true;
        this.isVirtual = true;
    }

    public void close() {
        this.checkIsDestroyed();
        this.preventSegfault();
        RtMidiLibrary.getInstance().rtmidi_close_port(this.ptr);
        this.isOpen = false;
        this.isVirtual = false;
        this.destroy();
        this.createPtr();
    }

    /**
     * Call this before any call to {@link RtMidiLibrary#getInstance()}.
     *
     * @throws NullPointerException if {@link #ptr} is null to prevent segfaults happening in the native code,
     *                              A segfault will always crash the VM without any way to catch it, so this is the safest thing to do to
     *                              prevent that.
     */
    protected final void preventSegfault() throws NullPointerException { requireNonNull(this.ptr, "ptr cannot be null!"); }

    protected final void checkIsDestroyed() throws RtMidiException {
        if (this.isDestroyed)
            throw new RtMidiException("Cannot proceed, the MidiPort:\n"
                    + this.toString() + "\nhas already been destroyed");
    }

    //endregion Concrete Functions

    //region Getters

    public Info getInfo() { return info; }

    public boolean isOpen() { return isOpen; }

    public boolean isVirtual() { return isVirtual; }

    //endregion Getters

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
