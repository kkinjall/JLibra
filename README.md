# JLibra

## Project Overview
This project is a library management system in the form of a web application built with Node.js, Express, and JavaScript. Cypress was used to write UI tests. 
This project also involved Test-Driven-Development (TDD) through JUnit test cases in the master branch, and Behaviour-Driven-Development (BDD) test cases with a Cucumber framework and Gherkin scenarios in the A2-BDD-Kamboj branch. 

## Setup Instructions for Running TDD and BDD Tests
1. Clone the reposiotry from the master and A2-BDD-Kamboj branch.
2. For master branch, simply run the tests through running the test file. For the second branch, simply run the RunCucumberTest.java file.

## Setup Instructions for Running Cypress UI Tests
1. Clone the repository from A3-Cypress-Kamboj branch.
2. Open the terminal within the project folder (Command Prompt preferred). You will have to open it inside the directory where you can see the README, server.js, package.json and similar config files.
3. Run 'npm install' to install the dependencies.

## Running the Application
1. To start the Express server enter 'npm start'.
2. Open the browser at http://localhost:3000

## Running Cypress Tests
1. In another terminal within the same project folder, enter 'npm run cypress:run' to run Cypress in headless mode. 
2. You can also view the scenarios run through an interface by entering 'npx cypress open', click on E2E Testing in the interface that
pops up, click on Chrome, click on Start E2E Testing in Chrome, and click on library.cy.js to see the scenarios run.
