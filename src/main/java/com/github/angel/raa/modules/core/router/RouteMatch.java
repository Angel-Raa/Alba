package com.github.angel.raa.modules.core.router;

import com.github.angel.raa.modules.handler.Handler;

import java.util.Map;

public class RouteMatch {
    private Handler handler;
    private Map<String, String> params;

    public RouteMatch() {
    }

    public RouteMatch(Handler handler) {
        this.handler = handler;
    }

    public RouteMatch(Map<String, String> params, Handler handler) {
        this.params = params;
        this.handler = handler;
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

}
