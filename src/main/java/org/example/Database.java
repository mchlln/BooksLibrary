package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for interacting with an in-memory H2 database.
 */
public class Database {
    private static final String H2_MEM_URL = "jdbc:h2:mem:default";
    private static Connection currentConnection;
    static String schemaScript = "src/main/resources/schema.sql";
    static String dataScript = "src/main/resources/default.sql";

    /**
     * Instantiate the H2 in-memory database and load it with the schema and data from provided scripts.
     *
     * The database will stay in the memory and keep its content until JVM is terminated or the connection
     * is explicitly closed.
     *
     * @param dataScript sql script containing commands to insert data into tables
     * @throws SQLException sql exception
     */
    public static void launchDatabase(String dataScript) throws SQLException {
        closeCurrentConnection();

        String url = String.format("%s;DB_CLOSE_DELAY=-1;INIT=runscript from '%s'\\;runscript from '%s'",
                H2_MEM_URL, schemaScript, dataScript);

        currentConnection = DriverManager.getConnection(url);
    }

    /**
     * Closes the current database connection if it is open.
     *
     * @throws SQLException If there is an issue with closing the connection.
     */
    private static void closeCurrentConnection() throws SQLException {
        if (currentConnection != null && !currentConnection.isClosed()) {
            currentConnection.close();
            currentConnection = null;
        }
    }

    /**
     * Retrieves a connection to the in-memory H2 database.
     *
     * @return A valid database Connection object.
     * @throws SQLException If there is an issue with establishing the connection.
     */
    private static Connection getConnection() throws SQLException {
        if (currentConnection == null || currentConnection.isClosed()) {
            currentConnection = DriverManager.getConnection(H2_MEM_URL);
        }
        return currentConnection;
    }

    /**
     * Adds a new book entry into the database.
     *
     * @param id        The ID of the book.
     * @param title     The title of the book.
     * @param author    The author of the book.
     * @param publisher The publisher of the book.
     * @param year      The publication year of the book.
     * @param synopsis  The synopsis of the book.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static void addBook(int id, String title, String author, String publisher, int year, String synopsis) throws SQLException {
        String insertSQL = "INSERT INTO BOOKS (ID, TITLE, AUTHOR, PUBLISHER, PUBLICATION_YEAR, SYNOPSIS) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, publisher);
            pstmt.setInt(5, year);
            pstmt.setString(6, synopsis);
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a book entry from the database by its ID.
     *
     * @param id The ID of the book to delete.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static void deleteBook(int id) throws SQLException {
        String deleteSQL = "DELETE FROM BOOKS WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing book entry in the database.
     *
     * @param id        The ID of the book to update.
     * @param title     The updated title of the book.
     * @param author    The updated author of the book.
     * @param publisher The updated publisher of the book.
     * @param year      The updated publication year of the book.
     * @param synopsis  The updated synopsis of the book.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static void updateBook(int id, String title, String author, String publisher, int year, String synopsis) throws SQLException {
        String updateSQL = "UPDATE BOOKS SET TITLE = ?, AUTHOR = ?, PUBLISHER = ?, PUBLICATION_YEAR = ?, SYNOPSIS = ? WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, publisher);
            pstmt.setInt(4, year);
            pstmt.setString(5, synopsis);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Executes a SQL query and returns the result as a formatted string.
     *
     * @param query The SQL query to execute.
     * @return A formatted string containing the query result.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static String executeQuery(String query) throws SQLException {
        StringBuilder result = new StringBuilder();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                result.append(printBookAsString(rs)).append("\n");
            }
        }
        return result.toString();
    }

    /**
     * Finds books by the specified author and returns the result as a formatted string.
     *
     * @param author The author to search for.
     * @return A formatted string containing books by the author.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static String findByAuthor(String author) throws SQLException {
        StringBuilder result = new StringBuilder();
        String selectSQL = "SELECT * FROM BOOKS WHERE AUTHOR = ?";
        return getString(author, result, selectSQL);
    }

    /**
     * Finds books by the specified title and returns the result as a formatted string.
     *
     * @param title The title to search for.
     * @return A formatted string containing books with the title.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static String findByTitle(String title) throws SQLException {
        StringBuilder result = new StringBuilder();
        String selectSQL = "SELECT * FROM BOOKS WHERE TITLE = ?";
        return getString(title, result, selectSQL);
    }

    /**
     * Executes a SQL query with one parameter and returns the result as a formatted string.
     *
     * @param title  The parameter value for the SQL query.
     * @param result StringBuilder to accumulate the formatted results.
     * @param selectSQL The SQL SELECT query template with a placeholder for the parameter.
     * @return A formatted string containing the result of the SQL query.
     * @throws SQLException If there is an issue with SQL execution.
     */
    private static String getString(String title, StringBuilder result, String selectSQL) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, title);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.append(printBookAsString(rs)).append("\n");
                }
            }
        }
        return result.toString();
    }

    /**
     * Finds books by the specified publisher and returns the result as a formatted string.
     *
     * @param publisher The publisher to search for.
     * @return A formatted string containing books published by the publisher.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static String findByPublisher(String publisher) throws SQLException {
        StringBuilder result = new StringBuilder();
        String selectSQL = "SELECT * FROM BOOKS WHERE PUBLISHER = ?";
        return getString(publisher, result, selectSQL);
    }

    /**
     * Finds books published in the specified year and returns the result as a formatted string.
     *
     * @param year The publication year to search for.
     * @return A formatted string containing books published in the year.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static String findByYear(int year) throws SQLException {
        StringBuilder result = new StringBuilder();
        String selectSQL = "SELECT * FROM BOOKS WHERE PUBLICATION_YEAR = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setInt(1, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.append(printBookAsString(rs)).append("\n");
                }
            }
        }
        return result.toString();
    }

    /**
     * Retrieves all books from the database and returns the result as a formatted string.
     *
     * @return A formatted string containing all books in the database.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static String viewAllBooksAsString() throws SQLException {
        StringBuilder result = new StringBuilder();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM BOOKS")) {

            while (rs.next()) {
                result.append(printBookAsString(rs)).append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Format the ResultSet passed as a parameter into a String.
     *
     * @param rs ResultSet containing the result of a query
     * @return a formatted string containing the elements of the rs
     * @throws SQLException If there is an issue with SQL execution.
     */
    private static String printBookAsString(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID");
        String title = rs.getString("TITLE");
        String author = rs.getString("AUTHOR");
        String publisher = rs.getString("PUBLISHER");
        int year = rs.getInt("PUBLICATION_YEAR");
        String synopsis = rs.getString("SYNOPSIS");

        return String.format("ID: %d, Title: %s, Author: %s, Publisher: %s, Year: %d, Synopsis: %s",
                id, title, author, publisher, year, synopsis);
    }

    /**
     * Exports the database contents to a SQL script file.
     *
     * @param outputFile The output file to export the database contents to.
     * @throws SQLException If there is an issue with SQL execution.
     * @throws IOException  If there is an issue with writing to the output file.
     */
    public static void exportDatabase(String outputFile) throws SQLException, IOException {
        File file = new File(outputFile);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             FileWriter writer = new FileWriter(outputFile)) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM BOOKS");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String title = rs.getString("TITLE").replace("'", "''");
                String author = rs.getString("AUTHOR").replace("'", "''");
                String publisher = rs.getString("PUBLISHER").replace("'", "''");
                int year = rs.getInt("PUBLICATION_YEAR");
                String synopsis = rs.getString("SYNOPSIS");

                if (synopsis != null) {
                    synopsis = synopsis.replace("'", "''");
                    writer.write(String.format("INSERT INTO BOOKS (ID, TITLE, AUTHOR, PUBLISHER, PUBLICATION_YEAR, SYNOPSIS) VALUES (%d, '%s', '%s', '%s', %d, '%s');\n",
                            id, title, author, publisher, year, synopsis));
                } else {
                    writer.write(String.format("INSERT INTO BOOKS (ID, TITLE, AUTHOR, PUBLISHER, PUBLICATION_YEAR) VALUES (%d, '%s', '%s', '%s', %d);\n",
                            id, title, author, publisher, year));
                }
            }
        }
    }

    /**
     * Retrieves a list of all book items in the database as formatted strings :
     * each element of a book is separated by a semicolon.
     *
     * @return A list of formatted strings representing each book's details.
     * @throws SQLException If there is an issue with SQL execution.
     */
    public static List<String> allItems() throws SQLException {
        List<String> result = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM BOOKS")) {
            while (rs.next()) {
                int id = rs.getInt("ID");
                String title = rs.getString("TITLE");
                String author = rs.getString("AUTHOR");
                String publisher = rs.getString("PUBLISHER");
                int year = rs.getInt("PUBLICATION_YEAR");
                String synopsis = rs.getString("SYNOPSIS");

                result.add(String.format("%d;%s;%s;%s;%d;%s", id, title, author, publisher, year, synopsis));
            }
        }
        return result;
    }
}
