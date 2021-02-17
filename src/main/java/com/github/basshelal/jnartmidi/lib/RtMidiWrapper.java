package com.github.basshelal.jnartmidi.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * ! \brief Wraps an RtMidi object for C function return statuses.<br>
 * <i>native declaration : rtmidi_c.h:7</i><br>
 */
@Structure.FieldOrder({"ptr", "data", "ok", "msg"})
public class RtMidiWrapper extends Structure {
    /**
     * C type : void*
     */
    public Pointer ptr;
    /**
     * C type : void*
     */
    public Pointer data;
    /**
     * C type : bool
     */
    public byte ok;
    /**
     * C type : const char*
     */
    public String msg;

    public RtMidiWrapper() {
        super();
    }

    public RtMidiWrapper(Pointer ptr, Pointer data, byte ok, String msg) {
        super();
        this.ptr = ptr;
        this.data = data;
        this.ok = ok;
        this.msg = msg;
    }

    public RtMidiWrapper(Pointer peer) {
        super(peer);
    }

    public void reset() {
        ptr = Pointer.NULL;
        data = Pointer.NULL;
        ok = 0;
        msg = "";
    }

    public static class ByReference extends RtMidiWrapper implements Structure.ByReference {

    }

    public static class ByValue extends RtMidiWrapper implements Structure.ByValue {

    }
}

