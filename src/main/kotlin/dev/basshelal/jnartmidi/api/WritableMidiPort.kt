package dev.basshelal.jnartmidi.api

import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiOutPtr

class WritableMidiPort : MidiPort<RtMidiOutPtr> {

    override lateinit var ptr: RtMidiOutPtr

    private var messageBuffer: ByteArray = ByteArray(0)

    constructor(info: Info) : super(info) {
        this.createPtr()
    }

    constructor(info: Info, api: RtMidiApi, name: String) : super(info, api, name) {
        this.createPtr()
    }

    override fun destroy() {
        checkIsDestroyed()
        RtMidiLibrary.instance.rtmidi_out_free(ptr)
        checkErrors()
        isDestroyed = true
    }

    override fun getApi(): RtMidiApi {
        checkIsDestroyed()
        val result = RtMidiLibrary.instance.rtmidi_out_get_current_api(ptr)
        checkErrors()
        return RtMidiApi.fromInt(result)
    }

    override fun createPtr() {
        ptr = chosenApi?.let { api ->
            chosenClientName?.let { clientName ->
                RtMidiLibrary.instance.rtmidi_out_create(api.number, clientName)
            }
        } ?: RtMidiLibrary.instance.rtmidi_out_create_default()
        checkErrors()
        isDestroyed = false
    }

    /**
     * Sends the passed in [midiMessage] to this port, the data of the message will not be modified
     * @throws RtMidiException if an error occurred in RtMidi's native code
     */
    fun sendMessage(midiMessage: MidiMessage) {
        checkIsDestroyed()
        this.midiMessage = MidiMessage(midiMessage)
        if (messageBuffer.size < midiMessage.size) messageBuffer = ByteArray(midiMessage.size)
        midiMessage.data.forEachIndexed { index, it -> messageBuffer[index] = it.toByte() }
        RtMidiLibrary.instance.rtmidi_out_send_message(ptr, messageBuffer, messageBuffer.size)
        checkErrors()
    }
}