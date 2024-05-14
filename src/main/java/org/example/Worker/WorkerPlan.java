package org.example.Worker;

import org.example.Request.Request;

public interface WorkerPlan {
    String processRequest(Request request);
}
