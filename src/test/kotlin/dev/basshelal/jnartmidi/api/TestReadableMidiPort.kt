package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.defaultBeforeAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/** Tests [ReadableMidiPort] including its supertype [MidiPort] */
internal class TestReadableMidiPort {

    companion object {
        @BeforeAll
        @JvmStatic
        fun `Before All`() = defaultBeforeAll()

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }

    @Test
    fun `Info Constructor`() {
    }

    @Test
    fun `Full Constructor`() {
    }

    @Test
    fun `No API Constructor`() {
    }

    @Test
    fun Open() {
    }

    @Test
    fun `Open Virtual`() {
    }

    @Test
    fun Close() {
    }

    @Test
    fun Destroy() {
    }

    @Test
    fun `Set Callback`() {
    }

    @Test
    fun `Set Callback On Invalid Port`() {
        // TODO: 28/02/2021 Create a writable port then a readable port to read it,
        //  then destroy the writable port and see what happens with the callback, with the port etc
    }

    @Test
    fun `Ignore Types`() {
    }

    @Test
    fun `Open Port After Info Index Change`() {
    }

    @Test
    fun `GCed Callback`() {
        // TODO: 23/02/2021 Make a Port and set its callback then make it unreachable and thus ready for GC,
        //  then force a GC and see what happens.
    }

}