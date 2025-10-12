package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class MainATest {
    @Test
    @DisplayName("A-TEST-01: Multi-User Borrow and Return with Availability Validated")
    void A_TEST_01() {
        String input =
                //User1 logs in
                "spongebob\nilovegary!\n" +
                        //menu, borrow
                        "1\n" +
                        //choose book 15 ("The Great Gatsby")
                        "15\ny\n\n" +
                        //User1 logout
                        "y\n" +

                        //User2 logs in
                        "sandy_cheeks\ntexasgurl004\n" +
                        //menu, borrow
                        "1\n" +
                        //choose book 15 again (checked out)
                        "15\n" +
                        //confirm attempt, decline to place hold
                        "y\nn\n" +
                        //User2 logout
                        "y\n";

        Scanner scanner = new Scanner(input);
        StringWriter outputString = new StringWriter();
        PrintWriter output = new PrintWriter(outputString);

        Library library = new Library();
        library.initializeLibrary();

        //User1 login, borrow book, and logout
        library.authenticateUser(scanner, output);
        library.displayMenu(output);
        library.displayBookCollection(scanner, output);
        library.selectBookToBorrow(scanner, output);
        assertTrue(outputString.toString().contains("You have successfully borrowed The Great Gatsby"));
        library.logout(scanner, output);

        //User2 login and see it is Checked Out
        library.authenticateUser(scanner, output);
        library.displayMenu(output);
        library.displayBookCollection(scanner, output);
        assertTrue(outputString.toString().contains("15. Title: The Great Gatsby, Author: F. Scott Fitzgerald, Status: CHECKED_OUT, Due:"));
        library.selectBookToBorrow(scanner, output);
        library.logout(scanner, output);

        //User1 logs in, returns borrowed book
        input = "spongebob\nilovegary!\n1\ny\n\ny\n";
        scanner = new Scanner(input);
        library.authenticateUser(scanner, output);
        library.returnBook(scanner, output);
        assertTrue(outputString.toString().contains("You have successfully returned The Great Gatsby"));
        library.logout(scanner, output);

        //User2 logs in, sees book is available
        input = "sandy_cheeks\ntexasgurl004\n1";
        scanner = new Scanner(input);
        library.authenticateUser(scanner, output);
        library.displayBookCollection(scanner, output);
        assertTrue(outputString.toString().contains("15. Title: The Great Gatsby, Author: F. Scott Fitzgerald, Status: AVAILABLE"));
    }

    @Test
    @DisplayName("A-TEST-02: Initialization and Authentication with Error Handling")
    void A_TEST_02() {
        String input =
                //User1 has valid login
                "spongebob\nilovegary!\n" +
                        //User1 logout
                        "y\n" +
                        //User2 login with invalid credentials
                        "squidwardTent\n0clarinet_fan0\n";

        Scanner scanner = new Scanner(input);
        StringWriter outputString = new StringWriter();
        PrintWriter output = new PrintWriter(outputString);

        Library library = new Library();
        library.initializeLibrary();

        //Check initialization
        assertEquals(20, library.getNumBooks());
        assertEquals(3, library.getNumBorrowers());

        //Valid login
        library.authenticateUser(scanner, output);
        assertTrue(outputString.toString().contains("Authentication successful!"));
        library.displayMenu(output);
        assertTrue(outputString.toString().contains("--- Library Menu ---\n" +
                "1. Borrow a book\n" +
                        "2. Return a book\n" +
                        "3. Logout\n" +
                        "Enter choice (1-3): "));
        library.logout(scanner, output);
        assertTrue(outputString.toString().contains("You have successfully logged out."));

        //Invalid login
        library.authenticateUser(scanner, output);
        assertTrue(outputString.toString().contains("Authentication unsuccessful"));
    }
}
