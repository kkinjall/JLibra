package org.example;

/*Main file that initiates and runs the library system*/
public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        library.initializeLibrary();

        boolean session = true;
        while (session) {
            System.out.println("===== Library System =====");
        }
    }
}