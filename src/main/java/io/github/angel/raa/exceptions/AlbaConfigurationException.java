package io.github.angel.raa.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Excepción para errores relacionados con la configuración de la aplicación.
 */
public class AlbaConfigurationException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = -7014856190745766939L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AlbaConfigurationException.class);


    /**
     * Constructor con solo mensaje.
     *
     * @param message
     */
    public AlbaConfigurationException(String message) {
        super(message);
        logError(message, null);
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje descriptivo del error.
     * @param cause   Causa original del error.
     */
    public AlbaConfigurationException(String message, Throwable cause) {
        super(message, cause);
        logError(message, cause);
    }

    /**
     * Constructor para errores con claves específicas de configuración.
     *
     * @param key   Clave de la propiedad con error.
     * @param value Valor problemático.
     */
    public AlbaConfigurationException(String key, String value) {
        super("Error en la configuración: Clave [" + key + "] con valor inválido: " + value);
        logError("Clave: " + key + ", Valor inválido: " + value, null);
    }

    /**
     * Método privado para registrar el error en logs con SLF4J.
     *
     * @param message Mensaje del error.
     * @param cause   Causa original (puede ser null).
     */
    private void logError(String message, Throwable cause) {
        if (cause != null) {
            LOGGER.error(message, cause);
        } else {
            LOGGER.error(message);


        }
    }
}


