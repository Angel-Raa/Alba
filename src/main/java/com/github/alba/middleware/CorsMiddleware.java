package com.github.alba.middleware;

import com.github.alba.core.Request;
import com.github.alba.core.Response;

public class CorsMiddleware implements Middleware {

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return chain.next(request, response);
    }
}
