@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiOutPtr

public class WritableMidiPort : MidiPort<RtMidiOutPtr> {

    /** Used in [sendMessage], wraps the passed in [MidiMessage] data */
    private var messageBuffer: ByteArray = ByteArray(0)

    /** Initialized in [createPtr] */
    protected override lateinit var ptr: RtMidiOutPtr


    public override lateinit var api: RtMidiApi
        protected set

    /**
     * Create a [WritableMidiPort] from the passed in [portInfo]
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public constructor(portInfo: Info) : super(portInfo) {
        this.createPtr()
    }

    /**
     * Create a [WritableMidiPort] from the passed in [portInfo].
     * @param clientName the name which is used by RtMidi to group similar ports
     * (like those belonging to the same application).
     * @param api the [RtMidiApi] that this port will use, defaults to [RtMidiApi.UNSPECIFIED],
     * ie let RtMidi choose the first working API it finds which you can query using [MidiPort.api]
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmOverloads
    public constructor(portInfo: Info, clientName: String, api: RtMidiApi = RtMidiApi.UNSPECIFIED)
            : super(portInfo, clientName, api) {
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
        // RealTimeCritical
        checkIsDestroyed()
        if (isOpen) {
            this.midiMessage = midiMessage
            // rare but necessary memalloc in RealTimeCritical code
            if (messageBuffer.size < midiMessage.size) messageBuffer = ByteArray(midiMessage.size)
            // put midiMessage in messageBuffer and send only midiMessage size because it is the source of truth
            for (i in 0 until midiMessage.size) messageBuffer[i] = midiMessage[i]
            RtMidiLibrary.instance.rtmidi_out_send_message(ptr, messageBuffer, midiMessage.size)
            checkErrors()
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