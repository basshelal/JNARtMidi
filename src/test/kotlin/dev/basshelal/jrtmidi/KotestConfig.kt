package dev.basshelal.jrtmidi

import dev.basshelal.jrtmidi.api.RtMidiConfigTest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder

/** Global config for Kotest */
class KotestConfig : AbstractProjectConfig() {
    /** Specifies order of test execution, we need [RtMidiConfigTest] to run first */
    override val specExecutionOrder = SpecExecutionOrder.Annotated
}