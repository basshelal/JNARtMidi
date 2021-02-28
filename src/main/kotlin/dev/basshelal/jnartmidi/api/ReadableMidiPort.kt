@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.jnartmidi.api

import com.sun.jna.Pointer
import dev.basshelal.jnartmidi.lib.RtMidiInPtr
import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.NativeSize
import dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiCCallback

public class ReadableMidiPort : MidiPort<RtMidiInPtr> {

    /** Initialized in [createPtr] */
    protected override lateinit var ptr: RtMidiInPtr

    // TODO: 23/02/2021 Test idea! Make a Port and set its callback then make it unreachable and thus ready for GC,
    //  then force a GC and see what happens.

    /** Need to hold a reference to this because JNA, otherwise if it gets GCed will cause unexpected behavior */
    private var cCallback: RtMidiCCallback? = null

    /** Invoked from inside [cCallback] */
    private var midiMessageCallback: MidiMessageCallback? = null

    public override lateinit var api: RtMidiApi
        /** Set once only in [createPtr] */
        protected set

    public constructor(portInfo: Info) : super(portInfo) {
        require(portInfo.type == Info.Type.READABLE) {
            "Type of portInfo must be READABLE to create a ReadableMidiPort, found portInfo:\n$portInfo"
        }
        this.createPtr()
    }

    @JvmOverloads
    public constructor(portInfo: Info, clientName: String, api: RtMidiApi = RtMidiApi.UNSPECIFIED)
            : super(portInfo, clientName, api) {
        require(portInfo.type == Info.Type.READABLE) {
            "Type of portInfo must be READABLE to create a ReadableMidiPort, found portInfo: $portInfo"
        }
        this.createPtr()
    }

    public override fun destroy() {
        if (!this.isDestroyed) {
            close()
            removeCallback()
            RtMidiLibrary.instance.rtmidi_in_free(ptr)
            checkErrors()
            isDestroyed = true
        }
    }

    /**
     * Set this [ReadableMidiPort]'s callback which will be triggered when a new [MidiMessage] is sent to
     * this port. Ensure that the message you expect is not being ignored by calling [ignoreTypes] before
     * setting the callback.
     *
     * @param callback the [MidiMessageCallback] which will be triggered when a new [MidiMessage] is sent to this port
     * @throws RtMidiPortException if a callback already exists for this port which must be removed using [removeCallback]
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public fun setCallback(callback: MidiMessageCallback) {
        checkIsDestroyed()
        if (midiMessageCallback != null)
            throw RtMidiPortException("Cannot set callback there is an existing callback registered, call removeCallback() to remove.")
        midiMessageCallback = callback
        midiMessage = MidiMessage()
        cCallback = object : RtMidiCCallback {
            override fun invoke(timeStamp: Double, message: Pointer?, messageSize: NativeSize?, userData: Pointer?) {
                // RealTimeCritical
                if (message == null || messageSize == null) return  // prevent NPE or worse segfault
                val size = messageSize.toInt()
                midiMessage?.also { midiMessage ->
                    // rare but necessary memalloc in RealTimeCritical code
                    if (midiMessage.size < size) midiMessage.size = size
                    for (i in 0 until size) midiMessage[i] = message.getByte(i.toLong())
                    midiMessageCallback?.onMessage(midiMessage, timeStamp)
                }
            } // end RealTimeCritical
        }.also { cCallback ->
            RtMidiLibrary.instance.rtmidi_in_set_callback(ptr, cCallback, null)
            checkErrors()
        }
    }

    /**
     * Removes this [ReadableMidiPort]'s [MidiMessageCallback] if it exists, otherwise does nothing.
     * You must call this before calling [setCallback] if one already exists.
     * @throws RtMidiPortException if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public fun removeCallback() {
        checkIsDestroyed()
        if (midiMessageCallback == null) return
        RtMidiLibrary.instance.rtmidi_in_cancel_callback(ptr)
        checkErrors()
        cCallback = null
        midiMessageCallback = null
        midiMessage = null
    }

    /**
     * Instructs RtMidi to ignore messages of a given type such that this [ReadableMidiPort]'s callback
     * will not be triggered for such messages. `true` means ignore such messages and `false` means do *not* ignore them
     * @throws RtMidiPortException if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public fun ignoreTypes(midiSysex: Boolean, midiTime: Boolean, midiSense: Boolean) {
        checkIsDestroyed()
        RtMidiLibrary.instance.rtmidi_in_ignore_types(ptr, midiSysex, midiTime, midiSense)
        checkErrors()
    }

    protected override fun createPtr() {
        ptr = chosenApi?.let { api ->
            clientName?.let { clientName ->
                RtMidiLibrary.instance.rtmidi_in_create(api.number, clientName, DEFAULT_QUEUE_SIZE_LIMIT)
            }
        } ?: RtMidiLibrary.instance.rtmidi_in_create_default()
        checkErrors()
        isDestroyed = false
        val apiInt = RtMidiLibrary.instance.rtmidi_in_get_current_api(ptr)
        checkErrors()
        api = RtMidiApi.fromInt(apiInt)
    }

    internal companion object {
        /** This is the default queue size RtMidi uses internally if no size is passed in */
        internal const val DEFAULT_QUEUE_SIZE_LIMIT = 100
    }
}