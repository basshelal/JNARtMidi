package dev.basshelal.jnartmidi.api

/**
 * Used in [ReadableMidiPort] to listen to [MidiMessage]s that the [ReadableMidiPort] receives.
 *
 * @author Bassam Helal
 * @see ReadableMidiPort.setCallback
 */
interface MidiMessageCallback {

    /**
     * **Real time critical!**
     *
     * It is advised against performing any blocking or long running operations within this function,
     * such as allocating memory or using locks or waiting etc.
     *
     * This is because the function must successfully return before being called again, meaning if a long operation
     * has not finished by the time the next [MidiMessage] is received then this function will not be called in
     * time leading to late messages and a bad user experience.
     *
     * Also not that this function could be called *much more* than you expect as many MIDI controllers and sequencers
     * send time information messages and other meta messages very often.
     * RtMidi allows you to ignore these using [ReadableMidiPort.ignoreTypes] in which case the callback will
     * not be triggered for these messages.
     *
     * @param message   the [MidiMessage] that was received, this should ideally not be modified,
     * see [MidiMessage.getData] for more.
     * @param deltaTime the time difference in seconds since the last received message, as reported by RtMidi
     */
    fun onMessage(message: MidiMessage, deltaTime: Double)

    companion object {
        /**
         * For cleaner callback creation with Kotlin
         */
        @JvmStatic
        inline operator fun invoke(crossinline onMessage: (message: MidiMessage, deltaTime: Double) -> Unit): MidiMessageCallback {
            return object : MidiMessageCallback {
                override fun onMessage(message: MidiMessage, deltaTime: Double) {
                    onMessage(message, deltaTime)
                }
            }
        }

        /**
         * For cleaner callback creation with Kotlin
         */
        @JvmStatic
        inline operator fun invoke(crossinline onMessage: (message: MidiMessage) -> Unit): MidiMessageCallback {
            return object : MidiMessageCallback {
                override fun onMessage(message: MidiMessage, deltaTime: Double) {
                    onMessage(message)
                }
            }
        }
    }
}