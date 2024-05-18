package org.example.Worker;

import org.example.Request.Request;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Worker implements WorkerPlan {
    private static final String DB_URL = "jdbc:sqlite:mydatabase.db";
    private final String host;
    private final int port;
    private Connection connection;

    public Worker(String host, int port) {
        this.host = host;
        this.port = port;
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String processRequest(Request request) {
        WorkerTask workerTask = new WorkerTask(connection);
        return workerTask.processRequest(request);
    }

    public void startWorker() {
        try (Socket loadBalancerSocket = new Socket(host, port)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(loadBalancerSocket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(loadBalancerSocket.getOutputStream(), StandardCharsets.UTF_8));

            while (true) {
                String requestStr = reader.readLine();
                if (requestStr == null) {
                    break; 
                }

                Request request = Request.fromNetworkString(requestStr);
                String response = processRequest(request);

                writer.write(response + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Worker started");
        Worker worker = new Worker("localhost", 5555);
        worker.startWorker();
    }
}
