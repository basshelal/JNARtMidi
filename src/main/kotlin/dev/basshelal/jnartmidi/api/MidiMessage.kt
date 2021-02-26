package dev.basshelal.jnartmidi.api

/**
 * Represents a MIDI message of arbitrary size, wraps an [IntArray] in [data].
 *
 * Also contains useful MIDI constants in the companion object, ie as static fields.
 *
 * @param size the [size] of the [MidiMessage] to begin with, defaults to [MidiMessage.DEFAULT_DATA_SIZE]
 * @author Bassam Helal
 */
class MidiMessage
@JvmOverloads constructor(size: Int = DEFAULT_DATA_SIZE) {

    /**
     * @return the [IntArray] backing this [MidiMessage]
     * **WARNING:** any changes made to the array will be reflected in this [MidiMessage] causing
     * unexpected strange behavior, if you plan on modifying the returned array, use one of the functions that provide
     * you with a copy: [dataCopy] , [getDataCopy]
     */
    var data: IntArray = IntArray(size)
        private set

    /**
     * Gets or sets the size of this [MidiMessage],
     * resizing this [MidiMessage] will reset the contents unless the requested size is equal to the current size
     * in which case nothing happens
     */
    var size: Int
        get() = data.size
        set(value) {
            require(value > 0) { "Size cannot be less than 0: $value" }
            if (value != data.size) data = IntArray(value)
        }

    /** Create a new [MidiMessage] with the data initialized from the passed in array */
    constructor(data: IntArray) : this(data.size) {
        this.setData(data)
    }

    /** Create a new [MidiMessage] from the data of another [midiMessage] */
    constructor(midiMessage: MidiMessage) : this(midiMessage.data)

    init {
        this.size = size
    }

    /**
     * Sets the value at [index] to [value]
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    /**
     * @return the value at [index]
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    operator fun get(index: Int): Int = data[index]

    /**
     * Copies the data from [data] until [length] into this [MidiMessage]'s data
     * @throws IndexOutOfBoundsException if [length] is less than 0 or greater than [data]'s length
     */
    fun setData(data: IntArray, length: Int) {
        if (length < 0 || length > data.size) throw IndexOutOfBoundsException("Length out of bounds: $length")
        if (this.data.size < length) this.data = IntArray(length)
        data.copyInto(this.data, endIndex = length)
    }

    /** Copies the data from [data] into this [MidiMessage]'s data */
    fun setData(data: IntArray) = this.setData(data, data.size)

    /**
     * @return a *copy* of the [data] of this [MidiMessage] into the passed in [buffer]
     * @throws IllegalArgumentException if the passed in [buffer]'s length is less than this [size]
     */
    fun getDataCopy(buffer: IntArray): IntArray {
        require(buffer.size >= data.size) {
            "Passed in buffer is not large enough to contain data, buffer length: ${buffer.size} data size: ${data.size}"
        }
        return data.copyInto(buffer)
    }

    /** Gets a *copy* of the [data] in this [MidiMessage] */
    val dataCopy: IntArray
        get() = this.getDataCopy(IntArray(data.size))

    val status: Int
        get() = if (data.size < 1)
            throw IndexOutOfBoundsException("MidiMessage does not contain enough data:\n$this")
        else data[0] and 0xF0

    val channel: Int
        get() = if (data.size < 1)
            throw IndexOutOfBoundsException("MidiMessage does not contain enough data:\n$this")
        else data[0] and 0x0F

    val command: Int
        get() = if (data.size < 1)
            throw IndexOutOfBoundsException("MidiMessage does not contain enough data:\n$this")
        else data[0] and 0xF0

    val data1: Int
        get() = if (data.size < 2)
            throw IndexOutOfBoundsException("MidiMessage does not contain enough data:\n$this")
        else data[1]

    val data2: Int
        get() = if (data.size < 3)
            throw IndexOutOfBoundsException("MidiMessage does not contain enough data:\n$this")
        else data[2]

    // TODO: 23/02/2021 Check!
    /**
     * Sets the parameters for a MIDI message that takes no data bytes.
     *
     * @param status the MIDI status byte
     * @throws RtMidiException if `status` does not specify a
     * valid MIDI status byte for a message that requires no data bytes
     * @see .setMessage
     * @see .setMessage
     */
    fun setMessage(status: Int) {
        // check for valid values
        val dataLength = getDataLength(status) // can throw InvalidMidiDataException
        if (dataLength != 0) {
            throw RtMidiException("Status byte; $status requires $dataLength data bytes")
        }
        setMessage(status, 0, 0)
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
     * belonging to the message, do not specify a valid MIDI message
     * @see .setMessage
     * @see .setMessage
     */
    fun setMessage(status: Int, data1: Int, data2: Int) {
        // check for valid values
        val dataLength = getDataLength(status) // can throw RtMidiException
        if (dataLength > 0) {
            if (data1 < 0 || data1 > 127) {
                throw RtMidiException("data1 out of range: $data1")
            }
            if (dataLength > 1) {
                if (data2 < 0 || data2 > 127) {
                    throw RtMidiException("data2 out of range: $data2")
                }
            }
        }

        // set the data
        data[0] = status and 0xFF
        if (data.size > 1) {
            data[1] = data1 and 0xFF
            if (data.size > 2) {
                data[2] = data2 and 0xFF
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
     * belonging to the message, do not specify a valid MIDI message
     * @see .setMessage
     * @see .setMessage
     * @see .getCommand
     *
     * @see .getChannel
     *
     * @see .getData1
     *
     * @see .getData2
     */
    fun setMessage(command: Int, channel: Int, data1: Int, data2: Int) {
        // check for valid values
        if (command >= 0xF0 || command < 0x80) {
            throw RtMidiException("command out of range: 0x" + Integer.toHexString(command))
        }
        if (channel and -0x10 != 0) { // <=> (channel<0 || channel>15)
            throw RtMidiException("channel out of range: $channel")
        }
        setMessage(command and 0xF0 or (channel and 0x0F), data1, data2)
    }

    /**
     * Retrieves the number of data bytes associated with a particular status
     * byte value.
     *
     * @param status status byte value, which must represent a short MIDI message
     * @return data length in bytes (0, 1, or 2)
     * @throws RtMidiException if the `status` argument does not represent the status byte for any short message
     */
    private fun getDataLength(status: Int): Int {
        // system common and system real-time messages
        when (status) {
            TUNE_REQUEST, END_OF_EXCLUSIVE, TIMING_CLOCK, 0xF9, START, CONTINUE, STOP, 0xFD, ACTIVE_SENSING, SYSTEM_RESET -> return 0
            MIDI_TIME_CODE, SONG_SELECT -> return 1
            SONG_POSITION_POINTER -> return 2
        }
        return when (status and 0xF0) {
            NOTE_OFF, NOTE_ON, POLY_PRESSURE, CONTROL_CHANGE, PITCH_BEND -> 2
            PROGRAM_CHANGE, CHANNEL_PRESSURE -> 1
            else -> throw RtMidiException("Invalid status byte: $status")
        }
    }

    override fun hashCode(): Int = data.contentHashCode()

    override fun equals(other: Any?): Boolean = other is MidiMessage && this.data contentEquals other.data

    override fun toString(): String = "MidiMessage: ${data.contentToString()}"

    companion object {

        /** The default size to create a [MidiMessage] if none is specified */
        const val DEFAULT_DATA_SIZE = 3

        // System common messages

        /** Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241). */
        const val MIDI_TIME_CODE = 241

        /** Status byte for Song Position Pointer message (0xF2, or 242). */
        const val SONG_POSITION_POINTER = 242

        /** Status byte for MIDI Song Select message (0xF3, or 243). */
        const val SONG_SELECT = 243

        /** Status byte for Tune Request message (0xF6, or 246). */
        const val TUNE_REQUEST = 246

        /** Status byte for End of System Exclusive message (0xF7, or 247). */
        const val END_OF_EXCLUSIVE = 247

        // System real-time messages

        /** Status byte for Timing Clock message (0xF8, or 248). */
        const val TIMING_CLOCK = 248

        /** Status byte for Start message (0xFA, or 250). */
        const val START = 250

        /** Status byte for Continue message (0xFB, or 251). */
        const val CONTINUE = 251

        /** Status byte for Stop message (0xFC, or 252). */
        const val STOP = 252

        /** Status byte for Active Sensing message (0xFE, or 254). */
        const val ACTIVE_SENSING = 254

        /** Status byte for System Reset message (0xFF, or 255). */
        const val SYSTEM_RESET = 255

        // Channel voice message upper nibble defines

        /** Command value for Note Off message (0x80, or 128). */
        const val NOTE_OFF = 128

        /** Command value for Note On message (0x90, or 144). */
        const val NOTE_ON = 144

        /** Command value for Polyphonic Key Pressure (Aftertouch) message (0xA0, or 160). */
        const val POLY_PRESSURE = 160

        /** Command value for Control Change message (0xB0, or 176). */
        const val CONTROL_CHANGE = 176

        /** Command value for Program Change message (0xC0, or 192). */
        const val PROGRAM_CHANGE = 192

        /** Command value for Channel Pressure (Aftertouch) message (0xD0, or 208). */
        const val CHANNEL_PRESSURE = 208

        /** Command value for Pitch Bend message (0xE0, or 224). */
        const val PITCH_BEND = 224
    }
}