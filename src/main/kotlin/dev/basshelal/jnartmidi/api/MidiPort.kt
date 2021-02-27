@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.jnartmidi.api

import com.sun.jna.Platform
import dev.basshelal.jnartmidi.api.MidiPort.Info.Type
import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiPtr
import java.util.Objects

/**
 * TODO doc!
 *
 * You should always *try* to call [destroy] once you are no longer using this [MidiPort],
 * if unsure about needing the port later, call [close] to at least close the connection
 * (which you can later re[open]) so that no [MidiMessage]s are sent or received unnecessarily or unintentionally.
 *
 * @author Bassam Helal
 */
abstract class MidiPort<P : RtMidiPtr>
protected constructor(portInfo: Info) {

    /** The [RtMidiPtr] we will use to interact with [RtMidiLibrary] */
    protected abstract var ptr: P

    /** The [RtMidiApi] that was chosen in the constructor, used in [createPtr] to determine [ptr] creation method */
    protected var chosenApi: RtMidiApi? = null

    // TODO: 27/02/2021 RtMidi uses indexes to open ports, what if the system changed so much that the index here
    //  is no longer valid?? We can always do a rescan where necessary (open) and maybe even reassign but be careful
    //  this needs proper testing, make a lot of ports, then close and destroy all but the last one, that last one
    //  should have a high info number, try opening the port and see what happens

    /** The [Info] set in this [MidiPort]'s constructor holding the information of the actual system MIDI port */
    public val info: Info = portInfo

    /** The [RtMidiApi] that RtMidi is using for this port */
    public abstract var api: RtMidiApi
        protected set

    /**
     * The last [MidiMessage] sent (if [WritableMidiPort]) or received(if [ReadableMidiPort]) in this [MidiPort]
     * `null` if none exists
     */
    public var midiMessage: MidiMessage? = null
        protected set

    /** `true` if this [MidiPort] is open */
    public var isOpen = false
        protected set

    /** `true` if this [MidiPort] is open *and* was opened using [openVirtual] */
    public var isVirtual = false
        protected set

    /** `true` if this [MidiPort] is destroyed, useful to ensure no [RtMidiPortException]s are thrown after [destroy] */
    public var isDestroyed = false
        protected set

    /** The client name set in this [MidiPort]'s constructor, `null` if none was provided */
    public var clientName: String? = null
        protected set

    @JvmOverloads
    protected constructor(portInfo: Info, clientName: String, api: RtMidiApi = RtMidiApi.UNSPECIFIED) : this(portInfo) {
        this.chosenApi = api
        this.clientName = clientName
    }

    /**
     * Closes [close] and destroys this port (if it is not already destroyed) such that it can and will no longer be used,
     * attempting to use the port after this will throw an [RtMidiPortException] except when calling [destroy], which
     * is intentionally safe to call even if the port is already destroyed, in which case nothing will happen
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public abstract fun destroy()

    /**
     * Opens this [MidiPort] ready to send and receive messages, if this [MidiPort] is already open nothing will happen
     * @param portName an optional port name to be used, defaults to this [info] name
     * @throws RtMidiPortException if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmOverloads
    public fun open(portName: String = info.name) { // TODO: 27/02/2021 Change params, no default??
        checkIsDestroyed()
        if (!isOpen) {
            RtMidiLibrary.instance.rtmidi_open_port(ptr, info.index, portName)
            checkErrors()
            isOpen = true
            isVirtual = false
        }
    }

    /**
     * TODO doc!
     *
     * @param portName an optional port name to be used, defaults to this [info] name
     * @throws RtMidiPortException if this platform does not support virtual ports, see [RtMidi.supportsVirtualPorts]
     * or if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public fun openVirtual(portName: String = info.name) { // TODO: 27/02/2021 Change params, no default??
        checkIsDestroyed()
        if (!RtMidi.supportsVirtualPorts())
            throw RtMidiPortException("Platform ${Platform.RESOURCE_PREFIX} does not support virtual ports")
        if (!isOpen) {
            RtMidiLibrary.instance.rtmidi_open_virtual_port(ptr, portName)
            checkErrors()
            isOpen = true
            isVirtual = true
        }
    }

    /**
     * Closes this [MidiPort] only if it [isOpen], else does nothing
     * @throws RtMidiPortException if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public fun close() {
        checkIsDestroyed()
        if (isOpen) {
            RtMidiLibrary.instance.rtmidi_close_port(ptr)
            checkErrors()
            isOpen = false
            isVirtual = false
            destroy()
            createPtr()
        }
    }

    /**
     * Create [ptr] to be used in this port, equivalent to restarting everything so use with caution
     * Calls either the default create function like [RtMidiLibrary.rtmidi_in_create_default] or
     * the custom function [RtMidiLibrary.rtmidi_in_create] depending on how this [MidiPort] was constructed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    protected abstract fun createPtr()

    /** Call this early before any call to [RtMidiLibrary.instance] to avoid segfaults */
    protected fun checkIsDestroyed() =
            if (isDestroyed) throw RtMidiPortException("Cannot proceed, the MidiPort:\n$this\n has already been destroyed") else Unit

    /** Call this after any call to [RtMidiLibrary.instance] */
    protected fun checkErrors() = if (!ptr.ok) throw RtMidiNativeException(ptr) else Unit

    protected fun rescanInfo() {
        val found = (RtMidi.writableMidiPorts() + RtMidi.readableMidiPorts()).find {
            it.name == this.info.name && it.type == this.info.type
        }
        check(found != null) { "Could not find info:$info" }
        this.info.index = info.index
    }

    override fun hashCode(): Int = Objects.hash(ptr, info)

    override fun equals(other: Any?): Boolean =
            other is MidiPort<*> && this.javaClass == other.javaClass && ptr == other.ptr && info == other.info

    override fun toString(): String {
        return """${this.javaClass.simpleName} {
                |$info
                |api = $api
                |clientName = '$clientName'
                |isOpen = $isOpen
                |isVirtual = $isVirtual
                |isDestroyed = $isDestroyed
                |}""".trimMargin()
    }

    /**
     * Contains the information of a system MIDI port which is used to create new [MidiPort]s
     * You can query the system's MIDI ports using [RtMidi.readableMidiPorts] and [RtMidi.writableMidiPorts].
     * @param name the name of the port
     * @param index the index of the port, this can change throughout an port's lifecycle because it simply
     * represents its order in the system's list of MIDI ports, RtMidi uses this internally to identify ports
     * @param type the [Type] of the port, either [Type.READABLE] or [Type.WRITABLE],
     * [Type.UNKNOWN] indicates some error or unknown but should never realistically be seen
     */
    public data class Info(val name: String, var index: Int, val type: Type) {

        override fun toString(): String = "name = '$name'\nindex = $index\ntype = $type"

        enum class Type {
            READABLE, WRITABLE, UNKNOWN
        }
    }
}