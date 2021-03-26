@file:Suppress("unused", "ReplaceJavaStaticMethodWithKotlinAnalog")

package dev.basshelal.jrtmidi.api

import java.util.Arrays
import java.util.Objects

/**
 * Represents a MIDI message of arbitrary size, wraps a [ByteArray] in [data].
 *
 * Also contains useful MIDI constants in the companion object, ie as static fields.
 *
 * @param size the [size] of the [MidiMessage] to begin with, defaults to [MidiMessage.DEFAULT_DATA_SIZE]
 * @author Bassam Helal
 */
class MidiMessage
@JvmOverloads constructor(size: Int = DEFAULT_DATA_SIZE) : Iterable<Byte> {

    /**
     * @return the [ByteArray] backing this [MidiMessage]
     * **WARNING:** any changes made to the array will be reflected in this [MidiMessage] causing
     * unexpected strange behavior, if you plan on modifying the returned array,
     * use one of the functions that provide you with a copy: [dataCopy] , [getDataCopy]
     */
    var data: ByteArray = ByteArray(size)
        private set

    /**
     * Gets or sets the size of this [MidiMessage],
     * resizing this [MidiMessage] will reset the contents unless the requested size is equal to the current size
     * in which case nothing happens
     */
    var size: Int = size
        set(value) {
            require(value >= 0) { "Size cannot be less than 0: $value" }
            if (data.size < value) data = ByteArray(value) { if (it in data.indices) data[it] else 0 }
            field = value
        }

    /** Create a new [MidiMessage] with the data initialized from the passed in array */
    constructor(data: ByteArray) : this(data.size) {
        this.setData(data)
    }

    /** Create a new [MidiMessage] from the data of another [midiMessage] */
    constructor(midiMessage: MidiMessage) : this(midiMessage.dataCopy)

    /**
     * Sets the value at [index] to [value]
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    operator fun set(index: Int, value: Byte) {
        boundsCheck(index)
        data[index] = value
    }

    /**
     * Sets the value at [index] to [value]
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    operator fun set(index: Int, value: Int) = this.set(index, value.toByte())

    /**
     * @return the value at [index]
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    operator fun get(index: Int): Byte {
        boundsCheck(index)
        return data[index]
    }

    /**
     * Copies the data from [data] until [length] into this [MidiMessage]'s data
     * @throws IndexOutOfBoundsException if [length] is less than 0 or greater than [data]'s length
     */
    fun setData(data: ByteArray, length: Int) {
        if (length < 0 || length > data.size) throw IndexOutOfBoundsException("Length out of bounds: $length")
        if (this.size < length) this.size = length
        data.copyInto(this.data, endIndex = length)
    }

    /** Copies the data from [data] into this [MidiMessage]'s data */
    fun setData(data: ByteArray) = this.setData(data, data.size)

    /** Copies the data from [midiMessage] into this [MidiMessage] without modifying [midiMessage]'s data */
    fun setData(midiMessage: MidiMessage) = this.setData(midiMessage.dataCopy)

    /**
     * @return a *copy* of the [data] of this [MidiMessage] into the passed in [buffer]
     * @throws IllegalArgumentException if the passed in [buffer]'s length is less than this [size]
     */
    fun getDataCopy(buffer: ByteArray): ByteArray {
        require(buffer.size >= size) {
            "Passed in buffer is not large enough to contain message, " +
                    "buffer length: ${buffer.size} message size: ${data.size}"
        }
        return data.copyInto(buffer, endIndex = size)
    }

    /** Gets a *copy* of the [data] in this [MidiMessage] */
    val dataCopy: ByteArray get() = this.getDataCopy(ByteArray(size))

    private fun boundsCheck(index: Int) {
        if (index + 1 > size)
            throw IndexOutOfBoundsException("Passed in index: $index, greater than MidiMessage size of: $size" +
                    "\nGrow MidiMessage size by calling MidiMessage.setSize()")
    }

    override fun iterator() = object : Iterator<Byte> {
        var index: Int = 0
        override fun hasNext(): Boolean = index < size
        override fun next(): Byte = data[index++]
    }

    override fun hashCode(): Int = Objects.hash(this)

    override fun equals(other: Any?): Boolean = other is MidiMessage &&
            this.size == other.size &&
            Arrays.equals(this.data, 0, size, other.data, 0, size)

    override fun toString(): String = "MidiMessage: [${this.joinToString()}]"

    companion object {

        /** The default size to create a [MidiMessage] if none is specified */
        const val DEFAULT_DATA_SIZE = 3

        // System common messages

        /** Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241). */
        const val MIDI_TIME_CODE: Byte = 241.toByte()

        /** Status byte for Song Position Pointer message (0xF2, or 242). */
        const val SONG_POSITION_POINTER: Byte = 242.toByte()

        /** Status byte for MIDI Song Select message (0xF3, or 243). */
        const val SONG_SELECT: Byte = 243.toByte()

        /** Status byte for Tune Request message (0xF6, or 246). */
        const val TUNE_REQUEST: Byte = 246.toByte()

        /** Status byte for End of System Exclusive message (0xF7, or 247). */
        const val END_OF_EXCLUSIVE: Byte = 247.toByte()

        // System real-time messages

        /** Status byte for Timing Clock message (0xF8, or 248). */
        const val TIMING_CLOCK: Byte = 248.toByte()

        /** Status byte for Start message (0xFA, or 250). */
        const val START: Byte = 250.toByte()

        /** Status byte for Continue message (0xFB, or 251). */
        const val CONTINUE: Byte = 251.toByte()

        /** Status byte for Stop message (0xFC, or 252). */
        const val STOP: Byte = 252.toByte()

        /** Status byte for Active Sensing message (0xFE, or 254). */
        const val ACTIVE_SENSING: Byte = 254.toByte()

        /** Status byte for System Reset message (0xFF, or 255). */
        const val SYSTEM_RESET: Byte = 255.toByte()

        // Channel voice message upper nibble defines

        /** Command value for Note Off message (0x80, or 128). */
        const val NOTE_OFF: Byte = 128.toByte()

        /** Command value for Note On message (0x90, or 144). */
        const val NOTE_ON: Byte = 144.toByte()

        /** Command value for Polyphonic Key Pressure (Aftertouch) message (0xA0, or 160). */
        const val POLY_PRESSURE: Byte = 160.toByte()

        /** Command value for Control Change message (0xB0, or 176). */
        const val CONTROL_CHANGE: Byte = 176.toByte()

        /** Command value for Program Change message (0xC0, or 192). */
        const val PROGRAM_CHANGE: Byte = 192.toByte()

        /** Command value for Channel Pressure (Aftertouch) message (0xD0, or 208). */
        const val CHANNEL_PRESSURE: Byte = 208.toByte()

        /** Command value for Pitch Bend message (0xE0, or 224). */
        const val PITCH_BEND: Byte = 224.toByte()
    }
}