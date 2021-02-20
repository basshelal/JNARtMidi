package com.github.basshelal.jnartmidi.lib;

import com.github.basshelal.jnartmidi.api.RtMidi;
import com.github.basshelal.jnartmidi.api.RtMidiApi;
import com.github.basshelal.jnartmidi.api.RtMidiLibraryLoader;
import com.sun.jna.Platform;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiCCallback;
import static com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiInPtr;
import static com.github.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiOutPtr;
import static com.github.basshelal.jnartmidi.lib.RtMidiLibrary.getInstance;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests all 22 of the exported native C functions from the RtMidi library found in {@link RtMidiLibrary}
 */
public class TestRtMidiLibrary {

    private static RtMidiLibrary lib;

    // TODO: 20/02/2021 Make helper methods to avoid DRY

    private RtMidiInPtr inCreateDefault() {
        RtMidiInPtr in = lib.rtmidi_in_create_default();
        assertNotNull(in);
        assertTrue(in.ok);
        return in;
    }

    private RtMidiOutPtr outCreateDefault() {
        RtMidiOutPtr out = lib.rtmidi_out_create_default();
        assertNotNull(out);
        assertTrue(out.ok);
        return out;
    }

    @BeforeAll
    public static void setup() {
        RtMidiLibraryLoader.addSearchPath("bin/" + Platform.RESOURCE_PREFIX);
        lib = getInstance();
    }

    @AfterAll
    public static void teardown() {}

    @DisplayName("rtmidi_get_compiled_api")
    @Test
    public void testGetCompiledApi() {
        // Using array
        int[] arr = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int written = lib.rtmidi_get_compiled_api(arr, arr.length);

        assertTrue(written <= RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM);

        RtMidiApi[] apis = new RtMidiApi[written];
        for (int i = 0; i < written; i++)
            apis[i] = RtMidiApi.fromInt(arr[i]);

        assertTrue(apis.length > 0);
        for (RtMidiApi api : apis)
            assertNotNull(api);

        // using null
        int writtenNull = lib.rtmidi_get_compiled_api(null, -1);
        assertEquals(written, writtenNull);
    }

    @DisplayName("rtmidi_api_name")
    @Test
    public void testApiName() {
        assertEquals("unspecified", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        assertEquals("core", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        assertEquals("alsa", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        assertEquals("jack", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        assertEquals("winmm", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        assertEquals("dummy", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
    }

    @DisplayName("rtmidi_api_display_name")
    @Test
    public void testApiDisplayName() {
        assertEquals("Unknown", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        assertEquals("CoreMidi", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        assertEquals("ALSA", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        assertEquals("Jack", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        assertEquals("Windows MultiMedia", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        assertEquals("Dummy", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
    }

    @DisplayName("rtmidi_compiled_api_by_name")
    @Test
    public void testCompiledApiByName() {
        int apiNumber = RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED;
        if (Platform.isLinux()) {
            apiNumber = lib.rtmidi_compiled_api_by_name("alsa");
        } else if (Platform.isMac()) {
            apiNumber = lib.rtmidi_compiled_api_by_name("core");
        } else if (Platform.isWindows()) {
            apiNumber = lib.rtmidi_compiled_api_by_name("winmm");
        }
        assertTrue(apiNumber != RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED);
        RtMidiApi api = RtMidiApi.fromInt(apiNumber);
        assertNotEquals(api, RtMidiApi.UNSPECIFIED);
        assertEquals(api.getNumber(), apiNumber);
    }

    @DisplayName("rtmidi_open_port")
    @Test
    public void testOpenPort() {
        RtMidiInPtr in = inCreateDefault();
        RtMidiOutPtr out = outCreateDefault();

        int inPortCount = lib.rtmidi_get_port_count(in);
        int outPortCount = lib.rtmidi_get_port_count(out);

        assertTrue(inPortCount > 0);
        assertTrue(outPortCount > 0);

        // Open an in port with a unique name!
        String inPortName = "Test JNARtMidi In Port at " + (new Random()).nextInt();

        lib.rtmidi_open_port(in, 0, inPortName);

        int newOutPortCount = lib.rtmidi_get_port_count(out);

        assertNotEquals(outPortCount, newOutPortCount);
        assertEquals(outPortCount + 1, newOutPortCount);

        // out ports should contain our newly created in port

        ArrayList<String> outPortNames = new ArrayList<>(newOutPortCount);
        for (int i = 0; i < newOutPortCount; i++)
            outPortNames.add(lib.rtmidi_get_port_name(out, i));

        boolean foundOut = outPortNames.stream().anyMatch((String s) -> s.contains(inPortName));

        assertTrue(foundOut);

        // Open an out port with a unique name!
        String outPortName = "Test JNARtMidi Out Port at " + (new Random()).nextInt();

        lib.rtmidi_open_port(out, 0, outPortName);

        int newInPortCount = lib.rtmidi_get_port_count(in);

        assertNotEquals(inPortCount, newInPortCount);
        assertEquals(inPortCount + 1, newInPortCount);

        // in ports should contain our newly created out port

        ArrayList<String> inPortNames = new ArrayList<>(newInPortCount);
        for (int i = 0; i < newInPortCount; i++)
            inPortNames.add(lib.rtmidi_get_port_name(in, i));

        boolean foundIn = inPortNames.stream().anyMatch((String s) -> s.contains(outPortName));

        assertTrue(foundIn);

        lib.rtmidi_in_free(in);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("rtmidi_open_virtual_port")
    @Test
    public void testOpenVirtualPort() {
        Assumptions.assumeTrue(RtMidi.supportsVirtualPorts(),
                "Platform " + Platform.RESOURCE_PREFIX +
                        " does not support virtual ports, skipping test");

        RtMidiInPtr in = inCreateDefault();
        RtMidiOutPtr out = outCreateDefault();

        int inPortCount = lib.rtmidi_get_port_count(in);
        int outPortCount = lib.rtmidi_get_port_count(out);

        assertTrue(inPortCount > 0);
        assertTrue(outPortCount > 0);

        // Open an in port with a unique name!
        String inPortName = "Test JNARtMidi In Port at " + (new Random()).nextInt();

        lib.rtmidi_open_virtual_port(in, inPortName);

        int newOutPortCount = lib.rtmidi_get_port_count(out);

        assertNotEquals(outPortCount, newOutPortCount);
        assertEquals(outPortCount + 1, newOutPortCount);

        // out ports should contain our newly created in port

        ArrayList<String> outPortNames = new ArrayList<>(newOutPortCount);
        for (int i = 0; i < newOutPortCount; i++)
            outPortNames.add(lib.rtmidi_get_port_name(out, i));

        boolean foundOut = outPortNames.stream().anyMatch((String s) -> s.contains(inPortName));

        assertTrue(foundOut);

        // Open an out port with a unique name!
        String outPortName = "Test JNARtMidi Out Port at " + (new Random()).nextInt();

        lib.rtmidi_open_virtual_port(out, outPortName);

        int newInPortCount = lib.rtmidi_get_port_count(in);

        assertNotEquals(inPortCount, newInPortCount);
        assertEquals(inPortCount + 1, newInPortCount);

        // in ports should contain our newly created out port

        ArrayList<String> inPortNames = new ArrayList<>(newInPortCount);
        for (int i = 0; i < newInPortCount; i++)
            inPortNames.add(lib.rtmidi_get_port_name(in, i));

        boolean foundIn = inPortNames.stream().anyMatch((String s) -> s.contains(outPortName));

        assertTrue(foundIn);

        lib.rtmidi_in_free(in);
        lib.rtmidi_out_free(out);
    }

    @Disabled
    @DisplayName("rtmidi_close_port")
    @Test
    public void testClosePort() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_close_port(null);
    }

    @DisplayName("rtmidi_get_port_count")
    @Test
    public void testGetPortCount() {
        RtMidiInPtr in = inCreateDefault();
        RtMidiOutPtr out = outCreateDefault();

        int inCount = lib.rtmidi_get_port_count(in);
        int outCount = lib.rtmidi_get_port_count(out);

        assertTrue(inCount > 0);
        assertTrue(outCount > 0);

        lib.rtmidi_in_free(in);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("rtmidi_get_port_name")
    @Test
    public void testGetPortName() {
        RtMidiInPtr in = inCreateDefault();
        RtMidiOutPtr out = outCreateDefault();

        int inCount = lib.rtmidi_get_port_count(in);
        int outCount = lib.rtmidi_get_port_count(out);

        assertTrue(inCount > 0);
        assertTrue(outCount > 0);

        for (int i = 0; i < inCount; i++) {
            String portName = lib.rtmidi_get_port_name(in, i);
            assertNotNull(portName);
            assertNotEquals("", portName);
        }
        for (int i = 0; i < outCount; i++) {
            String portName = lib.rtmidi_get_port_name(out, i);
            assertNotNull(portName);
            assertNotEquals("", portName);
        }
        lib.rtmidi_in_free(in);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("rtmidi_in_create_default")
    @Test
    public void testInCreateDefault() {
        RtMidiInPtr in = lib.rtmidi_in_create_default();
        assertNotNull(in);
        assertTrue(in.ok);
        lib.rtmidi_in_free(in);
    }

    @DisplayName("rtmidi_in_create")
    @Test
    public void testInCreate() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";
        int queueSizeLimit = 1024;

        RtMidiInPtr in = lib.rtmidi_in_create(apis[0], clientName, queueSizeLimit);
        assertNotNull(in);
        assertTrue(in.ok);
        lib.rtmidi_in_free(in);
    }

    @DisplayName("rtmidi_in_free")
    @Test
    public void testInFree() {
        RtMidiInPtr in = inCreateDefault();

        RtMidiPtr copy = new RtMidiPtr(in);

        assertEquals(copy.ptr, in.ptr);
        assertEquals(copy.data, in.data);

        lib.rtmidi_in_free(in);

        assertNotEquals(copy.ptr, in.ptr);
        assertNotEquals(copy.data, in.data);

        // doing anything with `in` should cause a fatal error SIGSEGV (ie segfault)
    }

    @DisplayName("rtmidi_in_get_current_api")
    @Test
    public void testInGetCurrentApi() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";
        int queueSizeLimit = 1024;

        RtMidiInPtr in = lib.rtmidi_in_create(apis[0], clientName, queueSizeLimit);
        assertNotNull(in);
        assertTrue(in.ok);

        int usedApi = lib.rtmidi_in_get_current_api(in);
        assertEquals(apis[0], usedApi);
        lib.rtmidi_in_free(in);
    }

    @Disabled
    @DisplayName("rtmidi_in_set_callback")
    @Test
    public void testInSetCallback() {
        RtMidiInPtr in = inCreateDefault();

        // TODO: 20/02/2021 Implement

        RtMidiCCallback callback = (timeStamp, message, messageSize, userData) -> {

        };

        lib.rtmidi_in_set_callback(null, null, null);

        lib.rtmidi_in_free(in);
    }

    @Disabled
    @DisplayName("rtmidi_in_cancel_callback")
    @Test
    public void testInCancelCallback() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_cancel_callback(null);
    }

    @Disabled
    @DisplayName("rtmidi_in_ignore_types")
    @Test
    public void testInIgnoreTypes() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_ignore_types(null, false, false, false);
    }

    @Disabled
    @DisplayName("rtmidi_in_get_message")
    @Test
    public void testInGetMessage() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_get_message(null, null, -1);
    }

    @DisplayName("rtmidi_out_create_default")
    @Test
    public void testOutCreateDefault() {
        RtMidiOutPtr out = lib.rtmidi_out_create_default();
        assertNotNull(out);
        assertTrue(out.ok);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("rtmidi_out_create")
    @Test
    public void testOutCreate() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";

        RtMidiOutPtr out = lib.rtmidi_out_create(apis[0], clientName);
        assertNotNull(out);
        assertTrue(out.ok);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("rtmidi_out_free")
    @Test
    public void testOutFree() {
        RtMidiOutPtr out = outCreateDefault();

        RtMidiPtr copy = new RtMidiPtr(out);

        assertEquals(copy.ptr, out.ptr);
        assertEquals(copy.data, out.data);

        lib.rtmidi_out_free(out);

        assertNotEquals(copy.ptr, out.ptr);
        assertNotEquals(copy.data, out.data);

        // doing anything with `out` should cause a fatal error SIGSEGV (ie segfault)
    }

    @DisplayName("rtmidi_out_get_current_api")
    @Test
    public void testOutGetCurrentApi() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";

        RtMidiOutPtr out = lib.rtmidi_out_create(apis[0], clientName);
        assertNotNull(out);
        assertTrue(out.ok);

        int usedApi = lib.rtmidi_out_get_current_api(out);
        assertEquals(apis[0], usedApi);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("rtmidi_out_send_message")
    @Test
    public void testOutSendMessage() {
        RtMidiInPtr in = inCreateDefault();
        RtMidiOutPtr out = outCreateDefault();

        int inPortCount = lib.rtmidi_get_port_count(in);
        int outPortCount = lib.rtmidi_get_port_count(out);

        assertTrue(inPortCount > 0);
        assertTrue(outPortCount > 0);

        // Open an out port with a unique name!
        String outPortName = "Test JNARtMidi Out Port at " + (new Random()).nextInt();

        // open the port
        lib.rtmidi_open_port(out, 0, outPortName);

        // find it on the other side and open that

        int newInPortCount = lib.rtmidi_get_port_count(in);

        assertNotEquals(inPortCount, newInPortCount);
        assertEquals(inPortCount + 1, newInPortCount);

        // in ports should contain our newly created out port

        int inPortIndex = newInPortCount - 1;

        String inPortName = "Test JNARtMidi In Port at " + (new Random()).nextInt();
        lib.rtmidi_open_port(in, inPortIndex, inPortName);

        System.out.println(Arrays.toString(RtMidi.midiInPorts()));
        System.out.println(Arrays.toString(RtMidi.midiOutPorts()));

        // send the out message

        byte[] message = new byte[]{69, 69, 69};

        lib.rtmidi_out_send_message(out, message, message.length);

        // get the in message and assert they are equal

        byte[] receivedMessage = new byte[message.length];


        System.out.println(in.ptr);

        //  lib.rtmidi_in_get_message(in, receivedMessage, receivedMessage.length);

        assertArrayEquals(message, receivedMessage);

        lib.rtmidi_in_free(in);
        lib.rtmidi_out_free(out);
    }

}
