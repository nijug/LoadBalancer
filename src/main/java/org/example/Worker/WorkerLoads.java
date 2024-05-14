package org.example.Worker;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerLoads {
    private final ArrayList<AtomicInteger> workerLoads = new ArrayList<>();

    public WorkerLoads(int num_servers) {
        for (int i = 0; i < num_servers; i++)
            workerLoads.add(new AtomicInteger(0));
    }

    public synchronized int getMinLoadServer() {
        int minLoad = workerLoads.get(0).get(), min_ind = 0;
        for (int i = 1; i < workerLoads.size(); i++) {
            int thisLoad = workerLoads.get(i).get();
            if (thisLoad < minLoad) {
                minLoad = thisLoad;
                min_ind = i;
            }
        }
        return min_ind;
    }

    public void incrementLoad(int index){
        workerLoads.get(index).incrementAndGet();
    }

    public void decrementLoad(int index){
        workerLoads.get(index).decrementAndGet();
    }
}