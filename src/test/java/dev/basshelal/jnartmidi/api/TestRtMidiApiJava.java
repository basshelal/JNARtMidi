package dev.basshelal.jnartmidi.api;

import com.sun.jna.Platform;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import dev.basshelal.jnartmidi.lib.RtMidiLibrary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("KotlinInternalInJava")
public class TestRtMidiApiJava {

    private static RtMidiLibrary lib;

    @BeforeAll
    public static void setup() {
        RtMidi.addLibrarySearchPath("bin/" + Platform.RESOURCE_PREFIX);
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
        List<RtMidiApi> apis = RtMidi.availableApis();

        if (Platform.isLinux()) assertTrue(apis.contains(RtMidiApi.LINUX_ALSA));
        if (Platform.isWindows()) assertTrue(apis.contains(RtMidiApi.WINDOWS_MM));
        if (Platform.isMac()) assertTrue(apis.contains(RtMidiApi.MACOSX_CORE));
    }

    @Disabled
    @DisplayName("Midi In Ports")
    @Test
    public void testMidiInPorts() throws InterruptedException {
        List<MidiPort.Info> infos = RtMidi.readableMidiPorts();

        System.out.println("Readable Ports:");
        for (MidiPort.Info info : infos) { System.out.println(info); }

        List<ReadableMidiPort> ports = infos.stream().map(ReadableMidiPort::new).collect(Collectors.toList());

        System.out.println("Ports:");
        for (ReadableMidiPort port : ports) { System.out.println(port); }

        ReadableMidiPort port = ports.get(2);

        port.open();

        ReadableMidiPort port1 = new ReadableMidiPort(infos.get(2), RtMidiApi.LINUX_ALSA, "MY INPUT");

        port1.open();

        WritableMidiPort out = new WritableMidiPort(RtMidi.writableMidiPorts().get(1));

        out.open();

        ReadableMidiPort korg = new ReadableMidiPort(RtMidi.readableMidiPorts().get(3));
        korg.open();

        MidiMessageCallback callback = (message, deltaTime) -> {
            System.out.println(message);
            out.sendMessage(message);
        };

        port.setCallback(callback);
        // port1.setCallback(callback);

        System.out.println("Writable Ports:");
        for (MidiPort.Info info : RtMidi.writableMidiPorts()) { System.out.println(info); }

        System.out.println("Readable Ports:");
        for (MidiPort.Info info : RtMidi.readableMidiPorts()) { System.out.println(info); }


        korg.setCallback((message, deltaTime) -> {
            System.out.println(message + "\tKORG");
        });

        Thread.sleep(Long.MAX_VALUE);

        //  System.out.println(Arrays.toString(buf));

        //  Thread.sleep(Long.MAX_VALUE);
    }

}
