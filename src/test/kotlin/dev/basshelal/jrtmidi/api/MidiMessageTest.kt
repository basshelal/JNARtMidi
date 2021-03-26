@file:Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")

package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.allShouldThrow
import dev.basshelal.jrtmidi.defaultBeforeAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.Arrays
import kotlin.random.Random

private fun randomBytes(size: Int) = Random.nextBytes(size)

/** Tests [MidiMessage] */
internal class MidiMessageTest : StringSpec({
    beforeSpec { defaultBeforeAll() }

    afterSpec { }

    "Empty Constructor" {
        MidiMessage().apply {
            data.size shouldBe MidiMessage.DEFAULT_DATA_SIZE
            this.size shouldBe MidiMessage.DEFAULT_DATA_SIZE
            this shouldHaveSize MidiMessage.DEFAULT_DATA_SIZE
            data.forEach { it shouldBe 0 }
            this.forEach { it shouldBe 0 }
        }
    }

    "Size Constructor" {
        val size = 9
        MidiMessage(size).apply {
            data.size shouldBe size
            this.size shouldBe size
            this shouldHaveSize size
            data.forEach { it shouldBe 0 }
            this.forEach { it shouldBe 0 }
        }
    }

    "Array Constructor" {
        val size = 9
        val array = randomBytes(size)
        MidiMessage(array).apply {
            data.size shouldBe size
            this.size shouldBe size
            this shouldHaveSize size
            data shouldBe array
            data shouldNotBeSameInstanceAs array
        }
    }

    "MidiMessage Constructor" {
        val midiMessage = MidiMessage(randomBytes(9))
        val newMessage = MidiMessage(midiMessage)
        newMessage.data.size shouldBe midiMessage.data.size
        newMessage.size shouldBe midiMessage.size
        newMessage.data shouldBe midiMessage.data
        newMessage.data shouldNotBeSameInstanceAs midiMessage.data
        newMessage shouldBe midiMessage
        newMessage shouldNotBeSameInstanceAs midiMessage
    }

    "Operator functions Set & Get" {
        val size = 9
        val value = 100.toByte()
        val midiMessage = MidiMessage(size)
        midiMessage[0] = value
        midiMessage[8] = value
        midiMessage[0] shouldBe value
        midiMessage[8] shouldBe value
        allShouldThrow<IndexOutOfBoundsException>(
                listOf({ midiMessage[9] = value }, { midiMessage[9] })
        )
    }

    "Increase Size" {
        var size = 9
        MidiMessage(size).apply {
            this.size shouldBe size
            data.size shouldBe size
            dataCopy.size shouldBe size
            size = 100
            this.size = size
            size shouldBe size
            data.size shouldBe size
            dataCopy.size shouldBe size
        }
    }

    "Decrease Size" {
        var size = 100
        MidiMessage(size).apply {
            this.size shouldBe size
            data.size shouldBe size
            dataCopy.size shouldBe size
            size = 10
            this.size = size
            this.size shouldBe size
            data.size shouldNotBe size
            dataCopy.size shouldBe size
        }
    }

    "Set Data" {
        val default = ByteArray(MidiMessage.DEFAULT_DATA_SIZE)
        val data = randomBytes(9)
        MidiMessage().apply {
            this.data shouldBe default
            setData(data)
            this.data shouldBe data
            this.data shouldNotBeSameInstanceAs data
            setData(default, 3)
            this.data shouldBe default.copyInto(data, endIndex = 3)
        }
    }

    "Set Data From MidiMessage" {
        val data = randomBytes(9)
        val midiMessage = MidiMessage(data)
        MidiMessage().apply {
            this.data shouldNotBe data
            this.setData(midiMessage)
            this.data shouldBe data
            this.data shouldBe midiMessage.data
            this.data shouldNotBeSameInstanceAs data
            this.data shouldNotBeSameInstanceAs midiMessage.data
        }
    }

    "Get Data Copies" {
        val data = randomBytes(9)
        val midiMessage = MidiMessage(data)
        midiMessage.data shouldBe data
        midiMessage.data shouldNotBeSameInstanceAs data
        midiMessage.data shouldBe midiMessage.dataCopy
        midiMessage.data shouldNotBeSameInstanceAs midiMessage.dataCopy

        shouldThrow<IllegalArgumentException> { midiMessage.getDataCopy(ByteArray(0)) }
        val copyBuffer = ByteArray(midiMessage.size * 2)
        midiMessage.getDataCopy(copyBuffer)
        Arrays.equals(midiMessage.data, 0, midiMessage.size, copyBuffer, 0, midiMessage.size) shouldBe true
        copyBuffer.size shouldNotBe midiMessage.size
        copyBuffer[data.lastIndex + 1] shouldBe 0
    }

})