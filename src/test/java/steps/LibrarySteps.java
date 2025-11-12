package steps;

import io.cucumber.java.en.*;
import org.example.Book;
import org.example.BookStatus;
import org.example.Borrower;
import org.example.Library;

import java.io.PrintWriter;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarySteps {
    private Library library;

    @Given("the library system is initialized with all 20 books and 3 accounts")
    public void initialize_library_system() {
        library = new Library();
        library.initializeTestLibrary();
    }

    @And("{string} logs in the system")
    public void user_logs_in(String username) {
        Borrower borrower = library.findBorrower(username);
        library.confirmLogin(username, borrower.getPassword());
    }

    @And("{string} logs out the system")
    public void user_logs_out(String username) {
        library.confirmLogout();
    }

    @And("{string} attempts to borrow {string}")
    public void user_borrows_book(String username, String title) {
        PrintWriter output = new PrintWriter(System.out);
        library.attemptBookBorrow(username, title, false, output);
    }

    @Then("{string}'s status is CHECKED_OUT")
    public void book_status_is_checked_out(String title) {
        Book book = library.getBookByTitle(title);
        assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
    }

    @And("{string} cannot borrow {string}")
    public void user_cannot_borrow(String username, String title) {
        PrintWriter output = new PrintWriter(System.out);
        library.attemptBookBorrow(username, title, false, output);

        Borrower borrower = library.findBorrower(username);
        Book book = library.getBookByTitle(title);
        assertFalse(borrower.getBorrowedBooks().contains(book));
        assertNotEquals(borrower, book.getCurrentBorrower());
    }

    @And("{string} attempts to return the book {string}")
    public void user_attempts_book_return(String username, String title) {
        Book book = library.getBookByTitle(title);
        library.processBookReturn(username, book);
    }

    @Then("{string} successfully returns the book {string}")
    public void user_returns_book(String username, String title) {
        Borrower borrower = library.findBorrower(username);
        Book book = library.getBookByTitle(title);
        assertFalse(borrower.getBorrowedBooks().contains(book));
        assertNull(book.getCurrentBorrower());
        assertNull(book.getDueDate());
    }

    @Then("{string}'s status is AVAILABLE")
    public void book_status_is_available(String title) {
        Book book = library.getBookByTitle(title);
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertNull(book.getCurrentBorrower());
    }

    @Then("{string} can borrow the book {string}")
    public void user_can_now_borrow(String username, String title) {
        Book book = library.getBookByTitle(title);
        assertEquals(username, book.getCurrentBorrower().getUsername());
        assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
        assertNotNull(book.getDueDate());
    }

    //Scenario 2 Steps
    @When("{string} places a hold on {string}")
    public void user_places_hold(String username, String title){
        PrintWriter output = new PrintWriter(System.out);
        library.attemptBookBorrow(username, title, true, output);
    }

    @Then("the hold queue for {string} should list {string} first and {string} second")
    public void hold_queue_order(String title, String username1, String username2){
        Queue<String> queue = library.getBookByTitle(title).getHoldQueue();
        assertEquals(username1, queue.peek());
        assertEquals(username1, queue.iterator().next());
    }

    @Then("{string} should receive a notification that {string} is available")
    public void notification_received(String username, String title){
        List<Book> availableBooks = library.getAvailableBooksForNotification(username);
        List<String> notifications = library.notifyOfAvailableBooks(new PrintWriter(System.out));
        assertEquals(1, availableBooks.size());
        assertEquals(title, availableBooks.get(0).getTitle());
        assertEquals(1, notifications.size());
    }

    @Then("the system should allow {string} to place a hold on {string}")
    public void prevent_borrowing_at_limit(String username, String title) {
        PrintWriter output = new PrintWriter(System.out);
        library.attemptBookBorrow(username, title, true, output);

        Borrower borrower = library.findBorrower(username);
        Book book = library.getBookByTitle(title);
        assertFalse(borrower.getBorrowedBooks().contains(book));
        assertEquals(username, book.getHoldQueue().peek());
    }

    @Then("{string}'s status is ON_HOLD")
    public void book_status_available(String title){
        Book book = library.getBookByTitle(title);
        assertEquals(BookStatus.ON_HOLD, book.getStatus());
        assertNull(book.getCurrentBorrower());
    }

    @Then("{string} is at the borrow limit")
    public void user_at_borrow_limit(String username){
        Borrower borrower = library.findBorrower(username);
        assertTrue(borrower.atBorrowLimit());
    }

        @Then("{string}'s number of borrowed books should decrease by one to be {int}")
    public void decrease_num_borrowed_books(String username, int decreased_count){
        Borrower borrower = library.findBorrower(username);
        assertEquals(decreased_count, borrower.getNumBorrowedBooks());
        assertFalse(borrower.atBorrowLimit());
    }

    @When("{string} should receive a notification of {string} being available")
    public void user_next_in_hold_queue(String username, String title) {
        List<Book> availableBooks = library.getAvailableBooksForNotification(username);
        List<String> notifications = library.notifyOfAvailableBooks(new PrintWriter(System.out));

        assertEquals(1, availableBooks.size());
        assertEquals(title, availableBooks.get(0).getTitle());
        assertEquals(1, notifications.size());
    }

    //Scenario 4
    @And("{string} has no borrowed books")
    public void user_with_no_borrowed_books(String username) {
        Borrower borrower = library.findBorrower(username);
        assertNotNull(borrower);
        assertTrue(borrower.getBorrowedBooks().isEmpty());
    }

    @When("{string} attempts to return a book")
    public void user_attempts_to_return_all_books(String username) {
        Borrower borrower = library.findBorrower(username);
        for (Book book : borrower.getBorrowedBooks()) {
             library.processBookReturn(username, book);
        }
    }

    @Then("the system reports that {string} has no borrowed books")
    public void system_reports_no_borrowed_books(String username) {
        Borrower borrower = library.findBorrower(username);
        assertFalse(library.canReturnBooks(username));
        assertEquals(0, borrower.getNumBorrowedBooks());
    }

    @And("all books in the library appear available")
    public void all_books_in_library_appear_available() {
        for (Book book : library.getBooks()) {
            assertEquals(BookStatus.AVAILABLE, book.getStatus());
            assertNull(book.getCurrentBorrower());
        }
    }
}