package io.github.angel.raa.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


/**
 * Clase de utilidades `AlbaUtils`.
 * Esta clase proporciona métodos para validaciones de datos, conversión JSON, manejo de fechas
 * y generación de contraseñas seguras.
 */
public class AlbaUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Valida si una cadena es un correo electrónico válido.
     *
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Verifica si una cadena no está vacía ni es nula.
     *
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }

    /**
     * Valida si un número de tarjeta de crédito es válido utilizando el Algoritmo de Luhn.
     *
     */
    public static boolean isValidCreditCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) return false;
        cardNumber = cardNumber.replaceAll("\\s|-", "");
        if (!cardNumber.matches("\\d+") || cardNumber.length() < 13) return false;
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    /**
     * Convierte un objeto a JSON.
     *
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte una cadena JSON a un objeto.
     *
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte un objeto a JSON con formato legible.
     *
     */
    public static String toPrettyJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtiene la fecha actual en un formato específico.
     *
     */
    public static String getCurrentTimestamp(LocalDateTime date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * Obtiene la fecha y hora actual en formato ISO 8601.
     *
     */
    public static String getCurrentTimestamp() {
        return getCurrentTimestamp(LocalDateTime.now(), "dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Calcula la diferencia en días entre dos fechas.
     *
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toDays();
    }

    /**
     * Genera una contraseña segura aleatoria.
     *
     */
    public static String generateSecurePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    /**
     * Verifica si una cadena contiene un patrón de ruta dinámica.
     *
     */
    public static boolean isPathPattern(String path) {
        return path.contains("*");

    }
}
