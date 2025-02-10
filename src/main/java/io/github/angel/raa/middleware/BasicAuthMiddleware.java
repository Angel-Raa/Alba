package io.github.angel.raa.middleware;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;
import io.github.angel.raa.exceptions.InvalidPathPatternException;
import io.github.angel.raa.utils.AlbaUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Middleware de autenticación básica.
 * Este middleware verifica las credenciales de usuario y contraseña proporcionadas en el encabezado de autorización.
 * Si las credenciales son válidas, permite el acceso al recurso solicitado.
 * Si las credenciales no son válidas, devuelve un error de autenticación.
 *
 * <pre>{@code
 *         Ejemplo de uso:
 *         BasicAuthMiddleware basicAuthMiddleware = new BasicAuthMiddleware();
 *         basicAuthMiddleware.addCredential("user1", "password1");
 *         basicAuthMiddleware.addCredential("user2", "password2");
 *         server.use(basicAuthMiddleware);
 *         server.get("/protected", (request, response) -> {
 *             // Acceso restringido
 *             return new Response(200, new JSONObject().put("message", "Acceso permitido"));
 *         });
 *         server.get("/public", (request, response) -> {
 *             // Acceso público
 *             return new Response(200, new JSONObject().put("message", "Acceso permitido"));
 *         });
 *
 *          // Crear un middleware de autenticación básica
 *         BasicAuthMiddleware authMiddleware = new BasicAuthMiddleware();
 *         server.addController(new PostController(), authMiddleware);
 *         server.start();
 *        }
 *     </pre>

 */
public class BasicAuthMiddleware implements Middleware {
    private static final Logger logger = Logger.getLogger(BasicAuthMiddleware.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 5 * 60 * 1000;
    private final Map<String, String> validCredentials = new HashMap<>();
    private final List<String> excludedPaths = new ArrayList<>(List.of("/public"));
    private final Map<String, String> roles = new HashMap<>();
    private final List<String> excludedPathPatterns = new ArrayList<>(List.of("/public"));
    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<String, Long> lockoutTimes = new HashMap<>();
    private String realm = "Acceso restringido";


    public BasicAuthMiddleware() {
        String hashedPassword = hashPassword("admin");
        validCredentials.put("admin", hashedPassword);
    }

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        String path = request.getPath();


        // Verificar rutas excluidas
        if (isPublicRoute(path)) {
            return chain.next(request, response);
        }

        // Verificar encabezado de autenticación
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            response.setStatus(401);
            response.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
            response.setBody(new JSONObject().put("error", "Se requiere autenticación"));
            return false;
        }

        // Decodificar credenciales
        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        String credentials;
        try {
            credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            response.setBody(new JSONObject().put("error", "Credenciales malformadas"));
            return false;
        }

        if (credentials.isEmpty()) {
            response.setStatus(400);
            response.setBody(new JSONObject().put("error", "Credenciales inválidas"));
            return false;
        }

        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            response.setStatus(400);
            response.setBody(new JSONObject().put("error", "Credenciales inválidas"));
            return false;
        }

        String username = parts[0];
        String password = parts[1];

        // Verificar bloqueo temporal
        if (lockoutTimes.containsKey(username)) {
            long lockoutTime = lockoutTimes.get(username);
            if (System.currentTimeMillis() - lockoutTime < LOCKOUT_DURATION) {
                long remainingTime = LOCKOUT_DURATION - (System.currentTimeMillis() - lockoutTime);
                response.setStatus(429); // Too Many Requests
                response.setBody(new JSONObject()
                        .put("code", 429)
                        .put("message", "Demasiados intentos fallidos. Inténtalo más tarde.")
                        .put("retryAfter", remainingTime / 1000 + " segundos"));
                return false;
            } else {
                lockoutTimes.remove(username); // Eliminar bloqueo expirado
            }
        }

        // Verificar credenciales
        if (!validCredentials.containsKey(username) || !verifyPassword(username, password)) {
            failedAttempts.put(username, failedAttempts.getOrDefault(username, 0) + 1);
            if (failedAttempts.get(username) >= MAX_FAILED_ATTEMPTS) {
                lockoutTimes.put(username, System.currentTimeMillis());
                response.setStatus(429); // Too Many Requests
                response.setBody(new JSONObject()
                        .put("code", 429)
                        .put("message", "Demasiados intentos fallidos. Inténtalo más tarde."));
                return false;
            }
            response.setStatus(401); // Unauthorized
            response.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
            response.setBody(new JSONObject().put("error", "Credenciales incorrectas"));
            logAuthenticationAttempt(username, false);
            return false;
        }

        failedAttempts.remove(username); // Reiniciar intentos fallidos
        logAuthenticationAttempt(username, true);
        request.setAttribute("username", username);
        return chain.next(request, response);
    }


    /**
     * Agrega una credencial válida
     *
     */
    public void addCredential(String username, String password) {
//        if(!isStrongPassword(password)){
//            throw new IllegalArgumentException("La contraseña no es segura");
//
//        }
        validCredentials.put(username, hashPassword(password));
    }

    /**
     * Elimina una credencial válida
     *
     */
    public void removeCredential(String username) {
        validCredentials.remove(username);
    }

    /**
     * Agrega una ruta a la lista de rutas excluidas
     *
     */
    public BasicAuthMiddleware addExcludedPath(String path) {
        if (AlbaUtils.isPathPattern(path)) {
            throw new InvalidPathPatternException("La ruta " + path + " no es válida");
        }
        excludedPaths.add(path);
        return this;
    }

    /**
     * Agrega un patrón de ruta dinámico a la lista de rutas excluidas
     *
     */
    public BasicAuthMiddleware addExcludedPathPattern(String pathPattern) {
        if (!AlbaUtils.isPathPattern(pathPattern)) {
            throw new InvalidPathPatternException("La ruta " + pathPattern + " no es válida");
        }
        if (excludedPathPatterns.contains(pathPattern)) {
            throw new InvalidPathPatternException("La ruta " + pathPattern + " ya está excluida");
        }
        excludedPathPatterns.add(pathPattern);
        return this;
    }

    /**
     * Establece el realm de autenticación
     *
     */
    public BasicAuthMiddleware setRealm(String realm) {
        if (AlbaUtils.isNotBlank(realm)) {
            throw new IllegalArgumentException("El realm no puede ser nulo o vacío");
        }
        this.realm = realm;
        return this;
    }

    private boolean matchesExcludedPathPatterns(String path) {
        for (String pattern : excludedPathPatterns) {
            if (path.matches(pattern.replace("*", ".*"))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Encripta una contraseña con BCrypt
     *
     */
    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }


    /**
     * Verifica si una contraseña es válida
     *
     */
    private boolean verifyPassword(String username, String password) {
        String hashedPassword = validCredentials.get(username);
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }

    /**
     * Registra un intento de autenticación
     *
     */
    private void logAuthenticationAttempt(String username, boolean b) {
        String message = b ? "Autenticación exitosa para el usuario " + username : "Autenticación fallida para el usuario " + username;
        logger.info(message);

    }

    /**
     * Verifica si una ruta es pública
     *
     */
    private boolean isPublicRoute(String path) {
        return matchesExcludedPathPatterns(path) || excludedPaths.contains(path);
    }

    /**
     * Verifica si una contraseña es segura
     *
     */
    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") && // Al menos una letra mayúscula
                password.matches(".*[a-z].*") && // Al menos una letra minúscula
                password.matches(".*\\d.*") &&   // Al menos un número
                password.matches(".*[@#$%^&+=!].*"); // Al menos un carácter especial
    }

}
