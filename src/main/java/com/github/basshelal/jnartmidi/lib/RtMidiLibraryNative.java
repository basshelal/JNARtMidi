package com.github.basshelal.jnartmidi.lib;

import com.sun.jna.Native;

public class RtMidiLibraryNative implements RtMidiLibrary {

    static {
        Native.register(RtMidiLibrary.LIBRARY_NAME);
    }

    // TODO: 15/02/2021 Add all functions from RtMidiLibrary as native

}
