package org.example;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class BookDelete extends Stage {

    private ComboBox<String> comboBox;
    private boolean isDeleted = false;

    /**
     * Constructs a new BookFormDelete stage.
     * Allow the user to choose a book to delete from the database.
     *
     * @param primaryScene the primary scene to copy stylesheets from
     */
    public BookDelete(Scene primaryScene) {
        Label label = new Label("Select a book to delete:");
        try {
            List<String> allBooks = Database.allItems();
            comboBox = new ComboBox<>(FXCollections.observableArrayList(allBooks));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button deleteButton = new Button("Delete book");
        deleteButton.setOnAction(event -> {
            String selectedBook = comboBox.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                try {
                    Database.deleteBook(getId(selectedBook));
                    isDeleted = true;
                    this.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> close());

        GridPane grid = new GridPane();
        grid.addRow(0, deleteButton, cancelButton);

        grid.setHgap(10);
        grid.setVgap(10);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(label, comboBox, grid);
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox);
        scene.getStylesheets().addAll(primaryScene.getStylesheets());
        setMinWidth(400);
        setMinHeight(400);
        setScene(scene);
        setTitle("Delete Book");
    }

    /**
     * Extracts the ID of the selected book from the ComboBox item.
     *
     * @param selectedBook the selected book string from the ComboBox
     * @return the ID of the selected book
     */
    private int getId(String selectedBook) {
        String[] split = selectedBook.split(";");
        return Integer.parseInt(split[0]);
    }

    /**
     * Checks if the book was successfully deleted.
     *
     * @return true if the book was deleted, false otherwise
     */
    public boolean isBookRemovedSuccessfully() {
        return isDeleted;
    }
}

