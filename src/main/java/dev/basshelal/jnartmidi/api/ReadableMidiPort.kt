package dev.basshelal.jnartmidi.api

import com.sun.jna.Pointer
import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.NativeSize
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiCCallback
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr

// TODO: 23/02/2021 More specific exceptions!
class ReadableMidiPort : MidiPort<RtMidiInPtr> {

    // TODO: 23/02/2021 Test idea! Make a Port and set its callback then make it unreachable and thus ready for GC,
    //  then force a GC and see what happens.
    private var cCallback: RtMidiCCallback? = null
    private var midiMessageCallback: MidiMessageCallback? = null

    /**
     * @return the last received [MidiMessage] from the callback or `null` if none exists
     */
    var midiMessage: MidiMessage? = null
        private set


    constructor(portInfo: Info) : super(portInfo) {
        // TODO: 23/02/2021 Require type of portInfo to be READABLE?? Same for WritableMidiPort??
        createPtr()
    }

    constructor(portInfo: Info, api: RtMidiApi, clientName: String) : super(portInfo, api, clientName) {
        createPtr()
    }

    /**
     * @inheritDoc
     */
    override fun destroy() {
        // TODO: 23/02/2021 do nothing if is already destroyed
        checkIsDestroyed()
        removeCallback()
        preventSegfault()
        RtMidiLibrary.instance.rtmidi_in_free(ptr)
        checkErrors()
        isDestroyed = true
    }

    /**
     * @inheritDoc
     */
    override fun getApi(): RtMidiApi {
        checkIsDestroyed()
        preventSegfault()
        val result = RtMidiLibrary.instance.rtmidi_in_get_current_api(ptr!!)
        checkErrors()
        return RtMidiApi.fromInt(result)
    }

    /**
     * @inheritDoc
     */
    override fun createPtr() {
        ptr = if (api != null && clientName != null)
            RtMidiLibrary.instance.rtmidi_in_create(api.number, clientName, DEFAULT_QUEUE_SIZE_LIMIT)
        else RtMidiLibrary.instance.rtmidi_in_create_default()
        checkErrors()
        isDestroyed = false
    }

    /**
     * Set this [ReadableMidiPort]'s callback which will be triggered when a new [MidiMessage] is sent to
     * this port. Ensure that the message you expect is not being ignored by calling [.ignoreTypes] before
     * setting the callback.
     *
     * @param callback the callback which will be triggered when a new [MidiMessage] is sent to this port
     * @throws NullPointerException if `callback` is `null`
     * @throws RtMidiPortException  if a callback already exists for this port which must be removed using [.removeCallback]
     */
    fun setCallback(callback: MidiMessageCallback?) {
        checkIsDestroyed()
        if (midiMessageCallback != null) throw RtMidiPortException("Cannot set callback there is an existing callback registered, " +
                "call removeCallback() to remove.")
        midiMessageCallback = callback
        midiMessage = MidiMessage()
        cCallback = object : RtMidiCCallback {
            override fun invoke(timeStamp: Double, message: Pointer?, messageSize: NativeSize?, userData: Pointer?) {
                if (message == null || messageSize == null) return  // prevent NPE or worse segfault
                val size = messageSize.toInt()
                midiMessage?.also { midiMessage ->
                    // memalloc in realtime code! Dangerous but necessary and extremely rare
                    if (midiMessage.size < size) midiMessage.size = size
                    for (i in 0 until size) midiMessage[i] = message.getByte(i.toLong()).toInt()
                    midiMessageCallback?.onMessage(midiMessage, timeStamp)
                }
            }
        }
        cCallback?.also { cCallback ->
            preventSegfault()
            RtMidiLibrary.instance.rtmidi_in_set_callback(ptr, cCallback, null)
            checkErrors()
        }
    }

    /**
     * Removes this [ReadableMidiPort]'s [MidiMessageCallback] if it exists, otherwise does nothing.
     * You must call this before calling [.setCallback] if one already exists.
     */
    fun removeCallback() {
        checkIsDestroyed()
        if (midiMessageCallback == null) return
        preventSegfault()
        RtMidiLibrary.instance.rtmidi_in_cancel_callback(ptr)
        checkErrors()
        cCallback = null
        midiMessageCallback = null
        midiMessage = null
    }

    fun ignoreTypes(midiSysex: Boolean, midiTime: Boolean, midiSense: Boolean) {
        checkIsDestroyed()
        preventSegfault()
        RtMidiLibrary.instance.rtmidi_in_ignore_types(ptr, midiSysex, midiTime, midiSense)
        checkErrors()
    }

    companion object {
        /**
         * This is the default queue size RtMidi uses if no size is passed in
         */
        private const val DEFAULT_QUEUE_SIZE_LIMIT = 100
    }
}