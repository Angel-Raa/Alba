package com.github.angel.raa.modules.core;

import com.github.angel.raa.modules.exceptions.RouteException;
import org.json.JSONObject;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa una respuesta HTTP
 *
 * <p>
 *     Ejemplos de uso
 *
 *     <pre>
 *         {@code
 *         Response response = new Response();
 *         response.setStatus(200);
 *         response.addHeader("Content-Type", "text/html");
 *         response.addCookie("session", "1234567890");
 *         response.setBody("Hello World");
 *         }
 *     </pre>
 *     <pre>
 *         {@code
 *         Response response = new Response(200, "Hello World");
 *         }
 * </p>
 */
public class Response<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -318743879132789673L;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();
    private int status = 200;
    private T body;
    private String charset = "UTF-8";

    public Response() {
        headers.put("Content-Type", "application/json; charset=" + charset);
        headers.put("Access-Control-Allow-Origin", "*");
    }

    /**
     * Constructor para JSON (application/json)
     * @param status
     * @param body
     */
    public Response(int status, T body) {
        this.status = status;
        this.body = body;
        headers.put("Content-Type", "application/json; charset=" + charset);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param status
     * @param body
     */
    public Response(int status, T body) {
        this.status = status;
        this.body = body;
        headers.put("Content-Type", "text/plain; charset=" + charset);
    }


    /**
     * Agrega una cabecera al response
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Constructor para archivos (application/octet-stream)
     * @param status
     * @param body
     * @param contentType
     */
    public Response(int status, byte[] body, String contentType) {
        this.status = status;
        this.body = body;
        headers.put("Content-Type", contentType);
    }
    /**
     * Elimina una cabecera del response
     * @param key
     */
    public void removeHeader(String key) {
        headers.remove(key);
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        if (status < 100 || status > 599) {
            throw new IllegalArgumentException("Código de estado HTTP inválido: " + status);
        }
        this.status = status;
    }

    /**
     * Obtiene el cuerpo del response
     * @return
     */
    public void setBody(JSONObject body) {
        this.body = body;
    }

    /**
     * Obtiene las cabeceras del response
     * @return
     */
    public Map<String, String> getHeaders() {
        return headers;
    }


    public  void redirect(String url) {
        if (url == null || url.isEmpty()) {
            throw new RouteException("URL no puede ser nula o vacía");
        }
        this.setStatus(302);
        this.addHeader("Location", url);

    }
    public static Response Ok(JSONObject body) {
        return new Response(200, body);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response Ok(String body) {
        return new Response(200, body);
    }

    /**
     * Constructor para archivos (application/octet-stream)
     * @param body
     * @param contentType
     * @return
     */
    public static Response Ok(byte[] body, String contentType) {
        return new Response(200, body, contentType);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response NotFound(JSONObject body) {
        return new Response(404, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response NotFound(String body) {
        return new Response(404, body);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response BadRequest(JSONObject body) {
        return new Response(400, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response BadRequest(String body) {
        return new Response(400, body);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response Unauthorized(JSONObject body) {
        return new Response(401, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response Unauthorized(String body) {
        return new Response(401, body);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response Forbidden(JSONObject body) {
        return new Response(403, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response Forbidden(String body) {
        return new Response(403, body);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response InternalServerError(JSONObject body) {
        return new Response(500, body);
    }
    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response InternalServerError(String body) {
        return new Response(500, body);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response Created(JSONObject body) {
        return new Response(201, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response Created(String body) {
        return new Response(201, body);
    }
    public static Response NoContent(JSONObject body) {
        return new Response(204, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response NoContent(String body) {
        return new Response(204, body);
    }

    /**
     * Constructor para archivos (application/octet-stream)
     * @param body
     * @param contentType
     * @return
     */
    public static Response NoContent(byte[] body, String contentType) {
        return new Response(204, body, contentType);
    }

    /**
     * Constructor para JSON (application/json)
     * @param body
     * @return
     */
    public static Response Accepted(JSONObject body) {
        return new Response(202, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     * @param body
     * @return
     */
    public static Response Accepted(String body) {
        return new Response(202, body);
    }

    /**
     * Constructor para archivos (application/octet-stream)
     * @param body
     * @param contentType
     * @return
     */
    public static Response Accepted(byte[] body, String contentType) {
        return new Response(202, body, contentType);
    }

    /**
     * Metodo que construye la respuesta HTTP
     * @return String
     */
    public String build() {
        StringBuilder responseBuilder = new StringBuilder();

        // Línea de estado
        String statusMessage = switch (status) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown Status";
        };
        responseBuilder.append("HTTP/1.1 ").append(status).append(" ").append(statusMessage).append("\r\n");

        // Agregar encabezados
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        // Agregar cookies
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            responseBuilder.append("Set-Cookie: ").append(entry.getValue()).append("\r\n");
        }

        // Separador entre encabezados y cuerpo
        responseBuilder.append("\r\n");

        // Agregar cuerpo
        if (body instanceof JSONObject) {
            responseBuilder.append(body);
        } else if (body instanceof String) {
            responseBuilder.append(body);
        } else if (body instanceof byte[]) {
            return Arrays.toString(responseBuilder.toString().getBytes(StandardCharsets.ISO_8859_1)); // Para contenido binario
        }

        return responseBuilder.toString();
    }


    /**
     * Agrega una cookie al response
     * @param name
     * @param value
     * @param maxAge
     * @param httpOnly
     * @param secure
     */
    public void addCookie(String name, String value, int maxAge, boolean httpOnly, boolean secure) {
        StringBuilder cookieValue = new StringBuilder();
        cookieValue.append(name).append("=").append(value);
        if (maxAge > 0) {
            cookieValue.append("; Max-Age=").append(maxAge);
        }
        if (httpOnly) {
            cookieValue.append("; HttpOnly");
        }
        if (secure) {
            cookieValue.append("; Secure");
        }
        cookieValue.append("; Path=/");
        cookies.put(name, cookieValue.toString());
    }

    /**
     * Elimina una cookie del response
     * @param name
     */
    public void deleteCookie(String name) {
        addHeader("Set-Cookie", name + "=; Max-Age=0; HttpOnly; Secure");
    }

    /**
     * Agrega una cabecera CORS al response
     * @param origin
     * @param methods
     * @param headers
     */
    public  void setCORS(String origin, String methods, String headers) {
        addHeader("Access-Control-Allow-Origin", origin);
        addHeader("Access-Control-Allow-Methods", methods);
        addHeader("Access-Control-Allow-Headers", headers);
    }
}
