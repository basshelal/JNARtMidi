package dev.basshelal.jnartmidi.lib;

import com.sun.jna.Platform;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.basshelal.jnartmidi.api.MidiMessage;
import dev.basshelal.jnartmidi.api.RtMidi;

import static dev.basshelal.jnartmidi.lib.RtMidiLibrary.RtMidiCCallback;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests all 22 of the exported native C functions from the RtMidi library found in {@link RtMidiLibrary}
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings({"unused"})
public class TestRtMidiLibraryJava {

    private static RtMidiLibrary lib;

    private static void log(String message) { System.out.println(message); }

    private static void logPorts() {
        System.out.println("\nReadable Midi Ports:");
        RtMidi.readableMidiPorts().forEach(System.out::println);
        System.out.println("\nWritable Midi Ports:");
        RtMidi.writableMidiPorts().forEach(System.out::println);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkPtrOk(RtMidiPtr ptr) {
        assertNotNull(ptr);
        assertTrue(ptr.ok);
    }

    private RtMidiInPtr inCreateDefault() {
        RtMidiInPtr in = lib.rtmidi_in_create_default();
        checkPtrOk(in);
        return in;
    }

    private RtMidiOutPtr outCreateDefault() {
        RtMidiOutPtr out = lib.rtmidi_out_create_default();
        checkPtrOk(out);
        return out;
    }

    private String inPortName() { return "Test JNARtMidi In Port at " + (new Random()).nextInt(); }

    private String outPortName() { return "Test JNARtMidi Out Port at " + (new Random()).nextInt(); }

    @BeforeAll
    public static void setup() {
        RtMidi.addLibrarySearchPath("bin/" + Platform.RESOURCE_PREFIX);
        lib = RtMidiLibrary.getInstance();
    }

    // GC or JUnit causes something to go wong when running all tests in succession, a slight wait fixes it somehow
    @BeforeEach
    public void beforeEach() { sleep(50); }

    @DisplayName("5 rtmidi_open_virtual_port")
    @Order(5)
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
        String inPortName = inPortName();

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
        String outPortName = outPortName();

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

    @DisplayName("6 rtmidi_close_port")
    @Order(6)
    @Test
    public void testClosePort() {
        RtMidiInPtr in = inCreateDefault();
        RtMidiOutPtr out = outCreateDefault();

        int inPortCount = lib.rtmidi_get_port_count(in);
        int outPortCount = lib.rtmidi_get_port_count(out);

        assertTrue(inPortCount > 0);
        assertTrue(outPortCount > 0);

        // Open an in port with a unique name!
        String inPortName = inPortName();

        lib.rtmidi_open_port(in, 0, inPortName);

        int newOutPortCount = lib.rtmidi_get_port_count(out);

        assertNotEquals(outPortCount, newOutPortCount);
        assertEquals(outPortCount + 1, newOutPortCount);

        lib.rtmidi_close_port(in);
        lib.rtmidi_in_free(in); // necessary to truly close!

        assertEquals(outPortCount, lib.rtmidi_get_port_count(out));

        lib.rtmidi_out_free(out);
    }

    @DisplayName("13 rtmidi_in_set_callback")
    @Order(13)
    @Test
    public void testInSetCallback() {
        RtMidiInPtr readable = inCreateDefault();
        RtMidiOutPtr writable = outCreateDefault();

        String writableName = outPortName();
        lib.rtmidi_open_port(writable, 0, writableName);

        String readableName = inPortName();
        lib.rtmidi_open_port(readable, lib.rtmidi_get_port_count(readable) - 1, readableName);

        byte[] sentMessage = new byte[]{(byte) MidiMessage.NOTE_ON, 69, 69};
        AtomicBoolean messageReceived = new AtomicBoolean(false);

        RtMidiCCallback callback = (timeStamp, message, messageSize, userData) -> {
            assertNotNull(message);
            assertNotNull(messageSize);
            assertEquals(sentMessage.length, messageSize.intValue());
            for (int i = 0; i < messageSize.intValue(); i++)
                assertEquals(sentMessage[i], message.getByte(i));
            messageReceived.set(true);
        };

        lib.rtmidi_in_set_callback(readable, callback, null);

        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.length);

        sleep(500); // wait a little for flag to have changed
        assertTrue(messageReceived.get());

        lib.rtmidi_in_free(readable);
        lib.rtmidi_out_free(writable);
    }

    @DisplayName("14 rtmidi_in_cancel_callback")
    @Order(14)
    @Test
    public void testInCancelCallback() {
        RtMidiInPtr readable = inCreateDefault();
        RtMidiOutPtr writable = outCreateDefault();

        String writableName = outPortName();
        lib.rtmidi_open_port(writable, 0, writableName);

        String readableName = inPortName();
        lib.rtmidi_open_port(readable, lib.rtmidi_get_port_count(readable) - 1, readableName);

        byte[] sentMessage = new byte[]{(byte) MidiMessage.NOTE_ON, 69, 69};
        AtomicBoolean messageReceived = new AtomicBoolean(false);

        RtMidiCCallback callback = (timeStamp, message, messageSize, userData) -> {
            assertNotNull(message);
            assertNotNull(messageSize);
            assertEquals(sentMessage.length, messageSize.intValue());
            for (int i = 0; i < messageSize.intValue(); i++)
                assertEquals(sentMessage[i], message.getByte(i));
            messageReceived.set(true);
        };

        lib.rtmidi_in_set_callback(readable, callback, null);

        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.length);

        sleep(500); // wait a little for flag to have changed
        assertTrue(messageReceived.get());

        messageReceived.set(false);

        lib.rtmidi_in_cancel_callback(readable);

        // try to send some messages, if readable received them then flag will have changed
        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.length);
        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.length);
        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.length);

        sleep(500); // wait a little for flag to have changed
        assertFalse(messageReceived.get());

        lib.rtmidi_in_free(readable);
        lib.rtmidi_out_free(writable);
    }

    @DisplayName("15 rtmidi_in_ignore_types")
    @Order(15)
    @Test
    public void testInIgnoreTypes() {

        RtMidiInPtr readable = inCreateDefault();
        RtMidiOutPtr writable = outCreateDefault();

        String writableName = outPortName();
        lib.rtmidi_open_port(writable, 0, writableName);

        String readableName = inPortName();
        lib.rtmidi_open_port(readable, lib.rtmidi_get_port_count(readable) - 1, readableName);

        AtomicBoolean ignoring = new AtomicBoolean(false);
        lib.rtmidi_in_ignore_types(readable, ignoring.get(), ignoring.get(), ignoring.get());

        byte[] sentMessage = new byte[]{(byte) MidiMessage.TIMING_CLOCK};
        AtomicBoolean messageReceived = new AtomicBoolean(false);

        RtMidiCCallback callback = (timeStamp, message, messageSize, userData) -> messageReceived.set(true);

        lib.rtmidi_in_set_callback(readable, callback, null);

        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.length);

        sleep(500); // wait a little for flag to have changed
        assertTrue(messageReceived.get());

        ignoring.set(true);
        lib.rtmidi_in_ignore_types(readable, ignoring.get(), ignoring.get(), ignoring.get());

        messageReceived.set(false);
        lib.rtmidi_out_send_message(writable, sentMessage, sentMessage.length);

        sleep(500); // wait a little for flag to have changed
        assertFalse(messageReceived.get());

        lib.rtmidi_in_free(readable);
        lib.rtmidi_out_free(writable);
    }

    @DisplayName("16 rtmidi_in_get_message")
    @Order(16)
    @Test
    public void testInGetMessage() {
        this.testOutSendMessage();
    }

    @DisplayName("21 rtmidi_out_send_message")
    @Order(21)
    @Test
    public void testOutSendMessage() {
        RtMidiInPtr in = inCreateDefault();
        RtMidiOutPtr out = outCreateDefault();

        int inPortCount = lib.rtmidi_get_port_count(in);
        int outPortCount = lib.rtmidi_get_port_count(out);

        assertTrue(inPortCount > 0);
        assertTrue(outPortCount > 0);

        // Open an out port with a unique name!
        String outPortName = outPortName();

        // open the port
        lib.rtmidi_open_port(out, 0, outPortName);

        // find it on the other side and open that

        int newInPortCount = lib.rtmidi_get_port_count(in);

        assertNotEquals(inPortCount, newInPortCount);
        assertEquals(inPortCount + 1, newInPortCount);

        // in ports should contain our newly created out port

        int inPortIndex = newInPortCount - 1;

        String inPortName = inPortName();
        lib.rtmidi_open_port(in, inPortIndex, inPortName);

        // send the out message

        byte[] message = new byte[]{(byte) MidiMessage.NOTE_ON, 69, 69};

        int sent = lib.rtmidi_out_send_message(out, message, message.length);

        assertTrue(sent != -1);

        // get the in message and assert they are equal

        byte[] receivedMessage = new byte[]{-1, -1, -1};

        double got = lib.rtmidi_in_get_message(in, ByteBuffer.wrap(receivedMessage),
                new RtMidiLibrary.NativeSizeByReference(receivedMessage.length));

        assertTrue(got != -1);

        assertArrayEquals(message, receivedMessage);

        lib.rtmidi_in_free(in);
        lib.rtmidi_out_free(out);
    }

}
