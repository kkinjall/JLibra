package org.example;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;

public class Book {
    private String title;
    private String author;
    private BookStatus status;
    private LocalDate dueDate;
    private Queue<String> holdQueue;
    private Queue<String> borrowQueue;

    private static int days = 14;

    //constructor
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.status = BookStatus.AVAILABLE;
        this.dueDate = null;
        this.holdQueue = new LinkedList<>();
        this.borrowQueue = new LinkedList<>();
    }

    public BookStatus getStatus(){
        return status;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthor(){
        return author;
    }

    public LocalDate getDueDate(){
        return dueDate;
    }

    public Queue<String> getHoldQueue() {
        return holdQueue;
    }

    public Queue<String> getBorrowQueue() {
        return borrowQueue;
    }


    //setters
    public void setStatus(BookStatus newStatus){
        status = newStatus;
    }

    public void setDueDate(){
        dueDate = LocalDate.now().plusDays(days);
    }

    public void addHoldQueue(String username){
        holdQueue.add(username);
    }

    public void addBorrowQueue(String username){
        borrowQueue.add(username);
    }
}
