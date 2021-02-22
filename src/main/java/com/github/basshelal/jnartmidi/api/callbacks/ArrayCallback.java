package com.github.basshelal.jnartmidi.api.callbacks;

public interface ArrayCallback {
    // RealTimeCritical
    public void onMessage(int[] message, double deltaTime);
}
