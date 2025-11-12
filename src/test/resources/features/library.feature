Feature: Library Operations

#  Background: The library system is initialized
#    Given the library system is initialized with all 20 books and 3 accounts

  #Scenario 1: Corresponding to A1 A-TEST-01,
  # test the basic borrow-return cycle with two users and one book
  Scenario Outline: A borrowed book becomes unavailable, but when returned is available again
    Given the library system is initialized with all 20 books and 3 accounts
    When "<user1>" logs in the system
    And "<user1>" attempts to borrow "<book>"
    Then "<user1>" can borrow the book "<book>"

    When "<user1>" logs out the system
    And "<user2>" logs in the system
    And "<user2>" attempts to borrow "<book>"
    Then "<user2>" cannot borrow "<book>"
    And "<user2>" places a hold on "<book>"

    When "<user2>" logs out the system
    And "<user1>" logs in the system
    And "<user1>" attempts to return the book "<book>"
    Then "<user1>" successfully returns the book "<book>"
    And "<book>"'s status is ON_HOLD

    When "<user1>" logs out the system
    And "<user2>" logs in the system
    And "<user2>" attempts to borrow "<book>"
    Then "<user2>" can borrow the book "<book>"

    Examples:
      | book                | user1 | user2   |
      | The Iliad           | alice | bob     |
      | The Great Gatsby    | alice | charlie |
      | Pride and Prejudice | bob   | charlie |


  # Scenario 2: Test the hold queue system with three users competing for the same book
  Scenario Outline: Hold queue processes correctly for multiple users
    Given the library system is initialized with all 20 books and 3 accounts
    When "<user1>" logs in the system
    And "<user1>" attempts to borrow "<book>"
    Then "<user1>" can borrow the book "<book>"

    When "<user1>" logs out the system
    And "<user2>" logs in the system
    And "<user2>" attempts to borrow "<book>"
    Then "<user2>" cannot borrow "<book>"
    And "<user2>" places a hold on "<book>"

    When "<user2>" logs out the system
    And "<user3>" logs in the system
    And "<user3>" attempts to borrow "<book>"
    Then "<user3>" cannot borrow "<book>"
    And "<user3>" places a hold on "<book>"
    And the hold queue for "<book>" should list "<user2>" first and "<user3>" second

    When "<user3>" logs out the system
    And "<user1>" logs in the system
    And "<user1>" attempts to return the book "<book>"
    Then "<user1>" successfully returns the book "<book>"

    When "<user1>" logs out the system
    And "<user2>" logs in the system
    Then "<user2>" should receive a notification that "<book>" is available

    When "<user2>" attempts to borrow "<book>"
    Then "<user2>" can borrow the book "<book>"

    When "<user2>" attempts to return the book "<book>"
    Then "<user2>" successfully returns the book "<book>"

    When "<user2>" logs out the system
    And "<user3>" logs in the system
    Then "<user3>" should receive a notification that "<book>" is available

    When "<user3>" attempts to borrow "<book>"
    Then "<user3>" can borrow the book "<book>"

    Examples:
      | book     | user1   | user2   | user3   |
      | Hamlet   | alice   | bob     | charlie |
      | Ulysses  | charlie | alice   | bob     |
      | Hamlet   | bob     | charlie | alice   |


#  # Scenario 3: Test the interaction between borrowing limits and holds
  Scenario Outline: Borrowing limits interact with hold requests
    #CANNOT DO  Given "<user1>" has borrowed "<book2>", "<book3>", "<book4>" and "<user2>" borrowed "<book1>"
    Given the library system is initialized with all 20 books and 3 accounts
    When "<user2>" logs in the system
    And "<user2>" attempts to borrow "<book1>"
    Then "<user2>" can borrow the book "<book1>"

    When "<user2>" logs out the system
    And "<user1>" logs in the system
    And "<user1>" attempts to borrow "<book2>"
    Then "<user1>" can borrow the book "<book2>"

    When "<user1>" attempts to borrow "<book3>"
    Then "<user1>" can borrow the book "<book3>"

    When "<user1>" attempts to borrow "<book4>"
    Then "<user1>" can borrow the book "<book4>"
    And "<user1>" is at the borrow limit

    When "<user1>" attempts to borrow "<book1>"
    Then "<user1>" cannot borrow "<book1>"
    And the system should allow "<user1>" to place a hold on "<book1>"

    When "<user1>" attempts to return the book "<book4>"
    Then "<user1>" successfully returns the book "<book4>"
    And "<book4>"'s status is AVAILABLE
    And "<user1>"'s number of borrowed books should decrease by one to be 2

    When "<user1>" logs out the system
    And "<user2>" logs in the system
    And "<user2>" attempts to return the book "<book1>"
    Then "<user2>" successfully returns the book "<book1>"
    And "<book1>"'s status is ON_HOLD

    When "<user2>" logs out the system
    And "<user1>" logs in the system
    Then "<user1>" should receive a notification of "<book1>" being available

    Examples:
      | user1   | user2    | book1                   | book2                | book3                  | book4  |
      | alice   | bob      | Pride and Prejudice     | The Great Gatsby     | To Kill A Mockingbird  | 1984   |
      | bob     | charlie  | The Divine Comedy       | Don Quixote          | Wuthering Heights      | Hamlet |
      | charlie | alice    | Pride and Prejudice     | Crime and Punishment | Lord of the Flies      | 1984   |

  # Scenario 4: Test system behaviour when users have no borrowed books
  Scenario Outline: System correctly handles when users have no books borrowed
    Given the library system is initialized with all 20 books and 3 accounts
    When "<user>" logs in the system
    And "<user>" attempts to return a book
    Then the system reports that "<user>" has no borrowed books
    And all books in the library appear available

    Examples:
      | user    |
      | alice   |
      | bob     |
      | charlie |