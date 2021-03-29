package dev.basshelal.jrtmidi.lib

import dev.basshelal.jrtmidi.api.RtMidiNativeException

internal object LibraryWrapper {

    internal fun compiledApis(): List<RtMidiApi> {
        val arr = IntArray(RtMidiApis.RTMIDI_API_NUM)
        return library.rtmidi_get_compiled_api(arr, arr.size).let { size ->
            if (size < 0) throw RtMidiNativeException("Error trying to get compiled apis") else List(size) { arr[it] }
        }
    }
}