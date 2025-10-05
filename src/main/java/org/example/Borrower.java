package org.example;

public class Borrower {
    private String username;
    private String password;

    //constructor
    public Borrower(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public int getNumBorrowedBooks(){
        return 1;
    }
}
