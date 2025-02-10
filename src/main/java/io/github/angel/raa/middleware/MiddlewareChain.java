package io.github.angel.raa.middleware;

import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;

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
