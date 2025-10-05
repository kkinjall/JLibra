package org.example;

public class Borrower {
    private String username;
    private String password;
    private int numBorrowedBooks;

    //constructor
    public Borrower(String username, String password) {
        this.username = username;
        this.password = password;
        this.numBorrowedBooks = 0;
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
}
