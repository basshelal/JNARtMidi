package com.github.basshelal.jnartmidi.lib;

import com.github.basshelal.jnartmidi.api.MidiInPort;
import com.github.basshelal.jnartmidi.api.MidiOutPort;
import com.github.basshelal.jnartmidi.api.MidiPort;
import com.github.basshelal.jnartmidi.api.RtMidi;
import com.github.basshelal.jnartmidi.api.RtMidiApi;
import com.github.basshelal.jnartmidi.api.RtMidiLibraryLoader;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRtMidiLibrary {

    private static RtMidiLibrary lib;

    @BeforeAll
    public static void setup() {
        RtMidiLibraryLoader.addSearchPath("bin/linux-x86-64");
        lib = RtMidiLibrary.getInstance();
    }

    @AfterAll
    public static void teardown() {}

    @DisplayName("API names")
    @Test
    public void testApiNames() {
        // UNSPECIFIED
        assertEquals("unspecified", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        assertEquals("Unknown", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        // MACOSX_CORE
        assertEquals("core", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        assertEquals("CoreMidi", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        // LINUX_ALSA
        assertEquals("alsa", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        assertEquals("ALSA", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        // UNIX_JACK
        assertEquals("jack", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        assertEquals("Jack", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        // WINDOWS_MM
        assertEquals("winmm", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        assertEquals("Windows MultiMedia", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        // RTMIDI_DUMMY
        assertEquals("dummy", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
        assertEquals("Dummy", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
    }

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
