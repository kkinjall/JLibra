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
        books.add(new Book("We Should All Be Feminists", "Chimamanda Ngozi Adichie"));
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
            book.addBorrowQueue(currentUser);
            book.setStatus(BookStatus.CHECKED_OUT);
            borrower.addNumBorrowedBooks();
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

            if (book.getStatus().equals(BookStatus.CHECKED_OUT) && !book.getBorrowQueue().contains(currentUser) && borrower.getNumBorrowedBooks() < 3){
                output.println("This book is currently checked out. Would you like to place a hold? (y/n)");
                output.flush();
            }

            if (book.getStatus().equals(BookStatus.ON_HOLD) && !book.getHoldQueue().contains(currentUser)){
                output.println("This book is currently on hold by another borrower. Would you like to place a hold? (y/n)");
                output.flush();
            }

            if (book.getStatus().equals(BookStatus.AVAILABLE) && borrower.getNumBorrowedBooks() == 3){
                output.println("You have met the 3 book borrow limit and currently can not borrow this book. Would you like to place a hold? (y/n)");
                output.flush();
            }

        }
        else{
            output.println("Borrowing cancelled.");
            output.flush();
            return false;
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
