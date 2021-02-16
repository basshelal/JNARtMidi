package com.github.basshelal.jnartmidi.api;

public class RtMidiException extends Exception {

    public RtMidiException() {
    }

    public RtMidiException(String msg) {
        super(msg);
    }

    public RtMidiException(Throwable e) {
        super(e);
    }

    public RtMidiException(String msg, Throwable e) {
        super(msg, e);
    }

}
