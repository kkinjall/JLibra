package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Borrower {
    private String username;
    private String password;
    private int numBorrowedBooks;
    private List<Book> booksOnHold;
    private List<Book> borrowedBooks;


    //constructor
    public Borrower(String username, String password) {
        this.username = username;
        this.password = password;
        this.numBorrowedBooks = 0;
        this.booksOnHold = new ArrayList<>();
        this.borrowedBooks = new ArrayList<>();
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public int getNumBorrowedBooks(){
        return numBorrowedBooks;
    }

    public List<Book> getBooksOnHold(){
        return booksOnHold;
    }

    public void addNumBorrowedBooks(){
        numBorrowedBooks += 1;
    }

    public void addBorrowedBook(Book book){
        borrowedBooks.add(book);
    }

    public List<Book> getBorrowedBooks(){
        return borrowedBooks;
    }
}
