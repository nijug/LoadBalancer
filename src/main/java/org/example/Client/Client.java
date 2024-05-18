package org.example.Client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String sendRequest(String query) {
        StringBuilder responseBuilder = new StringBuilder();
        try (Socket clientSocket = new Socket(host, port);
             BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {

            clientWriter.write(query + "\n");
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
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    String response = sendRequest("SELECT * FROM books WHERE id < 5");
                    System.out.println("Response from server: " + response);

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 5555);
        client.sendPeriodicRequest();
    }
}

