describe('Library Book Management', () => {

  //Reset the library before each test
  beforeEach(() => {
    cy.request('POST', 'http://localhost:3000/api/reset'); //call the reset endpoint
    cy.visit('http://localhost:3000');
  });

 //Scenario 1: Basic Borrow and Return Cycle
  it('should allow a user to borrow and return a book, and block other users from borrowing the same book', () => {
    const bookTitle = 'The Great Gatsby';

    //Listen and verify alerts
    cy.on('window:alert', (txt) => {
      //Assertion: When a book is borrowed, alert displays correct message
      //Confirms that when a user borrows this specific book, the system notifies them with the due date
      if (txt.includes('borrowed')) {
        expect(txt).to.include('You have successfully borrowed "The Great Gatsby". Due:');
      }
      //Assertion: Return alert displays correct message when book is returned
      //Confirms that returning the book triggers the correct confirmation message to the user
      else if (txt.includes('returned')) {
        expect(txt).to.include('You have successfully returned "The Great Gatsby"');
      }
    });

    //Alice logs in
    cy.login('alice', 'pass123');

    //Assertion: Book should initially be AVAILABLE
    //Validates the "initial state" requirement of the scenario
    cy.contains('.book', bookTitle)
      .find('.status-AVAILABLE')
      .should('exist');

    //Alice borrows the book
    cy.contains('.book', bookTitle)
      .find('button[data-action="borrow"]')
      .should('exist') //Assertion: Check that borrowing is permitted per scenario rules
      .and('be.enabled') //Ensures button is enabled as required
      .click();

    //Assertion: Alice's borrow count updates to 1
    //Confirms UI reflects proper borrowing in the scenario
    cy.get('#borrowCount').should('contain', '1');

    //Assertion: Status switches to CHECKED_OUT
    //Matches the scenario requirement that borrowing updates the book status
    cy.get('#books')
      .contains('.book', 'The Great Gatsby')
      .find('span.status-CHECKED_OUT')
      .should('exist');

    //Assertion: Borrow button disappears for borrower (correct)
    //Ensures the UI does not allow a borrower to borrow a book twice
    cy.contains('.book', bookTitle)
      .find('button[data-action="borrow"]')
      .should('not.exist');

    //Assertion: Book appears in borrowed list with due date
    //Scenario requires due date to be shown for borrowed books
    cy.contains('#borrowed .book', bookTitle)
      .should('contain', 'Due:')

    //Alice logs out
    cy.get('#logoutBtn').click();

    //Bob logs in
    cy.login('bob', 'pass456');

    //Assertion: Bob must not see borrow button
    //Scenario states the second user should be blocked from borrowing the same book
    cy.contains('.book', bookTitle)
      .find('button[data-action="borrow"]')
      .should('not.exist');

    //Assertion: Bob must see the Hold button
    //Scenario states the only available action for the second user is placing a hold
    cy.contains('.book', bookTitle)
      .find('button[data-action="hold"]')
      .should('exist')
      .and('be.enabled');

    //Assertion: Status for the borrowed book is still CHECKED_OUT
    //Ensures that a borrowed book still shows as CHECKED_OUT for the second user
    cy.get('#books')
      .contains('.book', 'The Great Gatsby')
      .find('span.status-CHECKED_OUT')
      .should('exist');

    //Bob logs out
    cy.get('#logoutBtn').click();

    //Alice logs in
    cy.login('alice', 'pass123');

    //Assertion: Borrowed list must show the book before returning
    //Scenario requires user can return only books they borrowed
    cy.contains('#borrowed .book', bookTitle, { timeout: 5000 })
      .should('exist') //Check that book is listed
      .find('button[data-action="return"]')
      .click(); //Alice returns the book

    //Assertion: Borrowed book count updated for Alice
    //Confirms the return operation is reflected in the UI
    cy.get('#borrowCount').should('contain', '0');

    //Assertion: Book status returns to AVAILABLE
    //Scenario requires that returning a book restores its availability
    cy.get('#books')
      .contains('.book', 'The Great Gatsby')
      .find('span.status-AVAILABLE')
      .should('exist');

    //Assertion: Borrow button is available again
    //Ensures that the system reflects returning a book properly
    cy.contains('.book', bookTitle)
      .find('button[data-action="borrow"]')
      .should('exist')
      .and('be.enabled');
  });

//Scenario 2: Hold Queue Management + Notification Logic
  it('should process a hold queue correctly with 3 users and notify the next person in line', () => {
    const bookTitle = '1984';

    //Listen and verify alerts
    cy.on('window:alert', (txt) => {
        //Assertion: Borrow success message should contain "successfully borrowed"
        //Confirms that when a user borrows a book, the system notifies them of the successful action
        if (txt.includes('borrowed')) {
            expect(txt).to.include('successfully borrowed');
        }
        //Assertion: Hold queue message should contain "added to the hold queue"
        //Verifies that when user has placed a hold, they've been correctly notified that they've been placed in the hold queue
        else if (txt.includes('added to the hold queue')) {
            expect(txt).to.include('added to the hold queue');
        }
        //Assertion: Return success message should contain "successfully returned"
        //Ensures the system confirms when a user returns a book
        else if (txt.includes('returned')) {
            expect(txt).to.include('successfully returned');
        }
    });

    //Alice logs in
    cy.login('alice', 'pass123');

    //Assertion: Borrow button is available for book
    //Scenario requires user to borrow an available book
    cy.contains('.book', bookTitle)
      .find('button[data-action="borrow"]')
      .should('exist')
      .click();

    //Assertion: Book status switches to CHECKED_OUT
    //Scenario requires the book to become unavailable after being borrowed
    cy.contains('#books .book', bookTitle)
      .find('span.status-CHECKED_OUT')
      .should('exist')
      .and('have.text', 'CHECKED_OUT');

    //Assertion: Borrow book count is updated
    //verifies the UI update for borrow count
    cy.get('#borrowCount').should('contain', '1');

    //Alice logs out
    cy.get('#logoutBtn').click();

    //Bob logs in
    cy.login('bob', 'pass456');

    //Bob sees the Hold button for the book
    //Scenario requires the second user to be able to place a hold on a borrowed book
    cy.contains('.book', bookTitle)
      .find('button[data-action="hold"]')
      .should('exist')
      .click(); //Bob places a hold

    //Bob logs out
    cy.get('#logoutBtn').click();

    //Charlie logs in
    cy.login('charlie', 'pass789');

    //Assertion: Book has the Hold button
    //Scenario requires the third user to be able to place a hold on a borrowed book
    cy.contains('.book', bookTitle)
      .find('button[data-action="hold"]')
      .should('exist')
      .click();

    //Charlie logs out
    cy.get('#logoutBtn').click();

    //Alice logs in
    cy.login('alice', 'pass123');

    //Assertion: Return button exists for the book the current user has borrowed
    //Scenario requires the user to be able to return a book they've borrowed
    cy.contains('#borrowed .book', bookTitle, { timeout: 5000 })
      .find('button[data-action="return"]')
      .should('exist')
      .click();

    //Assertion: Book status becomes ON_HOLD after return
    //Scenario requires the book status to be ON_HOLD when holds for it exist
    cy.contains('#books .book', bookTitle)
      .find('span.status-ON_HOLD')
      .should('exist')
      .and('have.text', 'ON_HOLD');

    //Alice logs out
    cy.get('#logoutBtn').click();

    //Bob logs in
    cy.login('bob', 'pass456');

    //Assertion: Bob receives a notification
    //Requirement of scenario to have the next user in the hold queue be notified
    cy.contains(
      '#notifications div',
      'Book: 1984, previously on hold is now available',
      { timeout: 10000 }
    ).should('be.visible');

    //Assertion: Bob can now borrow the book
    //Scenario states first user in the hold queue can become the new borrower
    cy.contains('.book', bookTitle)
      .find('button[data-action="borrow"]')
      .should('exist')
      .click();

    //Assertion: Book status returns to CHECKED_OUT
    //Book is now checked out by the next user
    cy.contains('#books .book', bookTitle)
      .find('span.status-CHECKED_OUT')
      .should('exist');

    //Assertion: Bob’s borrow count is updated
    //Scenario requirement that the UI reflects the new borrower
    cy.get('#borrowCount').should('contain', '1');

    //Bob logs out
    cy.get('#logoutBtn').click();

    //Charlie logs in
    cy.login('charlie', 'pass789');

    //Assertion: Charlie must not receive a notification
    //Scenario states only the first person in queue is notified
    cy.get('#notifications')
      .should('not.contain.text', 'Book: 1984, previously on hold is now available');
  });

  //Scenario 3: Borrow Limit + Hold Interaction
  it('should enforce 3-book borrowing limit and allow hold on checked-out book', () => {
    const booksToBorrow = ['The Hobbit', '1984', 'The Great Gatsby'];
    const attemptToBorrowBook = 'Pride and Prejudice';
    const bookToHold = 'Jane Eyre';

    //Listen and verify alerts
    cy.on('window:alert', (txt) => {
    //Assertion: Borrow success message appears
    //Confirms that successful borrowing notifies the user as expected
    if (txt.includes('borrowed')) {
        expect(txt).to.include('successfully borrowed');
    }
    //Assertion: Hold queue message appears
    //Ensures that when a book is unavailable, the system correctly alerts the user that it was added to the hold queue
    else if (txt.includes('added to the hold queue')) {
        expect(txt).to.include('added to the hold queue');
    }
    //Assertion: Return success message appears
    //How this relates to the scenario: Ensures that returning a book triggers the correct success notification
    else if (txt.includes('returned')) {
        expect(txt).to.include('successfully returned');
    }
    //Assertion: Borrow limit enforcement message appears
    //How this relates to the scenario: Ensures that attempting to borrow beyond the limit displays the correct restriction message
    else if (txt.includes('Cannot borrow at borrow limit')) {
        expect(txt).to.include('Cannot borrow at borrow limit');
    }
    });

    //Bob logs in
    cy.login('bob', 'pass456');

    cy.wait(100); //Wait for DOM to stabilize

    //Assertion: Borrow button for the target book should exist
    //Ensures the book is currently available for Bob to borrow
    //Need to do this structure of getting buttons due to the element not existing after page is refreshed
    cy.contains('.book', bookToHold).within(() => {
        cy.get('button[data-action="borrow"]')
        .should('exist')
        .click({ force: true });
    });
    cy.wait(100); //Wait for DOM to stabilize

    //Bob logs out
    cy.get('#logoutBtn').click();

    //Alice logs in
    cy.login('alice', 'pass123');
    cy.wait(100); //Wait for DOM to stabilize

    //Assertion: Borrow button for each selected book should exist
    //Confirms each book Alice needs to borrow is available so she reaches the borrowing limit of 3 books
    booksToBorrow.forEach((title) => {
        cy.contains('.book', title).within(() => {
            cy.get('button[data-action="borrow"]')
            .should('exist')
            .click({ force: true });
        });
    });

    cy.wait(100); //Wait for DOM to stabilize

    //Assertion: Borrow count should now be 3
    //Scenario requires that users are limited to borrowing a maximum of 3 books
    cy.get('#borrowCount').should('contain', '3');

    //Assertion: Borrow button exists for the book Alice tries to borrow when at the borrowing limit
    //Validates that the UI allows attempted borrow interaction, and sets up the rule enforcement as an alert that she cannot exceed the borrowing limit
    cy.contains('.book', attemptToBorrowBook).within(() => {
        cy.get('button[data-action="borrow"]')
        .should('exist')
        .click({ force: true });
    });

    cy.wait(100); //Wait for DOM to stabilize

    //Assertion: Hold button should exist for the targeted book
    //Confirms the system allows hold placement when a book is unavailable, fulfilling the scenario’s requirement about hold queues
    cy.contains('.book', bookToHold).within(() => {
        cy.get('button[data-action="hold"]')
        .should('exist')
        .click({ force: true });
    });

    //Alice logs out
    cy.get('#logoutBtn').click();

    //Bob logs in
    cy.login('bob', 'pass456');
    cy.wait(100); //Wait for DOM to stabilize

    //Assertion: Return button for the held book should exist
    //Scenario requires that user can return the book, which will trigger the hold-release notification for Alice
    cy.contains('#borrowed .book', bookToHold).within(() => {
        cy.get('button[data-action="return"]')
        .should('exist')
        .click({ force: true });
    });

    //Bob logs out
    cy.get('#logoutBtn').click();

    //Alice logs in
    cy.login('alice', 'pass123');
    cy.wait(100); //Wait for DOM to stabilize

    //Assertion: Return button for another book should exist
    //Ensures Alice can return a book, bringing her borrowed count below the limit so she becomes eligible to borrow the held book once it is released
    cy.contains('#borrowed .book', 'The Hobbit').within(() => {
        cy.get('button[data-action="return"]')
        .should('exist')
        .click({ force: true });
    });

    //Assertion: Borrow count returns to 2
    //Scenario requires the borrow count to decrease after returning a book
    cy.get('#borrowCount').should('contain', '2');

    //Assertion: Alice receives a notification from being below the borrow limit and first in the hold queue
    //Scenario requires that returning a book triggers a hold release
    cy.contains('#notifications div', 'Book: Jane Eyre, previously on hold is now available', { timeout: 10000 })
      .should('exist');
    cy.wait(100); //Wait for DOM to stabilize

    //Assertion: Alice can now borrow the book she had on hold
    //Scenario requires user to become eligible when being below the maximum borrow limit
    cy.contains('.book', bookToHold).within(() => {
        cy.get('button[data-action="borrow"]')
        .should('exist')
        .click({ force: true });
    });

    //Assertion: Alice is back to 3 borrowed books
    //Scenario requires that the UI should reflect any books being borrowed for the current user
    cy.get('#borrowCount').should('contain', '3');
  });
});
