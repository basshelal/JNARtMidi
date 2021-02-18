package com.github.basshelal.jnartmidi.api;

import com.sun.jna.NativeLibrary;

public class RtMidiLibraryLoader {

    // must be called before using ANYTHING from the library!
    public static void addSearchPath(String path) {
        NativeLibrary.addSearchPath(com.github.basshelal.jnartmidi.lib.RtMidiLibrary.LIBRARY_NAME, path);
    }
}
