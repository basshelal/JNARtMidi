package com.github.basshelal.jnartmidi.api;

public interface MidiMessageCallback {
    // RealTimeCritical
    public void onMessage(final MidiMessage message, final double deltaTime);
}