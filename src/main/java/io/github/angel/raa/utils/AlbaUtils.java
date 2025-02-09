package io.github.angel.raa.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


/**
 * Clase de utilidades `AlbaUtils`.
 * <p>
 * Esta clase proporciona métodos para validaciones de datos, conversión JSON, manejo de fechas
 * y generación de contraseñas seguras.
 */
public class AlbaUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Valida si una cadena es un correo electrónico válido.
     *
     * @param email Correo electrónico a validar.
     * @return true si es un correo electrónico válido, false en caso contrario.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Verifica si una cadena no está vacía ni es nula.
     *
     * @param str Cadena a validar.
     * @return true si la cadena no está vacía ni es nula, false en caso contrario.
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }

    /**
     * Valida si un número de tarjeta de crédito es válido utilizando el Algoritmo de Luhn.
     *
     * @param cardNumber Número de tarjeta de crédito.
     * @return true si es válido, false en caso contrario.
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
     * @param obj Objeto a convertir.
     * @return Cadena JSON o null en caso de error.
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
     * @param json  JSON a convertir.
     * @param clazz Clase del objeto destino.
     * @param <T>   Tipo genérico del objeto.
     * @return Objeto convertido o null en caso de error.
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
     * @param obj Objeto a convertir.
     * @return JSON con formato legible o null en caso de error.
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
     * @param date   Fecha a formatear.
     * @param format Formato deseado (ejemplo: "dd/MM/yyyy HH:mm:ss").
     * @return Fecha formateada como cadena.
     */
    public static String getCurrentTimestamp(LocalDateTime date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * Obtiene la fecha y hora actual en formato ISO 8601.
     *
     * @return Fecha y hora actual en formato "dd/MM/yyyy HH:mm:ss".
     */
    public static String getCurrentTimestamp() {
        return getCurrentTimestamp(LocalDateTime.now(), "dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Calcula la diferencia en días entre dos fechas.
     *
     * @param start Fecha inicial.
     * @param end   Fecha final.
     * @return Número de días entre ambas fechas.
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toDays();
    }

    /**
     * Genera una contraseña segura aleatoria.
     *
     * @param length Longitud de la contraseña.
     * @return Contraseña generada aleatoriamente.
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
     * @param path
     * @return
     */
    public static boolean isPathPattern(String path) {
        return path.contains("*");

    }
}
