package org.example.Worker;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/* w oryginale workerzy są czytani z pliku ale to mało eleganckie wiec zrobiłem tą klasę*/

/* implemetacja na generyku żeby manager był niezależny od implementacji workerów */
public class WorkerManager<T extends WorkerPlan> {

    private final List<T> workers;

    public WorkerManager(int numWorkers, Class<T> workerClass, Object... constructorArgs) {
        this.workers = new ArrayList<>();
        try {
            Class<?>[] constructorArgClasses = new Class[constructorArgs.length];
            for (int i = 0; i < constructorArgs.length; i++) {
                if (constructorArgs[i] instanceof Integer) {
                    constructorArgClasses[i] = int.class; // Use int.class for Integer
                } else {
                    constructorArgClasses[i] = constructorArgs[i].getClass();
                }
            }
            Constructor<T> constructor = workerClass.getDeclaredConstructor(constructorArgClasses);

            for (int i = 0; i < numWorkers; i++) {
                workers.add(constructor.newInstance(constructorArgs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T getWorker(int index) {
        return workers.get(index);
    }

    public int getWorkerCount() {
        return workers.size();
    }

}