package com.github.basshelal.jnartmidi.api;

public class MidiInDevice extends MidiDevice {

    public MidiInDevice(String name, int number) {
        super(name, number);
    }

    @Override
    public String toString() {
        return "MidiInDevice{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}
