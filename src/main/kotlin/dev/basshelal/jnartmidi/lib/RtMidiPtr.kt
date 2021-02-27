package dev.basshelal.jnartmidi.lib

import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder

/**
 * The struct used by RtMidi to wrap an RtMidi object for C function return statuses.
 *
 * @author Bassam Helal
 */
@FieldOrder("ptr", "data", "ok", "msg")
open class RtMidiPtr : Structure { // should be internal but because MidiPort uses it then must be public :/

    /** C type : void* */
    @JvmField
    internal var ptr: Pointer? = null

    /** C type : void* */
    @JvmField
    internal var data: Pointer? = null

    /** C type : bool */
    @JvmField
    internal var ok: Boolean = false

    /** C type : const char* */
    @JvmField
    internal var msg: String? = null

    internal constructor() : super()

    internal constructor(peer: Pointer?) : super(peer)

    internal constructor(ptr: Pointer?, data: Pointer?, ok: Boolean, msg: String?) : super() {
        this.ptr = ptr
        this.data = data
        this.ok = ok
        this.msg = msg
    }

    internal constructor(other: RtMidiPtr) : this(other.pointer) {
        this.ptr = other.ptr
        this.data = other.data
        this.ok = other.ok
        this.msg = other.msg
    }
}

/** Used for functions expecting an in device like [RtMidiLibrary.rtmidi_in_free] */
class RtMidiInPtr : RtMidiPtr()

/** Used for functions expecting an out device like [RtMidiLibrary.rtmidi_out_free] */
class RtMidiOutPtr : RtMidiPtr()