package com.github.basshelal.jnartmidi.lib;

import com.github.basshelal.jnartmidi.api.MidiInPort;
import com.github.basshelal.jnartmidi.api.MidiOutPort;
import com.github.basshelal.jnartmidi.api.RtMidi;
import com.github.basshelal.jnartmidi.api.RtMidiApi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRtMidiLibrary {

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

    @Test
    void test() throws InterruptedException {
        MidiInPort in = new MidiInPort(RtMidiApi.LINUX_ALSA, "My In Device", 1000);
        in.openPort(2, "My Port");
        MidiInPort in1 = new MidiInPort(RtMidiApi.LINUX_ALSA, "My In Device Again", 1000);
        in1.openPort(2, "My Port Again");
        System.out.println(in1.portName(2));


        MidiOutPort test = new MidiOutPort();
        test.openPort(4, "TTTTTTTTTTTTTTTTTTT");

        MidiInPort.Callback callback = (int[] message, double deltaTime) -> {
            System.out.println(System.currentTimeMillis() + "\n" + Arrays.toString(message) + "\n");
        };

        in.setCallback(callback);
        in1.setCallback(callback);

        test.sendMessage();
        test.sendMessage();
        test.sendMessage();

        System.out.println(Arrays.toString(RtMidi.midiInDevices()));
        System.out.println(Arrays.toString(RtMidi.midiOutDevices()));


        // >$ aconnect -l

        Thread.sleep(Long.MAX_VALUE);
    }

    @DisplayName("RtMidiIn Create Default")
    @Test
    public void test1() throws InterruptedException {
        RtMidiWrapper wrapper = lib.rtmidi_in_create_default();
        System.out.println(wrapper);
        int count = lib.rtmidi_get_port_count(wrapper);
        System.out.println(count);
        System.out.println(lib.rtmidi_get_port_name(wrapper, 0));
        System.out.println(lib.rtmidi_get_port_name(wrapper, 1));

        lib.rtmidi_open_port(wrapper, 1, "My Port");
        RtMidiWrapper s = lib.rtmidi_in_create(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA, "My Client", 1000);
        System.out.println(s);
        int counts = lib.rtmidi_get_port_count(s);
        System.out.println(counts);
        System.out.println(lib.rtmidi_get_port_name(s, 0));
        System.out.println(lib.rtmidi_get_port_name(s, 1));
        lib.rtmidi_open_port(s, 1, "My  s  Port");


        lib.rtmidi_in_ignore_types(s, true, true, true);


        Thread.sleep(Long.MAX_VALUE);
    }

}
