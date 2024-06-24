package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class BookAdd extends Stage {

    private final TextField idField;
    private final TextField titleField;
    private final TextField authorField;
    private final TextField publisherField;
    private final TextField yearField;
    private final TextField synopsisField;

    private boolean bookAddedSuccessfully = false;

    /**
     * Constructs a new BookAdd stage.
     * Allows the user to add a book to the database.
     *
     * @param primaryScene the primary scene to copy stylesheets from
     */
    public BookAdd(Scene primaryScene) {
        setTitle("Add New Book");

        GridPane gridPane = new GridPane();

        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label idLabel = new Label("Id:");
        idField = new TextField();
        gridPane.addRow(0, idLabel, idField);

        Label titleLabel = new Label("Title:");
        titleField = new TextField();
        gridPane.addRow(1, titleLabel, titleField);

        Label authorLabel = new Label("Author:");
        authorField = new TextField();
        gridPane.addRow(2, authorLabel, authorField);

        Label publisherLabel = new Label("Publisher:");
        publisherField = new TextField();
        gridPane.addRow(3, publisherLabel, publisherField);

        Label yearLabel = new Label("Year:");
        yearField = new TextField();
        gridPane.addRow(4, yearLabel, yearField);

        Label synopsisLabel = new Label("Synopsis:");
        synopsisField = new TextField();
        gridPane.addRow(5, synopsisLabel, synopsisField);

        Button addButton = new Button("Add");
        addButton.setOnAction(event -> {
            addBook();
            close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> close());
        gridPane.addRow(6, addButton, cancelButton);

        Scene scene = new Scene(gridPane);
        scene.getStylesheets().addAll(primaryScene.getStylesheets());
        setMinWidth(400);
        setMinHeight(400);
        setScene(scene);
    }

    /**
     * Tries to add a new book to the database using the input from the text fields.
     * If the input is valid and the book is successfully added, sets bookAddedSuccessfully to true.
     */
    private void addBook() {
        try {
            int id = Integer.parseInt(idField.getText());
            String title = titleField.getText();
            String author = authorField.getText();
            String publisher = publisherField.getText();
            int year = Integer.parseInt(yearField.getText());
            String synopsis = synopsisField.getText();

            Database.addBook(id, title, author, publisher, year, synopsis);
            bookAddedSuccessfully = true;
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Public method to check if the book was successfully added to the database.
     *
     * @return true if the book was added, false otherwise
     */
    public boolean isBookAddedSuccessfully() {
        return bookAddedSuccessfully;
    }
}
