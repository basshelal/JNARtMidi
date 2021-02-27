package dev.basshelal.jnartmidi.api

import com.sun.jna.Platform
import dev.basshelal.jnartmidi.lib.RtMidiLibrary
import dev.basshelal.jnartmidi.lib.RtMidiPtr
import java.util.Objects

abstract class MidiPort<P : RtMidiPtr> {

    protected abstract var ptr: P

    abstract val api: RtMidiApi

    val info: Info

    /** @return the last sent (if [WritableMidiPort]) or received(if [ReadableMidiPort]) [MidiMessage] in this [MidiPort] */
    var midiMessage: MidiMessage? = null
        protected set

    var isOpen = false
        protected set

    var isVirtual = false
        protected set

    protected var isDestroyed = false

    protected var chosenApi: RtMidiApi? = null

    var clientName: String? = null
        protected set

    protected constructor(portInfo: Info) {
        info = portInfo
    }

    @JvmOverloads
    protected constructor(portInfo: Info, clientName: String, api: RtMidiApi = RtMidiApi.UNSPECIFIED) : this(portInfo) {
        this.chosenApi = api
        this.clientName = clientName
    }

    /**
     * Destroys this port such that it can and will no longer be used, attempting to use the port
     * after this will throw an [RtMidiException], see [checkIsDestroyed]
     *
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    abstract fun destroy()

//    /**
//     * @return the [RtMidiApi] that this port is using
//     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
//     */
//    abstract fun getApi(): RtMidiApi

    /**
     * Create [.ptr] to be used in this port.
     * Calls either the default create function like [RtMidiLibrary.rtmidi_in_create_default] or
     * the custom function [RtMidiLibrary.rtmidi_in_create] depending on how this was constructed
     *
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    protected abstract fun createPtr()

    @JvmOverloads
    fun open(info: Info = this.info) {
        checkIsDestroyed()
        requireNotNull(info) { "info cannot be null!" }
        RtMidiLibrary.instance.rtmidi_open_port(ptr, info.number, info.name)
        checkErrors()
        isOpen = true
        isVirtual = false
    }

    /**
     * TODO doc!
     *
     * @param name
     * @throws RtMidiPortException   if this platform does not support virtual ports, see [RtMidi.supportsVirtualPorts]
     * or if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    fun openVirtual(name: String) {
        checkIsDestroyed()
        if (!RtMidi.supportsVirtualPorts())
            throw RtMidiPortException("Platform ${Platform.RESOURCE_PREFIX} does not support virtual ports")
        RtMidiLibrary.instance.rtmidi_open_virtual_port(ptr, name)
        checkErrors()
        isOpen = true
        isVirtual = true
    }

    /**
     * TODO doc!
     *
     * @throws RtMidiPortException   if this port has already been destroyed
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    fun close() {
        checkIsDestroyed()
        RtMidiLibrary.instance.rtmidi_close_port(ptr)
        checkErrors()
        isOpen = false
        isVirtual = false
        destroy()
        createPtr()
    }

    protected fun checkIsDestroyed() =
            if (isDestroyed) throw RtMidiPortException("Cannot proceed, the MidiPort:\n$this\n has already been destroyed") else Unit

    /**
     * Call this after any call to [RtMidiLibrary.getInstance]
     *
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    protected fun checkErrors() = if (!ptr.ok) throw RtMidiNativeException(ptr) else Unit

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

    data class Info(val name: String, val number: Int, val type: Type) {

        override fun toString(): String =
                """name = '$name'
                |number = $number
                |type = $type""".trimMargin()

        enum class Type {
            READABLE, WRITABLE, UNKNOWN
        }
    }
}