package com.github.alba.middleware;

import com.github.alba.core.Request;
import com.github.alba.core.Response;

public class SessionMiddleware implements  Middleware{
    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {

        return false;
    }
}
