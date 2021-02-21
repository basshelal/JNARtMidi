package com.github.basshelal.jnartmidi.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * ! \brief Wraps an RtMidi object for C function return statuses.<br>
 * <i>native declaration : rtmidi_c.h:7</i><br>
 */
@Structure.FieldOrder({"ptr", "data", "ok", "msg"})
public class RtMidiPtr extends Structure {
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
    public boolean ok;
    /**
     * C type : const char*
     */
    public String msg;

    public RtMidiPtr() { super(); }

    public RtMidiPtr(Pointer ptr, Pointer data, boolean ok, String msg) {
        super();
        this.ptr = ptr;
        this.data = data;
        this.ok = ok;
        this.msg = msg;
    }

    public RtMidiPtr(Pointer peer) { super(peer); }

    public RtMidiPtr(RtMidiPtr other) {
        this(other.getPointer());
        this.ptr = other.ptr;
        this.data = other.data;
        this.ok = other.ok;
        this.msg = other.msg;
    }

}

