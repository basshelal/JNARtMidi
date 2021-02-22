package com.github.basshelal.jnartmidi.api.callbacks;

import com.github.basshelal.jnartmidi.api.MidiMessage;

public interface MidiMessageCallback {
    // RealTimeCritical
    public void onMessage(MidiMessage message);
}