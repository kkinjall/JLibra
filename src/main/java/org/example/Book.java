package org.example;

public class Book {
    private String title;
    private String author;
    private BookStatus status;

    //constructor
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.status = BookStatus.CHECKED_OUT;
    }

    public BookStatus getStatus(){
        return status;
    }

    public String getTitle(){
        return "title";
    }

    public String getAuthor(){
        return author;
    }

}
