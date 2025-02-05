package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;
import org.json.JSONObject;

import java.util.concurrent.*;

/**
 * Middleware para manejar timeout de las peticiones
 */
public class TimeoutMiddleware implements Middleware {
    private final long timeoutMillis;

    public TimeoutMiddleware(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> chain.next(request, response));
        try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            response.setStatus(504);
            response.setBody(new JSONObject().put("error", "Request timed out"));
            return false;
        } catch (Exception e) {
            response.setStatus(500);
            response.setBody(new JSONObject().put("error", "Request timed out"));
            return false;

        } finally {
            future.cancel(true);
            executor.shutdown();
        }

    }
}
