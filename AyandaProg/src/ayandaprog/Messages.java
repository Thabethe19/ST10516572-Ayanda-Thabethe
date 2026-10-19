
package ayandaprog;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Messages {

    private static final Random random = new Random();

    private static final ArrayList<Message> sentMessages =
            new ArrayList<>();

    private static final ArrayList<Message> disregardedMessages =
            new ArrayList<>();

    private static final ArrayList<Message> storedMessages =
            new ArrayList<>();

    private static int messageCounter = 0;

    // Open the Messages menu after login
    public static void start(Scanner input) {

        int option = 0;

        System.out.println("\nWelcome to QuickChat.");

        while (option != 3) {

            System.out.println("\n===== QUICKCHAT MENU =====");
            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Quit");
            System.out.print("Enter your option: ");

            String choice = input.nextLine();

            try {
                option = Integer.parseInt(choice);
            } catch (NumberFormatException e) {
                option = 0;
            }

            switch (option) {

                case 1:
                    sendMessage(input);
                    break;

                case 2:
                    displaySentMessages();
                    break;

                case 3:
                    System.out.println(
                            "Thank you for using QuickChat.");
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please try again.");
            }
        }
    }

    // Send, disregard or store a message
    public static void sendMessage(Scanner input) {

        System.out.print(
                "\nEnter recipient cellphone number "
                + "(e.g. +27821234567): ");

        String recipient = input.nextLine().trim();

        if (!isValidRecipient(recipient)) {
            System.out.println(
                    "Invalid recipient cellphone number.");
            return;
        }

        System.out.print("Enter your message: ");
        String text = input.nextLine();

        if (text.trim().isEmpty() || text.length() >= 250) {
            System.out.println(
                    "Message must contain 1 to 249 characters.");
            return;
        }

        int nextNumber = messageCounter + 1;
        String id = generateMessageID();

        String hash = generateMessageHash(
                id, nextNumber, text);

        Message newMessage = new Message(
                id, nextNumber, recipient, text, hash, "Pending");

        System.out.println("\n===== MESSAGE PREVIEW =====");
        displayMessage(newMessage);

        System.out.println("\nChoose an action:");
        System.out.println("1. Send Message");
        System.out.println("0. Disregard Message");
        System.out.println("2. Store Message to send later");
        System.out.print("Enter your choice: ");

        String action = input.nextLine().trim();

        switch (action) {

            case "1":
                newMessage.messageStatus = "Sent";
                sentMessages.add(newMessage);
                messageCounter = nextNumber;

                System.out.println("Message successfully sent.");
                displayMessage(newMessage);
                break;

            case "0":
                newMessage.messageStatus = "Disregarded";
                disregardedMessages.add(newMessage);
                messageCounter = nextNumber;

                System.out.println("Message disregarded.");
                break;

            case "2":
                newMessage.messageStatus = "Stored";
                storedMessages.add(newMessage);
                messageCounter = nextNumber;

                storeMessages();
                break;

            default:
                System.out.println(
                        "Invalid choice. Message was not processed.");
        }
    }

    // Generate a random 10-digit message ID
    public static String generateMessageID() {

        String id;

        do {
            long number = 1_000_000_000L
                    + (long) (random.nextDouble()
                    * 9_000_000_000L);

            id = String.valueOf(number);

        } while (messageIDExists(id));

        return id;
    }

    private static boolean messageIDExists(String id) {

        for (Message msg : sentMessages) {
            if (msg.messageID.equals(id)) {
                return true;
            }
        }

        for (Message msg : disregardedMessages) {
            if (msg.messageID.equals(id)) {
                return true;
            }
        }

        for (Message msg : storedMessages) {
            if (msg.messageID.equals(id)) {
                return true;
            }
        }

        return false;
    }

    // Validate an international cellphone number
    public static boolean isValidRecipient(String recipient) {
        return recipient.matches("\\+[1-9][0-9]{7,14}");
    }

    // Generate the message hash
    public static String generateMessageHash(
            String id, int number, String text) {

        String[] words = text.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return (id.substring(0, 2) + ":"
                + number + ":"
                + firstWord + lastWord).toUpperCase();
    }

    // Display a message
    public static void displayMessage(Message msg) {

        System.out.println("Message ID: " + msg.messageID);
        System.out.println("Message Number: " + msg.messageNumber);
        System.out.println("Message Hash: " + msg.messageHash);
        System.out.println("Recipient: " + msg.recipient);
        System.out.println("Message: " + msg.message);
        System.out.println("Status: " + msg.messageStatus);
    }

    // Display recently sent messages
    public static void displaySentMessages() {

        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages available.");
            return;
        }

        System.out.println("\n===== SENT MESSAGES =====");

        for (Message msg : sentMessages) {
            displayMessage(msg);
            System.out.println("-------------------------");
        }

        System.out.println(
                "Total messages sent: " + sentMessages.size());
    }

    // Save stored messages to a JSON file
    public static void storeMessages() {

        try (FileWriter writer = new FileWriter("messages.json")) {

            writer.write("[\n");

            for (int i = 0; i < storedMessages.size(); i++) {

                Message msg = storedMessages.get(i);

                writer.write("  {\n");
                writer.write("    \"messageID\": \""
                        + escapeJSON(msg.messageID) + "\",\n");
                writer.write("    \"messageNumber\": "
                        + msg.messageNumber + ",\n");
                writer.write("    \"messageHash\": \""
                        + escapeJSON(msg.messageHash) + "\",\n");
                writer.write("    \"recipient\": \""
                        + escapeJSON(msg.recipient) + "\",\n");
                writer.write("    \"message\": \""
                        + escapeJSON(msg.message) + "\",\n");
                writer.write("    \"status\": \""
                        + escapeJSON(msg.messageStatus) + "\"\n");
                writer.write("  }");

                if (i < storedMessages.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]\n");

            System.out.println(
                    "Stored messages saved to messages.json.");

        } catch (IOException e) {
            System.out.println(
                    "Error saving messages: " + e.getMessage());
        }
    }

    // Escape special characters for JSON
    public static String escapeJSON(String value) {

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Message class
    static class Message {

        private String messageID;
        private int messageNumber;
        private String recipient;
        private String message;
        private String messageHash;
        private String messageStatus;

        public Message(String messageID, int messageNumber,
                       String recipient, String message,
                       String messageHash, String messageStatus) {

            this.messageID = messageID;
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.message = message;
            this.messageHash = messageHash;
            this.messageStatus = messageStatus;
        }
    }
}
