# Books Database Management Application

This Java application manages a database of books using JavaFX for the GUI and H2 for data storage. It allows users to perform various operations such as adding, updating, deleting books, executing custom SQL queries, and exporting/importing database files.
The application is specific to the schema in the resource folder.

## Setup Instructions

### Prerequisites

Java Development Kit (JDK) installed.

### Dependencies

JavaFX libraries.

### Running the Application

Clone the repository or download the project files.
Open the project in your IDE or go to the folder of the project.
Set up JavaFX SDK in your IDE if required.
Ensure Maven is installed and configured.
Run the following command in the terminal `mvn clean javafx:run`

## Application usage

Upon launching, the main window displays options to interact with the database.
Use buttons to perform CRUD operations on books.
Enter SQL queries directly or use predefined queries.
When launching the application, you are connected to the database via the default file in the resource folder, empty the first time, but the contents can change if you save the database contents in it.
There is a menu where you can load a different file, save the changes to a file, change styles or exit the application.

## Features

View Books: Displays all books currently in the database.
Add Book: Opens a form to add a new book to the database.
Delete Book: Removes a book from the database.
Update Book: Allows modification of existing book details.
Send a Simple Query: Executes predefined queries based on user input.
Send a Query: Executes custom SQL queries entered by the user.
Save to File: Exports the database to a specified file.
Load File: Imports data from an SQL file into the database.
Style Options: Allows users to switch between different GUI styles (light, dark, pastel).

## Technologies used

JavaFX for building the graphical user interface.
JDBC (H2) for local database management.
File handling for exporting and importing database files.
Styling via CSS for dynamic UI appearance.

## Notes

The project also works without the GUI.