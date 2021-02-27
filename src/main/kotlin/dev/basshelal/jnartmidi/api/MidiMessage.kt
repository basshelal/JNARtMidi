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
            require(value >= 0) { "Size cannot be less than 0: $value" }
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

    /** Copies the data from [midiMessage] into this [MidiMessage] without modifying [midiMessage]'s data */
    fun setDataFrom(midiMessage: MidiMessage) = this.setData(midiMessage.data)

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

    private inline val exception: IndexOutOfBoundsException
        get() = IndexOutOfBoundsException("MidiMessage does not contain enough data:\n$this")

    // TODO: 26/02/2021 Behavior of these below should be well defined because there's some
    //  bit shifting to do to make usable values, read the MIDI spec to understand this properly

    var status: Int
        set(value) = if (data.isEmpty()) throw exception else data[0] = value
        get() = if (data.isEmpty()) throw exception else data[0] and 0xF0

    var command: Int
        set(value) = if (data.isEmpty()) throw exception else data[0] = value
        get() = if (data.isEmpty()) throw exception else data[0] and 0xF0

    var channel: Int
        set(value) = if (data.isEmpty()) throw exception else data[0] = value
        get() = if (data.isEmpty()) throw exception else data[0] and 0x0F

    override fun hashCode(): Int = data.contentHashCode()

    override fun equals(other: Any?): Boolean = other is MidiMessage && this.data contentEquals other.data

    override fun toString(): String = "MidiMessage: ${data.contentToString()}"

    companion object {

        /**
         * @return the number of data bytes associated with the passed in [status]
         * @throws IllegalArgumentException if the [status] argument does not have a known size
         */
        @JvmStatic
        fun getDataLength(status: Int): Int =
                when (status) { // system common and system real-time messages
                    TUNE_REQUEST, END_OF_EXCLUSIVE, TIMING_CLOCK, 0xF9, START, CONTINUE, STOP, 0xFD, ACTIVE_SENSING, SYSTEM_RESET -> 0
                    MIDI_TIME_CODE, SONG_SELECT -> 1
                    SONG_POSITION_POINTER -> 2
                    else -> when (status and 0xF0) {
                        NOTE_OFF, NOTE_ON, POLY_PRESSURE, CONTROL_CHANGE, PITCH_BEND -> 2
                        PROGRAM_CHANGE, CHANNEL_PRESSURE -> 1
                        else -> throw IllegalArgumentException("Invalid status byte: $status")
                    }
                }

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