
package ayandaprog;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Messages {

    private static final Random random = new Random();

    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();

    private static int messageCounter = 0;

    public static ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    public static ArrayList<Message> getStoredMessages() {
        return storedMessages;
    }

    public static void setMessageCounterAtLeast(int number) {
        if (number > messageCounter) {
            messageCounter = number;
        }
    }

    public static void start(Scanner input) {
        StoredMessages.loadFromJSON();

        int option = 0;

        System.out.println("\nWelcome to QuickChat.");

        while (option != 4) {
            System.out.println("\n===== QUICKCHAT MENU =====");
            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Stored message options");
            System.out.println("4. Quit");
            System.out.print("Enter your option: ");

            String choice;

            try {
                choice = input.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("No input available. Exiting.");
                return;
            }

            try {
                option = Integer.parseInt(choice.trim());
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
                    StoredMessages.displayStoredMessagesMenu(input);
                    break;

                case 4:
                    System.out.println("Thank you for using QuickChat.");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    public static void sendMessage(Scanner input) {

        System.out.print("\nEnter recipient cellphone number (e.g. +27821234567): ");
        String recipient = input.nextLine().trim();

        if (!isValidRecipient(recipient)) {
            System.out.println("Invalid recipient cellphone number.");
            return;
        }

        System.out.print("Enter your message: ");
        String text = input.nextLine();

        if (text.trim().isEmpty() || text.length() >= 250) {
            System.out.println("Message must contain 1 to 249 characters.");
            return;
        }

        int nextNumber = messageCounter + 1;

        String id = generateMessageID();
        String hash = generateMessageHash(id, nextNumber, text);

        Message newMessage = new Message(
                id,
                nextNumber,
                recipient,
                text,
                hash,
                "Pending"
        );

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
                messageCounter = nextNumber;

                StoredMessages.addStoredMessage(newMessage);
                break;

            default:
                System.out.println("Invalid choice. Message was not processed.");
                break;
        }
    }

    public static String generateMessageID() {

        String id;

        do {
            long number = 1_000_000_000L
                    + (long) (random.nextDouble() * 9_000_000_000L);

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

    public static boolean isValidRecipient(String recipient) {

        return recipient != null
                && recipient.matches("\\+[1-9][0-9]{7,14}");
    }

    public static String generateMessageHash(String id, int number, String text) {

        String[] words = text.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return (id.substring(0, 2) + ":"
                + number + ":"
                + firstWord
                + lastWord).toUpperCase();
    }

    public static void displayMessage(Message msg) {

        if (msg == null) {
            System.out.println("No message to display.");
            return;
        }

        System.out.println("Message ID: " + msg.messageID);
        System.out.println("Message Number: " + msg.messageNumber);
        System.out.println("Message Hash: " + msg.messageHash);
        System.out.println("Recipient: " + msg.recipient);
        System.out.println("Message: " + msg.message);
        System.out.println("Status: " + msg.messageStatus);
    }

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

        System.out.println("Total messages sent: " + sentMessages.size());
    }

    public static void storeMessages() {
        StoredMessages.saveToJSON();
    }

    public static String escapeJSON(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static class Message {

        private String messageID;
        private int messageNumber;
        private String recipient;
        private String message;
        private String messageHash;
        private String messageStatus;

        public Message(String messageID,
                       int messageNumber,
                       String recipient,
                       String message,
                       String messageHash,
                       String messageStatus) {

            this.messageID = messageID;
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.message = message;
            this.messageHash = messageHash;
            this.messageStatus = messageStatus;
        }

        public String getMessageID() {
            return messageID;
        }

        public int getMessageNumber() {
            return messageNumber;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getMessage() {
            return message;
        }

        public String getMessageHash() {
            return messageHash;
        }

        public String getMessageStatus() {
            return messageStatus;
        }
    }
}
