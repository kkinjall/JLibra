package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*Test file for all Responsibilities and corresponding unit tests*/
public class MainTest {
    @Test
    @DisplayName("Check library has initialized exactly 20 books")
    void RESP_01_test_01(){
        Library library = new Library();
        library.initializeLibrary();
        int numOfBooks = library.getNumBooks();
        assertEquals(20, numOfBooks);
    }

    @Test
    @DisplayName("Check library has initialized correct book details")
    void RESP_01_test_02(){
        Library library = new Library();
        library.initializeLibrary();

        Book book1 = library.getBookByTitle("A Room of One’s Own"); //first book
        Book book2 = library.getBookByTitle("Beloved"); //tenth book
        Book book3 = library.getBookByTitle("Just Keep Walking"); //twentieth book

        //check if titles and authors match
        assertEquals("A Room of One’s Own", book1.getTitle());
        assertEquals("Virginia Woolf", book1.getAuthor());
        assertEquals("Beloved", book2.getTitle());
        assertEquals("Toni Morrison", book2.getAuthor());
        assertEquals("Just Keep Walking", book3.getTitle());
        assertEquals("Erin Soderberg", book3.getAuthor());
    }

    @Test
    @DisplayName("Check library has status of all books set to Available")
    void RESP_01_test_03(){
        Library library = new Library();
        library.initializeLibrary();

        //loop through all books after initializing library
        //check if each of their status is available
        for (Book book : library.getBooks()) {
            assertEquals(BookStatus.AVAILABLE, book.getStatus());
        }
    }

    @Test
    @DisplayName("Check library initialized with exactly 3 borrower accounts")
    void RESP_02_test_01(){
        Library library = new Library();
        library.initializeLibrary();
        int numOfBorrowers = library.getNumBorrowers();
        assertEquals(3, numOfBorrowers);
    }

    @Test
    @DisplayName("Check all borrower accounts are unique")
    void RESP_02_test_02(){
        Library library = new Library();
        library.initializeLibrary();

        //get number of unique usernames
        long count = library.getBorrowers().stream()
                .map(Borrower::getUsername)
                .distinct()
                .count();

        //check if number of usernames is equal to number of borrowers
        assertEquals(library.getBorrowers().size(), count);
    }

    @Test
    @DisplayName("Check all borrower accounts initially have zero borrowed books")
    void RESP_02_test_03(){
        Library library = new Library();
        library.initializeLibrary();

        //check all initialized borrowers have 0 books borrowed
        for (Borrower borrower : library.getBorrowers()) {
            assertEquals(0, borrower.getNumBorrowedBooks());
        }
    }

    @Test
    @DisplayName("Check valid borrower authentication succeeds")
    void RESP_03_test_01() {
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();

        boolean correctCredentials = library.authenticateUser(scanner, new PrintWriter(output));
        assertTrue(correctCredentials);
        assertTrue(output.toString().contains("Authentication successful!"));
    }

    @Test
    @DisplayName("Check invalid borrower authentication fails")
    void RESP_03_test_02() {
        String input = "spongemop\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();

        boolean incorrectCredentials = library.authenticateUser(scanner, new PrintWriter(output));
        assertFalse(incorrectCredentials);
        assertTrue(output.toString().contains("Authentication unsuccessful"));
    }

    @Test
    @DisplayName("Check authenticated borrower is set as current user for session (session established)")
    void RESP_04_test_01() {
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));

        assertEquals("spongebob", library.getCurrentUser());
    }

    @Test
    @DisplayName("Check not authenticated borrower is not set as current user (session not established)")
    void RESP_04_test_02() {
        String input = "spongebob\n!ilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));

        assertEquals(null, library.getCurrentUser());
    }

    @Test
    @DisplayName("Check if no notification is displayed if a borrower has no books on hold")
    void RESP_05_test_01() {
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        StringWriter output2 = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));
        library.notifyOfAvailableBooks(new PrintWriter(output2));

        //check if no notification is printed
        assertEquals("", output2.toString().trim());
    }

    @Test
    @DisplayName("Check if no notification is displayed if a borrower has books on hold that are not available")
    void RESP_05_test_02() {
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        StringWriter output2 = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));

        library.addBookOnHold("Persepolis");
        library.addBookOnHold("The Color Purple");
        library.getBookByTitle("Persepolis").setStatus(BookStatus.CHECKED_OUT);
        library.getBookByTitle("The Color Purple").setStatus(BookStatus.CHECKED_OUT);

        library.notifyOfAvailableBooks(new PrintWriter(output2));

        //check if no notification is printed
        assertEquals("", output2.toString().trim());
    }

    @Test
    @DisplayName("Check if a notification is displayed if a borrower has books on hold and they are available")
    void RESP_05_test_03() {
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));

        library.addBookOnHold("Beloved");
        library.addBookOnHold("The Color Purple");
        library.addBookOnHold("The Yellow Library");
        library.getBookByTitle("Beloved").setStatus(BookStatus.ON_HOLD);
        library.getBookByTitle("The Color Purple").setStatus(BookStatus.ON_HOLD);
        library.getBookByTitle("The Yellow Library").setStatus(BookStatus.CHECKED_OUT);

        library.notifyOfAvailableBooks(new PrintWriter(output));

        //check that only the books with status On Hold are printed in the notification
        assertTrue(output.toString().contains("Beloved"));
        assertTrue(output.toString().contains("The Color Purple"));
        assertFalse(output.toString().contains("The Yellow Library"));
    }

}
