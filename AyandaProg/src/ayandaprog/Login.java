
package ayandaprog;

public class Login {

    // VARIABLES
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellphoneNumber;
    private boolean loginStatus;

    // CONSTRUCTOR
    public Login(String firstName, String lastName,
                 String username, String password,
                 String cellphoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphoneNumber = cellphoneNumber;
        this.loginStatus = false;
    }

    // CHECK USERNAME
    // Must contain an underscore and be no longer than 5 characters
    public boolean checkUsername(String username) {

        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    // CHECK PASSWORD COMPLEXITY
    // At least 8 characters, one uppercase letter,
    // one number and one special character
    public boolean checkPasswordComplexity(String password) {

        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (int i = 0; i < password.length(); i++) {

            char character = password.charAt(i);

            if (Character.isUpperCase(character)) {
                hasCapitalLetter = true;
            }

            if (Character.isDigit(character)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapitalLetter
                && hasNumber
                && hasSpecialCharacter;
    }

    // Method used by AyandaProg.java
    public boolean checkPassword(String password) {
        return checkPasswordComplexity(password);
    }

    // CHECK SOUTH AFRICAN CELLPHONE NUMBER
    // Accepts 0821234567 or +27821234567
    public boolean checkCellphoneNumber(String cellphoneNumber) {

        if (cellphoneNumber == null) {
            return false;
        }

        if (cellphoneNumber.matches("0[0-9]{9}")) {
            return true;
        }

        return cellphoneNumber.matches("\\+27[0-9]{9}");
    }

    // CHECK WHETHER A STRING CONTAINS ONLY DIGITS
    private boolean allDigits(String value) {

        for (int i = 0; i < value.length(); i++) {

            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    // LOGIN USER
    public boolean loginUser(String enteredUsername,
                             String enteredPassword) {

        boolean success = username != null
                && password != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);

        loginStatus = success;

        return success;
    }

    // CHECK LOGIN STATUS
    public boolean isLoginSuccessful(String username) {

        return loginStatus
                && this.username != null
                && this.username.equals(username);
    }

    // DISPLAY LOGIN SUCCESS MESSAGE
    public void displayLoginMessage() {

        System.out.println();
        System.out.println("Login successful!");
        System.out.println(
                "Welcome " + firstName + " " + lastName
                + ", it is great to see you again!");
    }

    // GETTERS
    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }
}
