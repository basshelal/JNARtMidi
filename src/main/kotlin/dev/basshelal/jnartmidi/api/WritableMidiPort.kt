@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiOutPtr

/**
 * A [MidiPort] that can be written to, meaning you can send a [MidiMessage] to via [WritableMidiPort.sendMessage].
 * This can be thought of as a MIDI out device, ie it is going out of the application,
 * but the choice of "writable" is more intuitive to the programmer as it means the programmer can "write" to the [MidiPort].
 *
 * After creating and [open]ing a [WritableMidiPort] it will appear in the system's readable MIDI ports in
 * [RtMidi.readableMidiPorts].
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
     * Create a [WritableMidiPort] from the passed in [portInfo]
     * @param portInfo the [MidiPort.Info] that this [MidiPort] represents
     * @throws IllegalArgumentException if the passed in [portInfo] was not of type WRITABLE
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public constructor(portInfo: Info) : super(portInfo) {
        require(portInfo.type == Info.Type.WRITABLE) {
            "Type of portInfo must be WRITABLE to create a WritableMidiPort, found portInfo:\n$portInfo"
        }
        this.createPtr()
    }

    /**
     * Create a [WritableMidiPort] from the passed in [portInfo].
     * @param portInfo the [MidiPort.Info] that this [MidiPort] represents
     * @param clientName the name which is used by RtMidi to group similar ports
     * (like those belonging to the same application).
     * @param api the [RtMidiApi] that this port will use, defaults to [RtMidiApi.UNSPECIFIED],
     * ie let RtMidi choose the first working API it finds which you can query using [MidiPort.api]
     * @throws IllegalArgumentException if the passed in [portInfo] was not of type WRITABLE
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmOverloads
    public constructor(portInfo: Info, clientName: String, api: RtMidiApi = RtMidiApi.UNSPECIFIED)
            : super(portInfo, clientName, api) {
        require(portInfo.type == Info.Type.WRITABLE) {
            "Type of portInfo must be WRITABLE to create a WritableMidiPort, found portInfo: $portInfo"
        }
        this.createPtr()
    }

    public override fun destroy() {
        if (!this.isDestroyed) {
            close()
            RtMidiLibrary.instance.rtmidi_out_free(ptr)
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
            RtMidiLibrary.instance.rtmidi_out_send_message(ptr, midiMessage.data, midiMessage.size)
            checkErrors()
            this.midiMessage = midiMessage // assign only if message was successful in case caller caught exceptions
        }
    } // end RealTimeCritical

    protected override fun createPtr() {
        ptr = chosenApi?.let { api ->
            clientName?.let { clientName ->
                RtMidiLibrary.instance.rtmidi_out_create(api.number, clientName)
            }
        } ?: RtMidiLibrary.instance.rtmidi_out_create_default()
        checkErrors()
        isDestroyed = false
        val apiInt = RtMidiLibrary.instance.rtmidi_out_get_current_api(ptr)
        checkErrors()
        api = RtMidiApi.fromInt(apiInt)
    }
}