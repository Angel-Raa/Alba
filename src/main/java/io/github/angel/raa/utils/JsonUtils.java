package io.github.angel.raa.utils;

import org.json.JSONObject;

public class JsonUtils {
    public static JSONObject parse(String json) {
        return new JSONObject(json);
    }

    public static String stringify(JSONObject json) {
        return json.toString();
    }
}
