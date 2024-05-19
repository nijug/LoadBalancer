package org.example.Request;

import com.google.gson.Gson;

public class Request {
    private String content;

    // dla stringów
    public static Request fromString(String networkString) {
        Request request = new Request();
        request.setContent(networkString);
        return request;
    }

    public static Request fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Request.class);
    }

    //serializacja obiekut request do jsona
    public String toJson() {
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