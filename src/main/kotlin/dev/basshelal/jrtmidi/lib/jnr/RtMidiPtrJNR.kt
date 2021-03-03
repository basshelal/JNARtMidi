package dev.basshelal.jrtmidi.lib.jnr

import jnr.ffi.Runtime
import jnr.ffi.Struct

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
}

class RtMidiInPtr(rt: Runtime) : RtMidiPtr(rt)

class RtMidiOutPtr(rt: Runtime) : RtMidiPtr(rt)