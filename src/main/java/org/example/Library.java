package org.example;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;

    //constructor
    public Library(){
        this.books = new ArrayList<Book>();
    }

    //initialize library with 20 books
    public void initializeLibrary(){
        books.add(new Book("wrong title", "wrong author"));
    }

    public int getNumBooks(){
        return books.size();
    }

    public List<Book> getBooks(){
        return books;
    }

    public Book getBookByTitle(String title){
        return new Book("wrong title", "wrong author");
    }
}
