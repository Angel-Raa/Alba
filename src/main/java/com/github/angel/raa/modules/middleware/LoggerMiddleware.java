package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;

public class LoggerMiddleware implements Middleware {
    private final System.Logger logger;
    private final System.Logger.Level logLevel;

    public LoggerMiddleware(System.Logger logger, System.Logger.Level logLevel) {
        this.logger = logger;
        this.logLevel = logLevel;
    }

    public LoggerMiddleware() {
        this(System.getLogger(LoggerMiddleware.class.getName()), System.Logger.Level.INFO);

    }

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        long startTime = System.currentTimeMillis();
        logger.log(logLevel, "Inicio de solicitud: {0} {1}", request.getMethod(), request.getPath());

        if (logLevel == System.Logger.Level.DEBUG) {
            logRequestDetails(request);
        }
        boolean result = chain.next(request, response);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.log(logLevel, "Fin de solicitud: {0} {1} (Tiempo: {2} ms)", request.getMethod(), request.getPath(), duration);

        return result;
    }

    private void logRequestDetails(Request request) {
        logger.log(System.Logger.Level.DEBUG, "Encabezados: {0}", request.getHeaders());
        logger.log(System.Logger.Level.DEBUG, "Parámetros: {0}", request.getParams());

        // Si hay un cuerpo en la solicitud, registrarlo
        if (request.getBody() != null) {
            logger.log(System.Logger.Level.DEBUG, "Cuerpo: {0}", request.getBody().toString());
        }
    }
}
