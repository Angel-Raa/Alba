package com.github.angel.raa.modules.core;

import com.github.angel.raa.modules.handler.Handler;

import java.util.HashMap;
import java.util.Map;

public class RouteMatch {
    private Handler handler;
    private Map<String, String> params = new HashMap<>();

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
}
