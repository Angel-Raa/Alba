package io.github.angel.raa.middleware;

import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * ServerTimingMiddleware es un middleware que registra el tiempo de ejecución de las operaciones en el servidor.
 * También agrega encabezados de Server-Timing para que el cliente pueda medir el tiempo de ejecución de las operaciones.
 *
 *
 * <pre>{@code
 *       ServerTimingMiddleware serverTimingMiddleware = new ServerTimingMiddleware();
 *       serverTimingMiddleware.addTiming("db", "Database operation", 100);
 *       serverTimingMiddleware.addTiming("cache", "Cache operation", 50);
 *       serverTimingMiddleware.addTiming("network", "Network operation", 200);
 *       serverTimingMiddleware.handle(request, response, (req, res) -> {
 *           // Operación asíncrona
 *           return Response.Ok("Hello World");
 *       });
 *     }
 *     </pre>
 *
 */
public class ServerTimingMiddleware implements Middleware {
    private static final Logger LOGGER = Logger.getLogger(ServerTimingMiddleware.class.getName());
    private final Map<String, Long> timings = new HashMap<>();
    private final Map<String, String> descriptions = new HashMap<>();

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        long start = System.currentTimeMillis();
        boolean result = chain.next(request, response);
        // Medir tiempo total
        long end = System.currentTimeMillis();
        addTiming("total", "Total time", end - start);
        // Construir el encabezado Server-Timing
        String serverTimingHeader = formatServerTimingHeader();
        response.addHeader("Server-Timing", serverTimingHeader);
        response.addHeader("Access-Control-Expose-Headers", "Server-Timing");
        // Registrar métricas
        logMetrics();

        return result;
    }

    /**
     * Agrega una métrica al middleware.
     *
     */
    public void addTiming(String metric, String description, long time) {
        if (metric == null || metric.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la métrica no puede ser nulo o vacío");
        }
        if (time < 0) {
            throw new IllegalArgumentException("El tiempo no puede ser negativo");
        }
        timings.put(metric, time);
        descriptions.put(metric, description);
    }

    /**
     * Formatea el encabezado Server-Timing.
     *
     * @return El encabezado Server-Timing formateado como una cadena de texto.
     */
    private String formatServerTimingHeader() {
        return timings.entrySet().stream()
                .map(entry -> String.format("%s;desc=\"%s\";dur=%d", entry.getKey(), descriptions.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Registra las métricas en el registro.
     */
    private void logMetrics() {
        timings.forEach((metric, time) -> {
            LOGGER.log(Level.INFO, "Métrica: {0}, Descripción: {1}, Tiempo: {2} ms",
                    new Object[]{metric, descriptions.get(metric), time});

        });
    }

    /**
     * Medir una operación asíncrona.
     *
     */
    public void measureAsyncOperation(String metric, String description, Runnable operation) {
        long start = System.currentTimeMillis();
        operation.run();
        long end = System.currentTimeMillis();
        addTiming(metric, description, end - start);
    }
}
