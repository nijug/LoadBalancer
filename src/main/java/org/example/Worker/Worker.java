package org.example.Worker;

import org.example.Request.Request;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*zaimplementowac main do łączenia sie z LB i bazą danyc, odpalanie workerTask*/

public class Worker implements WorkerPlan {
    private final String host;
    private final int port;

    public Worker(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String processRequest(Request request) {
        String response = "";
        try {
            Socket workerSocket = new Socket(host, port);
            BufferedWriter workerWriter = new BufferedWriter(new OutputStreamWriter(workerSocket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader workerReader = new BufferedReader(new InputStreamReader(workerSocket.getInputStream(), StandardCharsets.UTF_8));

            // Send request to worker and get response.
            workerWriter.write(request.toNetworkString() + "\n");
            workerWriter.flush();
            response = workerReader.readLine();

            workerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void main(String[] args) {
        System.out.println("Worker started");
    }

}
