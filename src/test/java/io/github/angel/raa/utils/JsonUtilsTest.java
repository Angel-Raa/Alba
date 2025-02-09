package io.github.angel.raa.utils;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void parse() {
        String json = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        JSONObject jsonObject = JsonUtils.parse(json);
        assertEquals("John", jsonObject.getString("name"));
        assertEquals(30, jsonObject.getInt("age"));
        assertEquals("New York", jsonObject.getString("city"));
    }

    @Test
    void stringify() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "John");
        jsonObject.put("age", 30);
        jsonObject.put("city", "New York");
        String json = JsonUtils.stringify(jsonObject);
        assertEquals("{\"city\":\"New York\",\"age\":30,\"name\":\"John\"}", json);
    }
}