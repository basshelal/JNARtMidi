package dev.basshelal.jnartmidi.lib

import dev.basshelal.jnartmidi.lib.jnr.RtMidiLibraryJNR
import dev.basshelal.jnartmidi.log
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class TestRtMidiLibraryJNR {

    companion object {

        @BeforeAll
        @JvmStatic
        fun `Before All`() {
        }

        @AfterAll
        @JvmStatic
        fun `After All`() = Unit
    }

    @Test
    fun `Test`() {
        RtMidiLibraryJNR.library.rtmidi_api_name(3).log()
        RtMidiLibraryJNR.library.rtmidi_api_display_name(3).log()
        val inPtr = RtMidiLibraryJNR.library.rtmidi_in_create_default()

        inPtr.log()
        inPtr.ptr.log()
        if (inPtr.data.get() != null) inPtr.data.log()
        inPtr.ok.log()
        inPtr.msg.log()

        val count = RtMidiLibraryJNR.library.rtmidi_get_port_count(inPtr)

        count.log()

        val arr = IntArray(6)

        val api = RtMidiLibraryJNR.library.rtmidi_get_compiled_api(arr, arr.size)

        api.log()
        arr.joinToString().log()
    }
}