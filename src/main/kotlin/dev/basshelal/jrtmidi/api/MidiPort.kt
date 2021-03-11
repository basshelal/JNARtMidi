@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.jrtmidi.api

import dev.basshelal.jrtmidi.api.MidiPort.Info.Type
import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import dev.basshelal.jrtmidi.lib.RtMidiPtr
import jnr.ffi.Platform
import java.util.Objects

/**
 * Represents a MIDI port, which can receive messages or send messages.
 * A MIDI port that can receive messages is readable by the programmer and thus is a [ReadableMidiPort].
 * A MIDI port that can send messages is writable by the programmer and thus is a [WritableMidiPort].
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
     * Closes and destroys this port (if it is not already destroyed) such that it can and will no longer be used.
     * Attempting to use the port (except for querying state such as [isOpen], [isDestroyed] etc) after this will throw
     * an [RtMidiPortException] except when calling [destroy], which is intentionally safe to call even if the port
     * is already destroyed, in which case nothing will happen
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public abstract fun destroy()

    /**
     * Opens this [MidiPort] ready to send and receive messages, if this [MidiPort] is already open nothing will happen
     * @param portName the name that this port will use
     * @throws RtMidiPortException if this port cannot be opened because the system MIDI port represented by [info]
     * could not be found or if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public fun open(portName: String) {
        checkIsDestroyed()
        if (!isOpen) {
            resetInfoIndex()
            RtMidiLibrary.instance.rtmidi_open_port(ptr, info.index, portName)
            checkErrors()
            isOpen = true
            isVirtual = false
        }
    }

    /**
     * Similar to [open] except using a "virtual" port, which may not be supported on all platforms,
     * see [RtMidi.supportsVirtualPorts]
     * @param portName the name that this port will use
     * @throws RtMidiPortException if this platform does not support virtual ports, see [RtMidi.supportsVirtualPorts]
     * or if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    public fun openVirtual(portName: String) {
        checkIsDestroyed()
        if (!RtMidi.supportsVirtualPorts())
            throw RtMidiPortException("Platform ${Platform.getNativePlatform().name} does not support virtual ports")
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
    protected fun checkErrors() = if (!ptr.ok.get()) throw RtMidiNativeException(ptr) else Unit

    /**
     * Fixes the index of this [MidiPort]'s [info]
     * RtMidi [open]s ports by index, ie [Info.index], but the index in this [info] may have changed since this
     * [MidiPort] was created, so we need to reset the index by finding the [Info] with the same [Info.name] and
     * [Info.type] and get its current index, that way the correct port will be opened.
     * If the [Info] cannot be found then we cannot proceed and the port cannot be opened.
     */
    protected fun resetInfoIndex() {
        val found = (RtMidi.writableMidiPorts() + RtMidi.readableMidiPorts()).find {
            it.type == this.info.type && it.name == this.info.name
        } ?: throw RtMidiPortException("Cannot open port, could not find port with info:\n$info")
        this.info.index = found.index
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
     * Creating and opening a new [WritableMidiPort] means that it can now be found as a readable port in
     * [RtMidi.readableMidiPorts] and vice versa.
     * @param name the name of the port
     * @param index the index of the port, this can change throughout an port's lifecycle because it simply
     * represents its order in the system's list of MIDI ports, RtMidi uses this internally to identify ports
     * @param type the [Type] of the port, either [Type.READABLE] or [Type.WRITABLE],
     * [Type.UNKNOWN] indicates some error or unknown but should never realistically be seen
     */
    public data class Info(val name: String, var index: Int, val type: Type) {

        override fun toString(): String = "name = '$name', index = $index, type = $type"

        enum class Type {
            READABLE, WRITABLE, UNKNOWN
        }
    }
}