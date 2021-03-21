package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.matchers.shouldBe

@Order(1)
internal class RtMidiConfigTest : StringSpec({

    defaultTestConfig = TestCaseConfig(enabled = !RtMidi.Config.loaded)

    "Config" {
        RtMidi.Config.loaded shouldBe false
        RtMidi.Config.disallowJACK(true)
        RtMidi.Config.useBundledLibraries(true)
        RtMidi.Config.load()

        shouldNotThrowAny { RtMidiLibrary.instance }

        RtMidi.compiledApis().contains(RtMidiApi.UNIX_JACK) shouldBe false
    }

})