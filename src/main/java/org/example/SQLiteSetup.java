package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/* do wygnerowania testowej bazy danych w sqlite */
public class SQLiteSetup {
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");

            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY," +
                    "title TEXT NOT NULL," +
                    "author TEXT NOT NULL," +
                    "genre TEXT NOT NULL," +
                    "year_published INTEGER NOT NULL" +
                    ");";
            Statement stmt = connection.createStatement();
            stmt.execute(sqlCreateTable);

            String[] genres = {"Fiction", "Non-Fiction", "Science Fiction", "Fantasy", "Mystery", "Thriller", "Romance"};

            for (int i = 1; i <= 100; i++) {
                String title = "Book Title " + i;
                String author = "Author " + i;
                String genre = genres[i % genres.length];
                int year_published = 1950 + (i % 71);

                String sqlInsertData = "INSERT INTO books (id, title, author, genre, year_published) VALUES (" +
                        i + ", '" + title + "', '" + author + "', '" + genre + "', " + year_published + ");";
                stmt.execute(sqlInsertData);
            }

            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}