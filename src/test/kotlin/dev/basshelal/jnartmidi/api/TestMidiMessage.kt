package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.defaultBeforeAll
import dev.basshelal.jnartmidi.mustBe
import dev.basshelal.jnartmidi.mustNotBe
import dev.basshelal.jnartmidi.mustNotBeSameAs
import dev.basshelal.jnartmidi.mustThrow
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/** Tests [MidiMessage] */
internal class TestMidiMessage {
    companion object {
        @BeforeAll
        @JvmStatic
        fun `Before All`() = defaultBeforeAll()

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }

    @Test
    fun `Empty Constructor & Size Constructor`() {
        var midiMessage = MidiMessage()
        midiMessage.data.size mustBe MidiMessage.DEFAULT_DATA_SIZE
        midiMessage.data.forEach { it mustBe 0 }
        val newSize = 9
        midiMessage = MidiMessage(newSize)
        midiMessage.data.size mustBe newSize
        midiMessage.data.forEach { it mustBe 0 }
    }

    @Test
    fun `Array Constructor & MidiMessage Constructor`() {
        val size = 7
        val array = ByteArray(size) { it.toByte() }
        val midiMessage = MidiMessage(array)
        midiMessage.data.size mustBe size
        midiMessage.data mustBe array
        midiMessage.data mustNotBeSameAs array
        val newMidiMessage = MidiMessage(midiMessage)
        midiMessage.data.size mustBe newMidiMessage.data.size
        midiMessage.data mustBe newMidiMessage.data
        midiMessage.data mustNotBeSameAs newMidiMessage.data
        midiMessage mustBe midiMessage
    }

    @Test
    fun `Operator functions Set & Get`() {
        val size = 9
        val value = 100
        val midiMessage = MidiMessage(size)
        midiMessage[0] = value
        midiMessage[8] = value
        { midiMessage[9] = value } mustThrow IndexOutOfBoundsException::class
        midiMessage[0] mustBe value
        midiMessage[8] mustBe value
        { midiMessage[9] } mustThrow IndexOutOfBoundsException::class
    }

    @Test
    fun Size() {
        var size = 9
        val midiMessage = MidiMessage(size)
        midiMessage.size mustBe size
        size = 100
        midiMessage.size = size
        midiMessage.size mustBe size
        midiMessage.data.size mustBe size
    }

    @Test
    fun `Set Data`() {
        val default = ByteArray(MidiMessage.DEFAULT_DATA_SIZE)
        val midiMessage = MidiMessage()
        val data = byteArrayOf(0, 1, 2, 3, 4)
        midiMessage.data mustBe default
        midiMessage.setData(data)
        midiMessage.data mustBe data
        midiMessage.data mustNotBeSameAs data
        midiMessage.setData(default, 3)
        default.copyInto(data, endIndex = 3)
        midiMessage.data mustBe data
    }

    @Test
    fun `Set Data From MidiMessage`() {
        val data = byteArrayOf(0, 1, 2, 3, 4)
        val midiMessage = MidiMessage(data)
        val copy = MidiMessage()
        copy.data mustNotBe data
        copy.setDataFrom(midiMessage)
        copy.data mustBe data
        copy.data mustBe midiMessage.data
        copy.data mustNotBeSameAs data
        copy.data mustNotBeSameAs midiMessage.data
    }

    @Test
    fun `Modify data`() {
        val value = 69
        val midiMessage = MidiMessage(2)
        midiMessage.data[0] = value.toByte()
        midiMessage[0] mustBe value
        midiMessage[1] mustBe 0
    }

    @Test
    fun `Get Data Copies`() {
        val data = byteArrayOf(0, 1, 2, 3, 4)
        val midiMessage = MidiMessage(data)
        midiMessage.data mustBe data
        midiMessage.data mustNotBeSameAs data
        midiMessage.data mustBe midiMessage.dataCopy
        midiMessage.data mustNotBeSameAs midiMessage.dataCopy

        var copyBuffer = ByteArray(0);
        { midiMessage.getDataCopy(copyBuffer) } mustThrow IllegalArgumentException::class
        copyBuffer = ByteArray(data.size * 2)
        midiMessage.getDataCopy(copyBuffer)
        midiMessage.data.forEachIndexed { index, it -> copyBuffer[index] mustBe it }
        copyBuffer.size mustNotBe data.size
        copyBuffer[data.lastIndex + 1] mustBe 0
    }

    @Test
    fun `Get First Byte`() {
        val midiMessage = MidiMessage(0);
        { midiMessage.status } mustThrow IndexOutOfBoundsException::class
        { midiMessage.channel } mustThrow IndexOutOfBoundsException::class
        { midiMessage.command } mustThrow IndexOutOfBoundsException::class
        midiMessage.size = 1
        throw NotImplementedError("Not yet implemented") // TODO: 26/02/2021 Implement!
    }

}