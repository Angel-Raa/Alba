package com.github.alba.middleware;

import com.github.alba.core.Request;
import com.github.alba.core.Response;

@FunctionalInterface
public interface Middleware {


    boolean handle(Request request, Response response, MiddlewareChain chain);


}
