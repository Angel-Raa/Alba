package com.github.alba.core;

import org.json.JSONObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = -318743879132789673L;
    private final Map<String, String> headers = new HashMap<>();
    private int status = 200;
    private Object body;

    public Response() {
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
    }

    // Constructor para JSON con estado
    public Response(int status, JSONObject body) {
        this.status = status;
        this.body = body;
        this.headers.put("Content-Type", "application/json");
    }

    // Constructor para texto plano (text/plain)
    public Response(int status, String body) {
        this.status = status;
        this.body = body;
        this.headers.put("Content-Type", "text/plain");
    }




    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public void setBody(JSONObject body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    // Método para construir la respuesta
    public String build() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(status).append(" OK\r\n");

        // Agregar cabeceras
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        responseBuilder.append("\r\n");

        // Si el cuerpo es un JSONObject, se agrega como texto JSON
        if (body instanceof JSONObject) {
            responseBuilder.append(((JSONObject) body).toString());
        } else if (body instanceof String) {
            // Si el cuerpo es un String, lo agregamos como texto plano
            responseBuilder.append(body.toString());
        }

        return responseBuilder.toString();
    }


}
