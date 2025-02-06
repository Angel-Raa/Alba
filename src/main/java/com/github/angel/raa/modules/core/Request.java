package com.github.angel.raa.modules.core;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final Map<String, String> headers;
    private final String method;
    private final String path;
    private final JSONObject body;
    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, Object> sessionAttributes = new HashMap<>(); // Nuevo: Atributos de sesión

    // Constructor para inicializar el Request
    public Request(String method, String path, Map<String, String> headers, JSONObject body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body != null ? body : new JSONObject(); // Asegúrate de que el body no sea null
    }

    // Método para construir el Request a partir de un InputStream
    public static Request buildRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Solicitud vacía");
        }

        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            throw new IOException("Solicitud malformada");
        }

        String method = requestParts[0]; // GET, POST, etc.
        String path = requestParts[1]; // Ruta solicitada
        Map<String, String> headers = new HashMap<>();
        String line;

        // Leer los encabezados HTTP
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // Leer el cuerpo de la solicitud si el método es POST o PUT
        StringBuilder bodyBuilder = new StringBuilder();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            while (reader.ready()) {
                bodyBuilder.append((char) reader.read());
            }
        }

        // Convertir el cuerpo en JSONObject si no está vacío
        JSONObject body = new JSONObject();
        if (!bodyBuilder.isEmpty()) {
            body = new JSONObject(bodyBuilder.toString());
        }

        return new Request(method, path, headers, body);
    }

    // Getters para acceder a los valores
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public JSONObject getBody() {
        return body;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object getSessionAttribute(String key) {
        return sessionAttributes.get(key);
    }

    public Throwable getParams() {
        return (Throwable) attributes.get("params");
    }
}
