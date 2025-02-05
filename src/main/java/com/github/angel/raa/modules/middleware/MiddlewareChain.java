package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;

import java.util.Iterator;

public class MiddlewareChain {
    private final Iterator<Middleware> iterator;

    public MiddlewareChain(Iterator<Middleware> iterator) {
        this.iterator = iterator;
    }

    public boolean next(Request request, Response response) {
        if (iterator.hasNext()) {
            Middleware middleware = iterator.next();
            return middleware.handle(request, response, this);
        }
        return true;
    }

}
