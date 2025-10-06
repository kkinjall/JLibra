package org.example;

import java.io.PrintWriter;
import java.util.Scanner;

/*Main file that initiates and runs the library system*/
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PrintWriter output = new PrintWriter(System.out, true);

        Library library = new Library();
        library.initializeLibrary();

        boolean session = true;
        while (session) {
            System.out.println("===== Library System =====");

            //Login Loop
            boolean loggedIn = false;
            while (!loggedIn) {
                loggedIn = library.authenticateUser(scanner, output);
            }

            // Main menu loop
            boolean inMenu = true;
            while (inMenu) {
                library.displayMenu(output);

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        while (true){
                            library.displayBookCollection(new Scanner("1\n"), output);
                            if (library.selectBookToBorrow(scanner, output)) {
                                break;
                            }
                        }
                        break;
                }
            }
        }
    }
}