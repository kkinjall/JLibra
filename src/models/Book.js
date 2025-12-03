//models/Book.js

class Book {
    static BORROW_DAYS = 14;

    constructor(title, author) {
        this.title = title;
        this.author = author;
        this.status = 'AVAILABLE';
        this.dueDate = null;
        this.holdQueue = [];
        this.currentBorrower = null;
    }

    // getters
    getStatus() {
        return this.status;
    }

    getTitle() {
        return this.title;
    }

    getAuthor() {
        return this.author;
    }

    getDueDate() {
        return this.dueDate;
    }

    getHoldQueue() {
        return this.holdQueue;
    }

    getCurrentBorrower() {
        return this.currentBorrower;
    }

    // setters / actions
    setStatus(newStatus) {
        this.status = newStatus;
    }

    setDueDate() {
        const now = new Date();
        now.setDate(now.getDate() + Book.BORROW_DAYS);
        this.dueDate = now;
    }

    clearDueDate() {
        this.dueDate = null;
    }

    addHoldQueue(username) {
        if (!this.holdQueue.includes(username)) {
            this.holdQueue.push(username);
        }
    }

    removeBorrowerHoldQueue(username) {
        this.holdQueue = this.holdQueue.filter(u => u !== username);
    }

    setCurrentBorrower(borrower) {
        this.currentBorrower = borrower;
    }
}

module.exports = Book;