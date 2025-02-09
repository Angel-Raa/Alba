package io.github.angel.raa.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
 * Clase Request que representa una solicitud HTTP entrante.
 * Contiene información sobre el cliente, el método, la ruta, los encabezados y el cuerpo de la solicitud.
 * También proporciona métodos para acceder a los datos de la solicitud.
 * Además, se encarga de parsear el cuerpo de la solicitud si está presente y en formato JSON.
 * También se encarga de parsear los parámetros de la ruta y los parámetros de consulta.
 *
 * <p>
 * Ejemplos de uso:
 * <pre>{@code
 *         Request request = new Request(clientSocket, method, path, headers, body);
 *         String clientIp = request.getClientIp();
 *         String method = request.getMethod();
 *         String path = request.getPath();
 *         Map<String, String> headers = request.getHeaders();
 *         JSONObject body = request.getBody();
 *         }
 *      </pre>
 * <pre>{@code
 *         String userId = request.getParams().get("id");
 *         String name = request.getBody().getString("name");
 *         }
 *      </pre>
 * </p>
 * @Author Angel Aguero
 */
public class Request {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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
     */
    private static String extractPathWithoutQuery(String fullPath) {
        int queryIndex = fullPath.indexOf('?');
        return queryIndex != -1 ? fullPath.substring(0, queryIndex) : fullPath;
    }

    /**
     * Este método es útil para decodificar valores de parámetros de ruta y consulta.
     *
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

     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * Obtiene el método HTTP de la solicitud.
     *
     */
    public String getMethod() {
        return method;
    }

    /**
     * Obtiene la ruta solicitada.
     *
     */
    public String getPath() {
        return path;
    }

    /**
     * Obtiene los encabezados de la solicitud.
     *

     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Obtiene el cuerpo de la solicitud.
     *

     */
    public JSONObject getBody() {
        return body;
    }

    /**
     * Obtiene el valor de un encabezado específico.
     *
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * Obtiene el valor de un parámetro específico.
     *
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Obtiene el valor de un atributo específico.
     *
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Obtiene los parámetros de la solicitud.
     *
     */
    public Map<String, String> getParams() {
        return params != null ? params : new HashMap<>();
    }

    /**
     * Obtiene los parámetros de la solicitud.
     *
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * Obtiene el valor de un parámetro de ruta.
     * Si el parámetro no existe, devuelve null.
     * Si el parámetro existe pero no tiene valor, devuelve una cadena vacía.
     *
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

     */
    public Double getPathParamAsDouble(String key) {
        String value = params.get(key);
        return value != null ? Double.parseDouble(value) : null;
    }

    /**
     * Obtiene el valor de un parámetro de consulta.
     * Si el parámetro no existe, devuelve null.
     * Si el parámetro existe pero no tiene valor, devuelve una cadena vacía.
     *
     */
    public String getQueryParam(String key) throws NullPointerException, IllegalArgumentException {
        return queryParams.getOrDefault(key, null);
    }

    /**
     * Obtiene el valor de un parámetro de consulta como Long.
     * Si el valor no es un número válido, devuelve null.
     * Si el valor es null, devuelve null.
     *

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

     */
    public Double getQueryParamAsDouble(String key) throws NumberFormatException, NullPointerException {
        String value = queryParams.get(key);
        return value != null ? Double.parseDouble(value) : null;
    }

    /**
     * Obtene el valor de un atributo de sesión.
     *

     */
    public Object getSessionAttribute(String key) throws NullPointerException {
        return sessionAttributes.get(key);
    }

    /**
     * Obtiene un valor del cuerpo como String.
     *

     */
    public String getBodyString(String key) {
        return body.has(key) ? body.getString(key) : null;
    }

    /**
     * Obtiene un valor del cuerpo como Long.
     *

     */
    public Long getBodyLong(String key) {
        return body.has(key) ? body.getLong(key) : null;
    }

    /**
     * Obtiene un valor del cuerpo como Integer.
     *
     */
    public Integer getBodyInt(String key) {
        return body.has(key) ? body.getInt(key) : null;
    }

    /**
     * Obtiene un valor del cuerpo como Double.
     *
     */
    public Double getBodyDouble(String key) {
        return body.has(key) ? body.getDouble(key) : null;
    }

    /**
     * Obtiene el cuerpo como un mapa de objetos.
     *
     */
    public Map<String, Object> getBodyAsMap() {
        return body.toMap();
    }

    public <T> T getBodyAs(Class<T> clazz) throws IllegalArgumentException {
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("El cuerpo de la solicitud está vacío");
        }
        try {
            return objectMapper.readValue(body.toString(), clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir el cuerpo a la clase especificada", e);
        }
    }

    /**
     * Obtiene el cuerpo como un objeto de la clase especificada.
     */
    public <T> T getBodyAs(TypeReference<T> typeReference) throws IllegalArgumentException {
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("El cuerpo de la solicitud está vacío");
        }
        try {
            return objectMapper.readValue(body.toString(), typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir el cuerpo a la clase especificada", e);
        }
    }
}
