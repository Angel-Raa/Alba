package com.github.angel.raa.modules.templates;


import com.github.angel.raa.modules.exceptions.AlbaConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Configuration class for template handling in the Alba framework.
 * This class manages template-related settings loaded from alba.properties file.
 */
public class TemplateConfig {
    private boolean cacheEnabled;
    private String prefix;
    private String suffix;

    /**
     * Constructs a new TemplateConfig instance with default values and loads
     * configuration from alba.properties.
     * Default values are:
     * - cacheEnabled: false
     * - prefix: "/templates/"
     * - suffix: ".html"
     */
    public TemplateConfig() {
        this.cacheEnabled = false;
        this.prefix = "/templates/";
        this.suffix = ".html";
        loadConfig();
    }


    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * Loads configuration from alba.properties file.
     * This method reads the following properties:
     * - alba.template.cache: enables/disables template caching
     * - alba.template.prefix: sets the template prefix path
     * - alba.template.suffix: sets the template file suffix
     *
     * @throws AlbaConfigurationException if the properties file is not found,
     *         if there's an error loading the file, or if prefix/suffix values are blank
     */
    private void loadConfig(){
        Properties properties = new Properties();
        try(InputStream input = getClass().getClassLoader().getResourceAsStream("alba.properties")){
            if (input == null) {
                throw new AlbaConfigurationException("No se encontró el archivo alba.properties en el classpath.");
            }
            properties.load(input);
            this.cacheEnabled = Boolean.parseBoolean(
                    properties.getProperty("alba.template.cache", "false")
            );
            this.prefix = properties.getProperty("alba.template.prefix", "/templates/");
            this.suffix = properties.getProperty("alba.template.suffix", ".html");

            // Validación de configuración
            if (this.prefix.isBlank() || this.suffix.isBlank()) {
                throw new AlbaConfigurationException("alba.template.prefix o alba.template.suffix", this.prefix + this.suffix);
            }

        }
        catch (IOException e){
            throw new AlbaConfigurationException("Error al cargar alba.properties", e);
        }
    }
}
