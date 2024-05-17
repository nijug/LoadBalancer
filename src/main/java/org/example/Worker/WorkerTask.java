package org.example.Worker;

import org.example.Request.Request;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WorkerTask implements WorkerPlan{
    private final Connection connection;

    public WorkerTask(Connection connection) {
        this.connection = connection;
    }
    @Override
    public String processRequest(Request request) {
        String response;
        String query = request.toString();

        try (Statement statement = connection.createStatement()) {
            if (query.trim().toLowerCase().startsWith("select")) {
                ResultSet resultSet = statement.executeQuery(query);
                response = resultSetToString(resultSet);
            } else {
                int updateCount = statement.executeUpdate(query);
                response = "Update count: " + updateCount;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = "Error processing request: " + e.getMessage();
        }

        return response;
    }

    private String resultSetToString(ResultSet resultSet) throws Exception {
        StringBuilder result = new StringBuilder();
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(resultSet.getMetaData().getColumnName(i)).append(": ").append(resultSet.getString(i)).append("\t");
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String DB_URL = "jdbc:sqlite:mydatabase.db";
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            WorkerTask workerTask = new WorkerTask(connection);
            System.out.println("Worker task started");

            Request request = new Request();
            request.setContent("SELECT * FROM books");
            String response = workerTask.processRequest(request);
            System.out.println("Response from database: \n" + response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
