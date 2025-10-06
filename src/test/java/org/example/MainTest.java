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

    @Test
    @DisplayName("Check if menu options to borrow book, return book, and logout are displayed")
    void RESP_06_test_01() {
        StringWriter output = new StringWriter();
        Library library = new Library();
        library.displayMenu(new PrintWriter(output));

        String menu = "\n--- Library Menu ---\n" +
                "1. Borrow a book\n" +
                "2. Return a book\n" +
                "3. Logout\n" +
                "Enter choice (1-3): ";

        assertTrue(output.toString().contains(menu));
    }

    @Test
    @DisplayName("Check if borrow book option displays current book count = 0 when no books are borrowed ")
    void RESP_07_test_01() {
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));
        output.flush();

        library.displayMenu(new PrintWriter(output));
        input = "1\n";
        scanner = new Scanner(input);
        output.flush();

        library.displayBookCollection(scanner, new PrintWriter(output));
        assertTrue(output.toString().contains("Current number of books borrowed: " + 0));
    }

    @Test
    @DisplayName("Check if borrow book option displays correct current book count when books are borrowed ")
    void RESP_07_test_02() {
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));
        output.flush();

        //borrowed two books
        library.borrowBook("Beloved");
        library.borrowBook("The Yellow Library");

        library.displayMenu(new PrintWriter(output));
        input = "1\n";
        scanner = new Scanner(input);
        output.flush();

        library.displayBookCollection(scanner, new PrintWriter(output));
        //check if count is two
        assertTrue(output.toString().contains("Current number of books borrowed: " + 2));
    }

    @Test
    @DisplayName("Check if correct authors and titles are displayed")
    void RESP_08_test_01(){
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));
        output.flush();

        library.displayMenu(new PrintWriter(output));
        input = "1\n";
        scanner = new Scanner(input);
        output.flush();
        library.displayBookCollection(scanner, new PrintWriter(output));

        //check if titles and corresponding authors are printed
        //check for first, tenth and twentieth books
        assertTrue(output.toString().contains("Title: A Room of One’s Own, Author: Virginia Woolf"));
        assertTrue(output.toString().contains("Title: Beloved, Author: Toni Morrison"));
        assertTrue(output.toString().contains("Title: Just Keep Walking, Author: Erin Soderberg"));
    }

    @Test
    @DisplayName("Check if books of each status: Available, Checked Out and On Hold are displayed")
    void RESP_08_test_02(){
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));
        output.flush();

        //add borrower to hold queue as only borrower, and change book status to on hold
        //this book should display as available to current borrower
        library.addBookOnHold("Sister Outsider");
        library.getBookByTitle("Sister Outsider").setStatus(BookStatus.ON_HOLD);

        library.getBookByTitle("Persepolis").setStatus(BookStatus.ON_HOLD);
        library.getBookByTitle("I Know Why the Caged Bird Sings").setStatus(BookStatus.CHECKED_OUT);

        library.displayMenu(new PrintWriter(output));
        input = "1\n";
        scanner = new Scanner(input);
        output.flush();
        library.displayBookCollection(scanner, new PrintWriter(output));

        //check if statuses are correct for a selection of books
        assertTrue(output.toString().contains("Title: Sister Outsider, Author: Audre Lorde, Status: AVAILABLE"));
        assertTrue(output.toString().contains("Title: Beloved, Author: Toni Morrison, Status: AVAILABLE"));
        assertTrue(output.toString().contains("Title: Persepolis, Author: Marjane Satrapi, Status: ON_HOLD"));
        assertTrue(output.toString().contains("Title: I Know Why the Caged Bird Sings, Author: Maya Angelou, Status: CHECKED_OUT"));
    }

    @Test
    @DisplayName("Check if the Due date heading is printed for books that are Checked Out")
    void RESP_08_test_03(){
        String input = "spongebob\nilovegary!\n";
        Scanner scanner = new Scanner(input);
        StringWriter output = new StringWriter();
        Library library = new Library();

        library.initializeLibrary();
        library.authenticateUser(scanner, new PrintWriter(output));
        output.flush();

        library.getBookByTitle("Sister Outsider").setStatus(BookStatus.ON_HOLD); //due heading should not print for this book
        library.getBookByTitle("Beloved").setStatus(BookStatus.CHECKED_OUT);
        library.getBookByTitle("I Know Why the Caged Bird Sings").setStatus(BookStatus.CHECKED_OUT);

        library.displayMenu(new PrintWriter(output));
        input = "1\n";
        scanner = new Scanner(input);
        output.flush();
        library.displayBookCollection(scanner, new PrintWriter(output));

        //check if due date heading is present for a selection of books
        assertTrue(output.toString().contains("Title: Sister Outsider, Author: Audre Lorde, Status: ON_HOLD"));
        assertTrue(output.toString().contains("Title: Beloved, Author: Toni Morrison, Status: CHECKED_OUT, Due: "));
        assertTrue(output.toString().contains("Title: I Know Why the Caged Bird Sings, Author: Maya Angelou, Status: CHECKED_OUT, Due: "));
    }
}
