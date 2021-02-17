package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.sun.jna.Platform;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRtMidiApi {

    private static RtMidiLibrary lib;

    @BeforeAll
    public static void setup() {
        lib = RtMidiLibrary.getInstance();
    }

    @AfterAll
    public static void teardown() {}

    @DisplayName("API names")
    @Test
    public void testApiNames() {
        // UNSPECIFIED
        assertEquals(RtMidiApi.UNSPECIFIED.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        assertEquals(RtMidiApi.UNSPECIFIED.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        // MACOSX_CORE
        assertEquals(RtMidiApi.MACOSX_CORE.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        assertEquals(RtMidiApi.MACOSX_CORE.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        // LINUX_ALSA
        assertEquals(RtMidiApi.LINUX_ALSA.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        assertEquals(RtMidiApi.LINUX_ALSA.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        // UNIX_JACK
        assertEquals(RtMidiApi.UNIX_JACK.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        assertEquals(RtMidiApi.UNIX_JACK.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        // WINDOWS_MM
        assertEquals(RtMidiApi.WINDOWS_MM.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        assertEquals(RtMidiApi.WINDOWS_MM.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        // RTMIDI_DUMMY
        assertEquals(RtMidiApi.RTMIDI_DUMMY.getName(), lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
        assertEquals(RtMidiApi.RTMIDI_DUMMY.getDisplayName(), lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
    }


    @DisplayName("Available APIs")
    @Test
    void testAvailableAPIs() throws RtMidiException {
        RtMidiApi[] apis = RtMidi.getAvailableApis();
        List<RtMidiApi> list = Arrays.asList(apis);

        if (Platform.isLinux()) {
            assertTrue(list.contains(RtMidiApi.LINUX_ALSA));
            // If Linux and more than 1 then JACK is likely installed
            if (apis.length > 1) assertTrue(list.contains(RtMidiApi.UNIX_JACK));
        }
        if (Platform.isWindows()) assertTrue(list.contains(RtMidiApi.WINDOWS_MM));
        if (Platform.isMac()) assertTrue(list.contains(RtMidiApi.MACOSX_CORE));
    }

    @DisplayName("MidiInDevice")
    @Test
    public void testMidiIn() throws InterruptedException {
        MidiInDevice in = new MidiInDevice();

        System.out.println(Arrays.toString(in.getPorts()));

        System.out.println(in.portCount());
        in.openPort(2, "My Midi In Port");

        in.setCallback((int[] message, double deltaTime) -> {
            System.out.println(Arrays.toString(message));
        });

        // >$ aseqdump -p portNumber

        Thread.sleep(Long.MAX_VALUE);
    }

}
