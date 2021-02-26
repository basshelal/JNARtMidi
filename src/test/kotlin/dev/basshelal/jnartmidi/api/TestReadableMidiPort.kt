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
}