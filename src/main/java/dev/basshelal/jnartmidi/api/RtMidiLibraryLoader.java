package dev.basshelal.jnartmidi.api;

import com.sun.jna.NativeLibrary;

import dev.basshelal.jnartmidi.lib.RtMidiLibrary;

/**
 * Used to configure the Native Library loading done by JNA for RtMidi's library.
 *
 * Make sure to call these functions before using anything else from the library otherwise the functions may not take
 * effect correctly.
 *
 * @author Bassam Helal
 */
public class RtMidiLibraryLoader {

    /**
     * Use this to add search paths for the RtMidi native library.
     *
     * If the user already has RtMidi installed on their system, this shouldn't be necessary, however, if you are
     * bundling built RtMidi libraries with your application, you must call this function with the locations to your
     * libraries.
     *
     * The convention is to separate folders by {@link com.sun.jna.Platform#RESOURCE_PREFIX} such as:
     * <ul>
     *     <li>linux-x86-64</li>
     *     <li>linux-aarch64</li>
     *     <li>win32-x86-64</li>
     *     <li>darwin-x86-64</li>
     * </ul>
     *
     * Where each folder contains that platform's shared library using that platform's naming convention convention.
     *
     * This way you can only call this function once no matter the platform, as long as it is supported.
     *
     * For example, if your application is supported on x86-64 Linux, Windows and MacOS you would just do:
     * <br>
     * <code>
     *     if (Platform.ARCH.equals("x86-64") && (Platform.isLinux() || Platform.isWindows() || Platform.isMac())) {
     *         RtMidiLibraryLoader.addSearchPath("your-libs-directory" + Platform.RESOURCE_PREFIX);
     *     } else throw new IllegalStateException("Unsupported Platform " + Platform.RESOURCE_PREFIX);
     * </code>
     * <br>
     * This is all from JNA's handling and convention of library loading, see {@link com.sun.jna.NativeLibrary} or
     * the JNA documentation for more.
     *
     * @param path the path to add to the search list for JNA to use when attempting to load the RtMidi native library
     */
    public static void addSearchPath(String path) {
        NativeLibrary.addSearchPath(RtMidiLibrary.LIBRARY_NAME, path);
    }
}
