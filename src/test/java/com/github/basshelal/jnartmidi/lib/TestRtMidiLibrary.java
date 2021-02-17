package com.github.basshelal.jnartmidi.lib;

import com.github.basshelal.jnartmidi.api.RtMidi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRtMidiLibrary {

    private static RtMidiLibraryNative lib;

    @BeforeAll
    public static void setup() {
        lib = new RtMidiLibraryNative();
    }

    @AfterAll
    public static void teardown() {}

    @DisplayName("API names")
    @Test
    public void testApiNames() {
        // UNSPECIFIED
        assertEquals(RtMidi.Apis.UNSPECIFIED.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        assertEquals(RtMidi.Apis.UNSPECIFIED.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        // MACOSX_CORE
        assertEquals(RtMidi.Apis.MACOSX_CORE.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        assertEquals(RtMidi.Apis.MACOSX_CORE.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        // LINUX_ALSA
        assertEquals(RtMidi.Apis.LINUX_ALSA.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        assertEquals(RtMidi.Apis.LINUX_ALSA.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        // UNIX_JACK
        assertEquals(RtMidi.Apis.UNIX_JACK.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        assertEquals(RtMidi.Apis.UNIX_JACK.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        // WINDOWS_MM
        assertEquals(RtMidi.Apis.WINDOWS_MM.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        assertEquals(RtMidi.Apis.WINDOWS_MM.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        // RTMIDI_DUMMY
        assertEquals(RtMidi.Apis.RTMIDI_DUMMY.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
        assertEquals(RtMidi.Apis.RTMIDI_DUMMY.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
    }

    @Test
    public void test() {
        int[] arr = new int[6];
        int s = lib.rtmidi_get_compiled_api(arr, 6);
        System.out.println(s);
        System.out.println(Arrays.toString(arr));
    }

}
