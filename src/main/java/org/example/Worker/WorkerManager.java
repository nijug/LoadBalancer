package org.example.Worker;


import java.util.ArrayList;
import java.util.List;

/* w oryginale workerzy są czytani z pliku ale to mało eleganckie wiec zrobiłem tą klasę*/

/* implemetacja na generyku żeby manager był niezależny od implementacji workerów */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerManager {
    private final List<Worker> workers;
    private final ExecutorService executorService;

    WorkerManager(int numWorkers, int workerStartPort) {
        this.workers = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            System.out.println("Worker created");
            workers.add(new Worker("localhost", workerStartPort+i, i));
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WorkerManager <number of workers>");
            return;
        }

        int numWorkers;
        try {
            numWorkers = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of workers. Please enter a valid integer.");
            return;
        }

        int workerStartPort;
        try {
            workerStartPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid worker start port. Please enter a valid integer.");
            return;
        }

        System.out.println("WorkerManager started");
        WorkerManager workerManager = new WorkerManager(numWorkers, workerStartPort);
        workerManager.workers.forEach(workerManager.executorService::submit);
        workerManager.executorService.shutdown();
    }
}