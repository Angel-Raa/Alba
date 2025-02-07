package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorsMiddleware implements Middleware {
    private List<String> allowedOrigins = new ArrayList<>();
    private List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE");
    private List<String> allowedHeaders = Arrays.asList("Content-Type", "Authorization");
    private boolean allowCredentials = false;
    private int maxAge = 600;

    /**
     * Constructor predeterminado: Bloquea todos los orígenes si no se configuran explícitamente.
     */
    public CorsMiddleware() {
    }

    /**
     * Constructor para configurar CORS.
     *
     * @param allowedOrigins Lista de orígenes permitidos (puede ser "*").
     * @param allowedMethods Lista de métodos HTTP permitidos.
     * @param allowedHeaders Lista de encabezados permitidos.
     */
    public CorsMiddleware(List<String> allowedOrigins, List<String> allowedMethods, List<String> allowedHeaders) {
        this.allowedOrigins = allowedOrigins;
        this.allowedMethods = allowedMethods;
        this.allowedHeaders = allowedHeaders;
    }

    /**
     * Configura el middleware para permitir todos los orígenes.
     *
     * @return CorsMiddleware configurado para permitir todos los orígenes.
     */
    public static CorsMiddleware allowAll() {
        CorsMiddleware corsMiddleware = new CorsMiddleware();
        corsMiddleware.allowedOrigins.add("*");
        return corsMiddleware;
    }

    /**
     * Agrega un origen permitido al middleware.
     *
     * @param origin
     * @return
     */
    public CorsMiddleware addAllowedOrigin(String origin) {
        if (!this.allowedOrigins.contains(origin)) {
            this.allowedOrigins.add(origin);
        }
        return this;
    }

    /**
     * Agrega un método HTTP permitido al middleware.
     *
     * @param method
     * @return
     */
    public CorsMiddleware addAllowedMethod(String method) {
        if (!this.allowedMethods.contains(method)) {
            this.allowedMethods.add(method);
        }
        return this;
    }

    /**
     * Agrega un encabezado permitido al middleware.
     *
     * @param header
     */
    public CorsMiddleware addAllowedHeader(String header) {
        if (!this.allowedHeaders.contains(header)) {
            this.allowedHeaders.add(header);
        }
        return this;
    }

    /**
     * Agrega una lista de orígenes permitidos al middleware.
     *
     * @param origins
     * @return CorsMiddleware configurado para permitir los orígenes especificados.
     */
    public CorsMiddleware addAllowedOrigins(List<String> origins) {
        allowedOrigins.addAll(origins.stream().filter(o -> !allowedOrigins.contains(o)).toList());
        return this;
    }

    /**
     * Agrega una lista de orígenes permitidos al middleware.
     *
     * @param origins
     * @return CorsMiddleware configurado para permitir los orígenes especificados.
     */
    public CorsMiddleware addAllowedOrigins(String... origins) {
        allowedOrigins.addAll(Arrays.stream(origins).filter(o -> !allowedOrigins.contains(o)).toList());
        return this;
    }
    /**
     * Agrega una lista de métodos HTTP permitidos al middleware.
     *
     * @param methods
     * @return CorsMiddleware configurado para permitir los métodos HTTP especificados.
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
     * @param methods
     * @return CorsMiddleware configurado para permitir los métodos HTTP especificados.
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
     * @param headers
     * @return CorsMiddleware configurado para permitir los encabezados especificados.
     */
    public CorsMiddleware addAllowedHeaders(String... headers) {
        allowedHeaders.addAll(Arrays.stream(headers)
                .filter(h -> !allowedHeaders.contains(h)).toList());
        return this;
    }

    /**
     * Establece el tiempo máximo de vida (en segundos) de la respuesta preflight.
     *
     * @param maxAge
     * @return CorsMiddleware configurado para permitir los encabezados especificados.
     * @throws IllegalArgumentException Si maxAge es negativo o mayor que 86400
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
     * @param allowCredentials
     * @return CorsMiddleware configurado para permitir las credenciales.
     */
    public CorsMiddleware setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    /**
     * Agrega una lista de encabezados permitidos al middleware.
     *
     * @param headers
     * @return CorsMiddleware configurado para permitir los encabezados especificados.
     */
    public CorsMiddleware addAllowedHeaders(List<String> headers) {
        allowedHeaders.addAll(headers.stream().filter(h -> !allowedHeaders.contains(h)).toList());
        return this;
    }

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        String origin = request.getHeader("Origin");

        // Validar origen permitido
        if (!isOriginAllowed(origin)) {
            response.setStatus(403); // Forbidden
            response.setBody(new JSONObject().put("error", "Origen no permitido"));
            return false;

        }
        if (isOriginAllowed(origin)) {
            response.addHeader("Access-Control-Allow-Origin", origin);
        } else {
            response.setStatus(403); // Forbidden
            response.setBody(new JSONObject().put("error", "Origen no permitido"));
            return false; // Detener la cadena
        }

        // Agregar métodos y encabezados permitidos
        response.addHeader("Access-Control-Allow-Methods", String.join(", ", allowedMethods));
        response.addHeader("Access-Control-Allow-Headers", String.join(", ", allowedHeaders));

        // Manejo de credenciales (opcional)
        if (allowCredentials) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
        response.addHeader("Access-Control-Max-Age", String.valueOf(maxAge));
        response.addHeader("Vary", "Origin");

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
     * @param origin
     * @return
     */
    private boolean isOriginAllowed(String origin) {
        if (origin == null) {
            return false;
        }
        if (allowedOrigins.isEmpty()) {
            return false;
        }
        return allowedOrigins.contains("*") || allowedOrigins.contains(origin);
    }

}
