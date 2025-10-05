package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Library {
    private List<Book> books;
    private List<Borrower> borrowers;

    //constructor
    public Library(){
        this.books = new ArrayList<Book>();
        this.borrowers = new ArrayList<Borrower>();
    }

    //initialize library with 20 books
    public void initializeLibrary(){
        books.clear();

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
                output.println("Authentication successful!");
                output.flush();
                return true;
            }
        }
        output.println("Authentication unsuccessful");
        output.flush();
        return false;
    }

    public String getCurrentUser(){
        return "no one";
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
}
