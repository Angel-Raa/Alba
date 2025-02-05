package com.github.alba.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties;

    public Config() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("framework.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo de configuración.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la configuración.", e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public String getServerHost() {
        return properties.getProperty("server.host");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }
}
