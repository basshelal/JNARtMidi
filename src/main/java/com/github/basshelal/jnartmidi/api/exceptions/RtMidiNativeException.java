package com.github.basshelal.jnartmidi.api.exceptions;

import com.github.basshelal.jnartmidi.lib.RtMidiPtr;

/**
 * An {@link RtMidiException} signalling something went wrong in RtMidi's native code, and not in the Java code.
 *
 * Ideally this should never be thrown but it exists to distinguish where the error occurred, ie in the native code
 * and not in the Java code.
 *
 * @implNote this should be thrown when {@link RtMidiPtr#ok} is false with the message {@link RtMidiPtr#msg}
 */
public class RtMidiNativeException extends RtMidiException {

    public RtMidiNativeException(String msg) { super(msg); }

    public RtMidiNativeException(RtMidiPtr ptr) { this("An error occurred in the native code of RtMidi\n" + ptr.msg); }

}
