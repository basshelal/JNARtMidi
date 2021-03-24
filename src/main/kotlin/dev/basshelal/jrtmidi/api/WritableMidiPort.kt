@file:Suppress("RedundantVisibilityModifier", "ConvertSecondaryConstructorToPrimary")

package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.lib.RtMidiOutPtr
import dev.basshelal.jrtmidi.lib.library

/**
 * A [MidiPort] that can be written to, meaning you can send a [MidiMessage] to via [WritableMidiPort.sendMessage].
 * This can be thought of as a MIDI out device, ie it is going out of the application,
 * but the choice of "writable" is more intuitive to the programmer as it means the programmer can "write" to the [MidiPort].
 *
 * After creating and [open]ing a [WritableMidiPort] it will appear in the system's readable MIDI ports in
 * [RtMidi.readableMidiPorts].
 *
 * Read [MidiPort]'s documentation for further details.
 *
 * @author Bassam Helal
 */
public class WritableMidiPort : MidiPort<RtMidiOutPtr> {

    /** Initialized in [createPtr] */
    protected override lateinit var ptr: RtMidiOutPtr

    public override lateinit var api: RtMidiApi
        /** Set once only in [createPtr] */
        protected set

    /**
     * Create a [WritableMidiPort] from the passed in [portInfo].
     * @param portInfo the [MidiPort.Info] that this [MidiPort] represents
     * @param clientName the name which is used by RtMidi to group similar ports
     * (like those belonging to the same application).
     * @param api the [RtMidiApi] that this port will use, defaults to [RtMidiApi.UNSPECIFIED],
     * ie let RtMidi choose the first working API it finds which you can then query using [MidiPort.api]
     * @throws IllegalArgumentException if the passed in [portInfo] was not of type WRITABLE
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmOverloads
    public constructor(portInfo: Info? = null, clientName: String? = null, api: RtMidiApi = RtMidiApi.UNSPECIFIED)
            : super(portInfo, clientName, api) {
        if (portInfo != null) require(portInfo.type == Info.Type.WRITABLE) {
            "Type of portInfo must be WRITABLE to create a WritableMidiPort, found portInfo: $portInfo"
        }
        this.createPtr()
    }

    public override fun destroy() {
        if (!this.isDestroyed) {
            close()
            library.rtmidi_out_free(ptr)
            checkErrors()
            isDestroyed = true
        }
    }

    /**
     * Sends the passed in [midiMessage] to this port, the data of the message will not be modified
     * This port must be open for the message to be sent, you can check this by calling [MidiPort.isOpen],
     * otherwise nothing will happen
     * @throws RtMidiPortException if this port has already been destroyed
     * @throws RtMidiException if an error occurred in RtMidi's native code
     */
    public fun sendMessage(midiMessage: MidiMessage) {
        // RealTimeCritical because of potential use in callback for a Midi-Through application
        checkIsDestroyed()
        if (isOpen) {
            library.rtmidi_out_send_message(ptr, midiMessage.data, midiMessage.size)
            checkErrors()
            this.midiMessage = midiMessage // assign only if message was successful in case caller caught exceptions
        }
    } // end RealTimeCritical

    protected override fun createPtr() {
        ptr = clientName?.let { clientName ->
            library.rtmidi_out_create(chosenApi.number, clientName)
        } ?: library.rtmidi_out_create_default()
        checkErrors()
        isDestroyed = false
        val apiInt = library.rtmidi_out_get_current_api(ptr)
        checkErrors()
        api = RtMidiApi.fromInt(apiInt)
    }
}