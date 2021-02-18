package com.github.basshelal.jnartmidi.api;

public abstract class MidiDevice {

    protected final String name;
    protected final int number;

    public MidiDevice(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() { return name; }

    public int getNumber() { return number; }

}
