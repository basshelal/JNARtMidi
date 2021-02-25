package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.lib.RtMidiPtr

open class RtMidiException(msg: String) : RuntimeException(msg)

/**
 * An [RtMidiException] signalling something went wrong in RtMidi's native code, and not in the JVM code.
 *
 * Ideally this should never be seen at runtime but it exists to distinguish where the error occurred,
 * ie in the native code and not in the library's Kotlin code.
 *
 * Implementation Note: this should be thrown when [RtMidiPtr.ok] is false with the message [RtMidiPtr.msg]
 */
class RtMidiNativeException : RtMidiException {

    constructor(msg: String) : super(msg)

    internal constructor(ptr: RtMidiPtr) : this("An error occurred in the native code of RtMidi:\n${ptr.msg}")

}

class RtMidiPortException(msg: String) : RtMidiException(msg)
