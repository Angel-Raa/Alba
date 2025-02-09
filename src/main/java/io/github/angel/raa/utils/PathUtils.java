package io.github.angel.raa.utils;

import java.util.HashMap;
import java.util.Map;

public class PathUtils {
    public static Map<String, String> extractParams(String routePath, String requestPath) {
        Map<String, String> params = new HashMap<>();
        String[] routeParts = routePath.split("/");
        String[] requestParts = requestPath.split("/");
        for (int i = 0; i < routeParts.length; i++) {
            if (routeParts[i].startsWith(":")) {
                params.put(routeParts[i].substring(1), requestParts[i]);
            }
        }
        return params;
    }
}
