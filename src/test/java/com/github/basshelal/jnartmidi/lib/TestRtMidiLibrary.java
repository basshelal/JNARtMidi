package com.github.basshelal.jnartmidi.lib;

import com.github.basshelal.jnartmidi.api.RtMidiApi;
import com.github.basshelal.jnartmidi.api.RtMidiLibraryLoader;
import com.sun.jna.Platform;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests all 22 of the exported native C functions from the RtMidi library found in {@link RtMidiLibrary}
 */
public class TestRtMidiLibrary {

    private static RtMidiLibrary lib;

    @BeforeAll
    public static void setup() {
        RtMidiLibraryLoader.addSearchPath("bin/" + Platform.RESOURCE_PREFIX);
        lib = RtMidiLibrary.getInstance();
    }

    @AfterAll
    public static void teardown() {}

    @DisplayName("Get Compiled API")
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

    @DisplayName("API name")
    @Test
    public void testApiName() {
        assertEquals("unspecified", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        assertEquals("core", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        assertEquals("alsa", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        assertEquals("jack", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        assertEquals("winmm", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        assertEquals("dummy", lib.rtmidi_api_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
    }

    @DisplayName("API Display Name")
    @Test
    public void testApiDisplayName() {
        assertEquals("Unknown", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNSPECIFIED));
        assertEquals("CoreMidi", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_MACOSX_CORE));
        assertEquals("ALSA", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_LINUX_ALSA));
        assertEquals("Jack", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_UNIX_JACK));
        assertEquals("Windows MultiMedia", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_WINDOWS_MM));
        assertEquals("Dummy", lib.rtmidi_api_display_name(RtMidiLibrary.RtMidiApi.RTMIDI_API_RTMIDI_DUMMY));
    }

    @DisplayName("Compiled API by Name")
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

    @Disabled
    @DisplayName("Open Port")
    @Test
    public void testOpenPort() {
        // TODO: 20/02/2021 Implement
        assertAll(() -> {
            lib.rtmidi_open_port(lib.rtmidi_in_create_default(), -1, "");
        });
    }

    @Disabled
    @DisplayName("Open Virtual Port")
    @Test
    public void testOpenVirtualPort() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_open_virtual_port(lib.rtmidi_in_create_default(), "");
    }

    @Disabled
    @DisplayName("Close Port")
    @Test
    public void testClosePort() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_close_port(null);
    }

    @DisplayName("Get Port Count")
    @Test
    public void testGetPortCount() {
        RtMidiWrapper in = lib.rtmidi_in_create_default();
        assertNotNull(in);
        assertTrue(in.ok);
        RtMidiWrapper out = lib.rtmidi_out_create_default();
        assertNotNull(out);
        assertTrue(out.ok);

        int inCount = lib.rtmidi_get_port_count(in);
        int outCount = lib.rtmidi_get_port_count(out);

        assertTrue(inCount > 0);
        assertTrue(outCount > 0);

        lib.rtmidi_in_free(in);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("Get Port Name")
    @Test
    public void testGetPortName() {
        RtMidiWrapper in = lib.rtmidi_in_create_default();
        assertNotNull(in);
        assertTrue(in.ok);
        RtMidiWrapper out = lib.rtmidi_out_create_default();
        assertNotNull(out);
        assertTrue(out.ok);

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

    @DisplayName("In Create Default")
    @Test
    public void testInCreateDefault() {
        RtMidiWrapper in = lib.rtmidi_in_create_default();
        assertNotNull(in);
        assertTrue(in.ok);
        lib.rtmidi_in_free(in);
    }

    @DisplayName("In Create")
    @Test
    public void testInCreate() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";
        int queueSizeLimit = 1024;

        RtMidiWrapper in = lib.rtmidi_in_create(apis[0], clientName, queueSizeLimit);
        assertNotNull(in);
        assertTrue(in.ok);
        lib.rtmidi_in_free(in);
    }

    @DisplayName("In Free")
    @Test
    public void testInFree() {
        RtMidiWrapper in = lib.rtmidi_in_create_default();
        assertNotNull(in);
        assertTrue(in.ok);

        RtMidiWrapper copy = new RtMidiWrapper(in);

        assertEquals(copy.ptr, in.ptr);
        assertEquals(copy.data, in.data);

        lib.rtmidi_in_free(in);

        assertNotEquals(copy.ptr, in.ptr);
        assertNotEquals(copy.data, in.data);

        // doing anything with `in` should cause a fatal error SIGSEGV (ie segfault)
    }

    @DisplayName("In Get Current API")
    @Test
    public void testInGetCurrentApi() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";
        int queueSizeLimit = 1024;

        RtMidiWrapper in = lib.rtmidi_in_create(apis[0], clientName, queueSizeLimit);
        assertNotNull(in);
        assertTrue(in.ok);

        int usedApi = lib.rtmidi_in_get_current_api(in);
        assertEquals(apis[0], usedApi);
        lib.rtmidi_in_free(in);
    }

    @Disabled
    @DisplayName("In Set Callback")
    @Test
    public void testInSetCallback() {
        RtMidiWrapper in = lib.rtmidi_in_create_default();
        assertNotNull(in);
        assertTrue(in.ok);

        // TODO: 20/02/2021 Implement

        RtMidiLibrary.RtMidiCCallback callback = (timeStamp, message, messageSize, userData) -> {

        };
        lib.rtmidi_in_free(in);
    }

    @Disabled
    @DisplayName("In Cancel Callback")
    @Test
    public void testInCancelCallback() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_cancel_callback(null);
    }

    @Disabled
    @DisplayName("In Ignore Types")
    @Test
    public void testInIgnoreTypes() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_ignore_types(null, false, false, false);
    }

    @Disabled
    @DisplayName("In Get Message")
    @Test
    public void testInGetMessage() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_get_message(null, null, null);
    }

    @DisplayName("Out Create Default")
    @Test
    public void testOutCreateDefault() {
        RtMidiWrapper out = lib.rtmidi_out_create_default();
        assertNotNull(out);
        assertTrue(out.ok);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("Out Create")
    @Test
    public void testOutCreate() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";

        RtMidiWrapper out = lib.rtmidi_out_create(apis[0], clientName);
        assertNotNull(out);
        assertTrue(out.ok);
        lib.rtmidi_out_free(out);
    }

    @DisplayName("Out Free")
    @Test
    public void testOutFree() {
        RtMidiWrapper out = lib.rtmidi_out_create_default();
        assertNotNull(out);
        assertTrue(out.ok);

        RtMidiWrapper copy = new RtMidiWrapper(out);

        assertEquals(copy.ptr, out.ptr);
        assertEquals(copy.data, out.data);

        lib.rtmidi_out_free(out);

        assertNotEquals(copy.ptr, out.ptr);
        assertNotEquals(copy.data, out.data);

        // doing anything with `out` should cause a fatal error SIGSEGV (ie segfault)
    }

    @DisplayName("Out Get Current API")
    @Test
    public void testOutGetCurrentApi() {
        int[] apis = new int[RtMidiLibrary.RtMidiApi.RTMIDI_API_NUM];
        int totalApis = lib.rtmidi_get_compiled_api(apis, apis.length);
        assertTrue(totalApis > 0);

        String clientName = "Test JNARtMidi Client";

        RtMidiWrapper out = lib.rtmidi_out_create(apis[0], clientName);
        assertNotNull(out);
        assertTrue(out.ok);

        int usedApi = lib.rtmidi_out_get_current_api(out);
        assertEquals(apis[0], usedApi);
        lib.rtmidi_out_free(out);
    }

    @Disabled
    @DisplayName("Out Send Message")
    @Test
    public void testOutSendMessage() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_out_send_message(null, null, -1);
    }

}
