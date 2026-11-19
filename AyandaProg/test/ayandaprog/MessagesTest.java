
package ayandaprog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessagesTest {

    private ByteArrayOutputStream output;
    private PrintStream originalOutput;

    @Before
    public void setUp() throws Exception {

        // Save the original console output
        originalOutput = System.out;

        // Capture output for testing
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        // Clear sent messages
        Messages.getSentMessages().clear();

        // Clear stored messages
        Messages.getStoredMessages().clear();

        // Clear disregarded messages
        Field disregarded = Messages.class.getDeclaredField(
                "disregardedMessages");

        disregarded.setAccessible(true);

        ((ArrayList<?>) disregarded.get(null)).clear();

        // Reset message counter
        Field counter = Messages.class.getDeclaredField(
                "messageCounter");

        counter.setAccessible(true);
        counter.setInt(null, 0);

        // Delete old JSON file before testing
        File file = new File("messages.json");

        if (file.exists()) {
            file.delete();
        }
    }

    @After
    public void tearDown() {

        // Restore original console output
        System.setOut(originalOutput);

        // Delete JSON test file
        File file = new File("messages.json");

        if (file.exists()) {
            file.delete();
        }
    }

    private String getOutput() {
        return output.toString();
    }

    private Scanner scannerOf(String... lines) {
        return new Scanner(String.join(
                System.lineSeparator(), lines));
    }

    @Test
    public void testMessageConstructorAndGetters() {

        Messages.Message message = new Messages.Message(
                "1234567890",
                1,
                "+27821234567",
                "Hello there",
                "12:1:HELLOTHERE",
                "Sent"
        );

        assertEquals("1234567890", message.getMessageID());
        assertEquals(1, message.getMessageNumber());
        assertEquals("+27821234567", message.getRecipient());
        assertEquals("Hello there", message.getMessage());
        assertEquals("12:1:HELLOTHERE", message.getMessageHash());
        assertEquals("Sent", message.getMessageStatus());
    }

    @Test
    public void testValidRecipient() {
        assertTrue(Messages.isValidRecipient("+27821234567"));
    }

    @Test
    public void testInvalidRecipientWithoutCountryCode() {
        assertFalse(Messages.isValidRecipient("0821234567"));
    }

    @Test
    public void testInvalidRecipientWithLetters() {
        assertFalse(Messages.isValidRecipient("+27821ABC567"));
    }

    @Test
    public void testInvalidRecipientNull() {
        assertFalse(Messages.isValidRecipient(null));
    }

    @Test
    public void testGenerateMessageID() {

        String id = Messages.generateMessageID();

        assertEquals(10, id.length());
        assertTrue(id.matches("[0-9]{10}"));
    }

    @Test
    public void testGenerateUniqueMessageIDs() {

        String firstID = Messages.generateMessageID();
        String secondID = Messages.generateMessageID();

        assertNotEquals(firstID, secondID);
    }

    @Test
    public void testGenerateMessageHash() {

        String hash = Messages.generateMessageHash(
                "1234567890", 1, "Hello there");

        assertEquals("12:1:HELLOTHERE", hash);
    }

    @Test
    public void testGenerateHashWithOneWord() {

        String hash = Messages.generateMessageHash(
                "1234567890", 1, "Hello");

        assertEquals("12:1:HELLO", hash);
    }

    @Test
    public void testGenerateHashWithExtraSpaces() {

        String hash = Messages.generateMessageHash(
                "1234567890", 1, "  Hello   there  ");

        assertEquals("12:1:HELLOTHERE", hash);
    }

    @Test
    public void testEscapeJSON() {

        String result = Messages.escapeJSON(
                "Hello \"World\"");

        assertEquals("Hello \\\"World\\\"", result);
    }

    @Test
    public void testEscapeJSONNull() {
        assertEquals("", Messages.escapeJSON(null));
    }

    @Test
    public void testDisplayMessage() {

        Messages.Message message = new Messages.Message(
                "1234567890",
                1,
                "+27821234567",
                "Hello",
                "12:1:HELLO",
                "Sent"
        );

        Messages.displayMessage(message);

        assertTrue(getOutput().contains("Message ID: 1234567890"));
        assertTrue(getOutput().contains("Recipient: +27821234567"));
        assertTrue(getOutput().contains("Message: Hello"));
        assertTrue(getOutput().contains("Status: Sent"));
    }

    @Test
    public void testDisplayNullMessage() {

        Messages.displayMessage(null);

        assertTrue(getOutput().contains("No message to display."));
    }

    @Test
    public void testDisplaySentMessagesWhenEmpty() {

        Messages.displaySentMessages();

        assertTrue(getOutput().contains(
                "No sent messages available."));
    }

    @Test
    public void testSendMessageSuccessfully() {

        Messages.sendMessage(scannerOf(
                "+27821234567",
                "Hello there",
                "1"
        ));

        assertEquals(1, Messages.getSentMessages().size());

        assertTrue(getOutput().contains(
                "Message successfully sent."));
    }

    @Test
    public void testSendMessageDisregarded() {

        Messages.sendMessage(scannerOf(
                "+27821234567",
                "Hello there",
                "0"
        ));

        assertEquals(0, Messages.getSentMessages().size());

        assertTrue(getOutput().contains(
                "Message disregarded."));
    }

    @Test
    public void testSendMessageStored() {

        Messages.sendMessage(scannerOf(
                "+27821234567",
                "Send me later",
                "2"
        ));

        assertEquals(1, Messages.getStoredMessages().size());

        assertTrue(getOutput().contains(
                "Message successfully stored."));
    }

    @Test
    public void testSendMessageInvalidRecipient() {

        Messages.sendMessage(scannerOf(
                "0821234567"
        ));

        assertEquals(0, Messages.getSentMessages().size());

        assertTrue(getOutput().contains(
                "Invalid recipient cellphone number."));
    }

    @Test
    public void testSendMessageEmptyText() {

        Messages.sendMessage(scannerOf(
                "+27821234567",
                ""
        ));

        assertEquals(0, Messages.getSentMessages().size());

        assertTrue(getOutput().contains(
                "Message must contain 1 to 249 characters."));
    }

    @Test
    public void testSendMessageTooLong() {

        String longMessage = "A".repeat(250);

        Messages.sendMessage(scannerOf(
                "+27821234567",
                longMessage
        ));

        assertEquals(0, Messages.getSentMessages().size());

        assertTrue(getOutput().contains(
                "Message must contain 1 to 249 characters."));
    }

    @Test
    public void testSendMessageMaximumValidLength() {

        String message = "A".repeat(249);

        Messages.sendMessage(scannerOf(
                "+27821234567",
                message,
                "1"
        ));

        assertEquals(1, Messages.getSentMessages().size());
    }

    @Test
    public void testSendMessageInvalidAction() {

        Messages.sendMessage(scannerOf(
                "+27821234567",
                "Hello",
                "9"
        ));

        assertEquals(0, Messages.getSentMessages().size());

        assertTrue(getOutput().contains(
                "Invalid choice. Message was not processed."));
    }

    @Test
    public void testMessageCounterIncreases() {

        Messages.sendMessage(scannerOf(
                "+27821234567",
                "First message",
                "1"
        ));

        Messages.sendMessage(scannerOf(
                "+27821234567",
                "Second message",
                "1"
        ));

        assertEquals(2, Messages.getSentMessages().size());

        assertEquals(
                1,
                Messages.getSentMessages().get(0).getMessageNumber());

        assertEquals(
                2,
                Messages.getSentMessages().get(1).getMessageNumber());
    }

    @Test
    public void testSetMessageCounterAtLeast() {

        Messages.setMessageCounterAtLeast(5);

        Messages.sendMessage(scannerOf(
                "+27821234567",
                "Hello",
                "1"
        ));

        assertEquals(
                6,
                Messages.getSentMessages().get(0).getMessageNumber());
    }

    @Test
    public void testStoreMessages() {

        Messages.Message message = new Messages.Message(
                "1234567890",
                1,
                "+27821234567",
                "Hello",
                "12:1:HELLO",
                "Stored"
        );

        Messages.getStoredMessages().add(message);

        Messages.storeMessages();

        assertTrue(new File("messages.json").exists());
    }

    @Test
    public void testStartQuit() {

        Messages.start(scannerOf("4"));

        assertTrue(getOutput().contains("Welcome to QuickChat."));
        assertTrue(getOutput().contains(
                "Thank you for using QuickChat."));
    }

    @Test
    public void testStartInvalidOption() {

        Messages.start(scannerOf("abc", "9", "4"));

        assertTrue(getOutput().contains(
                "Invalid option. Please try again."));
    }

    @Test
    public void testStartShowSentMessagesWhenEmpty() {

        Messages.start(scannerOf("2", "4"));

        assertTrue(getOutput().contains(
                "No sent messages available."));
    }

    @Test
    public void testStartStoredMessageMenu() {

        Messages.start(scannerOf("3", "7", "4"));

        assertTrue(getOutput().contains(
                "STORED MESSAGE OPTIONS"));

        assertTrue(getOutput().contains(
                "Returning to main menu."));
    }
}
