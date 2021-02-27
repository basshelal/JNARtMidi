@file:Suppress("RedundantVisibilityModifier")

package dev.basshelal.jnartmidi.api

import com.sun.jna.Platform
import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiPtr
import java.util.Objects

abstract class MidiPort<P : RtMidiPtr>
protected constructor(portInfo: Info) {

    protected abstract var ptr: P

    protected var chosenApi: RtMidiApi? = null

    public val info: Info = portInfo

    /** The [RtMidiApi] that RtMidi is using for this port */
    public abstract var api: RtMidiApi
        protected set

    /**
     * The last sent (if [WritableMidiPort]) or received(if [ReadableMidiPort]) [MidiMessage] in this [MidiPort]
     * `null` if none exists
     */
    public var midiMessage: MidiMessage? = null
        protected set

    public var isOpen = false
        protected set

    public var isVirtual = false
        protected set

    public var isDestroyed = false
        protected set

    public var clientName: String? = null
        protected set

    @JvmOverloads
    protected constructor(portInfo: Info, clientName: String, api: RtMidiApi = RtMidiApi.UNSPECIFIED) : this(portInfo) {
        this.chosenApi = api
        this.clientName = clientName
    }

    /**
     * [close]s and destroys this port (if it is not already destroyed) such that it can and will no longer be used,
     * attempting to use the port after this will throw an [RtMidiException] except when calling [destroy], which
     * is intentionally safe to call even if the port is already destroyed, in which case nothing will happen
     *
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
    public fun open(portName: String = info.name) { // TODO: 27/02/2021 Change params??
        checkIsDestroyed()
        if (!isOpen) {
            RtMidiLibrary.instance.rtmidi_open_port(ptr, info.number, portName)
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
    public fun openVirtual(portName: String = info.name) { // TODO: 27/02/2021 Change params??
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

    override fun hashCode(): Int = Objects.hash(ptr, info)

    override fun equals(other: Any?): Boolean =
            other is MidiPort<*> && this.javaClass == other.javaClass
                    && ptr == other.ptr && info == other.info

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

    public data class Info(val name: String, val number: Int, val type: Type) {

        override fun toString(): String =
                """name = '$name'
                |number = $number
                |type = $type""".trimMargin()

        enum class Type {
            READABLE, WRITABLE, UNKNOWN
        }
    }

    // Protected helper functions

    @Suppress("NOTHING_TO_INLINE")
    protected inline fun checkIsDestroyed() =
            if (isDestroyed) throw RtMidiPortException("Cannot proceed, the MidiPort:\n$this\n has already been destroyed") else Unit

    /**
     * Call this after any call to [RtMidiLibrary.getInstance]
     *
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    protected fun checkErrors() = if (!ptr.ok) throw RtMidiNativeException(ptr) else Unit

    /**
     * Create [ptr] to be used in this port.
     * Calls either the default create function like [RtMidiLibrary.rtmidi_in_create_default] or
     * the custom function [RtMidiLibrary.rtmidi_in_create] depending on how this was constructed
     *
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    protected abstract fun createPtr()
}