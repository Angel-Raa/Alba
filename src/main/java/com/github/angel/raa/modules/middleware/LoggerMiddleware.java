package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;

public class LoggerMiddleware implements Middleware {


    private static final System.Logger logger = System.getLogger(LoggerMiddleware.class.getName());

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        logger.log(System.Logger.Level.INFO, "Request: {0} {1}", request.getMethod(), request.getPath());
        return chain.next(request, response);
    }
}
