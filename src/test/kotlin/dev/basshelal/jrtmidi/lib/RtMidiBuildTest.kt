package dev.basshelal.jrtmidi.lib

import dev.basshelal.jrtmidi.api.RtMidi
import dev.basshelal.jrtmidi.mustBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

/** Tests [RtMidiBuild] */
internal class RtMidiBuildTest {

    companion object {
        lateinit var lib: RtMidiLibrary

        @BeforeAll
        @JvmStatic
        fun `Before All`() {
            RtMidi.useBundledLibraries()
            lib = RtMidiLibrary.instance
            ::lib.isInitialized mustBe true
        }

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }


}