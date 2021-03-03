package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.lib.RtMidiPtr

/**
 * Superclass of all exceptions in JNARtMidi, this is never actually thrown, instead, its subclasses are thrown
 * containing more detailed information, such as [RtMidiPortException] or [RtMidiNativeException]
 * You can use this as a means to "catch all" exceptions from this library.
 */
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

/**
 * An [RtMidiException] signalling that something went wrong with regards to a [MidiPort].
 * Most often this is thrown when a destroyed port is trying to be used after destruction but can also be thrown if
 * an operation was unsuccessful such as when opening a port that no longer exists trying to open a virtual port on a
 * platform that does not support them etc.
 */
class RtMidiPortException(msg: String) : RtMidiException(msg)
