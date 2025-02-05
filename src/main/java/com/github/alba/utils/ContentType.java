package com.github.alba.utils;

public enum ContentType {
    JSON("application/json"),
    TEXT("text/plain");


    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
