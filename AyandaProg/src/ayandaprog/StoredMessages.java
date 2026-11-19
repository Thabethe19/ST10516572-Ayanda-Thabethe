package ayandaprog;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StoredMessages {

    private static final String JSON_FILE = "messages.json";

    // Instance variables used for unit testing
    private final ArrayList<String> ids = new ArrayList<>();
    private final ArrayList<String> senders = new ArrayList<>();
    private final ArrayList<String> recipients = new ArrayList<>();
    private final ArrayList<String> texts = new ArrayList<>();
    private final ArrayList<String> hashes = new ArrayList<>();

    // Constructor
    public StoredMessages(String[] messageIDs,
                          String[] messageSenders,
                          String[] messageRecipients,
                          String[] messageTexts,
                          String[] messageHashes,
                          int count) {

        if (messageIDs == null || messageSenders == null
                || messageRecipients == null || messageTexts == null
                || messageHashes == null) {
            throw new IllegalArgumentException("Message arrays cannot be null.");
        }

        int total = Math.max(0, Math.min(count, Math.min(
                messageIDs.length,
                Math.min(messageSenders.length,
                Math.min(messageRecipients.length,
                Math.min(messageTexts.length, messageHashes.length))))));

        for (int i = 0; i < total; i++) {
            ids.add(messageIDs[i]);
            senders.add(messageSenders[i]);
            recipients.add(messageRecipients[i]);
            texts.add(messageTexts[i]);
            hashes.add(messageHashes[i]);
        }
    }

    // The one shared list of stored messages (owned by Messages)
    private static ArrayList<Messages.Message> stored() {
        return Messages.getStoredMessages();
    }

    // Sent + stored messages (a message is only ever in one of these lists)
    private static ArrayList<Messages.Message> allMessages() {

        ArrayList<Messages.Message> all = new ArrayList<>();

        if (Messages.getSentMessages() != null) {
            all.addAll(Messages.getSentMessages());
        }

        all.addAll(stored());

        return all;
    }

    private static String getString(JSONObject obj, String key) {
        Object value = obj.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    // Add a stored message and save the full list to JSON
    public static void addStoredMessage(Messages.Message msg) {

        if (msg == null) {
            System.out.println("Message cannot be empty.");
            return;
        }

        stored().add(msg);

        saveToJSON();

        System.out.println("Message successfully stored.");
    }

    // Load stored messages from JSON
    public static void loadFromJSON() {

        stored().clear();

        File file = new File(JSON_FILE);

        if (!file.exists()) {
            System.out.println("No existing stored messages found.");
            return;
        }

        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(file)) {

            Object result = parser.parse(reader);

            if (!(result instanceof JSONArray)) {
                System.out.println("Invalid messages file format.");
                return;
            }

            JSONArray messages = (JSONArray) result;

            for (Object object : messages) {

                if (!(object instanceof JSONObject)) {
                    continue;
                }

                JSONObject jsonMessage = (JSONObject) object;

                Object numberObject = jsonMessage.get("messageNumber");

                if (!(numberObject instanceof Number)) {
                    continue;
                }

                int number = ((Number) numberObject).intValue();

                Messages.Message msg = new Messages.Message(
                        getString(jsonMessage, "messageID"),
                        number,
                        getString(jsonMessage, "recipient"),
                        getString(jsonMessage, "message"),
                        getString(jsonMessage, "messageHash"),
                        getString(jsonMessage, "status")
                );

                stored().add(msg);

                Messages.setMessageCounterAtLeast(number);
            }

            System.out.println("Stored messages loaded successfully.");

        } catch (IOException | ParseException e) {

            System.out.println("Error loading messages: " + e.getMessage());
        }
    }

    // Save the stored messages to JSON
    @SuppressWarnings("unchecked")
    public static void saveToJSON() {

        JSONArray messages = new JSONArray();

        for (Messages.Message msg : stored()) {

            JSONObject jsonMessage = new JSONObject();

            jsonMessage.put("messageID", msg.getMessageID());
            jsonMessage.put("messageNumber", msg.getMessageNumber());
            jsonMessage.put("messageHash", msg.getMessageHash());
            jsonMessage.put("recipient", msg.getRecipient());
            jsonMessage.put("message", msg.getMessage());
            jsonMessage.put("status", msg.getMessageStatus());

            messages.add(jsonMessage);
        }

        try (FileWriter writer = new FileWriter(JSON_FILE)) {

            writer.write(messages.toJSONString());

            System.out.println("Messages saved successfully.");

        } catch (IOException e) {

            System.out.println("Error saving messages: " + e.getMessage());
        }
    }

    // Display stored messages menu
    public static void displayStoredMessagesMenu(Scanner input) {

        if (input == null) {
            return;
        }

        int option = 0;

        while (option != 7) {

            System.out.println("\n==============================");
            System.out.println("    STORED MESSAGE OPTIONS");
            System.out.println("==============================");
            System.out.println("1. Display all stored messages");
            System.out.println("2. Search message by ID");
            System.out.println("3. Search messages by recipient");
            System.out.println("4. Display longest message");
            System.out.println("5. Delete message by hash");
            System.out.println("6. Display full message report");
            System.out.println("7. Return to main menu");
            System.out.print("Enter your option: ");

            try {
                option = Integer.parseInt(input.nextLine().trim());

            } catch (NumberFormatException e) {

                System.out.println("Enter a valid number.");
                continue;

            } catch (NoSuchElementException e) {

                System.out.println("No input available. Returning to main menu.");
                return;
            }

            switch (option) {

                case 1:
                    displayAllStoredMessages();
                    break;

                case 2:
                    searchMessageByID(input);
                    break;

                case 3:
                    searchMessagesByRecipient(input);
                    break;

                case 4:
                    displayLongestMessage();
                    break;

                case 5:
                    deleteStoredMessage(input);
                    break;

                case 6:
                    displayFullReport();
                    break;

                case 7:
                    System.out.println("Returning to main menu.");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Option 1: Display all stored messages
    public static void displayAllStoredMessages() {

        if (stored().isEmpty()) {

            System.out.println("No stored messages available.");
            return;
        }

        for (Messages.Message msg : stored()) {

            Messages.displayMessage(msg);
            System.out.println("-------------------------");
        }
    }

    // Option 2: Search message by ID
    public static void searchMessageByID(Scanner input) {

        System.out.print("Enter message ID: ");

        String id = input.nextLine().trim();

        for (Messages.Message msg : allMessages()) {

            if (Objects.equals(msg.getMessageID(), id)) {

                Messages.displayMessage(msg);
                return;
            }
        }

        System.out.println("Message not found.");
    }

    // Option 3: Search messages by recipient
    public static void searchMessagesByRecipient(Scanner input) {

        System.out.print("Enter recipient cellphone number: ");

        String recipient = input.nextLine().trim();

        boolean found = false;

        for (Messages.Message msg : allMessages()) {

            if (Objects.equals(msg.getRecipient(), recipient)) {

                Messages.displayMessage(msg);
                System.out.println("-------------------------");
                found = true;
            }
        }

        if (!found) {

            System.out.println("No messages found for this recipient.");
        }
    }

    // Option 4: Display longest message
    public static void displayLongestMessage() {

        ArrayList<Messages.Message> all = allMessages();

        if (all.isEmpty()) {

            System.out.println("No messages available.");
            return;
        }

        Messages.Message longest = null;
        int longestLength = -1;

        for (Messages.Message msg : all) {

            int length = msg.getMessage() == null
                    ? 0 : msg.getMessage().length();

            if (length > longestLength) {
                longest = msg;
                longestLength = length;
            }
        }

        System.out.println("\nLongest message:");

        Messages.displayMessage(longest);
    }

    // Option 5: Delete stored message using its hash
    public static void deleteStoredMessage(Scanner input) {

        System.out.print("Enter the message hash to delete: ");

        String hash = input.nextLine().trim();

        ArrayList<Messages.Message> list = stored();

        for (int i = 0; i < list.size(); i++) {

            Messages.Message msg = list.get(i);

            if (Objects.equals(msg.getMessageHash(), hash)) {

                list.remove(i);

                saveToJSON();

                System.out.println("Message: \""
                        + msg.getMessage()
                        + "\" successfully deleted.");

                return;
            }
        }

        System.out.println("Message hash not found.");
    }

    // Option 6: Display full message report
    public static void displayFullReport() {

        ArrayList<Messages.Message> all = allMessages();

        if (all.isEmpty()) {

            System.out.println("No messages available.");
            return;
        }

        System.out.println("\n========== FULL MESSAGE REPORT ==========");

        for (Messages.Message msg : all) {

            System.out.println("\nSender: You");
            System.out.println("Recipient: " + msg.getRecipient());
            System.out.println("Message: " + msg.getMessage());
            System.out.println("Message Hash: " + msg.getMessageHash());
            System.out.println("Message ID: " + msg.getMessageID());
            System.out.println("Status: " + msg.getMessageStatus());
        }

        System.out.println("\nTotal messages: " + all.size());
    }

    // ---------- Instance methods (used for unit testing) ----------

    // Print one message from the testing arrays
    private void printEntry(int i) {

        System.out.println("Message ID: " + ids.get(i));
        System.out.println("Message Hash: " + hashes.get(i));
        System.out.println("Sender: " + senders.get(i));
        System.out.println("Recipient: " + recipients.get(i));
        System.out.println("Message: " + texts.get(i));
    }

    // Display stored senders and recipients
    public void displayStoredSendersAndRecipients() {

        for (int i = 0; i < texts.size(); i++) {

            System.out.println("\n--- Stored Message " + (i + 1) + " ---");

            System.out.println("Sender: " + senders.get(i));
            System.out.println("Recipient: " + recipients.get(i));
            System.out.println("Message: " + texts.get(i));
        }
    }

    // Find the longest stored message
    public String getLongestStoredMessage() {

        if (texts.isEmpty()) {

            System.out.println("No stored messages available.");
            return null;
        }

        int longest = 0;

        for (int i = 1; i < texts.size(); i++) {

            String current = texts.get(i);
            String best = texts.get(longest);

            if (current != null && (best == null || current.length() > best.length())) {

                longest = i;
            }
        }

        System.out.println("\nLongest Stored Message:");

        printEntry(longest);

        return texts.get(longest);
    }

    // Search by message ID
    public void searchByMessageID(Scanner input) {

        System.out.print("Enter message ID: ");

        String id = input.nextLine().trim();

        for (int i = 0; i < ids.size(); i++) {

            if (Objects.equals(ids.get(i), id)) {

                System.out.println("Message found!");

                printEntry(i);

                return;
            }
        }

        System.out.println("Message ID not found.");
    }

    // Search messages by recipient
    public void searchByRecipient(Scanner input) {

        System.out.print("Enter recipient cellphone number: ");

        String recipient = input.nextLine().trim();

        boolean found = false;

        for (int i = 0; i < recipients.size(); i++) {

            if (Objects.equals(recipients.get(i), recipient)) {

                printEntry(i);

                found = true;
            }
        }

        if (!found) {

            System.out.println("No messages found for this recipient.");
        }
    }

    // Delete a message using its hash
    public void deleteMessageByHash(Scanner input) {

        System.out.print("Enter the message hash to delete: ");

        String hash = input.nextLine().trim();

        for (int i = 0; i < hashes.size(); i++) {

            if (Objects.equals(hashes.get(i), hash)) {

                System.out.println("Message found!");

                System.out.println("Message: " + texts.get(i));

                ids.remove(i);
                senders.remove(i);
                recipients.remove(i);
                texts.remove(i);
                hashes.remove(i);

                System.out.println("Message successfully deleted!");

                return;
            }
        }

        System.out.println("Message hash not found.");
    }

    // Display full message report
    public void displayFullMessageReport() {

        System.out.println("\n========== FULL MESSAGE REPORT ==========");

        for (int i = 0; i < texts.size(); i++) {

            System.out.println();

            printEntry(i);
        }

        System.out.println("\nTotal Stored Messages: " + texts.size());
    }

    // Get all stored messages
    public static ArrayList<Messages.Message> getStoredMessages() {

        return stored();
    }
}
