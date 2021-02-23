package com.github.basshelal.jnartmidi.api;

/**
 * Used in {@link ReadableMidiPort} to listen to {@link MidiMessage}s that the {@link ReadableMidiPort} receives.
 *
 * @author Bassam Helal
 * @see ReadableMidiPort#setCallback
 */
public interface MidiMessageCallback {
    /**
     * <b>Real time critical!</b>
     * <br>
     * It is advised against performing any blocking or long running operations within this function,
     * such as allocating memory or using locks or waiting etc.
     * <br>
     * This is because the function must successfully return before being called again, meaning if a long operation
     * has not finished by the time the next {@link MidiMessage} is received then this function will not be called in
     * time leading to late messages and a bad user experience.
     * <br>
     * Also not that this function could be called <i>much more</i> than you expect as many MIDI controllers and sequencers
     * send time information messages and other meta messages very often.
     * RtMidi allows you to ignore these using {@link ReadableMidiPort#ignoreTypes} in which case the callback will
     * not be triggered for these messages.
     *
     * @param message   the {@link MidiMessage} that was received, this should ideally not be modified,
     *                  see {@link MidiMessage#getData()} for more.
     * @param deltaTime the time difference in seconds since the last received message, as reported by RtMidi
     */
    @SuppressWarnings("UnnecessaryInterfaceModifier")
    public void onMessage(final MidiMessage message, final double deltaTime);
}