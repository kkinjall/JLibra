package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
