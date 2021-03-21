@file:Suppress("RedundantVisibilityModifier", "ConvertSecondaryConstructorToPrimary")

package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.lib.RtMidiInPtr
import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import dev.basshelal.jrtmidi.lib.RtMidiLibrary.RtMidiCCallback
import jnr.ffi.Pointer

/**
 * A [MidiPort] that can be read from, meaning you can have a callback registered to listen when new MIDI events are
 * received by using [setCallback].
 * This can be thought of as a MIDI in device, ie it is going in to the application,
 * but the choice of "readable" is more intuitive to the programmer as it means the programmer can "read" from the [MidiPort].
 *
 * After creating and [open]ing a [ReadableMidiPort] it will appear in the system's writable MIDI ports in
 * [RtMidi.writableMidiPorts].
 *
 * Read [MidiPort]'s documentation for further details.
 *
 * **Be careful of memory leaks!**
 *
 * If you create a [ReadableMidiPort] and [open] it and call [setCallback] then lose access to that variable
 * containing the [ReadableMidiPort] and the port is garbage collected, the [MidiMessageCallback] you provided in [setCallback]
 * **will still run** and you will have no way to close or destroy that port without killing your JVM process.
 * Ensure that any open ports are still accessible to be [destroy]ed and make sure to call [destroy] when you are
 * done with a port to avoid memory leaks, meaning open ports that you no longer have access to and thus cannot
 * close and destroy without killing your JVM process.
 *
 * @author Bassam Helal
 */
public class ReadableMidiPort : MidiPort<RtMidiInPtr> {

    /** Initialized in [createPtr] */
    protected override lateinit var ptr: RtMidiInPtr

    /** Need to hold a reference to this because JNA, otherwise if it gets GCed will cause unexpected behavior */
    private var cCallback: RtMidiCCallback? = null

    /** Invoked from inside [cCallback] */
    private var midiMessageCallback: MidiMessageCallback? = null

    public override lateinit var api: RtMidiApi
        /** Set once only in [createPtr] */
        protected set

    /**
     * Create a [ReadableMidiPort] from the passed in [portInfo].
     * @param portInfo the [MidiPort.Info] that this [MidiPort] represents
     * @param clientName the name which is used by RtMidi to group similar ports
     * (like those belonging to the same application).
     * @param api the [RtMidiApi] that this port will use, defaults to [RtMidiApi.UNSPECIFIED],
     * ie let RtMidi choose the first working API it finds which you can query using [MidiPort.api]
     * @throws IllegalArgumentException if the passed in [portInfo] was not of type READABLE
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmOverloads
    public constructor(portInfo: Info? = null, clientName: String? = null, api: RtMidiApi = RtMidiApi.UNSPECIFIED)
            : super(portInfo, clientName, api) {
        if (portInfo != null) require(portInfo.type == Info.Type.READABLE) {
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
            override fun invoke(timeStamp: Double, message: Pointer?, messageSize: Long?, userData: Pointer?) {
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
        ptr = clientName?.let { clientName ->
            RtMidiLibrary.instance.rtmidi_in_create(chosenApi.number, clientName, DEFAULT_QUEUE_SIZE_LIMIT)
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