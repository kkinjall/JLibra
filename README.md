# COMP4004: Library Management System - Assignment 3

### Kinjal Kamboj, 101227444

## Project Overview
This project is a library management system in the form of a web application built with JavaScript, Node.js and Express.

## Setup Instructions
1. Clone the repository from A3-Cypress-Kamboj branch.
2. Open the terminal within the project folder (Command Prompt preferred).
3. Run 'npm install' to install the dependencies.

## Running the Application
1. To start the Express server enter 'npm start'.
2. Open the browser at http://localhost:3000

## Running Cypress Tests
1. In another terminal within the same project folder, enter 'npm run cypress:run' to run Cypress in headless mode. 
2. You can also view the scenarios run through an interface by entering 'npx cypress open', click on E2E Testing in the interface that
pops up, click on Chrome, click on Start E2E Testing in Chrome, and click on library.cy.js to see the scenarios run.

## Use of LLMs
ChatGPT was used to convert the Java code for the Book, Borrower, and Library classes, along with the BookStatus enum to
JavaScript. It was also used for the frontend development. Cypress UI tests were done without the use of AI.