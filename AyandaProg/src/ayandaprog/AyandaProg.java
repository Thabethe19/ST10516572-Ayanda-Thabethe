
package ayandaprog;

import java.util.Scanner;

public class AyandaProg {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        String firstName;
        String lastName;
        String username;
        String cellphoneNumber;
        String password;

        // REGISTRATION
        System.out.println("================================");
        System.out.println("       QUICKCHAT REGISTRATION");
        System.out.println("================================");

        System.out.print("Enter your first name: ");
        firstName = input.nextLine().trim();

        System.out.print("Enter your last name: ");
        lastName = input.nextLine().trim();

        // USERNAME
        while (true) {

            System.out.print("Enter your username: ");
            username = input.nextLine();

            Login check = new Login(
                    firstName, lastName, username, "", "");

            if (check.checkUsername(username)) {
                System.out.println(
                        "Username successfully captured.");
                break;
            }

            System.out.println("Invalid username.");
            System.out.println(
                    "Username must contain an underscore "
                    + "and be no longer than 5 characters.");
        }

        // CELLPHONE NUMBER
        while (true) {

            System.out.print("Enter your cellphone number: ");
            cellphoneNumber = input.nextLine();

            Login check = new Login(
                    firstName, lastName, username,
                    "", cellphoneNumber);

            if (check.checkCellphoneNumber(cellphoneNumber)) {
                System.out.println(
                        "Cellphone number successfully captured.");
                break;
            }

            System.out.println("Invalid cellphone number.");
            System.out.println(
                    "Use 0821234567 or +27821234567.");
        }

        // PASSWORD
        while (true) {

            System.out.print("Enter your password: ");
            password = input.nextLine();

            Login check = new Login(
                    firstName, lastName, username,
                    password, cellphoneNumber);

            if (check.checkPassword(password)) {
                System.out.println(
                        "Password successfully captured.");
                break;
            }

            System.out.println("Invalid password.");
            System.out.println(
                    "Password must contain at least 8 characters,");
            System.out.println(
                    "a capital letter, a number and a special character.");
        }

        // CREATE USER
        Login user = new Login(
                firstName, lastName, username,
                password, cellphoneNumber);

        System.out.println("\n================================");
        System.out.println("     REGISTRATION SUCCESSFUL");
        System.out.println("================================");
        System.out.println(
                "Welcome, " + firstName + " " + lastName + "!");

        // LOGIN
        System.out.println("\n================================");
        System.out.println("              LOGIN");
        System.out.println("================================");

        System.out.print("Enter your username: ");
        String loginUsername = input.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword = input.nextLine();

        if (user.loginUser(loginUsername, loginPassword)) {

            user.displayLoginMessage();

            // Open Messages after successful login
            Messages.start(input);

        } else {

            System.out.println("\nLogin failed.");
            System.out.println(
                    "Incorrect username or password.");
        }

        input.close();

        System.out.println("Application closed.");
    }
}
