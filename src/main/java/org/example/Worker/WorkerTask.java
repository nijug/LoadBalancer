package org.example.Worker;

import org.example.Request.Request;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WorkerTask{
    private final Connection connection;

    public WorkerTask(Connection connection) {
        this.connection = connection;
    }

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

}
