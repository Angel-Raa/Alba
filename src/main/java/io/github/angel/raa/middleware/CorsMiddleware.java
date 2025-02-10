package io.github.angel.raa.middleware;

import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;
import org.json.JSONObject;

import java.util.*;

/**
 * Middleware para manejar CORS (Cross-Origin Resource Sharing).
 * Permite configurar los orígenes permitidos, métodos HTTP permitidos y encabezados permitidos.
 * También permite configurar si se permiten credenciales y el tiempo máximo de caché.
 * Además, permite bloquear todos los orígenes si no se configuran explícitamente.
 * <p>
 * Ejemplo de uso:
 * <pre>{@code
 * CorsMiddleware corsMiddleware = CorsMiddleware.allowAll()
 *         .addAllowedOrigin("http://example.com")
 *         .addAllowedMethod("GET")
 *         .addAllowedHeader("Content-Type")
 *         .allowCredentials(true)
 *         .setMaxAge(3600);
 * }</pre>
 * <p>
 * Luego, puedes usar este middleware en tu servidor:
 * <pre>{@code
 * server.use(corsMiddleware);
 * }</pre>
 * <p>
 * Si no se configura CORS, se bloquearán todos los orígenes.
 * <pre>{@code
 * CorsMiddleware corsMiddleware = new CorsMiddleware();
 * }</pre>
 * <p>
 * Si se configura CORS, se permitirán los orígenes especificados.
 * <pre>{@code
 * CorsMiddleware corsMiddleware = CorsMiddleware.allowAll();
 * }</pre>
 */
public class CorsMiddleware implements Middleware {
    private final Set<String> allowedOrigins = new HashSet<>();
    private final Set<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    private final List<String> allowedHeaders = new ArrayList<>(List.of("Content-Type"));
    private boolean allowCredentials = false;
    private int maxAge = 600;

    /**
     * Constructor predeterminado: Bloquea todos los orígenes si no se configuran explícitamente.
     */
    public CorsMiddleware() {
    }


    public CorsMiddleware(Set<String> allowedOrigins, Set<String> allowedMethods, List<String> allowedHeaders) {
        this.allowedOrigins.addAll(allowedOrigins);
        this.allowedMethods.addAll(allowedMethods);
        this.allowedHeaders.addAll(allowedHeaders);

    }

    /**
     * Configura el middleware para permitir todos los orígenes.
     *
     */
    public static CorsMiddleware allowAll() {
        CorsMiddleware corsMiddleware = new CorsMiddleware();
        corsMiddleware.allowedOrigins.add("*");
        return corsMiddleware;
    }

    /**
     * Agrega un origen permitido al middleware.
     *
     */
    public CorsMiddleware addAllowedOrigin(String origin) {
        this.allowedOrigins.add(origin);
        return this;
    }

    /**
     * Agrega un método HTTP permitido al middleware.
     *
     */
    public CorsMiddleware addAllowedMethod(String method) {
        this.allowedMethods.add(method);
        return this;
    }

    /**
     * Agrega un encabezado permitido al middleware.
     *
     */
    public CorsMiddleware addAllowedHeader(String header) {
        if (!this.allowedHeaders.contains(header)) {
            this.allowedHeaders.add(header);
        }
        return this;
    }

    /**
     * Agrega una lista de orígenes permitidos al middleware.
     *.
     */
    public CorsMiddleware addAllowedOrigins(List<String> origins) {
        allowedOrigins.addAll(origins.stream().filter(o -> !allowedOrigins.contains(o)).toList());
        return this;
    }

    /**
     * Agrega una lista de orígenes permitidos al middleware.
     *
     */
    public CorsMiddleware addAllowedOrigins(String... origins) {
        allowedOrigins.addAll(Arrays.stream(origins).filter(o -> !allowedOrigins.contains(o)).toList());
        return this;
    }

    /**
     * Agrega una lista de métodos HTTP permitidos al middleware.
     *
     */
    public CorsMiddleware addAllowedMethods(List<String> methods) {
        allowedMethods.addAll(methods.stream()
                .map(String::toUpperCase)
                .filter(m -> !allowedMethods.contains(m)).toList());
        return this;
    }

    /**
     * Agrega una lista de métodos HTTP permitidos al middleware.
     *
     */
    public CorsMiddleware addAllowedMethods(String... methods) {
        allowedMethods.addAll(Arrays.stream(methods)
                .map(String::toUpperCase)
                .filter(m -> !allowedMethods.contains(m)).toList());
        return this;
    }

    /**
     * Agrega una lista de encabezados permitidos al middleware.
     *
     */
    public CorsMiddleware addAllowedHeaders(String... headers) {
        allowedHeaders.addAll(Arrays.stream(headers)
                .filter(h -> !allowedHeaders.contains(h)).toList());
        return this;
    }

    /**
     * Establece el tiempo máximo de vida (en segundos) de la respuesta preflight.
     *
     */
    public CorsMiddleware setMaxAge(int maxAge) throws IllegalArgumentException {
        if (maxAge < 0) {
            throw new IllegalArgumentException("Max age must be a non-negative integer");
        }

        if (maxAge > 86400) {
            throw new IllegalArgumentException("Max age must be less than or equal to 86400");
        }


        this.maxAge = maxAge;
        return this;
    }

    /**
     * Establece si se permiten credenciales en las solicitudes.
     *
     */
    public CorsMiddleware setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    /**
     * Agrega una lista de encabezados permitidos al middleware.
     *.
     */
    public CorsMiddleware addAllowedHeaders(List<String> headers) {
        allowedHeaders.addAll(headers.stream().filter(h -> !allowedHeaders.contains(h)).toList());
        return this;
    }

    @Override
    public boolean handle(final Request request, final Response response, final MiddlewareChain chain) {
        String origin = request.getHeader("Origin");

        if (origin == null) return chain.next(request, response);

        // Validar origen permitido
        if (!isOriginAllowed(origin)) {
            response.setStatus(403); // Forbidden
            response.setBody(new JSONObject().put("error", "Origen no permitido"));
            return false;

        }

        // Agregar encabezados CORS
        if (allowedOrigins.contains("*")) {
            response.addHeader("Access-Control-Allow-Origin", allowedOrigins.contains("*") ? "*" : origin);
        }
        // Validar método HTTP permitido
        String method = request.getMethod();
        if (!allowedMethods.contains(method.toUpperCase())) {
            response.setStatus(405); // Method Not Allowed
            response.setBody(new JSONObject().put("error", "Método no permitido"));
            return false;
        }

        // Agregar métodos y encabezados permitidos
        response.addHeader("Access-Control-Allow-Methods", String.join(", ", allowedMethods));
        response.addHeader("Access-Control-Allow-Headers", String.join(", ", allowedHeaders));

        // Manejo de credenciales (opcional)
        if (allowCredentials) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
        // Configurar tiempo de caché para preflight
        response.addHeader("Access-Control-Max-Age", String.valueOf(maxAge));

        // Manejar preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(204); // No Content
            return false; // Detener la cadena para preflight
        }

        // Continuar con la siguiente lógica
        return chain.next(request, response);
    }


    /**
     * Verifica si el origen está permitido.
     *
     */
    private boolean isOriginAllowed(String origin) {
        return allowedOrigins.contains("*") || allowedOrigins.contains(origin);
    }

}
