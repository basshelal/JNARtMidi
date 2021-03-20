package dev.basshelal.jrtmidi.lib

import dev.basshelal.jrtmidi.api.RtMidi
import io.kotest.core.spec.style.StringSpec

/** Tests [RtMidiBuild] */
internal class RtMidiBuildTest : StringSpec({
    lateinit var lib: RtMidiLibrary

    beforeSpec {
        RtMidi.Config.useBundledLibraries(true).load()
        lib = RtMidiLibrary.instance
    }

    afterSpec { }

})