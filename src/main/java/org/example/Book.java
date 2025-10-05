package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class Book {
    private String title;
    private String author;
    private BookStatus status;
    private Queue<String> holdQueue;

    //constructor
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.status = BookStatus.AVAILABLE;
        this.holdQueue = new LinkedList<>();
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

    public Queue<String> getHoldQueue() {
        return holdQueue;
    }


    //setters
    public void setStatus(BookStatus newStatus){
        status = newStatus;
    }

    public void addHoldQueue(String username){
        holdQueue.add(username);
    }

}
