package org.example.LoadBalancer;

import org.example.Request.Request;
import org.example.Worker.WorkerLoads;
import org.example.Worker.WorkerManager;
import org.example.Worker.WorkerPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class LoadBalancer {
    private static final Logger logger = LogManager.getLogger(LoadBalancer.class);
    private final ExecutorService executorService;
    private final WorkerManager workerManager;
    private final WorkerLoads workerLoads;
    private int currentIndex = 0;
    private String schedAlgo;
    private int port;

    public LoadBalancer(WorkerManager workerManager, String schedAlgo, int numThreads, int port) {
        this.workerManager = workerManager;
        this.workerLoads = new WorkerLoads(workerManager.getWorkerCount());
        executorService = Executors.newFixedThreadPool(numThreads);
        this.schedAlgo = schedAlgo;
        this.port = port;
    }

    /* funkcjonalność LBrequestServer została robita pod inna strukture projektu,
    czesc odpowiedzialna za procsowanie reqesta do workera została zamieniona na przekazywanie requesta który jest procesowany juz w klasie Worker,
    czesc odpowiedzialna za odbieranie i w zwracanie requesta dko klienta jest niżej*/
    public void distributeRequest(Socket clientSocket) {
        executorService.submit(() -> {
            try {
                int selectedWorkerIndex = selectWorker();
                WorkerPlan selectedWorker = workerManager.getWorker(selectedWorkerIndex);
                incrementLoad(selectedWorkerIndex);

                Request clientRequest = getClientRequest(clientSocket);
                String response = selectedWorker.processRequest(clientRequest);
                sendResponseToClient(clientSocket, response);

                decrementLoad(selectedWorkerIndex);
            } catch (IOException e) {
                logger.error("Error processing request", e);
            }
        });
    }

    private Request getClientRequest(Socket clientSocket) throws IOException {
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        return Request.fromNetworkString(clientReader.readLine());
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
        } else { // defaultowo to Round Robin
            selectedWorkerIndex = (currentIndex + 1) % workerManager.getWorkerCount();
            currentIndex = selectedWorkerIndex;
        }
        return selectedWorkerIndex;
    }

    private void incrementLoad(int workerIndex) {
        if (schedAlgo.equals("LC")) { // least connections
            workerLoads.incrementLoad(workerIndex);
        }
    }

    private void decrementLoad(int workerIndex) {
        if (schedAlgo.equals("LC")) { // least connections
            workerLoads.decrementLoad(workerIndex);
        }
    }


    public void startLoadBalancer() {
        try (ServerSocket balancerSocket = new ServerSocket(port)) {
            while (!Thread.interrupted()) {
                Socket clientSocket = balancerSocket.accept();
                distributeRequest(clientSocket);
            }
        } catch (IOException e) {
            logger.error("Error starting load balancer", e);
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
        WorkerManager workerManager = new WorkerManager(10, WorkerPlan.class);
        LoadBalancer loadBalancer = new LoadBalancer(workerManager, args[0], Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        loadBalancer.startLoadBalancer();
    }
}