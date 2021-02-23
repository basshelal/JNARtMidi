package com.github.basshelal.jnartmidi.api.callbacks;

public interface ArrayCallback {
    // RealTimeCritical
    public void onMessage(final int[] message, final double deltaTime);
}
