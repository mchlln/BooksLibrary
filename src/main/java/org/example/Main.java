package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Main class for the Books Database application.
 * Handles GUI setup and database operations.
 */
public class Main extends Application {

    private TextArea outputTextArea;
    private TextField queryTextField;
    private Scene scene;

    /**
     * Initializes and sets up the JavaFX application.
     * @param primaryStage The primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);

        queryTextField = new TextField();
        queryTextField.setPromptText("Enter SQL query (Example query : SELECT * FROM BOOKS)");

        Button viewBooksButton = new Button("Show Books");
        viewBooksButton.setOnAction(event -> viewAllBooks());

        Button simpleQueryButton = new Button("Filter By");
        simpleQueryButton.setOnAction(e -> sendSimpleQuery(scene));

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> addBook(scene));

        Button clearLogButton = new Button("Clear Log");
        clearLogButton.setOnAction(e -> outputTextArea.clear());

        Button updateBookButton = new Button("Update Book");
        updateBookButton.setOnAction(e -> updateBook(scene));

        Button deleteBookButton = new Button("Delete Book");
        deleteBookButton.setOnAction(e -> removeBook(scene));

        Button userQueryButton = new Button("Send a query");
        userQueryButton.setOnAction(e -> executeQuery());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        gridPane.add(viewBooksButton, 0, 0);
        gridPane.add(addBookButton, 0, 1);
        gridPane.add(deleteBookButton, 0, 2);
        gridPane.add(updateBookButton, 0, 3);
        gridPane.add(simpleQueryButton, 0, 4);
        gridPane.add(clearLogButton, 0, 5);

        VBox outputBox = new VBox();
        outputBox.getChildren().addAll(outputTextArea, queryTextField, userQueryButton);
        outputBox.setSpacing(10);
        outputBox.setPadding(new Insets(10));

        //General menu
        Menu menu = new Menu("Menu");
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        MenuItem saveItem = new MenuItem("Save to file");
        saveItem.setOnAction(e -> saveDbToFile(primaryStage));
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e-> primaryStage.close());
        MenuItem loadFileItem = new MenuItem("Load File");
        loadFileItem.setOnAction(e-> loadFile(primaryStage));

        HBox topContainer = new HBox(menuBar);
        topContainer.setPadding(new Insets(10));
        topContainer.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(gridPane);
        root.setRight(outputBox);

        scene = new Scene(root, 800, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/light.css")).toExternalForm());

        // Secondary menu : style of application
        Menu styleMenu = new Menu("Style");
        MenuItem lightMode = new MenuItem("Light");
        lightMode.setOnAction(e -> scene.getStylesheets().setAll(
                Objects.requireNonNull(getClass().getResource("/styles/light.css")).toExternalForm()));
        MenuItem darkMode = new MenuItem("Dark");
        darkMode.setOnAction(e -> scene.getStylesheets().setAll(
                Objects.requireNonNull(getClass().getResource("/styles/dark.css")).toExternalForm()));
        MenuItem pastelMode = new MenuItem("Pastel");
        pastelMode.setOnAction(e -> scene.getStylesheets().setAll(
                Objects.requireNonNull(getClass().getResource("/styles/pastel.css")).toExternalForm()));

        styleMenu.getItems().addAll(lightMode, darkMode, pastelMode);
        menu.getItems().addAll(loadFileItem, saveItem, exitItem, styleMenu);

        primaryStage.setTitle("Books Database");
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            Database.launchDatabase(Database.dataScript);
            log("Database initialized successfully with the default file.");
            viewAllBooks();
        } catch (SQLException e) {
            e.printStackTrace();
            log("Failed to initialize database: " + e.getMessage());
        }
    }

    /**
     * Opens a dialog to add a new book to the database.
     * The user needs to fill out the form to add a book.
     * @param scene The current scene of the application.
     */
    private void addBook(Scene scene) {
        BookAdd bookForm = new BookAdd(scene);
        bookForm.showAndWait();

        if (bookForm.isBookAddedSuccessfully()) {
            log("Book added successfully.");
        } else {
            log("Failed to add a book. Please check input values.");
        }
        viewAllBooks();
    }

    /**
     * Opens a dialog to remove a book from the database.
     * The user needs to choose a book in a scrolling menu.
     * @param scene The current scene of the application.
     */
    private void removeBook(Scene scene) {
        BookDelete bookForm = new BookDelete(scene);
        bookForm.showAndWait();

        if (bookForm.isBookRemovedSuccessfully()) {
            log("Book removed successfully.");
        } else {
            log("Failed to remove a book. Please check input values.");
        }
        viewAllBooks();
    }

    /**
     * Opens a dialog to update book information in the database.
     * The user needs to choose a book in a scrolling menu and then fill out a form
     * with the modifications.
     * @param scene The current scene of the application.
     */
    private void updateBook(Scene scene) {
        BookUpdate bookForm = new BookUpdate(scene);
        bookForm.showAndWait();

        if (bookForm.isUpdated()) {
            log("Book updated successfully.");
        } else {
            log("Failed to update a book. Please check input values.");
        }
        viewAllBooks();
    }

    /**
     * Opens a dialog to execute a simple SQL query.
     * The user has to choose a field to query and fill out what he is looking for.
     * @param scene The current scene of the application.
     */
    private void sendSimpleQuery(Scene scene){
        SimpleQuery query = new SimpleQuery(scene);
        query.showAndWait();

        if(query.isQueryOver()){
            log("Successfully sent a simple query.");
            log("Searched for " + query.getSelectedField() + "=" +query.getData());
            if(query.getResult().isEmpty()){
                log("No results found.");
            }else{
                log(query.getResult());
            }
        }else {
            log("Failed to send a simple query. Please check input values.");
        }
    }

    /**
     * Saves the database contents to a file.
     * @param primaryStage The primary stage of the application.
     */
    private void saveDbToFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save DB File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try {
                Database.exportDatabase(selectedFile.getAbsolutePath());
                log("Database saved successfully in file: " + selectedFile.getAbsolutePath());
            } catch (SQLException | IOException e) {
                log("Error saving the database to the file : "+ selectedFile.getAbsolutePath() + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads data into the database from a file.
     * @param primaryStage The primary stage of the application.
     */
    private void loadFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select SQL File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try {
                Database.launchDatabase(selectedFile.getAbsolutePath());
                log("Data loaded successfully from file: " + selectedFile.getAbsolutePath());
                viewAllBooks();
            } catch (SQLException e) {
                log("Error loading the data from the file : "+ selectedFile.getAbsolutePath() + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches and displays all books from the database.
     */
    private void viewAllBooks() {
        try {
            String booksInfo = Database.viewAllBooksAsString();
            if(booksInfo.isEmpty()){
                log("No books found.");
            }else{
                log(booksInfo);
            }

        } catch (SQLException e) {
            log("Error fetching books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes a custom SQL query entered by the user.
     */
    private void executeQuery() {
        String query = queryTextField.getText().trim();
        if (!query.isEmpty()) {
            try {
                log(Database.executeQuery(query));
            } catch (SQLException e) {
                log("Error executing query: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            log("Please enter an SQL query.");
        }
    }

    /**
     * Logs messages to the output text area of the application.
     * @param message The message to log.
     */
    private void log(String message) {
        outputTextArea.appendText(message + "\n");
    }

    /**
     * Main method to launch the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
