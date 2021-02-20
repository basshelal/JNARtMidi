package com.github.basshelal.jnartmidi.api;

import com.github.basshelal.jnartmidi.lib.RtMidiLibrary;
import com.sun.jna.Platform;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRtMidiApi {

    private static RtMidiLibrary lib;

    @BeforeAll
    public static void setup() {
        RtMidiLibraryLoader.addSearchPath("bin/" + Platform.RESOURCE_PREFIX);
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
    public void testAvailableAPIs() throws RtMidiException {
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

    @Disabled
    @DisplayName("Midi In Ports")
    @Test
    public void testMidiInPorts() throws InterruptedException {
        MidiPort.Info[] infos = RtMidi.midiInPorts();

        System.out.println("Midi In Ports:");
        for (MidiPort.Info info : infos) { System.out.println(info); }

        List<MidiInPort> ports = Arrays.stream(infos).map(MidiInPort::new).collect(Collectors.toList());

        System.out.println("Ports:");
        for (MidiInPort port : ports) { System.out.println(port); }

        MidiInPort port = ports.get(2);

        port.open();

        MidiInPort port1 = new MidiInPort(RtMidiApi.LINUX_ALSA, "MY INPUT", 1000, infos[2]);

        port1.open();

        MidiOutPort out = new MidiOutPort(RtMidi.midiOutPorts()[1]);
        out.open();

        MidiInPort.ArrayCallback callback = (message, deltaTime) -> {
            System.out.println(Arrays.toString(message));
            out.sendMessage(message);
        };

        port.setCallback(callback);
        // port1.setCallback(callback);

        System.out.println("Midi Out Ports:");
        for (MidiPort.Info info : RtMidi.midiOutPorts()) { System.out.println(info); }

        System.out.println("Midi In Ports:");
        for (MidiPort.Info info : RtMidi.midiInPorts()) { System.out.println(info); }


        MidiInPort korg = new MidiInPort(RtMidi.midiInPorts()[3]);
        korg.open();
        korg.setCallback((message, deltaTime) -> {
            System.out.println(Arrays.toString(message) + "\tKORG");
        });


        Thread.sleep(Long.MAX_VALUE);
    }

}
