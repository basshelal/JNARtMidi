package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.defaultBeforeAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

/** Tests [MidiMessage] */
internal class MidiMessageTest : StringSpec({
    beforeSpec { defaultBeforeAll() }

    afterSpec { }

    "Empty Constructor & Size Constructor" {
        var midiMessage = MidiMessage()
        midiMessage.data.size shouldBe MidiMessage.DEFAULT_DATA_SIZE
        midiMessage.data.forEach { it shouldBe 0 }
        val newSize = 9
        midiMessage = MidiMessage(newSize)
        midiMessage.data.size shouldBe newSize
        midiMessage.data.forEach { it shouldBe 0 }
    }

    "Array Constructor & MidiMessage Constructor" {
        val size = 7
        val array = ByteArray(size) { it.toByte() }
        val midiMessage = MidiMessage(array)
        midiMessage.data.size shouldBe size
        midiMessage.data shouldBe array
        midiMessage.data shouldNotBeSameInstanceAs array
        val newMidiMessage = MidiMessage(midiMessage)
        midiMessage.data.size shouldBe newMidiMessage.data.size
        midiMessage.data shouldBe newMidiMessage.data
        midiMessage.data shouldNotBeSameInstanceAs newMidiMessage.data
        midiMessage shouldBe midiMessage
    }

    "Operator functions Set & Get" {
        val size = 9
        val value = 100
        val midiMessage = MidiMessage(size)
        midiMessage[0] = value
        midiMessage[8] = value
        shouldThrowUnit<IndexOutOfBoundsException> {
            midiMessage[9] = value
        }
        midiMessage[0].toInt() shouldBe value
        midiMessage[8].toInt() shouldBe value
        shouldThrow<IndexOutOfBoundsException> {
            midiMessage[9]
        }
    }

    "Size" {
        var size = 9
        val midiMessage = MidiMessage(size)
        midiMessage.size shouldBe size
        size = 100
        midiMessage.size = size
        midiMessage.size shouldBe size
        midiMessage.data.size shouldBe size
    }

    "Set Data" {
        val default = ByteArray(MidiMessage.DEFAULT_DATA_SIZE)
        val midiMessage = MidiMessage()
        val data = byteArrayOf(0, 1, 2, 3, 4)
        midiMessage.data shouldBe default
        midiMessage.setData(data)
        midiMessage.data shouldBe data
        midiMessage.data shouldNotBeSameInstanceAs data
        midiMessage.setData(default, 3)
        default.copyInto(data, endIndex = 3)
        midiMessage.data shouldBe data
    }


    "Set Data From MidiMessage" {
        val data = byteArrayOf(0, 1, 2, 3, 4)
        val midiMessage = MidiMessage(data)
        val copy = MidiMessage()
        copy.data shouldNotBe data
        copy.setDataFrom(midiMessage)
        copy.data shouldBe data
        copy.data shouldBe midiMessage.data
        copy.data shouldNotBeSameInstanceAs data
        copy.data shouldNotBeSameInstanceAs midiMessage.data
    }


    "Modify data" {
        val value = 69
        val midiMessage = MidiMessage(2)
        midiMessage.data[0] = value.toByte()
        midiMessage[0].toInt() shouldBe value
        midiMessage[1].toInt() shouldBe 0
    }


    "Get Data Copies" {
        val data = byteArrayOf(0, 1, 2, 3, 4)
        val midiMessage = MidiMessage(data)
        midiMessage.data shouldBe data
        midiMessage.data shouldNotBeSameInstanceAs data
        midiMessage.data shouldBe midiMessage.dataCopy
        midiMessage.data shouldNotBeSameInstanceAs midiMessage.dataCopy

        var copyBuffer = ByteArray(0)
        shouldThrow<IllegalArgumentException> {
            midiMessage.getDataCopy(copyBuffer)
        }
        copyBuffer = ByteArray(data.size * 2)
        midiMessage.getDataCopy(copyBuffer)
        midiMessage.data.forEachIndexed { index, it -> copyBuffer[index] shouldBe it }
        copyBuffer.size shouldNotBe data.size
        copyBuffer[data.lastIndex + 1] shouldBe 0
    }

})