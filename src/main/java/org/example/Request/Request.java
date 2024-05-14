package org.example.Request;

import com.google.gson.Gson;

public class Request {
    private String content;

    // overload dla jsona
    public static Request fromNetworkString(String networkString, boolean isJson) {
        if (isJson) {
            Gson gson = new Gson();
            return gson.fromJson(networkString, Request.class);
        } else {
            Request request = new Request();
            request.setContent(networkString);
            return request;
        }
    }

    // dla stringów
    public static Request fromNetworkString(String networkString) {
        Request request = new Request();
        request.setContent(networkString);
        return request;
    }

    public String toNetworkString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}