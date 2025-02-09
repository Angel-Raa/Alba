package io.github.angel.raa.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.angel.raa.core.Request;
import io.github.angel.raa.core.Response;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Middleware para validar los datos de entrada de una solicitud HTTP.
 * <p>
 * Este middleware utiliza la biblioteca de validación de Java (JSR 380) para validar los datos de entrada de una solicitud HTTP.
 * Si los datos no son válidos, se devuelve una respuesta con un código de estado 400 y un mensaje de error.
 * <p>
 * El middleware puede ser utilizado en una aplicación web para validar los datos de entrada de una solicitud HTTP antes de que se procese por el controlador.
 * <p>
 * Ejemplo de uso:
 * <pre>{@code
 * server.post("/users", request -> new Response(200, new JSONObject().put("message", "Okey con POST")), new ValidationMiddleware<>(User.class));
 * }</pre>
 * <p>
 * Donde {@code User} es una clase que representa los datos de entrada de una solicitud HTTP.
 *
 * @param <T>
 */
public class ValidationMiddleware<T> implements Middleware {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;

    public ValidationMiddleware(Class<T> type) {
        this.type = type;
    }

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        try {
            JSONObject body = request.getBody();
            if (body == null || body.isEmpty()) {
                response.setStatus(400);
                response.setBody(new JSONObject().put("error", "El cuerpo de la solicitud es obligatorio"));
                return false;
            }

            // Convertir el cuerpo JSON al tipo especificado
            T entity = objectMapper.readValue(body.toString(), type);

            // Validar la entidad
            Set<ConstraintViolation<T>> violations = validator.validate(entity);
            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<T> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
                response.setStatus(400);
                response.setBody(new JSONObject(errors));
                return false; // Detener la cadena
            }
        } catch (Exception e) {
            response.setStatus(400);
            response.setBody(new JSONObject().put("error", "Error al procesar los datos de entrada"));
            return false; // Detener la cadena
        }

        // Continuar con la siguiente lógica
        return chain.next(request, response);
    }
}
