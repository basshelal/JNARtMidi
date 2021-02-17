package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;

import java.util.Objects;

public class RtMidiApi {
    public static final RtMidiApi UNSPECIFIED = new RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED);
    public static final RtMidiApi MACOSX_CORE = new RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE);
    public static final RtMidiApi LINUX_ALSA = new RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA);
    public static final RtMidiApi UNIX_JACK = new RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK);
    public static final RtMidiApi WINDOWS_MM = new RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM);
    public static final RtMidiApi RTMIDI_DUMMY = new RtMidiApi(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY);

    private final String name;
    private final String displayName;

    private RtMidiApi(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    private RtMidiApi(int api) {
        this.name = RtMidiLibrary.getInstance().rtmidi_api_name(api);
        this.displayName = RtMidiLibrary.getInstance().rtmidi_api_display_name(api);
    }

    public static RtMidiApi fromInt(int api) {
        switch (api) {
            case RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE:
                return RtMidiApi.MACOSX_CORE;
            case RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA:
                return RtMidiApi.LINUX_ALSA;
            case RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK:
                return RtMidiApi.UNIX_JACK;
            case RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM:
                return RtMidiApi.WINDOWS_MM;
            case RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY:
                return RtMidiApi.RTMIDI_DUMMY;
            default:
                return RtMidiApi.UNSPECIFIED;
        }
    }

    public String getName() { return name; }

    public String getDisplayName() { return displayName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RtMidiApi)) return false;
        RtMidiApi api = (RtMidiApi) o;
        return getName().equals(api.getName()) && getDisplayName().equals(api.getDisplayName());
    }

    @Override
    public int hashCode() { return Objects.hash(getName(), getDisplayName()); }

    @Override
    public String toString() {
        return "RtMidiApi{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
