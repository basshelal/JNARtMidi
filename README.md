# JNARtMidi

Work In Progress

Java bindings to [RtMidi](https://github.com/thestk/rtmidi)
using [Java Native Access](https://github.com/java-native-access/jna)
written in [Koltin JVM](https://kotlinlang.org/).

Loosely inspired by [JNAJack](https://github.com/jaudiolibs/jnajack) (hence the name).

## Installation

<!-- TODO add dependency notation here -->

## Getting Started

<!-- TODO add reference to example repo here -->

Before using anything, ensure that you point JNARtMidi to the location(s) of your built libraries of RtMidi:

```
RtMidi.addLibrarySearchPath("bin/my-libraries-path");
```

This library **does not** provide built versions of RtMidi, you must already have them before using this library.

Read the documentation of [`RtMidi.addLibrarySearchPath`](src/main/kotlin/dev/basshelal/jnartmidi/api/RtMidi.kt)
for more details regarding adding the library search path as this can be somewhat of a pain point at first.

A [`ReadableMidiPort`](src/main/kotlin/dev/basshelal/jnartmidi/api/ReadableMidiPort.kt)
is one that you as a programmer can read messages from, similarly a
[`WritableMidiPort`](src/main/kotlin/dev/basshelal/jnartmidi/api/WritableMidiPort.kt)
is one that you as a programmer can send messages to.

You can query all readable and writable MIDI ports in the system by using the
[`RtMidi`](src/main/kotlin/dev/basshelal/jnartmidi/api/RtMidi.kt) class's static functions:

```
List<MidiPort.Info> readablePorts = RtMidi.readableMidiPorts();
List<MidiPort.Info> writablePorts = RtMidi.writableMidiPorts();
```

These return `MidiPort.Info`s which are then used to create a port such as:

```
ReadableMidiPort readablePort = new ReadableMidiPort(readablePorts.get(0));
WritableMidiPort writablePort = new WritableMidiPort(writablePorts.get(0));
```

You can then `open()` a port to be ready to send or receive messages:

```
readablePort.open(/*portName=*/"My Readable Port");
readablePort.setCallback((MidiMessage midiMessage, double deltaTime) -> {
    // Your callback code here, note this code is real time critical!
});
```

Once you are done with a port, be sure to call `destroy()`, after which you will only be able to query the port

```
readablePort.destroy(); // closes and destroys
readablePort.getInfo(); // ok

readablePort.isDestroyed(); // ok
readablePort.open(/*portName=*/"My Readable Port"); // ERROR: will throw RtMidiPortException!
readablePort.destroy(); // ok, will not do anything if already destroyed
```

JNARtMidi's documentation is extensive and thorough and it is recommended to read through it to understand the
capabilities of the library.

## License

```
MIT License

Copyright (c) 2021 Bassam Helal

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```