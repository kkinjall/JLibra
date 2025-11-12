package org.example;

import java.util.ArrayList;
import java.util.List;

public class Borrower {
    private String username;
    private String password;
    private int numBorrowedBooks;
    private List<Book> borrowedBooks;


    //constructor
    public Borrower(String username, String password) {
        this.username = username;
        this.password = password;
        this.numBorrowedBooks = 0;
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

    public List<Book> getBorrowedBooks(){
        return borrowedBooks;
    }

    public void addNumBorrowedBooks(){
        numBorrowedBooks += 1;
    }

    public void addBorrowedBook(Book book){
        borrowedBooks.add(book);
    }

    public boolean atBorrowLimit(){
        return numBorrowedBooks == 3;
    }

    public void removeBorrowedBook(Book book){
        borrowedBooks.remove(book);
    }

    public void decreaseNumBorrowedBooks(){
        if (numBorrowedBooks > 0){
            numBorrowedBooks -= 1;
        }
    }
}
