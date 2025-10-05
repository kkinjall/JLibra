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
        books.clear();

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

    public int getNumBooks(){
        return books.size();
    }

    public List<Book> getBooks(){
        return books;
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
