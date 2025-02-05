package com.github.angel.raa.modules.core;

import com.github.angel.raa.modules.handler.Handler;

import java.util.HashMap;
import java.util.Map;


public class Router {
    private final Map<String, Handler> routes = new HashMap<>();

    public void addRoute(String method, String path, Handler handler) {
        routes.put(method + " " + path, handler);

    }

    public Handler getHandler(String method, String path) {
        RouteMatch routeMatch = getRouteMatch(method, path);
        return (routeMatch != null) ? routeMatch.getHandler() : null;
    }

    public RouteMatch getRouteMatch(String method, String path) {
        for (Map.Entry<String, Handler> entry : routes.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(method)) {
                String routePath = key.substring(method.length() + 1);
                Map<String, String> params = extractParams(routePath, path);
                if (params != null) {
                    return new RouteMatch(params, entry.getValue());
                }
            }
        }
        return null;
    }

    private boolean matchesPath(String routePath, String requestPath) {
        if (routePath.equals(requestPath)) {
            return true;
        }
        // Manejar parámetros en la ruta (ej: /users/:id)
        String[] routeParts = routePath.split("/");
        String[] requestParts = requestPath.split("/");
        if (routeParts.length != requestParts.length) {
            return false;
        }
        for (int i = 0; i < routeParts.length; i++) {
            if (routeParts[i].startsWith(":") && !requestParts[i].isEmpty()) {
                continue;
            }
            if (!routeParts[i].equals(requestParts[i])) {
                return false;
            }
        }
        return true;
    }

    private Map<String, String> extractParams(String routePath, String requestPath) {
        String[] routeParts = routePath.split("/");
        String[] requestParts = requestPath.split("/");
        if (routeParts.length != requestParts.length) return null;

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < routeParts.length; i++) {
            if (routeParts[i].startsWith(":")) {
                params.put(routeParts[i].substring(1), requestParts[i]);
            } else if (!routeParts[i].equals(requestParts[i])) {
                return null;
            }
        }
        return params;
    }

}
