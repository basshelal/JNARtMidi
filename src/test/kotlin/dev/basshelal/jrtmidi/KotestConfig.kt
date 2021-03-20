package dev.basshelal.jrtmidi

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder

/** Global config for Kotest */
class KotestConfig : AbstractProjectConfig() {
    override val specExecutionOrder = SpecExecutionOrder.Annotated
}