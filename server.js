//server.js
const express = require('express');
const path = require('path');
const Library = require('./src/models/Library');

const app = express();
const PORT = 3000;

const library = new Library();
library.initializeLibrary();

//track logged-in user with single user session
let currentUser = null;
app.use(express.json());

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'login.html'));
});
app.use(express.static(path.join(__dirname, 'public')));

//Authentication
app.post('/api/login', (req, res) => {
  const { username, password } = req.body;
  const ok = library.confirmLogin(username, password);
  if (ok) {
    // Library.confirmLogin sets library.currentUser internally; keep server copy too
    currentUser = library.getCurrentUser();
    return res.json({ success: true, user: currentUser });
  }
  return res.status(401).json({ success: false, message: 'Invalid credentials' });
});

//Logout
app.post('/api/logout', (req, res) => {
  library.logout();
  currentUser = null;
  return res.json({ success: true, message: "Successfully logged out" });
});

//Get current user session
app.get('/api/user', (req, res) => {
  if (!currentUser) return res.status(404).json({ error: 'No user logged in' });
  return res.json({ name: currentUser });
});


// Helper to serialize Book objects to plain JSON
function serializeBook(book) {
  const borrowerObj = book.getCurrentBorrower ? book.getCurrentBorrower() : null;
  return {
    title: book.title,
    author: book.author,
    status: book.status,
    dueDate: book.getDueDate ? (book.getDueDate() ? new Date(book.getDueDate()).toLocaleDateString() : null) : null,
    holdQueue: book.getHoldQueue ? book.getHoldQueue() : [],
    currentBorrower: borrowerObj && borrowerObj.getUsername
            ? borrowerObj.getUsername()
            : null,
    reservedFor:
            book.getHoldQueue && book.getHoldQueue().length > 0
            ? book.getHoldQueue()[0]
            : null
            };
      }

//all books
app.get('/api/books', (req, res) => {
  const books = library.getBooks().map(serializeBook);
  res.json(books);
});

//borrower's borrowed books
app.get('/api/borrowed', (req, res) => {
  if (!currentUser) return res.json([]);
  const borrower = library.findBorrower(currentUser);
  if (!borrower) return res.json([]);
  const borrowed = borrower.getBorrowedBooks().map(b => ({
    title: b.title,
    author: b.author,
    dueDate: b.getDueDate ? (b.getDueDate() ? new Date(b.getDueDate()).toLocaleDateString() : null) : null
  }));
  res.json(borrowed);
});

//get any notifications to display
app.get('/api/notifications', (req, res) => {
  if (!currentUser) return res.json([]);
  const notes = library.notifyOfAvailableBooks(); // already returns array of strings
  res.json(notes);
});


//Borrow (attempt)
app.post('/api/borrow/:title', (req, res) => {
  if (!currentUser) return res.status(401).json({ error: 'Not logged in' });

  const title = req.params.title;
  const book = library.getBookByTitle(title);
  if (!book) return res.status(404).json({ error: 'Book not found' });

  //If available or reserved for this user, try to borrow
  if (book.status === 'AVAILABLE' || (book.status === 'ON_HOLD' && book.holdQueue[0] === currentUser)) {
    const ok = library.borrowBook(title);
    if (ok) {
      return res.json({ message: `You have successfully borrowed "${book.title}". Due: ${book.getDueDate ? (book.getDueDate() ? new Date(book.getDueDate()).toLocaleDateString() : null) : 'N/A'}` });
    }
    else {
      const borrower = library.findBorrower(currentUser);
      if (borrower.atBorrowLimit()){
        return res.status(400).json({ error: 'Cannot borrow at borrow limit' });
      }
      else{
        return res.status(400).json({ error: 'Cannot borrow' });
      }
    }
  }

  //Otherwise it's checked out/on hold by someone else
  return res.status(400).json({ error: 'Book is not available to borrow right now' });
});

//Return book
app.post('/api/return/:title', (req, res) => {
    if (!currentUser) return res.status(401).json({ error: 'Not logged in' });
    const title = req.params.title;
    const result = library.returnBook(title);

    if (result.success) {
        return res.json(result);
    } else {
        return res.status(400).json({ error: result.message });
    }
});


//Place hold
app.post('/api/hold/:title', (req, res) => {
  if (!currentUser) return res.status(401).json({ error: 'Not logged in' });

  const title = req.params.title;
  const ok = library.addBookOnHold(title);
  if (ok) {
    return res.json({ message: `You have been added to the hold queue for "${title}".` });
  } else {
    return res.status(400).json({ error: 'Hold failed (book not found or already on hold for you)' });
  }
});


//Reset endpoint
app.post('/api/reset', (req, res) => {
  currentUser = null;
  library.initializeLibrary();
  res.json({ message: 'Library reset' });
});

//Start server
app.listen(PORT, () => {
  console.log(`Library server running at http://localhost:${PORT}`);
});
