package dev.basshelal.jrtmidi.api

/**
 * Used in [ReadableMidiPort] to listen to [MidiMessage]s that the [ReadableMidiPort] receives.
 *
 * @see ReadableMidiPort.setCallback
 * @author Bassam Helal
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
     * Also note that this function could be called *much more* than you expect as many MIDI controllers and sequencers
     * send time information messages and other meta messages very often.
     * RtMidi allows you to ignore these using [ReadableMidiPort.ignoreTypes] in which case the callback will
     * not be triggered for these messages.
     * Nevertheless, your callback should be able to handle being called possibly hundreds of times a second as it is
     * possible for even common MIDI controls such as pitch bend to send hundreds of messages in a second with a
     * quick flick from a hand.
     *
     * @param message the [MidiMessage] that was received, this should ideally not be modified,
     * see [MidiMessage.data] and [MidiMessage.getDataCopy] for more.
     * @param deltaTime the time difference in seconds since the last received message, as reported by RtMidi
     */
    fun onMessage(message: MidiMessage, deltaTime: Double)

    companion object {
        /**
         * For cleaner callback creation with Kotlin instead of creating a new object, as so:
         *
         * ```kotlin
         * MidiMessageCallback{message: MidiMessage, deltaTime: Double ->
         *     // code...
         * }
         * ```
         */
        inline operator fun invoke(crossinline onMessage: (message: MidiMessage, deltaTime: Double) -> Unit) = object : MidiMessageCallback {
            override fun onMessage(message: MidiMessage, deltaTime: Double) = onMessage(message, deltaTime)
        }

        /**
         * For cleaner callback creation with Kotlin instead of creating a new object, as so:
         *
         * ```kotlin
         * MidiMessageCallback{message: MidiMessage ->
         *     // code...
         * }
         * ```
         */
        inline operator fun invoke(crossinline onMessage: (message: MidiMessage) -> Unit) = object : MidiMessageCallback {
            override fun onMessage(message: MidiMessage, deltaTime: Double) = onMessage(message)
        }
    }
}