package com.github.angel.raa.modules.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Clase de utilidades para la aplicación.
 *
 */
public class AlbaUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Valida si una cadena es un correo electrónico válido.
     * @param email
     * @return true si es un correo electrónico válido, false en caso contrario.
     */
    public static boolean isValidEmail(String email){
        if(email == null || email.isEmpty()) return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Valida si una cadena no está vacía ni es nula.
     * @param str
     * @return true si la cadena no está vacía ni es nula, false en caso contrario.
     */
    public static boolean isNotBlank(String str){
        return str != null && !str.isBlank();
    }

    /**
     * Valida si una cadena es un número de tarjeta de crédito válido (usando Luhn Algorithm).
     * @param cardNumber
     * @return boolean
     */
    public  static boolean isValidCreditCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }

        cardNumber = cardNumber.replaceAll("\\s|-", "");

        // Verificar que la cadena contenga solo dígitos
        if (!cardNumber.matches("\\d+")) {
            return false;
        }

        // Verificar que la cadena tenga al menos 13 dígitos
        if (cardNumber.length() < 13) {
            return false;
        }

        // Aplicar el algoritmo de Luhn
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    /**
     * Convierte un objeto a JSON.
     * @param obj
     * @return JSON
     */
    public static String toJson(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte un JSON a un objeto.
     * @param json
     * @param clazz
     * @return Objeto
     */
    public static <T> T fromJson(String json, Class<T> clazz){
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte un objeto a JSON con formato legible.
     * @param obj
     * @return JSON
     */
    public static String toPrettyJson(Object obj){
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtiene la fecha actual en un formato específico.
     * @param format,  ejemplo: "dd/MM/yyyy HH:mm:ss"
     * @return String
     */
    public static  String getCurrentTimestamp(LocalDateTime date, String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);

    }

    /**
     * Obtiene la fecha y hora actual en formato ISO 8601.
     * @return
     */
    public  static String getCurrentTimestamp(){
        return getCurrentTimestamp(LocalDateTime.now(), "dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Calcula la diferencia en días entre dos fechas.
     * @param start
     * @param end
     * @return long
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end){
        return Duration.between(start, end).toDays();

    }

    /**
     * Genera una contraseña segura aleatoria.
     * @param length Longitud de la contraseña
     * @return String
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

}
