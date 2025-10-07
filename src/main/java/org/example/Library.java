package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Library {
    private List<Book> books;
    private List<Borrower> borrowers;
    private String currentUser;
    private static int MAX_BOOKS = 3;

    //constructor
    public Library(){
        this.books = new ArrayList<Book>();
        this.borrowers = new ArrayList<Borrower>();
        currentUser = null;
    }

    //initialize library with 20 books
    public void initializeLibrary(){
        books.clear();
        borrowers.clear();

        borrowers.add(new Borrower("spongebob", "ilovegary!"));
        borrowers.add(new Borrower("sandy_cheeks", "texasgurl004"));
        borrowers.add(new Borrower("squidwardTentacles", "0clarinet_fan0"));

        books.add(new Book("A Room of One’s Own", "Virginia Woolf"));
        books.add(new Book("The Feminine Mystique", "Betty Friedan"));
        books.add(new Book("Gender Trouble", "Judith Butler"));
        books.add(new Book("Sister Outsider", "Audre Lorde"));
        books.add(new Book("The Bell Jar", "Sylvia Plath"));
        books.add(new Book("Pride and Prejudice", "Jane Austen"));
        books.add(new Book("Jane Eyre", "Charlotte Brontë"));
        books.add(new Book("Wuthering Heights", "Emily Brontë"));
        books.add(new Book("To the Lighthouse", "Virginia Woolf"));
        books.add(new Book("Beloved", "Toni Morrison"));
        books.add(new Book("The Color Purple", "Alice Walker"));
        books.add(new Book("Persepolis", "Marjane Satrapi"));
        books.add(new Book("The Handmaid’s Tale", "Margaret Atwood"));
        books.add(new Book("The Bloody Chamber", "Angela Carter"));
        books.add(new Book("The Great Gatsby", "F. Scott Fitzgerald"));
        books.add(new Book("The Yellow Library", "Charlotte Perkins Gilman"));
        books.add(new Book("The Awakening", "Kate Chopin"));
        books.add(new Book("I Know Why the Caged Bird Sings", "Maya Angelou"));
        books.add(new Book("Women Don't Owe You Pretty", "Florence Given"));
        books.add(new Book("Just Keep Walking", "Erin Soderberg"));
    }

    public boolean authenticateUser(Scanner input, PrintWriter output){
        output.print("Enter username: ");
        output.flush();
        String username = input.nextLine();
        output.print("Enter password: ");
        output.flush();
        String password = input.nextLine();

        //for all borrowers, if the username or password entered matches any of theirs authentication is successful
        for (Borrower borrower: borrowers){
            if (borrower.getUsername().equals(username) && borrower.getPassword().equals(password)) {
                currentUser = borrower.getUsername();
                output.println("Authentication successful!");
                output.flush();
                return true;
            }
        }
        output.println("Authentication unsuccessful");
        output.flush();
        return false;
    }

    public void notifyOfAvailableBooks(PrintWriter out){
        if (currentUser == null) return;

        for (Book book : getBooks()) {
            //if book is available and the book's hold queue has the borrower next in line...
            //print notification
            if (!book.getHoldQueue().isEmpty()) {
                if (book.getStatus().equals(BookStatus.ON_HOLD) && book.getHoldQueue().peek().equals(currentUser)) {
                    out.println("Book: " + book.getTitle() + ", previously on hold is now available");
                }
            }
        }
    }

    public void displayMenu(PrintWriter output){
        String menu = "\n--- Library Menu ---\n" +
                "1. Borrow a book\n" +
                "2. Return a book\n" +
                "3. Logout\n" +
                "Enter choice (1-3): ";
        output.println(menu);
    }

    public boolean addBookOnHold(String title){
        Book book = getBookByTitle(title);
        book.addHoldQueue(currentUser);
        return true;
    }

    public boolean borrowBook(String title){
        Book book = getBookByTitle(title);
        Borrower borrower = findBorrower(currentUser);

        if (borrower.getNumBorrowedBooks() < MAX_BOOKS){
            book.setDueDate();
            book.setCurrentBorrower(borrower);
            book.setStatus(BookStatus.CHECKED_OUT);
            borrower.addNumBorrowedBooks();
            borrower.addBorrowedBook(book);
            return true;
        }

        return false;
    }

    public void displayBookCollection(Scanner scanner, PrintWriter output) {
        if (scanner.nextLine().equals("1")) {
            output.println("-------------------------------------");
            output.println("Current number of books borrowed: " + findBorrower(currentUser).getNumBorrowedBooks());
            output.println();
            output.println("---Collection of Books---");
            int count = 1;
            for (Book book : books) {
                //if book is checked out, display due date
                if (book.getStatus().equals(BookStatus.CHECKED_OUT)) {
                    output.println(count + ". Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Status: CHECKED_OUT, Due: ");
                }

                else if (book.getStatus().equals(BookStatus.AVAILABLE)){
                    output.println(count + ". Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Status: AVAILABLE");
                }


                else if (book.getStatus().equals(BookStatus.ON_HOLD) && !book.getHoldQueue().isEmpty()
                        && book.getHoldQueue().peek().equals(currentUser)){
                    output.println(count + ". Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Status: AVAILABLE");
                }

                else {
                    output.println(count + ". Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Status: ON_HOLD");
                }

                count += 1;
                output.flush();
            }
        }
    }

    public boolean selectBookToBorrow(Scanner scanner, PrintWriter output){
        output.println();
        output.println("Enter the number of the book you would like to borrow: ");
        Book book = getBookByNumber(Integer.parseInt(scanner.nextLine())); //find book by number

        //print book details and confirmation
        output.println("You've selected " + book.getTitle() + " by " + book.getAuthor() + ". Proceed with borrowing? (y/n)");

        String confirm = "";
        if (scanner.hasNextLine()) { confirm = scanner.nextLine().trim();}
        if (confirm.equalsIgnoreCase("Y")) {
            output.println("Borrowing transaction in progress...");
            output.flush();
            Borrower borrower = findBorrower(currentUser);

            //book is checked out, current borrower hasn't checked it out, and they have less than 3 books borrowed - offer to place hold
            if (book.getStatus().equals(BookStatus.CHECKED_OUT) && book.getCurrentBorrower() != borrower && borrower.getNumBorrowedBooks() < 3){
                output.println("This book is currently checked out. Would you like to place a hold? (y/n)");
                output.flush();
                if (scanner.hasNextLine() && scanner.nextLine().trim().equalsIgnoreCase("Y")){
                    addBookOnHold(book.getTitle());
                    output.println("You have been added to the hold queue for this book");
                    output.flush();
                    return false;
                }
                else{
                    output.println("Hold cancelled");
                    output.flush();
                    return false;
                }
            }

            //book is on hold and the current borrower hasn't placed a hold on it - offer to place hold
            if (book.getStatus().equals(BookStatus.ON_HOLD) && !book.getHoldQueue().contains(currentUser)){
                output.println("This book is currently on hold by another borrower. Would you like to place a hold? (y/n)");
                output.flush();
                if (scanner.hasNextLine() && scanner.nextLine().trim().equalsIgnoreCase("Y")){
                    addBookOnHold(book.getTitle());
                    output.println("You have been added to the hold queue for this book");
                    output.flush();
                    return false;
                }
                else{
                    output.println("Hold cancelled");
                    output.flush();
                    return false;
                }
            }

            //book is available, but current borrower has currently borrowed 3 books - offer to place hold
            if (book.getStatus().equals(BookStatus.AVAILABLE) && borrower.getNumBorrowedBooks() == 3){
                output.println("You have met the 3 book borrow limit and currently can not borrow this book. Would you like to place a hold? (y/n)");
                output.flush();
                if (scanner.hasNextLine() && scanner.nextLine().trim().equalsIgnoreCase("Y")){
                    addBookOnHold(book.getTitle());
                    output.println("You have been added to the hold queue for this book");
                    output.flush();
                    return true;
                }
                else{
                    output.println("Hold cancelled");
                    output.flush();
                    return true;
                }
            }

            //book is on hold, borrower has already placed hold, and they are not first in the hold queue - no hold or borrow
            if (book.getStatus().equals(BookStatus.ON_HOLD) && book.getHoldQueue().contains(currentUser) && !book.getHoldQueue().peek().contains(currentUser)){
                output.println("You already have a hold on this book");
                output.flush();
                return false;
            }

            //book is checked out, and borrower has borrowed it - no hold or borrow
            if (book.getStatus().equals(BookStatus.CHECKED_OUT) && book.getCurrentBorrower() == borrower){
                output.println("You already have this book checked out");
                output.flush();
                return false;
            }

            //book is on hold, but current borrower has currently borrowed 3 books - no borrow or hold
            if (book.getStatus().equals(BookStatus.ON_HOLD) && !book.getHoldQueue().isEmpty() && book.getHoldQueue().peek().equals(currentUser) && borrower.getNumBorrowedBooks() == 3){
                output.println("You have met the 3 book borrow limit and currently can not borrow this book. Please return at least one book before trying to borrow again.");
                output.flush();
            }

            //book is available and borrower has borrowed less than 3 books - allow borrow
            if ((book.getStatus().equals(BookStatus.AVAILABLE) || (book.getStatus().equals(BookStatus.ON_HOLD) && book.getHoldQueue().peek().equals(currentUser))) && borrower.getNumBorrowedBooks() < 3){
                borrowBook(book.getTitle());
                if (book.getHoldQueue().contains(getCurrentUser())){
                    book.removeBorrowerHoldQueue(getCurrentUser());
                }
                output.println("You have successfully borrowed " + book.getTitle() + ". Due date is " + book.getDueDate());
                output.println("To acknowledge completion, hit Enter: ");
                output.flush();

                while (true) {
                    //user entered next line as confirmation, return to main menu
                    if (scanner.nextLine().trim().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        else{
            output.println("Borrowing cancelled.");
            output.flush();
            return false;
        }
        return true;
    }

    public boolean returnBook(Scanner scanner, PrintWriter output){
            output.println("-------------------------");
            Borrower borrower = findBorrower(currentUser);

            //check if borrower has any borrowed books
            if (borrower.getNumBorrowedBooks() == 0) {
                output.println("You have no books currently borrowed.");
                output.flush();
                return true; // return to menu
            }

            //display borrowed books
            output.println("Your borrowed books:");
            List<Book> borrowedBooks = borrower.getBorrowedBooks();
            for (int i = 0; i < borrowedBooks.size(); i++) {
                Book b = borrowedBooks.get(i);
                output.println((i + 1) + ". " + b.getTitle() + " by " + b.getAuthor() + " - Due: " + b.getDueDate());
            }
            output.println("Which book would you like to return? (Enter number): ");
            output.flush();

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                Book bookToReturn = borrowedBooks.get(choice - 1);

                //check for pending holds
                if (!bookToReturn.getHoldQueue().isEmpty()) {
                    bookToReturn.setStatus(BookStatus.ON_HOLD);
                }
                else {
                    bookToReturn.setStatus(BookStatus.AVAILABLE);
                }

                borrower.removeBorrowedBook(bookToReturn); //remove book from original borrower account
                bookToReturn.setCurrentBorrower(null); //clear current borrower
                borrower.decreaseNumBorrowedBooks(); //decrease number of books borrowed
            }
        return true;
    }

    //Getters
    public String getCurrentUser(){
        return currentUser;
    }

    public int getNumBooks(){
        return books.size();
    }

    public int getNumBorrowers(){
        return borrowers.size();
    }

    public List<Book> getBooks(){
        return books;
    }

    public List<Borrower> getBorrowers(){
        return borrowers;
    }

    public Book getBookByTitle(String title){
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public Book getBookByNumber(int index) {
        return books.get(index - 1);
    }

    public Borrower findBorrower(String username) {
        for (Borrower borrower : borrowers) {
            if (borrower.getUsername().equals(username)) {
                return borrower;
            }
        }
        return null;
    }
}
