package dev.basshelal.jrtmidi.lib

import dev.basshelal.jrtmidi.api.RtMidi
import dev.basshelal.jrtmidi.log
import org.junit.jupiter.api.Test

class Tests {

    @Test
    fun tcs() {
        RtMidi.useBundledLibraries()

        RtMidi.compiledApis().log()
    }
}