package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.github.basshelal.jnartmidi.lib.RtMidiLibraryNative;
import com.github.basshelal.jnartmidi.lib.RtMidiWrapper;

public class RtMidi {

    private static final RtMidiLibraryNative lib;
    private static final RtMidiWrapper wrapper;

    static {
        lib = new RtMidiLibraryNative();
        wrapper = new RtMidiWrapper();
    }

    private RtMidi() {}

    public static String[] allMidiPorts() {
        // TODO: 17/02/2021 Are we using wrapper correctly?
        int count = lib.rtmidi_get_port_count(wrapper);
        String[] result = new String[count];
        for (int i = 0; i <= count; i++) {
            result[i] = lib.rtmidi_get_port_name(wrapper, i);
        }
        return result;
    }

    public static class Apis {
        public static final Api UNSPECIFIED = Api.fromInt(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED);
        public static final Api MACOSX_CORE = Api.fromInt(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE);
        public static final Api LINUX_ALSA = Api.fromInt(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA);
        public static final Api UNIX_JACK = Api.fromInt(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK);
        public static final Api WINDOWS_MM = Api.fromInt(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM);
        public static final Api RTMIDI_DUMMY = Api.fromInt(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY);
    }

    public static class Api {
        private final String name;
        private final String displayName;

        private Api(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }

        public static Api fromInt(int api) {
            return new Api(lib.rtmidi_api_name(api), lib.rtmidi_api_display_name(api));
        }

        public String getName() { return name; }

        public String getDisplayName() { return displayName; }
    }

}
