package org.example.Client;

import org.example.Request.Request;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {
    private final String host;
    private final int port;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String sendRequest(String query) {
        StringBuilder responseBuilder = new StringBuilder();

        try (Socket clientSocket = new Socket(host, port);
             BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {
            System.out.println("query: " +query);
            Request requestToLB = Request.fromString(query);
            clientWriter.write(requestToLB.toJson() + "\n");
            clientWriter.flush();
            String line;
            while ((line = clientReader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }

    public void sendPeriodicRequest() {
        final Random random = new Random();
        final Runnable requestSender = () -> {
            try {
                while (true) {
                    System.out.println("Sending request to server");
                    String response = sendRequest("SELECT * FROM books WHERE id < 5");
                    System.out.println("Response from server: " + response);

                    // Sleep for a random period between 0.1 and 0.5 seconds
                    Thread.sleep((random.nextInt(5) + 1) * 100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        scheduler.schedule(requestSender, 0, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Client <number of clients> <LB port>");
            return;
        }

        int numClients;
        int LBport;
        try {
            numClients = Integer.parseInt(args[0]);
            LBport = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of clients or client port. Please enter valid integers.");
            return;
        }

        for (int i = 0; i < numClients; i++) {
            Client client = new Client("localhost", LBport);
            client.sendPeriodicRequest();
        }
    }
}