package org.example.LoadBalancer;

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LoadBalancer {
    private final ExecutorService executorService;

    private final WorkerLoads workerLoads;
    private int currentIndex = 0;
    private final String schedAlgo;
    private final List<Socket> workerSockets = new ArrayList<>();
    private final int port;

    public LoadBalancer(String schedAlgo, int numWorkers, int port, int workerStartPort) {
        this.workerLoads = new WorkerLoads(numWorkers);
        this.executorService = Executors.newFixedThreadPool(numWorkers);
        this.schedAlgo = schedAlgo;
        this.port = port;

        for (int i = 0; i < numWorkers; i++) {
            try {
                workerSockets.add(new Socket("workermanager", workerStartPort + i));
            } catch (IOException e) {
                throw new RuntimeException("Error connecting to worker", e);
            }
        }
        System.out.println("Load balancer started on port " + port + " with " + numWorkers + " workers.");
    }

    public void distributeRequest(Socket clientSocket) {
        executorService.submit(() -> {
            try {
                int selectedWorkerIndex = selectWorker();
                workerLoads.incrementLoad(selectedWorkerIndex);

                String clientRequest = getClientRequest(clientSocket);
                String response = sendRequestToWorker(selectedWorkerIndex, clientRequest);

                sendResponseToClient(clientSocket, response);
                workerLoads.decrementLoad(selectedWorkerIndex);
            } catch (IOException e) {
               throw new RuntimeException("Error processing client request", e);
            }
        });
    }

    private String getClientRequest(Socket clientSocket) throws IOException {
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        return clientReader.readLine();
    }

    private void sendResponseToClient(Socket clientSocket, String response) throws IOException {
        BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        clientWriter.write(response + "\n");
        clientWriter.flush();
        clientSocket.close();
    }

    private int selectWorker() {
        int selectedWorkerIndex;
        if (schedAlgo.equals("LC")) { // least connections
            selectedWorkerIndex = workerLoads.getMinLoadServer();
        } else { // default to Round Robin
            selectedWorkerIndex = (currentIndex + 1) % workerLoads.getNumWorkers();
            currentIndex = selectedWorkerIndex;
        }
        return selectedWorkerIndex;
    }

    private String sendRequestToWorker(int workerIndex, String request) throws IOException {
        Socket workerSocket = workerSockets.get(workerIndex);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(workerSocket.getOutputStream(), StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(workerSocket.getInputStream(), StandardCharsets.UTF_8));

        writer.write(request + "\n");
        writer.flush();

        return reader.readLine();
    }

    private void startLoadBalancer() {
        try (ServerSocket balancerSocket = new ServerSocket(port)) {
            while (!Thread.interrupted()) {
                long startTime = System.currentTimeMillis();
                Socket clientSocket = balancerSocket.accept();
                //System.out.println("Received request from client: " + clientSocket);
                distributeRequest(clientSocket);
                long processingTime = System.currentTimeMillis() - startTime;
                //System.out.println("Request processing time: " + processingTime + " ms");
            }
        } catch (IOException e) {
           throw new RuntimeException("Error accepting client connection", e);
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java LoadBalancer <scheduling algorithm> <number of workers> <port number> <worker start port>");
            return;
        }

        String schedAlgo = args[0];
        if (!schedAlgo.equals("LC") && !schedAlgo.equals("RR")) {
            System.out.println("Invalid scheduling algorithm. Valid options are 'LC' for Least Connections and 'RR' for Round Robin.");
            return;
        }

        int numWorkers;
        int port;
        int workerStartPort;
        try {
            numWorkers = Integer.parseInt(args[1]);
            port = Integer.parseInt(args[2]);
            workerStartPort = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of workers, port number, or worker start port. Please enter valid integers.");
            return;
        }

        LoadBalancer loadBalancer = new LoadBalancer(schedAlgo, numWorkers, port, workerStartPort);
        loadBalancer.startLoadBalancer();
    }

    }