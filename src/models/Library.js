///models/library.js
const Book = require('./book');
const BookStatus = require('./bookstatus');
const Borrower = require('./borrower');


class Library {
    static MAX_BOOKS = 3;

    constructor() {
        this.books = [];
        this.borrowers = [];
        this.currentUser = null;
    }

    initializeLibrary() {
        this.books = [];
        this.borrowers = [];

        this.borrowers.push(new Borrower("alice", "pass123"));
        this.borrowers.push(new Borrower("bob", "pass456"));
        this.borrowers.push(new Borrower("charlie", "pass789"));

        this.books.push(new Book("The Great Gatsby", "F. Scott Fitzgerald"));
        this.books.push(new Book("To Kill a Mockingbird", "Harper Lee"));
        this.books.push(new Book("1984", "George Orwell"));
        this.books.push(new Book("Pride and Prejudice", "Jane Austen"));
        this.books.push(new Book("The Hobbit", "J.R.R. Tolkien"));
        this.books.push(new Book("Harry Potter", "J.K. Rowling"));
        this.books.push(new Book("The Catcher in the Rye", "J.D. Salinger"));
        this.books.push(new Book("Animal Farm", "George Orwell"));
        this.books.push(new Book("Lord of the Flies", "William Golding"));
        this.books.push(new Book("Jane Eyre", "Charlotte Brontë"));
        this.books.push(new Book("Wuthering Heights", "Emily Brontë"));
        this.books.push(new Book("Moby Dick", "Herman Melville"));
        this.books.push(new Book("The Odyssey", "Homer"));
        this.books.push(new Book("Hamlet", "William Shakespeare"));
        this.books.push(new Book("War and Peace", "Leo Tolstoy"));
        this.books.push(new Book("The Divine Comedy", "Dante Alighieri"));
        this.books.push(new Book("Crime and Punishment", "Fyodor Dostoevsky"));
        this.books.push(new Book("Don Quixote", "Miguel de Cervantes"));
        this.books.push(new Book("The Iliad", "Homer"));
        this.books.push(new Book("Ulysses", "James Joyce"));
    }

    // --- Authentication ---
    authenticateUser(username, password) {
        if (this.confirmLogin(username, password)) {
            return { success: true, message: "Authentication successful" };
        }
        return { success: false, message: "Authentication unsuccessful" };
    }

    confirmLogin(username, password) {
        for (let borrower of this.borrowers) {
            if (borrower.username === username && borrower.password === password) {
                this.currentUser = borrower.username;
                return true;
            }
        }
        return false;
    }

    // --- Notifications ---
    getAvailableBooksForNotification(user) {
        return this.books.filter(
            book =>
                book.holdQueue.length > 0 &&
                book.status === BookStatus.ON_HOLD &&
                book.holdQueue[0] === user
        );
    }

    notifyOfAvailableBooks() {
        if (!this.currentUser) return [];

        const borrower = this.findBorrower(this.currentUser);
        const notifications = [];

        for (let book of this.getAvailableBooksForNotification(this.currentUser)) {
            if (borrower.numBorrowedBooks < Library.MAX_BOOKS && !borrower.borrowedBooks.includes(book)) {
                notifications.push(`Book: ${book.title}, previously on hold is now available`);
            }
        }
        return notifications;
    }


    // --- Borrow / Return Books ---
    addBookOnHold(title) {
        const book = this.getBookByTitle(title);
        if (!book.holdQueue.includes(this.currentUser)) {
            book.holdQueue.push(this.currentUser);
        }
        return true;
    }

    borrowBook(title) {
        const book = this.getBookByTitle(title);
        const borrower = this.findBorrower(this.currentUser);

        if (borrower.numBorrowedBooks < Library.MAX_BOOKS) {
            book.setDueDate();
            book.currentBorrower = borrower;
            book.status = BookStatus.CHECKED_OUT;
            borrower.numBorrowedBooks += 1;
            borrower.borrowedBooks.push(book);
            return true;
        }
        return false;
    }

    displayBookCollection() {
        if (!this.currentUser) return [];

        const borrower = this.findBorrower(this.currentUser);
        const collection = [];

        collection.push(`Current number of books borrowed: ${borrower.numBorrowedBooks}`);
        collection.push("---Collection of Books---");

        this.books.forEach((book, index) => {
            let statusMessage = "";

            if (book.status === BookStatus.CHECKED_OUT) {
                statusMessage = `Status: CHECKED_OUT, Due: ${book.dueDate}`;
            } else if (book.status === BookStatus.AVAILABLE) {
                statusMessage = `Status: AVAILABLE`;
            } else if (book.status === BookStatus.ON_HOLD) {
                if (book.holdQueue.length > 0 && book.holdQueue[0] === this.currentUser) {
                    statusMessage = `Status: AVAILABLE`;
                } else {
                    statusMessage = `Status: ON_HOLD`;
                }
            }

            collection.push(`${index + 1}. Title: ${book.title}, Author: ${book.author}, ${statusMessage}, Borrower: ${book.currentBorrower || 'N/A'}`);        });

        return collection; // Front-end can iterate and render each line
    }

    selectBookToBorrow(bookIndex, wantsHold = false) {
        const book = this.getBookByNumber(bookIndex);
        const borrower = this.findBorrower(this.currentUser);
        if (!book || !borrower) return { success: false, message: "Invalid selection" };

        if ((book.status === BookStatus.AVAILABLE || (book.status === BookStatus.ON_HOLD && book.holdQueue[0] === this.currentUser))
            && borrower.numBorrowedBooks < Library.MAX_BOOKS) {

            this.borrowBook(book.title);
            if (book.holdQueue.includes(this.currentUser)) {
                book.holdQueue = book.holdQueue.filter(u => u !== this.currentUser);
            }
            return { success: true, message: `You have successfully borrowed ${book.title}. Due date: ${book.dueDate}` };
        }

        return this.attemptBookBorrow(this.currentUser, book.title, wantsHold);
    }

    attemptBookBorrow(username, title, wantsHold = false) {
        const borrower = this.findBorrower(username);
        const book = this.getBookByTitle(title);

        if (!book || !borrower) return { success: false, message: "Invalid operation" };

        // logic for checked out, on hold, limits
        if (book.status === BookStatus.CHECKED_OUT && book.currentBorrower !== borrower && borrower.numBorrowedBooks <= Library.MAX_BOOKS) {
            if (wantsHold) this.addBookOnHold(book.title);
            return { success: false, message: wantsHold ? "Book added to hold queue" : "Book currently checked out" };
        }

        if (book.status === BookStatus.ON_HOLD && !book.holdQueue.includes(this.currentUser)) {
            if (wantsHold) this.addBookOnHold(book.title);
            return { success: false, message: wantsHold ? "Book added to hold queue" : "Book on hold by another borrower" };
        }

        if (book.status === BookStatus.AVAILABLE && borrower.numBorrowedBooks === Library.MAX_BOOKS) {
            if (wantsHold) this.addBookOnHold(book.title);
            return { success: false, message: "Reached borrow limit, hold added if requested" };
        }

        if (book.status === BookStatus.ON_HOLD && borrower.numBorrowedBooks === Library.MAX_BOOKS) {
            return { success: false, message: "Reached borrow limit, cannot borrow" };
        }

        return { success: false, message: "Unable to borrow book" };
    }

    returnBook(bookTitle) {
        const borrower = this.findBorrower(this.currentUser);
        if (!borrower || borrower.borrowedBooks.length === 0) {
            return { success: false, message: "No books to return" };
        }

        const bookToReturn = borrower.borrowedBooks.find(b => b.title === bookTitle);
        if (!bookToReturn) return { success: false, message: "Invalid book" };

        this.processBookReturn(this.currentUser, bookToReturn);

        return {
            success: true,
            message: `You have successfully returned "${bookToReturn.title}"`,
            borrowedCount: borrower.borrowedBooks.length,
            bookStatus: bookToReturn.status,
            bookTitle: bookToReturn.title
        };
    }

    canReturnBooks(username) {
        const borrower = this.findBorrower(username);
        return borrower.borrowedBooks.length > 0;
    }

    processBookReturn(username, bookToReturn) {
        const borrower = this.findBorrower(username);
        if (!borrower || borrower.numBorrowedBooks === 0) return false;

        if (bookToReturn.holdQueue.length > 0) {
            bookToReturn.status = BookStatus.ON_HOLD;
        } else {
            bookToReturn.status = BookStatus.AVAILABLE;
        }

        borrower.borrowedBooks = borrower.borrowedBooks.filter(b => b !== bookToReturn);
        bookToReturn.currentBorrower = null;
        bookToReturn.clearDueDate();
        borrower.numBorrowedBooks -= 1;
        return true;
    }

    // --- Logout ---
    logout() {
        if (!this.currentUser) return false;
        if (this.confirmLogout()){return true;}
    }

    confirmLogout() {
        this.currentUser = null;
        return true;
    }

    // --- Getters ---
    getCurrentUser() { return this.currentUser; }
    getNumBooks() { return this.books.length; }
    getNumBorrowers() { return this.borrowers.length; }
    getBooks() { return this.books; }
    getBorrowers() { return this.borrowers; }

    getBookByTitle(title) {
        return this.books.find(b => b.title.toLowerCase() === title.toLowerCase()) || null;
    }

    getBookByNumber(index) {
        return this.books[index - 1] || null;
    }

    findBorrower(username) {
        return this.borrowers.find(b => b.username === username) || null;
    }
}
module.exports = Library;