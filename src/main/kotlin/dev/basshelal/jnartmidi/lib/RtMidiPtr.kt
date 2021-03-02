package dev.basshelal.jnartmidi.lib

import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder

/**
 * The struct used by RtMidi to wrap an RtMidi object for C function return statuses.
 * This should only be used internally for interaction with [RtMidiLibrary].
 * @author Bassam Helal
 */
@FieldOrder("ptr", "data", "ok", "msg")
open class RtMidiPtr : Structure { // should be internal but because MidiPort uses it then must be public :/

    /** C type : void* */
    @JvmField
    internal val ptr: Pointer?

    /** C type : void* */
    @JvmField
    internal val data: Pointer?

    /** C type : bool */
    @JvmField
    internal val ok: Boolean

    /** C type : const char* */
    @JvmField
    internal val msg: String?

    internal constructor() : super() {
        this.ptr = null
        this.data = null
        this.ok = false
        this.msg = null
    }

    internal constructor(peer: Pointer?) : super(peer) {
        this.ptr = null
        this.data = null
        this.ok = false
        this.msg = null
    }

    internal constructor(ptr: Pointer?, data: Pointer?, ok: Boolean, msg: String?) : super() {
        this.ptr = ptr
        this.data = data
        this.ok = ok
        this.msg = msg
    }
}

/** Used for functions expecting an in device like [RtMidiLibrary.rtmidi_in_free] */
class RtMidiInPtr : RtMidiPtr()

/** Used for functions expecting an out device like [RtMidiLibrary.rtmidi_out_free] */
class RtMidiOutPtr : RtMidiPtr()