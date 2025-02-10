package io.github.angel.raa.security;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Clase para generar tokens CSRF (Cross-Site Request Forgery).
 */
public class CsrfToken {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    /**
     * Genera un token CSRF.
     *
     */
    public static String generateToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return base64Encoder.encodeToString(tokenBytes);
    }
}
