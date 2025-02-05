package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CorsMiddleware implements Middleware {
    private final List<String> allowedOrigins;
    private final List<String> allowedMethods;
    private final List<String> allowedHeaders;

    public CorsMiddleware() {
        this(
                List.of("*"),
                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"),
                Arrays.asList("Content-Type", "Authorization")
        );
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

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        String origin = request.getHeader("Origin");

        // Validar origen permitido
        if (allowedOrigins.contains("*") || allowedOrigins.contains(origin)) {
            response.addHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        } else {
            response.setStatus(403); // Forbidden
            response.setBody(new JSONObject().put("error", "Origen no permitido"));
            return false; // Detener la cadena
        }

        // Agregar métodos y encabezados permitidos
        response.addHeader("Access-Control-Allow-Methods", String.join(", ", allowedMethods));
        response.addHeader("Access-Control-Allow-Headers", String.join(", ", allowedHeaders));

        // Manejo de credenciales (opcional)
        response.addHeader("Access-Control-Allow-Credentials", "true");

        // Manejar preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(204); // No Content
            return false; // Detener la cadena para preflight
        }

        // Continuar con la siguiente lógica
        return chain.next(request, response);
    }
}
