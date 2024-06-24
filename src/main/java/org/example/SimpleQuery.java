package org.example;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;


/**
 * A JavaFX stage for executing simple queries on a book database.
 * Allows the user to select a field (Title, Author, Year, Publisher) and enter data to search for in the database.
 * Displays the result of the query when executed.
 */
public class SimpleQuery extends Stage {

    private final ComboBox<String> comboBox;
    private boolean isDone = false;
    private final TextField textField;
    private String result;
    private String selectedField;
    private String data;

    /**
     * Constructs a new SimpleQuery stage.
     * Allow the user to do a simple query with one parameter in the WHERE of the query
     *
     * @param primaryScene the primary scene to copy stylesheets from
     */
    public SimpleQuery(Scene primaryScene) {
        Label label = new Label("Select field and and fill out the query\n SELECT * FROM BOOKS WHERE");

        String[] fields = {"Title", "Author", "Year", "Publisher"};
        comboBox = new ComboBox<>(FXCollections.observableArrayList(fields));
        textField = new TextField();

        Button sendButton = new Button("Send query");
        sendButton.setOnAction(event -> {
            selectedField = comboBox.getSelectionModel().getSelectedItem();
            data = textField.getText().trim();
            if (selectedField != null && !data.isEmpty()) {
                try {
                    switch (selectedField) {
                        case "Title":
                            result = Database.findByTitle(data);
                            break;
                        case "Author":
                            result = Database.findByAuthor(data);
                            break;
                        case "Year":
                            result = Database.findByYear(Integer.parseInt(data));
                            break;
                        case "Publisher":
                            result = Database.findByPublisher(data);
                            break;

                    }
                    isDone = true;
                    this.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> close());

        GridPane grid = new GridPane();
        grid.addRow(0, label);
        grid.addRow(1, comboBox,textField);
        grid.addRow(2, sendButton, cancelButton);

        grid.setHgap(10);
        grid.setVgap(10);

        Scene scene = new Scene(grid);
        scene.getStylesheets().addAll(primaryScene.getStylesheets());
        setMinWidth(400);
        setMinHeight(400);
        setScene(scene);
        setTitle("Delete Book");
    }


    /**
     * Checks if the query execution is complete.
     *
     * @return true if the query execution is complete, false otherwise
     */
    public boolean isQueryOver() {
        return isDone;
    }

    /**
     * Retrieves the result of the executed query.
     *
     * @return the result of the executed query
     */
    public String getResult() {
        return result;
    }

    /**
     * Retrieves the selected field for the query.
     *
     * @return the selected field for the query (e.g., "Title", "Author", "Year", "Publisher")
     */
    public String getSelectedField() {
        return selectedField;
    }

    /**
     * Retrieves the data entered for the selected field of the query.
     *
     * @return the data entered for the query
     */
    public String getData() {
        return data;
    }
}
