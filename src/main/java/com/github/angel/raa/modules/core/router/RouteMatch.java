package com.github.angel.raa.modules.core.router;

import com.github.angel.raa.modules.handler.Handler;
import com.github.angel.raa.modules.middleware.Middleware;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RouteMatch {
    private Handler handler;
    private Map<String, String> params;
    private List<Middleware> middlewares;

    public RouteMatch() {
    }

    public RouteMatch(Handler handler) {
        this.handler = handler;
        this.middlewares = Collections.emptyList();
    }

    public RouteMatch(Map<String, String> params, Handler handler) {
        this.params = params;
        this.handler = handler;
        this.middlewares = Collections.emptyList();
    }

    public RouteMatch(Map<String, String> params,Handler handler, List<Middleware> middlewares) {
        this.handler = handler;
        this.params = params;
        this.middlewares = middlewares != null ? middlewares : Collections.emptyList();
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public List<Middleware> getMiddlewares() {
        return middlewares;
    }

    public void setMiddlewares(List<Middleware> middlewares) {
        this.middlewares = middlewares != null ? middlewares : Collections.emptyList();
    }
}
