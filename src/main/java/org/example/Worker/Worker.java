package org.example.Worker;

import org.example.Request.Request;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker implements Runnable {
    private static final String DB_URL = "jdbc:sqlite:mydatabase.db";
    private final String host;
    private final int port;
    private Connection connection;
    private final int id;
    private final ExecutorService executorService;

    public Worker(String host, int port, int id) {
        this.host = host;
        this.port = port;
        this.id = id;
        connectToDatabase();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    private void connectToDatabaseSQLite() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Worker connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connectToDatabase() {
        String hostname = "db";
        int port = 3306;
        String database = "mydatabase";
        String username = "myuser";
        String password = "mypassword";
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Worker connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String processRequest(Request request) {
        WorkerTask workerTask = new WorkerTask(connection);
        return workerTask.processRequest(request);
    }

    @Override
    public void run() {
        startWorker();
    }

    public void startWorker() {
        try (ServerSocket workerSocket = new ServerSocket(port)) {
            while (true) {
                Socket loadBalancerSocket = workerSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(loadBalancerSocket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(loadBalancerSocket.getOutputStream(), StandardCharsets.UTF_8));

                String requestStr;
                while ((requestStr = reader.readLine()) != null) {
                    final String finalRequestStr = requestStr;
                    executorService.submit(() -> {
                        Request request = Request.fromJson(finalRequestStr);
                        System.out.println("Worker " + id + " port: " + port +" processing request: " + request.toString());
                        String response = processRequest(request);

                        try {
                            writer.write(response + "\n");
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
