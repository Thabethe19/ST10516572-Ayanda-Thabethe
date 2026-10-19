

package ayandaprog;

public class java {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellphoneNumber;
    private boolean loginStatus;

    // Constructor
    public java (String firstName, String lastName,
                 String username, String password,
                 String cellphoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphoneNumber = cellphoneNumber;
        this.loginStatus = false;
    }

    // Check username
    public boolean checkUsername(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    // Check cellphone number
    public boolean checkCellphoneNumber(String number) {
        if (number == null) {
            return false;
        }

        return number.matches("0[0-9]{9}")
                || number.matches("\\+27[0-9]{9}");
    }

    // Check password complexity
    public boolean checkPassword(String password) {
        if (password == null) {
            return false;
        }

        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Register user
    public String registerUser() {
        if (!checkUsername(username)) {
            return "Username is incorrectly formatted.";
        }

        if (!checkPassword(password)) {
            return "Password does not meet the complexity requirements.";
        }

        if (!checkCellphoneNumber(cellphoneNumber)) {
            return "Cellphone number is incorrectly formatted.";
        }

        return "Registration successful.";
    }

    // Login user
    public boolean loginUser(String enteredUsername,
                             String enteredPassword) {

        loginStatus = enteredUsername != null
                && enteredPassword != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);

        return loginStatus;
    }

    // Check login status
    public boolean isLoginSuccessful(String enteredUsername) {
        return loginStatus
                && username.equals(enteredUsername);
    }

    // Display login message
    public void displayLoginMessage() {
        if (loginStatus) {
            System.out.println("Welcome to QuickChat, "
                    + firstName + " " + lastName + "!");
            System.out.println("Login successful.");
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
