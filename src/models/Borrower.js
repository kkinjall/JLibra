//models/Borrower.js
class Borrower {
    constructor(username, password) {
        this.username = username;
        this.password = password;
        this.numBorrowedBooks = 0;
        this.borrowedBooks = []; // array of Book objects
        this.BORROW_LIMIT = 3;
    }

    // getters
    getUsername() {
        return this.username;
    }

    getPassword() {
        return this.password;
    }

    getNumBorrowedBooks() {
        return this.numBorrowedBooks;
    }

    getBorrowedBooks() {
        return this.borrowedBooks;
    }

    // actions
    addBorrowedBook(book) {
        this.borrowedBooks.push(book);
        this.numBorrowedBooks += 1;
    }

    removeBorrowedBook(book) {
        this.borrowedBooks = this.borrowedBooks.filter(b => b !== book);
        if (this.numBorrowedBooks > 0) {
            this.numBorrowedBooks -= 1;
        }
    }

    atBorrowLimit() {
        return this.numBorrowedBooks >= this.BORROW_LIMIT;
    }
}

module.exports = Borrower;