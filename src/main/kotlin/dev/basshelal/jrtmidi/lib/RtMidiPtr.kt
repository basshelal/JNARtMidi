package dev.basshelal.jrtmidi.lib

import jnr.ffi.Runtime
import jnr.ffi.Struct

/**
 * The struct used by RtMidi to wrap an RtMidi object for C function return statuses.
 * This should only be used internally for interaction with [RtMidiLibrary].
 * @author Bassam Helal
 */
open class RtMidiPtr(rt: Runtime) : Struct(rt) {

    /** C type : void* */
    @JvmField
    internal val ptr: Pointer = Pointer()

    /** C type : void* */
    @JvmField
    internal val data: Pointer = Pointer()

    /** C type : bool */
    @JvmField
    internal val ok: Boolean = Boolean()

    /** C type : const char* */
    @JvmField
    internal val msg: String = AsciiString(16)
    // TODO: 28-Mar-2021 @basshelal: Convert to Pointer!
}

/** Used for functions expecting an in device like [RtMidiLibrary.rtmidi_in_free] */
class RtMidiInPtr(rt: Runtime) : RtMidiPtr(rt)

/** Used for functions expecting an out device like [RtMidiLibrary.rtmidi_out_free] */
class RtMidiOutPtr(rt: Runtime) : RtMidiPtr(rt)