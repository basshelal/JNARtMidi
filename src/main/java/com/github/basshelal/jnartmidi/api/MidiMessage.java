package com.github.basshelal.jnartmidi.api;

import java.util.Arrays;

public class MidiMessage {

    public static int DEFAULT_DATA_SIZE = 3;

    private int[] data;

    //region Constructors

    public /* constructor */ MidiMessage() {
        this(DEFAULT_DATA_SIZE);
    }

    public /* constructor */ MidiMessage(int size) {
        this.setSize(size);
    }

    public /* constructor */ MidiMessage(int[] data) {
        this(data.length);
        this.setData(data);
    }

    public /* constructor */ MidiMessage(MidiMessage midiMessage) {
        this(midiMessage.data);
    }

    //endregion Constructors

    public void set(int index, int value) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.data.length)
            throw new IndexOutOfBoundsException("Index out of bounds, index: " + index + " length: " + this.data.length);
        this.data[index] = value;
    }

    public int get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.data.length)
            throw new IndexOutOfBoundsException("Index out of bounds, index: " + index + " length: " + this.data.length);
        return this.data[index];
    }

    public void setData(int[] data, int length) throws IndexOutOfBoundsException {
        if (length < 0 || length > data.length)
            throw new IndexOutOfBoundsException("Length out of bounds: " + length);

        if (this.data == null || this.data.length < length) {
            this.data = new int[length];
        }
        System.arraycopy(data, 0, this.data, 0, length);
    }

    public void setData(int[] data) { this.setData(data, data.length); }

    public int[] getData() { return this.data; }

    public int[] getData(int[] buffer) {
        if (buffer.length < this.data.length)
            throw new IllegalArgumentException("Passed in buffer is not large enough to contain data\n" +
                    "buffer length: " + buffer.length + " data size: " + this.data.length);
        System.arraycopy(this.data, 0, buffer, 0, this.data.length);
        return buffer;
    }

    public int[] getDataCopy() {
        int[] result = new int[this.data.length];
        System.arraycopy(this.data, 0, result, 0, this.data.length);
        return result;
    }

    public int size() { return this.data.length; }

    public void setSize(int size) {
        if (size < 0) throw new IndexOutOfBoundsException("Size out of bounds: " + size);
        if (this.data != null && size == this.data.length) return;
        this.data = new int[size];
    }

    public int getStatus() throws IndexOutOfBoundsException {
        if (this.data.length < 1)
            throw new IndexOutOfBoundsException("MidiMessage does not contain enough data:\n" + this.toString());
        else return (this.data[0] & 0xF0);
    }

    public int getChannel() throws IndexOutOfBoundsException {
        if (this.data.length < 1)
            throw new IndexOutOfBoundsException("MidiMessage does not contain enough data:\n" + this.toString());
        else return (this.data[0] & 0x0F);
    }

    public int getCommand() throws IndexOutOfBoundsException {
        if (this.data.length < 1)
            throw new IndexOutOfBoundsException("MidiMessage does not contain enough data:\n" + this.toString());
        else return (this.data[0] & 0xF0);
    }

    public int getData1() throws IndexOutOfBoundsException {
        if (this.data.length < 2)
            throw new IndexOutOfBoundsException("MidiMessage does not contain enough data:\n" + this.toString());
        else return data[1];
    }

    public int getData2() throws IndexOutOfBoundsException {
        if (this.data.length < 3)
            throw new IndexOutOfBoundsException("MidiMessage does not contain enough data:\n" + this.toString());
        else return data[2];
    }

    // TODO: 23/02/2021 Check!

    /**
     * Sets the parameters for a MIDI message that takes no data bytes.
     *
     * @param status the MIDI status byte
     * @throws RtMidiException if {@code status} does not specify a
     *                         valid MIDI status byte for a message that requires no data bytes
     * @see #setMessage(int, int, int)
     * @see #setMessage(int, int, int, int)
     */
    public void setMessage(int status) throws RtMidiException {
        // check for valid values
        int dataLength = getDataLength(status); // can throw InvalidMidiDataException
        if (dataLength != 0) {
            throw new RtMidiException("Status byte; " + status + " requires " + dataLength + " data bytes");
        }
        setMessage(status, 0, 0);
    }

    // TODO: 23/02/2021 Check!

    /**
     * Sets the parameters for a MIDI message that takes one or two data bytes.
     * If the message takes only one data byte, the second data byte is ignored;
     * if the message does not take any data bytes, both data bytes are ignored.
     *
     * @param status the MIDI status byte
     * @param data1  the first data byte
     * @param data2  the second data byte
     * @throws RtMidiException if the status byte, or all data bytes
     *                         belonging to the message, do not specify a valid MIDI message
     * @see #setMessage(int, int, int, int)
     * @see #setMessage(int)
     */
    public void setMessage(int status, int data1, int data2) throws RtMidiException {
        // check for valid values
        int dataLength = getDataLength(status); // can throw RtMidiException
        if (dataLength > 0) {
            if (data1 < 0 || data1 > 127) {
                throw new RtMidiException("data1 out of range: " + data1);
            }
            if (dataLength > 1) {
                if (data2 < 0 || data2 > 127) {
                    throw new RtMidiException("data2 out of range: " + data2);
                }
            }
        }


        // set the length
        // re-allocate array if ShortMessage(byte[]) constructor gave array with fewer elements
        if (data == null) {
            data = new int[3];
        }

        // set the data
        data[0] = (status & 0xFF);
        if (this.data.length > 1) {
            data[1] = (data1 & 0xFF);
            if (this.data.length > 2) {
                data[2] = (data2 & 0xFF);
            }
        }
    }

    // TODO: 23/02/2021 Check!

    /**
     * Sets the short message parameters for a channel message which takes up to
     * two data bytes. If the message only takes one data byte, the second data
     * byte is ignored; if the message does not take any data bytes, both data
     * bytes are ignored.
     *
     * @param command the MIDI command represented by this message
     * @param channel the channel associated with the message
     * @param data1   the first data byte
     * @param data2   the second data byte
     * @throws RtMidiException if the status byte or all data bytes
     *                         belonging to the message, do not specify a valid MIDI message
     * @see #setMessage(int, int, int)
     * @see #setMessage(int)
     * @see #getCommand
     * @see #getChannel
     * @see #getData1
     * @see #getData2
     */
    public void setMessage(int command, int channel, int data1, int data2) throws RtMidiException {
        // check for valid values
        if (command >= 0xF0 || command < 0x80) {
            throw new RtMidiException("command out of range: 0x" + Integer.toHexString(command));
        }
        if ((channel & 0xFFFFFFF0) != 0) { // <=> (channel<0 || channel>15)
            throw new RtMidiException("channel out of range: " + channel);
        }
        setMessage((command & 0xF0) | (channel & 0x0F), data1, data2);
    }

    /**
     * Retrieves the number of data bytes associated with a particular status
     * byte value.
     *
     * @param status status byte value, which must represent a short MIDI
     *               message
     * @return data length in bytes (0, 1, or 2)
     * @throws RtMidiException if the {@code status} argument does not
     *                         represent the status byte for any short message
     */
    private int getDataLength(int status) throws RtMidiException {
        // system common and system real-time messages
        switch (status) {
            case TUNE_REQUEST:
            case END_OF_EXCLUSIVE:
            case TIMING_CLOCK:
            case 0xF9:                      // Undefined
            case START:
            case CONTINUE:
            case STOP:
            case 0xFD:                      // Undefined
            case ACTIVE_SENSING:
            case SYSTEM_RESET:
                return 0;
            case MIDI_TIME_CODE:
            case SONG_SELECT:
                return 1;
            case SONG_POSITION_POINTER:
                return 2;
        }

        // channel voice and mode messages
        switch (status & 0xF0) {
            case NOTE_OFF:
            case NOTE_ON:
            case POLY_PRESSURE:
            case CONTROL_CHANGE:
            case PITCH_BEND:
                return 2;
            case PROGRAM_CHANGE:
            case CHANNEL_PRESSURE:
                return 1;
            default:
                throw new RtMidiException("Invalid status byte: " + status);
        }
    }

    @Override
    public int hashCode() { return Arrays.hashCode(getData()); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MidiMessage)) return false;
        MidiMessage that = (MidiMessage) o;
        return Arrays.equals(getData(), that.getData());
    }

    @Override
    public String toString() { return "MidiMessage: " + Arrays.toString(data); }

    // System common messages

    /**
     * Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int MIDI_TIME_CODE = 241;

    /**
     * Status byte for Song Position Pointer message (0xF2, or 242).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int SONG_POSITION_POINTER = 242;

    /**
     * Status byte for MIDI Song Select message (0xF3, or 243).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int SONG_SELECT = 243;

    /**
     * Status byte for Tune Request message (0xF6, or 246).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int TUNE_REQUEST = 246;

    /**
     * Status byte for End of System Exclusive message (0xF7, or 247).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int END_OF_EXCLUSIVE = 247;

    // System real-time messages

    /**
     * Status byte for Timing Clock message (0xF8, or 248).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int TIMING_CLOCK = 248;

    /**
     * Status byte for Start message (0xFA, or 250).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int START = 250;

    /**
     * Status byte for Continue message (0xFB, or 251).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int CONTINUE = 251;

    /**
     * Status byte for Stop message (0xFC, or 252).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int STOP = 252;

    /**
     * Status byte for Active Sensing message (0xFE, or 254).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int ACTIVE_SENSING = 254;

    /**
     * Status byte for System Reset message (0xFF, or 255).
     *
     * @see javax.sound.midi.MidiMessage#getStatus
     */
    public static final int SYSTEM_RESET = 255;

    // Channel voice message upper nibble defines

    /**
     * Command value for Note Off message (0x80, or 128).
     */
    public static final int NOTE_OFF = 128;

    /**
     * Command value for Note On message (0x90, or 144).
     */
    public static final int NOTE_ON = 144;

    /**
     * Command value for Polyphonic Key Pressure (Aftertouch) message (0xA0, or
     * 160).
     */
    public static final int POLY_PRESSURE = 160;

    /**
     * Command value for Control Change message (0xB0, or 176).
     */
    public static final int CONTROL_CHANGE = 176;

    /**
     * Command value for Program Change message (0xC0, or 192).
     */
    public static final int PROGRAM_CHANGE = 192;

    /**
     * Command value for Channel Pressure (Aftertouch) message (0xD0, or 208).
     */
    public static final int CHANNEL_PRESSURE = 208;

    /**
     * Command value for Pitch Bend message (0xE0, or 224).
     */
    public static final int PITCH_BEND = 224;

}
