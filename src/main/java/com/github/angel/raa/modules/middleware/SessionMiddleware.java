package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;

public class SessionMiddleware implements Middleware {
    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {

        return false;
    }
}
