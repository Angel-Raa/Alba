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
     */
    public AlbaConfigurationException(String message) {
        super(message);
        logError(message, null);
    }

    /**
     * Constructor con mensaje y causa.
     *
     */
    public AlbaConfigurationException(String message, Throwable cause) {
        super(message, cause);
        logError(message, cause);
    }

    /**
     * Constructor para errores con claves específicas de configuración.
     *
     */
    public AlbaConfigurationException(String key, String value) {
        super("Error en la configuración: Clave [" + key + "] con valor inválido: " + value);
        logError("Clave: " + key + ", Valor inválido: " + value, null);
    }

    /**
     * Método privado para registrar el error en logs con SLF4J.
     *
     */
    private void logError(String message, Throwable cause) {
        if (cause != null) {
            LOGGER.error(message, cause);
        } else {
            LOGGER.error(message);


        }
    }
}


