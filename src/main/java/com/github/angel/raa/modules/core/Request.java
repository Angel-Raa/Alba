package com.github.angel.raa.modules.core;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa una solicitud HTTP.
 * Contiene información sobre el método HTTP, encabezados, cuerpo y parámetros.
 * Además, proporciona métodos para acceder a estos datos.
 */
public class Request {
    private final String clientIp;
    private final Map<String, String> headers;
    private final String method;
    private final String path;
    private final JSONObject body;
    private final Map<String, String> queryParams = new HashMap<>();
    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, Object> sessionAttributes = new HashMap<>(); // Nuevo: Atributos de sesión
    private Map<String, String> params; // Parámetros dinámicos


    public Request(Socket clientSocket, String method, String path, Map<String, String> headers, JSONObject body) {
        this.clientIp = clientSocket.getInetAddress().getHostAddress();
        this.method = method;
        this.path = extractPathWithoutQuery(path);
        this.headers = headers;
        this.body = body != null ? body : new JSONObject();


        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            String queryString = path.substring(queryIndex + 1);
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int equalsIndex = pair.indexOf('=');
                if (equalsIndex != -1) {
                    String key = decode(pair.substring(0, equalsIndex));
                    String value = decode(pair.substring(equalsIndex + 1));
                    queryParams.put(key, value);
                }
            }
        }
    }

    /**
     * Este método es útil para procesar solicitudes entrantes.
     * Construye una instancia de Request a partir de un InputStream y un Socket.
     * Lanza una IOException si la solicitud está vacía o malformada.
     *
     * @param inputStream
     * @param clientSocket
     * @return Request
     * @throws IOException
     */
    public static Request buildRequest(InputStream inputStream, Socket clientSocket) throws IOException {
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

        // Leer los encabezados HTTP
        String line;
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

        return new Request(clientSocket, method, path, headers, body);
    }

    /**
     * Este método es útil para extraer la ruta sin la consulta.
     *
     * @param fullPath
     * @return Ruta sin la consulta.
     */
    private static String extractPathWithoutQuery(String fullPath) {
        int queryIndex = fullPath.indexOf('?');
        return queryIndex != -1 ? fullPath.substring(0, queryIndex) : fullPath;
    }

    /**
     * Este método es útil para decodificar valores de parámetros de ruta y consulta.
     *
     * @param value
     * @return Valor decodificado.
     */
    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value; // Devolver el valor original si falla la decodificación
        }
    }

    /**
     * Obtiene la dirección IP del cliente.
     *
     * @return IP
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * Obtiene el método HTTP de la solicitud.
     *
     * @return Método HTTP de la solicitud.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Obtiene la ruta solicitada.
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Obtiene los encabezados de la solicitud.
     *
     * @return Encabezados de la solicitud.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Obtiene el cuerpo de la solicitud.
     *
     * @return Cuerpo de la solicitud.
     */
    public JSONObject getBody() {
        return body;
    }

    /**
     * Obtiene el valor de un encabezado específico.
     *
     * @param key
     * @return Valor del encabezado o null si no se encuentra.
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * Obtiene el valor de un parámetro específico.
     *
     * @param key
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Obtiene el valor de un atributo específico.
     *
     * @param key
     * @return Valor del atributo o null si no se encuentra.
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Obtiene los parámetros de la solicitud.
     *
     * @return Parámetros de la solicitud.
     */
    public Map<String, String> getParams() {
        return params != null ? params : new HashMap<>();
    }

    /**
     * Obtiene los parámetros de la solicitud.
     *
     * @param params
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * Obtiene el valor de un parámetro de ruta.
     * Si el parámetro no existe, devuelve null.
     * Si el parámetro existe pero no tiene valor, devuelve una cadena vacía.
     *
     * @param key
     * @return Valor del parámetro de ruta o null si no existe.
     * @throws IllegalArgumentException
     */
    public String getPathParam(String key) throws IllegalArgumentException {
        return params.getOrDefault(key, null);
    }

    /**
     * Obtiene el valor de un parámetro de ruta como Long.
     * Si el valor no es un número válido, devuelve null.
     * Si el valor es null, devuelve null.
     * Si el valor es un número válido, devuelve el valor como Long.
     *
     * @param key
     * @return Valor del parámetro de ruta como Long o null si no es un número válido.
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Long getPathParamAsLong(String key) throws NullPointerException, IllegalArgumentException {
        String value = params.get(key);
        return value != null ? Long.parseLong(value) : null;
    }

    /**
     * Obtiene el valor de un parámetro de ruta como Integer.
     * Si el valor no es un número válido, devuelve null.
     * Si el valor es null, devuelve null.
     * Si el valor es un número válido, devuelve el valor como Integer.
     *
     * @param key
     * @return Valor del parámetro de ruta como Integer o null si no es un número válido.
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Integer getPathParamAsInt(String key) throws NullPointerException, IllegalArgumentException {
        String value = params.get(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    /**
     * Obtiene el valor de un parámetro de ruta como Double.
     * Si el valor no es un número válido, devuelve null.
     * Si el valor es null, devuelve null.
     * Si el valor es un número válido, devuelve el valor como Double.
     *
     * @param key
     * @return Valor del parámetro de ruta como Double o null si no es un número válido.
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Double getPathParamAsDouble(String key) throws IllegalArgumentException {
        String value = params.get(key);
        return value != null ? Double.parseDouble(value) : null;
    }

    /**
     * Obtiene el valor de un parámetro de consulta.
     * Si el parámetro no existe, devuelve null.
     * Si el parámetro existe pero no tiene valor, devuelve una cadena vacía.
     *
     * @param key
     * @return Valor del parámetro de consulta o null si no existe.
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public String getQueryParam(String key) throws NullPointerException, IllegalArgumentException {
        return queryParams.getOrDefault(key, null);
    }

    /**
     * Obtiene el valor de un parámetro de consulta como Long.
     * Si el valor no es un número válido, devuelve null.
     * Si el valor es null, devuelve null.
     *
     * @param key
     * @return Valor del parámetro de consulta como Long o null si no es un número válido.
     * @throws NumberFormatException
     * @throws NullPointerException
     */
    public Long getQueryParamAsLong(String key) throws NumberFormatException, NullPointerException {
        String value = queryParams.get(key);
        return value != null ? Long.parseLong(value) : null;
    }

    /**
     * Obtiene el valor de un parámetro de consulta como Integer.
     * Si el valor no es un número válido, devuelve null.
     * Si el valor es null, devuelve null.
     *
     * @param key
     * @return Valor del parámetro de consulta como Integer o null si no es un número válido.
     * @throws NumberFormatException
     * @throws NullPointerException
     */
    public Integer getQueryParamAsInt(String key) throws NumberFormatException, NullPointerException {
        String value = queryParams.get(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    /**
     * Obtiene el valor de un parámetro de consulta como Double.
     * Si el valor no es un número válido, devuelve null.
     * Si el valor es null, devuelve null.
     *
     * @param key
     * @return Valor del parámetro de consulta como Double o null si no es un número válido.
     * @throws NumberFormatException
     * @throws NullPointerException
     */
    public Double getQueryParamAsDouble(String key) throws NumberFormatException, NullPointerException {
        String value = queryParams.get(key);
        return value != null ? Double.parseDouble(value) : null;
    }

    /**
     * Obtene el valor de un atributo de sesión.
     *
     * @param key
     * @return Valor del atributo de sesión o null si no se encuentra.
     * @throws NullPointerException
     */
    public Object getSessionAttribute(String key) throws NullPointerException {
        return sessionAttributes.get(key);
    }
}
