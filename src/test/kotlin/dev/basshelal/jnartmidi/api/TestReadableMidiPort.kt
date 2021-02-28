package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.defaultBeforeAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

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

    // TODO: 28/02/2021 Create a writable port then a readable port to read it,
    //  then destroy the writable port and see what happens with the callback, with the port etc
}