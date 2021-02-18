package com.github.basshelal.jnartmidi.api;

public class MidiOutDevice extends MidiDevice {

    public MidiOutDevice(String name, int number) {
        super(name, number);
    }

    @Override
    public String toString() {
        return "MidiOutDevice{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}
