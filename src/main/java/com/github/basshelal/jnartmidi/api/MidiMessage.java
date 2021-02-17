package com.github.basshelal.jnartmidi.api;

public class MidiMessage {

    // System common messages

    /**
     * Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int MIDI_TIME_CODE = 0xF1; // 241

    /**
     * Status byte for Song Position Pointer message (0xF2, or 242).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int SONG_POSITION_POINTER = 0xF2; // 242

    /**
     * Status byte for MIDI Song Select message (0xF3, or 243).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int SONG_SELECT = 0xF3; // 243

    /**
     * Status byte for Tune Request message (0xF6, or 246).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int TUNE_REQUEST = 0xF6; // 246

    /**
     * Status byte for End of System Exclusive message (0xF7, or 247).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int END_OF_EXCLUSIVE = 0xF7; // 247

    // System real-time messages

    /**
     * Status byte for Timing Clock message (0xF8, or 248).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int TIMING_CLOCK = 0xF8; // 248

    /**
     * Status byte for Start message (0xFA, or 250).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int START = 0xFA; // 250

    /**
     * Status byte for Continue message (0xFB, or 251).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int CONTINUE = 0xFB; // 251

    /**
     * Status byte for Stop message (0xFC, or 252).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int STOP = 0xFC; //252

    /**
     * Status byte for Active Sensing message (0xFE, or 254).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int ACTIVE_SENSING = 0xFE; // 254

    /**
     * Status byte for System Reset message (0xFF, or 255).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int SYSTEM_RESET = 0xFF; // 255

    // Channel voice message upper nibble defines

    /**
     * Command value for Note Off message (0x80, or 128).
     */
    public static final int NOTE_OFF = 0x80;  // 128

    /**
     * Command value for Note On message (0x90, or 144).
     */
    public static final int NOTE_ON = 0x90;  // 144

    /**
     * Command value for Polyphonic Key Pressure (Aftertouch) message (0xA0, or
     * 160).
     */
    public static final int POLY_PRESSURE = 0xA0;  // 160

    /**
     * Command value for Control Change message (0xB0, or 176).
     */
    public static final int CONTROL_CHANGE = 0xB0;  // 176

    /**
     * Command value for Program Change message (0xC0, or 192).
     */
    public static final int PROGRAM_CHANGE = 0xC0;  // 192

    /**
     * Command value for Channel Pressure (Aftertouch) message (0xD0, or 208).
     */
    public static final int CHANNEL_PRESSURE = 0xD0;  // 208

    /**
     * Command value for Pitch Bend message (0xE0, or 224).
     */
    public static final int PITCH_BEND = 0xE0;  // 224

}
