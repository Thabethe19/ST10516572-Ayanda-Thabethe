package ayandaprog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Messages (with test data).
 *
 * @author Student
 */
public class MessagesTest {

    private static final String VALID_NUMBER = "+27821234567";

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @Before
    public void setUp() throws Exception {
        // Capture console output so it can be asserted on
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Reset the static state so tests don't affect each other
        clearList("sentMessages");
        clearList("disregardedMessages");
        clearList("storedMessages");

        Field counter = Messages.class.getDeclaredField("messageCounter");
        counter.setAccessible(true);
        counter.setInt(null, 0);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        new File("messages.json").delete();
    }

    // ---------- helpers ----------

    private void clearList(String fieldName) throws Exception {
        Field f = Messages.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        ((ArrayList<?>) f.get(null)).clear();
    }

    private String output() {
        return outContent.toString();
    }

    private Scanner scannerOf(String... lines) {
        return new Scanner(String.join("\n", lines) + "\n");
    }

    // ---------- isValidRecipient ----------

    @Test
    public void testIsValidRecipient_validNumbers() {
        assertTrue(Messages.isValidRecipient("+27821234567"));
        assertTrue(Messages.isValidRecipient("+27838884567"));
        assertTrue(Messages.isValidRecipient("+12025550123"));
        assertTrue(Messages.isValidRecipient("+12345678"));          // 8 digits (minimum)
        assertTrue(Messages.isValidRecipient("+123456789012345"));   // 15 digits (maximum)
    }

    @Test
    public void testIsValidRecipient_invalidNumbers() {
        assertFalse(Messages.isValidRecipient(""));
        assertFalse(Messages.isValidRecipient("0821234567"));        // no + prefix
        assertFalse(Messages.isValidRecipient("27821234567"));       // no + prefix
        assertFalse(Messages.isValidRecipient("+27"));               // too short
        assertFalse(Messages.isValidRecipient("+1234567"));          // 7 digits, too short
        assertFalse(Messages.isValidRecipient("+1234567890123456")); // 16 digits, too long
        assertFalse(Messages.isValidRecipient("+0821234567"));       // starts with 0
        assertFalse(Messages.isValidRecipient("+2782abc4567"));      // letters
        assertFalse(Messages.isValidRecipient("+27 82 123 4567"));   // spaces
    }

    // ---------- generateMessageID ----------

    @Test
    public void testGenerateMessageID() {
        for (int i = 0; i < 50; i++) {
            String id = Messages.generateMessageID();
            assertEquals(10, id.length());
            assertTrue("ID should be all digits: " + id, id.matches("\\d{10}"));
            assertNotEquals('0', id.charAt(0));
        }
    }

    // ---------- generateMessageHash ----------

    @Test
    public void testGenerateMessageHash_multipleWords() {
        String hash = Messages.generateMessageHash(
                "0012345678", 0, "Hi Mike, can you join us for dinner tonight");
        assertEquals("00:0:HITONIGHT", hash);
    }

    @Test
    public void testGenerateMessageHash_twoWords() {
        String hash = Messages.generateMessageHash(
                "1234567890", 1, "Hello world");
        assertEquals("12:1:HELLOWORLD", hash);
    }

    @Test
    public void testGenerateMessageHash_singleWord() {
        String hash = Messages.generateMessageHash(
                "9876543210", 3, "Thanks");
        assertEquals("98:3:THANKSTHANKS", hash);
    }

    @Test
    public void testGenerateMessageHash_extraWhitespace() {
        String hash = Messages.generateMessageHash(
                "5566778899", 2, "   Good   morning   ");
        assertEquals("55:2:GOODMORNING", hash);
    }

    // ---------- escapeJSON ----------

    @Test
    public void testEscapeJSON_plainText() {
        assertEquals("Hello world", Messages.escapeJSON("Hello world"));
    }

    @Test
    public void testEscapeJSON_empty() {
        assertEquals("", Messages.escapeJSON(""));
    }

    @Test
    public void testEscapeJSON_quotes() {
        assertEquals("He said \\\"hi\\\"", Messages.escapeJSON("He said \"hi\""));
    }

    @Test
    public void testEscapeJSON_backslash() {
        assertEquals("C:\\\\temp", Messages.escapeJSON("C:\\temp"));
    }

    @Test
    public void testEscapeJSON_controlCharacters() {
        assertEquals("a\\nb\\rc\\td", Messages.escapeJSON("a\nb\rc\td"));
    }

    // ---------- displayMessage ----------

    @Test
    public void testDisplayMessage() {
        Messages.Message msg = new Messages.Message(
                "0012345678", 1, VALID_NUMBER,
                "Hi tonight", "00:1:HITONIGHT", "Sent");

        Messages.displayMessage(msg);

        String out = output();
        assertTrue(out.contains("Message ID: 0012345678"));
        assertTrue(out.contains("Message Number: 1"));
        assertTrue(out.contains("Message Hash: 00:1:HITONIGHT"));
        assertTrue(out.contains("Recipient: " + VALID_NUMBER));
        assertTrue(out.contains("Message: Hi tonight"));
        assertTrue(out.contains("Status: Sent"));
    }

    // ---------- displaySentMessages ----------

    @Test
    public void testDisplaySentMessages_empty() {
        Messages.displaySentMessages();
        assertTrue(output().contains("No sent messages available."));
    }

    @Test
    public void testDisplaySentMessages_afterSending() {
        Messages.sendMessage(scannerOf(VALID_NUMBER, "Hello there", "1"));
        outContent.reset();

        Messages.displaySentMessages();

        String out = output();
        assertTrue(out.contains("===== SENT MESSAGES ====="));
        assertTrue(out.contains("Message: Hello there"));
        assertTrue(out.contains("Total messages sent: 1"));
    }

    // ---------- sendMessage ----------

    @Test
    public void testSendMessage_send() {
        Messages.sendMessage(scannerOf(VALID_NUMBER, "Hello there", "1"));

        String out = output();
        assertTrue(out.contains("===== MESSAGE PREVIEW ====="));
        assertTrue(out.contains("Message successfully sent."));
        assertTrue(out.contains("Status: Sent"));
        assertTrue(out.contains(":1:HELLOTHERE"));
    }

    @Test
    public void testSendMessage_disregard() {
        Messages.sendMessage(scannerOf(VALID_NUMBER, "Forget this", "0"));

        assertTrue(output().contains("Message disregarded."));

        outContent.reset();
        Messages.displaySentMessages();
        assertTrue(output().contains("No sent messages available."));
    }

    @Test
    public void testSendMessage_store() throws Exception {
        Messages.sendMessage(scannerOf(VALID_NUMBER, "Send me later", "2"));

        assertTrue(output().contains("Stored messages saved to messages.json."));

        File file = new File("messages.json");
        assertTrue(file.exists());

        String json = new String(Files.readAllBytes(file.toPath()));
        assertTrue(json.contains("\"recipient\": \"" + VALID_NUMBER + "\""));
        assertTrue(json.contains("\"message\": \"Send me later\""));
        assertTrue(json.contains("\"status\": \"Stored\""));
        assertTrue(json.contains("\"messageNumber\": 1"));
    }

    @Test
    public void testSendMessage_invalidRecipient() {
        Messages.sendMessage(scannerOf("0821234567"));
        assertTrue(output().contains("Invalid recipient cellphone number."));
    }

    @Test
    public void testSendMessage_emptyMessage() {
        Messages.sendMessage(scannerOf(VALID_NUMBER, "   "));
        assertTrue(output().contains("Message must contain 1 to 249 characters."));
    }

    @Test
    public void testSendMessage_messageTooLong() {
        String longText = "a".repeat(250);
        Messages.sendMessage(scannerOf(VALID_NUMBER, longText));
        assertTrue(output().contains("Message must contain 1 to 249 characters."));
    }

    @Test
    public void testSendMessage_maxLengthAccepted() {
        String maxText = "a".repeat(249);
        Messages.sendMessage(scannerOf(VALID_NUMBER, maxText, "1"));
        assertTrue(output().contains("Message successfully sent."));
    }

    @Test
    public void testSendMessage_invalidAction() {
        Messages.sendMessage(scannerOf(VALID_NUMBER, "Hello there", "9"));
        assertTrue(output().contains("Invalid choice. Message was not processed."));
    }

    @Test
    public void testSendMessage_numbersIncrement() {
        Messages.sendMessage(scannerOf(VALID_NUMBER, "First message", "1"));
        Messages.sendMessage(scannerOf(VALID_NUMBER, "Second message", "1"));

        String out = output();
        assertTrue(out.contains("Message Number: 1"));
        assertTrue(out.contains("Message Number: 2"));
    }

    // ---------- storeMessages ----------

    @Test
    public void testStoreMessages_noMessages() throws Exception {
        Messages.storeMessages();

        String json = new String(Files.readAllBytes(new File("messages.json").toPath()));
        assertEquals("[\n]\n", json);
    }

    // ---------- start (menu) ----------

    @Test
    public void testStart_quit() {
        Messages.start(scannerOf("3"));

        String out = output();
        assertTrue(out.contains("Welcome to QuickChat."));
        assertTrue(out.contains("Thank you for using QuickChat."));
    }

    @Test
    public void testStart_invalidOption() {
        Messages.start(scannerOf("abc", "7", "3"));

        String out = output();
        assertTrue(out.contains("Invalid option. Please try again."));
        assertTrue(out.contains("Thank you for using QuickChat."));
    }

    @Test
    public void testStart_showSentMessagesWhenEmpty() {
        Messages.start(scannerOf("2", "3"));
        assertTrue(output().contains("No sent messages available."));
    }

    @Test
    public void testStart_sendThenShow() {
        Messages.start(scannerOf(
                "1", VALID_NUMBER, "Hello there", "1",
                "2",
                "3"));

        String out = output();
        assertTrue(out.contains("Message successfully sent."));
        assertTrue(out.contains("Total messages sent: 1"));
    }
}