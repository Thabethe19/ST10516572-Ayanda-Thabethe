

package ayandaprog;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StoredMessagesTest {

    private StoredMessages storedMessages;

    private final String[] messageID = {
        "0000000001",
        "0000000002",
        "0000000003",
        "0838884567",
        "0000000005"
    };

    private final String[] messageSender = {
        "Developer", "Developer", "Developer",
        "Developer", "Developer"
    };

    private final String[] messageRecipient = {
        "+27834557896",
        "+27838884567",
        "+27834484567",
        "0838884567",
        "+27838884567"
    };

    private final String[] messageText = {
        "Did you get the cake?",
        "Where are you? You are late! I have asked you to be on time.",
        "Yohhooo, I am at your gate.",
        "It is dinner time !",
        "Ok, I am leaving without you."
    };

    private final String[] messageHash = {
        "01:1:DIDCAKE",
        "02:2:WHEREONTIME",
        "03:3:YOHATGATE",
        "08:4:ITTIME",
        "05:5:OKWITHOUTYOU"
    };

    // SET UP TEST DATA

    @Before
    public void setUp() {

        storedMessages = new StoredMessages(
                messageID.clone(),
                messageSender.clone(),
                messageRecipient.clone(),
                messageText.clone(),
                messageHash.clone(),
                5
        );
    }

    // CAPTURE OUTPUT

    private String captureOutput(Runnable action) {

        PrintStream originalOut = System.out;

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream testOut = new PrintStream(output);

        try {
            System.setOut(testOut);
            action.run();

        } finally {
            System.setOut(originalOut);
            testOut.close();
        }

        return output.toString();
    }

    // TEST 1: MESSAGES ARRAY CORRECTLY POPULATED

    @Test
    public void testMessagesArrayCorrectlyPopulated() {

        String output = captureOutput(() -> {
            storedMessages.displayStoredSendersAndRecipients();
        });

        assertTrue(output.contains("Did you get the cake?"));

        assertTrue(output.contains(
                "Where are you? You are late! I have asked you to be on time."
        ));

        assertTrue(output.contains(
                "Yohhooo, I am at your gate."
        ));

        assertTrue(output.contains("It is dinner time !"));

        assertTrue(output.contains(
                "Ok, I am leaving without you."
        ));

        assertTrue(output.contains("Developer"));

        assertTrue(output.contains("+27834557896"));
    }

    // TEST 2: DISPLAY THE LONGEST MESSAGE

    @Test
    public void testDisplayLongestMessage() {

        String output = captureOutput(() -> {
            storedMessages.getLongestStoredMessage();
        });

        assertTrue(output.contains("Longest Stored Message"));

        assertTrue(output.contains(
                "Where are you? You are late! I have asked you to be on time."
        ));
    }

    // TEST 3: SEARCH MESSAGE USING MESSAGE ID

    @Test
    public void testSearchByMessageID() {

        String output = captureOutput(() -> {

            try (Scanner input = new Scanner("0838884567")) {
                storedMessages.searchByMessageID(input);
            }
        });

        assertTrue(output.contains("Message found!"));

        assertTrue(output.contains("0838884567"));

        assertTrue(output.contains("It is dinner time !"));
    }

    // TEST 4: SEARCH ALL MESSAGES USING RECIPIENT

    @Test
    public void testSearchAllMessagesByRecipient() {

        String output = captureOutput(() -> {

            try (Scanner input = new Scanner("+27838884567")) {
                storedMessages.searchByRecipient(input);
            }
        });

        assertTrue(output.contains(
                "Where are you? You are late! I have asked you to be on time."
        ));

        assertTrue(output.contains(
                "Ok, I am leaving without you."
        ));

        assertTrue(output.contains("0000000002"));

        assertTrue(output.contains("0000000005"));
    }

    // TEST 5: DELETE MESSAGE USING MESSAGE HASH

    @Test
    public void testDeleteMessageUsingMessageHash() {

        String output = captureOutput(() -> {

            try (Scanner input = new Scanner("02:2:WHEREONTIME")) {
                storedMessages.deleteMessageByHash(input);
            }
        });

        assertTrue(output.contains("Message found!"));

        assertTrue(output.contains(
                "Where are you? You are late! I have asked you to be on time."
        ));

        assertTrue(output.contains(
                "Message successfully deleted!"
        ));

        // Check that the deleted message no longer exists

        String report = captureOutput(() -> {
            storedMessages.displayFullMessageReport();
        });

        assertFalse(report.contains(
                "Where are you? You are late! I have asked you to be on time."
        ));

        assertTrue(report.contains("Total Stored Messages: 4"));
    }

    // TEST 6: DISPLAY A FULL REPORT

    @Test
    public void testDisplayReport() {

        String output = captureOutput(() -> {
            storedMessages.displayFullMessageReport();
        });

        // Check message hashes

        assertTrue(output.contains("01:1:DIDCAKE"));
        assertTrue(output.contains("02:2:WHEREONTIME"));
        assertTrue(output.contains("03:3:YOHATGATE"));

        // Check recipients

        assertTrue(output.contains("+27834557896"));
        assertTrue(output.contains("+27838884567"));
        assertTrue(output.contains("+27834484567"));

        // Check messages

        assertTrue(output.contains("Did you get the cake?"));

        assertTrue(output.contains(
                "Where are you? You are late! I have asked you to be on time."
        ));

        assertTrue(output.contains(
                "Yohhooo, I am at your gate."
        ));

        assertTrue(output.contains("It is dinner time !"));

        assertTrue(output.contains(
                "Ok, I am leaving without you."
        ));
    }
}
