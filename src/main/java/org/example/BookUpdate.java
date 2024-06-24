package org.example;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/**
 * Stage for updating book details in the database.
 * Provides a form to select a book and update its information.
 */
public class BookUpdate extends Stage {
    private TextField idField;
    private TextField titleField;
    private TextField authorField;
    private TextField publisherField;
    private TextField yearField;
    private TextField synopsisField;
    private ComboBox<String> comboBox;
    private boolean isUpdated = false;

    /**
     * Constructs a new BookUpdate stage.
     * Allow the user to choose a book to update from the database.
     *
     * @param primaryScene the primary scene to copy stylesheets from
     */
    public BookUpdate(Scene primaryScene) {
        Label label = new Label("Select a book to update:");
        try {
            List<String> allBooks = Database.allItems();
            comboBox = new ComboBox<>(FXCollections.observableArrayList(allBooks));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button updateButton = new Button("Update book");
        updateButton.setOnAction(event -> {
            String selectedBook = comboBox.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                updateForm(primaryScene, selectedBook);
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> close());

        GridPane grid = new GridPane();
        grid.addRow(0, updateButton, cancelButton);

        grid.setHgap(10);
        grid.setVgap(10);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(label, comboBox, grid);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));  // Adding padding to VBox

        Scene scene = new Scene(vbox, 400, 400);  // Set initial width and height for the scene
        scene.getStylesheets().addAll(primaryScene.getStylesheets());
        setMinWidth(400);
        setMinHeight(400);
        setScene(scene);
        setTitle("Update Book");
    }

    /**
     * Updates the form with details of the selected book.
     *
     * @param primaryScene the primary scene to copy stylesheets from
     * @param selectedBook string containing all the information about the book to update
     */
    private void updateForm(Scene primaryScene, String selectedBook) {
        setTitle("Update Book");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label idLabel = new Label("Id:");
        idField = new TextField();
        idField.setEditable(false);  // ID should not be editable
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

        loadBookDetails(selectedBook);

        Button updateButton = new Button("Update");
        updateButton.setOnAction(event -> {
            updateBook();
            close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> close());
        gridPane.addRow(6, updateButton, cancelButton);

        Scene scene = new Scene(gridPane, 400, 400);
        scene.getStylesheets().addAll(primaryScene.getStylesheets());
        setMinWidth(400);
        setMinHeight(400);
        setScene(scene);
    }

    /**
     * Loads the details of the selected book into the form fields.
     *
     * @param selectedBook the selected book string from the ComboBox
     */
    private void loadBookDetails(String selectedBook) {
        String[] details = selectedBook.split(";");
        idField.setText(details[0]);
        titleField.setText(details[1]);
        authorField.setText(details[2]);
        publisherField.setText(details[3]);
        yearField.setText(details[4]);
        synopsisField.setText(details.length > 5 ? details[5] : "");
    }

    /**
     * Tries to update the book in the database using the input from the text fields.
     * If the input is valid and the book is successfully updated, sets isUpdated to true.
     */
    private void updateBook() {
        try {
            int id = Integer.parseInt(idField.getText());
            String title = titleField.getText();
            String author = authorField.getText();
            String publisher = publisherField.getText();
            int year = Integer.parseInt(yearField.getText());
            String synopsis = synopsisField.getText().isEmpty() ? null : synopsisField.getText();

            Database.updateBook(id, title, author, publisher, year, synopsis);
            isUpdated = true;
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the book was successfully updated.
     *
     * @return true if the book was updated, false otherwise
     */
    public boolean isUpdated() {
        return isUpdated;
    }
}
