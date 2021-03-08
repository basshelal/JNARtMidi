package dev.basshelal.jrtmidi.api

import com.sun.jna.NativeLibrary
import com.sun.jna.Platform
import dev.basshelal.jrtmidi.lib.RtMidiLibrary
import dev.basshelal.jrtmidi.lib.jnr.RtMidiLibraryJNR

/**
 * The entry point to the JRtMidi Library.
 *
 * ### Getting Started:
 *
 * Before using *anything* in the library be sure to call [RtMidi.addLibrarySearchPath]
 * first to ensure the RtMidi native library is loaded correctly, see [RtMidi.addLibrarySearchPath] for details.
 *
 * Create a [ReadableMidiPort] by getting a [MidiPort.Info] from [RtMidi.readableMidiPorts],
 * similarly for [WritableMidiPort]s by calling [RtMidi.writableMidiPorts].
 *
 * [MidiPort]s have a constructor
 * which allows you to choose the [RtMidiApi] to use on the port.
 * The [RtMidiApi]s available to use on the client's machine can be retrieved using [RtMidi.compiledApis].
 *
 * @author Bassam Helal
 */
object RtMidi {

    /**
     * Use this to add search paths for the RtMidi native library.
     *
     * If the user already has RtMidi installed on their system using the default library installing mechanisms,
     * this shouldn't be necessary, however, if you are bundling built RtMidi libraries with your application
     * (more likely), you must call this function with the locations to your libraries.
     *
     * The convention is to separate folders by [com.sun.jna.Platform.RESOURCE_PREFIX] such as:
     *
     *  * linux-x86-64
     *  * linux-aarch64
     *  * win32-x86-64
     *  * darwin-x86-64
     *  * etc...
     *
     * Where each folder contains that platform's shared library using that platform's shared library naming convention
     * convention, ie `librtmidi.so` on GNU/Linux.
     *
     * This way you can only call this function once no matter the platform, as long as it is supported.
     *
     * For example, if your application is supported on x86-64 Linux, Windows and MacOS you would just do:
     *
     * ```java
     * if (Platform.ARCH.equals("x86-64") && (Platform.isLinux() || Platform.isWindows() || Platform.isMac())) {
     *     RtMidi.addSearchPath("your-libs-directory" + Platform.RESOURCE_PREFIX);
     * } else throw new IllegalStateException("Unsupported Platform " + Platform.RESOURCE_PREFIX);
     *```
     *
     * This is all from JNA's handling and convention of library loading, see [com.sun.jna.NativeLibrary] or
     * the JNA documentation for more.
     *
     * You are of course free to handle this in any variety of ways as you see fit.
     *
     * @param path the path to add to the search list for JNA to use when attempting to load the RtMidi native library
     */
    @JvmStatic
    fun addLibrarySearchPath(path: String) {
        RtMidiLibraryJNR.libPaths.add(path)
        NativeLibrary.addSearchPath(RtMidiLibrary.LIBRARY_NAME, path)
    }

    // TODO: 08/03/2021 Make callers to use the default (bundled) libraries!
    //  we can add custom paths later on

    /**
     * @return true if this platform supports virtual ports, false otherwise,
     * currently only Windows does not support virtual ports
     */
    @JvmStatic
    fun supportsVirtualPorts(): Boolean = !Platform.isWindows()

    /**
     * @return the list of all [RtMidiApi]s that RtMidi detected when the native library of RtMidi was compiled that
     * are usable on this machine, this should be at most 2, ie on Unix (ALSA and JACK).
     * If an [RtMidiApi] was found by RtMidi at its compile time but then removed later (for example JACK on a Unix
     * system) it will still be reported as compiled.
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmStatic
    fun compiledApis(): List<RtMidiApi> {
        val arr = IntArray(RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM)
        val written = RtMidiLibrary.instance.rtmidi_get_compiled_api(arr, arr.size)
        return if (written < 0) throw RtMidiNativeException("Error trying to get compiled apis")
        else List(written) { RtMidiApi.fromInt(arr[it]) }
    }

    /**
     * @return a list of [MidiPort.Info]s for all [ReadableMidiPort]s on the system
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmStatic
    fun readableMidiPorts(): List<MidiPort.Info> {
        val ptr = RtMidiLibrary.instance.rtmidi_in_create_default()
        val portCount = RtMidiLibrary.instance.rtmidi_get_port_count(ptr)
        if (!ptr.ok) throw RtMidiNativeException(ptr)
        val result = List(portCount) {
            MidiPort.Info(name = RtMidiLibrary.instance.rtmidi_get_port_name(ptr, it),
                    index = it, type = MidiPort.Info.Type.READABLE)
        }
        RtMidiLibrary.instance.rtmidi_in_free(ptr)
        return result
    }

    /**
     * @return a list of [MidiPort.Info]s for all [WritableMidiPort]s on the system
     * @throws RtMidiNativeException if an error occurred in RtMidi's native code
     */
    @JvmStatic
    fun writableMidiPorts(): List<MidiPort.Info> {
        val ptr = RtMidiLibrary.instance.rtmidi_out_create_default()
        val portCount = RtMidiLibrary.instance.rtmidi_get_port_count(ptr)
        if (!ptr.ok) throw RtMidiNativeException(ptr)
        val result = List(portCount) {
            MidiPort.Info(name = RtMidiLibrary.instance.rtmidi_get_port_name(ptr, it),
                    index = it, type = MidiPort.Info.Type.WRITABLE)
        }
        RtMidiLibrary.instance.rtmidi_out_free(ptr)
        return result
    }
}