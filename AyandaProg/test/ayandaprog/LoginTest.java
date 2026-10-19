package ayandaprog;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Student
 */
public class LoginTest {
    // Registered user used across the tests
    private Login instance;

    private static final String FIRST_NAME = "Kyle";
    private static final String SURNAME = "Smith";

    private static final String VALID_USERNAME = "kyl_1";
    private static final String INVALID_USERNAME = "kyle!!!!!!!";

    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String INVALID_PASSWORD = "password";

    private static final String VALID_CELLPHONE = "+27838968976";
    private static final String INVALID_CELLPHONE = "08966553";

    public LoginTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

        instance = new Login(
                FIRST_NAME,
                SURNAME,
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_CELLPHONE
        );
    }

    @After
    public void tearDown() {
        instance = null;
    }

    // ==========================================
    // USERNAME TESTS
    // ==========================================

    /**
     * Test of checkUsername method, of class Login.
     *
     * Username correctly formatted:
     * contains an underscore and is no more
     * than five characters long.
     */
    @Test
    public void testCheckUsername() {

        System.out.println("checkUsername");

        boolean expResult = true;
        boolean result = instance.checkUsername(VALID_USERNAME);

        assertEquals(expResult, result);
    }

    /**
     * Test of checkUsername method with an
     * incorrectly formatted username.
     */
    @Test
    public void testCheckUsernameIncorrect() {

        System.out.println("checkUsername - incorrectly formatted");

        boolean expResult = false;
        boolean result = instance.checkUsername(INVALID_USERNAME);

        assertEquals(expResult, result);
    }

    /**
     * assertTrue - username correctly formatted.
     */
    @Test
    public void testCheckUsernameTrue() {

        System.out.println("checkUsername - assertTrue");

        assertTrue(instance.checkUsername(VALID_USERNAME));
    }

    /**
     * assertFalse - username incorrectly formatted.
     */
    @Test
    public void testCheckUsernameFalse() {

        System.out.println("checkUsername - assertFalse");

        assertFalse(instance.checkUsername(INVALID_USERNAME));
    }

    // ==========================================
    // PASSWORD TESTS
    // ==========================================

    /**
     * Test of checkPasswordComplexity method,
     * of class Login.
     *
     * Password must contain at least eight characters,
     * a capital letter, a number and a special character.
     */
    @Test
    public void testCheckPasswordComplexity() {

        System.out.println("checkPasswordComplexity");

        boolean expResult = true;
        boolean result;
        result = instance.checkPasswordComplexity(VALID_PASSWORD);

        assertEquals(expResult, result);
    }

    /**
     * Test of checkPasswordComplexity method with
     * a password that does not meet the requirements.
     */
    @Test
    public void testCheckPasswordComplexityIncorrect() {

        System.out.println(
                "checkPasswordComplexity - does not meet requirements"
        );

        boolean expResult = false;
        boolean result =
                instance.checkPasswordComplexity(INVALID_PASSWORD);

        assertEquals(expResult, result);
    }

    /**
     * assertTrue - password meets complexity requirements.
     */
    @Test
    public void testCheckPasswordComplexityTrue() {

        System.out.println(
                "checkPasswordComplexity - assertTrue"
        );

        assertTrue(
                instance.checkPasswordComplexity(VALID_PASSWORD)
        );
    }

    /**
     * assertFalse - password does not meet complexity requirements.
     */
    @Test
    public void testCheckPasswordComplexityFalse() {

        System.out.println(
                "checkPasswordComplexity - assertFalse"
        );

        assertFalse(
                instance.checkPasswordComplexity(INVALID_PASSWORD)
        );
    }

    // ==========================================
    // CELLPHONE TESTS
    // ==========================================

    /**
     * Test of checkCellphoneNumber method,
     * of class Login.
     *
     * Cell phone number correctly formatted
     * with an international code.
     */
    @Test
    public void testCheckCellphoneNumber() {

        System.out.println("checkCellphoneNumber");

        boolean expResult = true;
        boolean result =
                instance.checkCellphoneNumber(VALID_CELLPHONE);

        assertEquals(expResult, result);
    }

    /**
     * Test of checkCellphoneNumber method with
     * an incorrectly formatted number.
     */
    @Test
    public void testCheckCellphoneNumberIncorrect() {

        System.out.println(
                "checkCellphoneNumber - incorrectly formatted"
        );

        boolean expResult = false;
        boolean result =
                instance.checkCellphoneNumber(INVALID_CELLPHONE);

        assertEquals(expResult, result);
    }

    /**
     * assertTrue - cell phone number correctly formatted.
     */
    @Test
    public void testCheckCellphoneNumberTrue() {

        System.out.println(
                "checkCellphoneNumber - assertTrue"
        );

        assertTrue(
                instance.checkCellphoneNumber(VALID_CELLPHONE)
        );
    }

    /**
     * assertFalse - cell phone number incorrectly formatted.
     */
    @Test
    public void testCheckCellphoneNumberFalse() {

        System.out.println(
                "checkCellphoneNumber - assertFalse"
        );

        assertFalse(
                instance.checkCellphoneNumber(INVALID_CELLPHONE)
        );
    }

    // ==========================================
    // LOGIN USER TESTS
    // ==========================================

    /**
     * Test of loginUser method, of class Login.
     *
     * Login successful - the system returns true.
     */
    @Test
    public void testLoginUser() {

        System.out.println("loginUser - successful");

        boolean expResult = true;

        boolean result = instance.loginUser(
                VALID_USERNAME,
                VALID_PASSWORD
        );

        assertEquals(expResult, result);
    }

    /**
     * Test of loginUser method with incorrect details.
     *
     * Login failed - the system returns false.
     */
    @Test
    public void testLoginUserFailed() {

        System.out.println("loginUser - failed");

        boolean expResult = false;

        boolean result = instance.loginUser(
                INVALID_USERNAME,
                INVALID_PASSWORD
        );

        assertEquals(expResult, result);
    }

    /**
     * assertTrue - login successful.
     */
    @Test
    public void testLoginUserTrue() {

        System.out.println("loginUser - assertTrue");

        assertTrue(
                instance.loginUser(
                        VALID_USERNAME,
                        VALID_PASSWORD
                )
        );
    }

    /**
     * assertFalse - login failed.
     */
    @Test
    public void testLoginUserFalse() {

        System.out.println("loginUser - assertFalse");

        assertFalse(
                instance.loginUser(
                        INVALID_USERNAME,
                        INVALID_PASSWORD
                )
        );
    }

    // ==========================================
    // LOGIN STATUS TESTS
    // ==========================================

    /**
     * Test of isLoginSuccessful method,
     * of class Login.
     *
     * A successful login returns true.
     */
    @Test
    public void testIsLoginSuccessful() {

        System.out.println(
                "isLoginSuccessful - successful"
        );

        instance.loginUser(
                VALID_USERNAME,
                VALID_PASSWORD
        );

        assertTrue(
                instance.isLoginSuccessful(VALID_USERNAME)
        );
    }

    /**
     * Test of isLoginSuccessful method after
     * a failed login.
     */
    @Test
    public void testIsLoginSuccessfulFailed() {

        System.out.println(
                "isLoginSuccessful - failed"
        );

        instance.loginUser(
                INVALID_USERNAME,
                INVALID_PASSWORD
        );

        assertFalse(
                instance.isLoginSuccessful(INVALID_USERNAME)
        );
    }

    // ==========================================
    // FIRST NAME TEST
    // ==========================================

    /**
     * Test of getFirstName method, of class Login.
     */
    @Test
    public void testGetFirstName() {

        System.out.println("getFirstName");

        String expResult = FIRST_NAME;
        String result = instance.getFirstName();

        assertEquals(expResult, result);
    }

    // ==========================================
    // SURNAME TEST
    // ==========================================

    /**
     * Test of getSurname method, of class Login.
     */
    @Test
    public void testGetSurname() {

        System.out.println("getSurname");

        String expResult = SURNAME;
        String result = instance.getSurname();

        assertEquals(expResult, result);
    }
}