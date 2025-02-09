package io.github.angel.raa.core;

import io.github.angel.raa.exceptions.RouteException;
import io.github.angel.raa.templates.TemplateProcessor;
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
 * Ejemplos de uso
 *
 * <pre>
 *         {@code
 *         Response response = new Response();
 *         response.setStatus(200);
 *         response.addHeader("Content-Type", "text/html");
 *         response.addCookie("session", "1234567890");
 *         response.setBody("Hello World");
 *         }
 *     </pre>
 * <pre>
 *         {@code
 *         Response response = new Response(200, "Hello World");
 *         }
 * </p>
 */
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = -318743879132789673L;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();
    private final TemplateProcessor templateProcessor = new TemplateProcessor();
    private int status = 200;
    private Object body;
    private final String charset = "UTF-8";
    private final boolean isTemplate = false;

    public Response() {
        headers.put("Content-Type", "application/json; charset=" + charset);
        headers.put("Access-Control-Allow-Origin", "*");
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public Response(int status, JSONObject body) {
        this.status = status;
        this.body = body;
        headers.put("Content-Type", "application/json; charset=" + charset);
    }

    /**
     * Constructor para texto plano (text/plain)
     *

     */
    public Response(int status, String body) {
        this.status = status;
        this.body = body;
        headers.put("Content-Type", "text/plain; charset=" + charset);
    }


    /**
     * Constructor para archivos (application/octet-stream)
     *
     */
    public Response(int status, byte[] body, String contentType) {
        this.status = status;
        this.body = body;
        headers.put("Content-Type", contentType);
    }

    public static Response Ok(JSONObject body) {
        return new Response(200, body);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response Ok(String body) {
        return new Response(200, body);
    }

    /**
     * Constructor para archivos (application/octet-stream)
     *
     */
    public static Response Ok(byte[] body, String contentType) {
        return new Response(200, body, contentType);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response NotFound(JSONObject body) {
        return new Response(404, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     *
     */
    public static Response NotFound(String body) {
        return new Response(404, body);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response BadRequest(JSONObject body) {
        return new Response(400, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     *
     */
    public static Response BadRequest(String body) {
        return new Response(400, body);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response Unauthorized(JSONObject body) {
        return new Response(401, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     *
     */
    public static Response Unauthorized(String body) {
        return new Response(401, body);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response Forbidden(JSONObject body) {
        return new Response(403, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     */
    public static Response Forbidden(String body) {
        return new Response(403, body);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response InternalServerError(JSONObject body) {
        return new Response(500, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     *
     */
    public static Response InternalServerError(String body) {
        return new Response(500, body);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response Created(JSONObject body) {
        return new Response(201, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     */
    public static Response Created(String body) {
        return new Response(201, body);
    }

    public static Response NoContent(JSONObject body) {
        return new Response(204, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     *
     */
    public static Response NoContent(String body) {
        return new Response(204, body);
    }

    /**
     * Constructor para archivos (application/octet-stream)
     *
     */
    public static Response NoContent(byte[] body, String contentType) {
        return new Response(204, body, contentType);
    }

    /**
     * Constructor para JSON (application/json)
     *
     */
    public static Response Accepted(JSONObject body) {
        return new Response(202, body);
    }

    /**
     * Constructor para texto plano (text/plain)
     *
     */
    public static Response Accepted(String body) {
        return new Response(202, body);
    }

    /**
     * Constructor para archivos (application/octet-stream)
     *
     */
    public static Response Accepted(byte[] body, String contentType) {
        return new Response(202, body, contentType);
    }

    /**
     * Agrega una cabecera al response
     *
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Elimina una cabecera del response
     *
     */
    public void removeHeader(String key) {
        headers.remove(key);
    }

    /**
     * Obtene el status del response
     *
     */
    public int getStatus() {
        return status;
    }

    /**
     * Establece el estado de la respuesta HTTP.
     * <p>
     * Este método permite asignar un código de estado HTTP válido (entre 100 y 599)
     * al response. Si se proporciona un valor fuera de este rango, se lanzará una excepción.
     * </p>
     *
     */
    public void setStatus(int status) {
        if (status < 100 || status > 599) {
            throw new IllegalArgumentException("Código de estado HTTP inválido: " + status);
        }
        this.status = status;
    }

    /**
     * Establece el cuerpo de la respuesta HTTP.
     * <p>
     * Este método permite asignar un objeto como cuerpo de la respuesta,
     * que puede ser una cadena de texto, un objeto JSON, o cualquier otro tipo de contenido.
     * </p>
     *
     */
    public void setBody(Object body) {
        this.body = body;
    }

    /**
     * Obtiene el cuerpo del response
     *
     */
    public void setBody(JSONObject body) {
        this.body = body;
    }

    /**
     * Obtiene las cabeceras del response
     *
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void redirect(String url) {
        if (url == null || url.isEmpty()) {
            throw new RouteException("URL no puede ser nula o vacía");
        }
        this.setStatus(302);
        this.addHeader("Location", url);

    }

    /**
     * Metodo que construye la respuesta HTTP
     *

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
     *
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
     *
     */
    public void deleteCookie(String name) {
        addHeader("Set-Cookie", name + "=; Max-Age=0; HttpOnly; Secure");
    }

    /**
     * Agrega una cabecera CORS al response
     *

     */
    public void setCORS(String origin, String methods, String headers) {
        addHeader("Access-Control-Allow-Origin", origin);
        addHeader("Access-Control-Allow-Methods", methods);
        addHeader("Access-Control-Allow-Headers", headers);
    }

    /**
     * Renderiza una plantilla y la establece como el cuerpo de la respuesta.
     * <p>
     * Este método procesa una plantilla utilizando un motor de plantillas y
     * establece el resultado como el contenido de la respuesta HTTP.
     * Además, configura el encabezado {@code Content-Type} como {@code text/html}.
     * </p>
     *
     */
    public Response addTemplate(String template) {
        addHeader("Content-Type", "text/html; charset=" + charset);
        this.body = templateProcessor.render(template);
        return this;
    }

    /**
     * Renderiza una plantilla y la establece como el cuerpo de la respuesta.
     * <p>
     * Este método procesa una plantilla utilizando un motor de plantillas y
     * establece el resultado como el contenido de la respuesta HTTP.
     * Además, configura el encabezado {@code Content-Type} como {@code text/html}.
     * </p>
     *
     */
    public Response addTemplate(String template, String key, Object value) {
        addHeader("Content-Type", "text/html; charset=" + charset);
        this.body = templateProcessor.render(template, key, value);
        return this;
    }

    /**
     * Renderiza una plantilla y la establece como el cuerpo de la respuesta.
     * <p>
     * Este método procesa una plantilla utilizando un motor de plantillas y
     * establece el resultado como el contenido de la respuesta HTTP.
     * Además, configura el encabezado {@code Content-Type} como {@code text/html}.
     * </p>
     *
     */
    public Response addTemplate(String template, Map<String, Object> model) {
        addHeader("Content-Type", "text/html; charset=" + charset);
        this.body = templateProcessor.render(template, model);
        return this;
    }
}
