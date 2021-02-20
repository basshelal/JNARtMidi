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

    @Disabled
    @DisplayName("Get Port Count")
    @Test
    public void testGetPortCount() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_get_port_count(null);
    }

    @Disabled
    @DisplayName("Get Port Name")
    @Test
    public void testGetPortName() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_get_port_name(null, -1);
    }

    @Disabled
    @DisplayName("In Create Default")
    @Test
    public void testInCreateDefault() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_create_default();
    }

    @Disabled
    @DisplayName("In Create")
    @Test
    public void testInCreate() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_create(-1, "", -1);
    }

    @Disabled
    @DisplayName("In Free")
    @Test
    public void testInFree() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_free(null);
    }

    @Disabled
    @DisplayName("In Get Current API")
    @Test
    public void testInGetCurrentApi() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_get_current_api(null);
    }

    @Disabled
    @DisplayName("In Set Callback")
    @Test
    public void testInSetCallback() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_in_set_callback(null, null, null);
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

    @Disabled
    @DisplayName("Out Create Default")
    @Test
    public void testOutCreateDefault() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_out_create_default();
    }

    @Disabled
    @DisplayName("Out Create")
    @Test
    public void testOutCreate() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_out_create(-1, "");
    }

    @Disabled
    @DisplayName("Out Free")
    @Test
    public void testOutFree() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_out_free(null);
    }

    @Disabled
    @DisplayName("Out Get Current API")
    @Test
    public void testOutGetCurrentApi() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_out_get_current_api(null);
    }

    @Disabled
    @DisplayName("Out Send Message")
    @Test
    public void testOutSendMessage() {
        // TODO: 20/02/2021 Implement
        lib.rtmidi_out_send_message(null, null, -1);
    }

}
