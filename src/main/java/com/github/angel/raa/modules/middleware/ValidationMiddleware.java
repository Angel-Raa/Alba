package com.github.angel.raa.modules.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;
import jakarta.validation.*;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is a middleware that validates the request body.
 * It uses the jakarta.validation library to validate the request body.
 * @param <T>
 */
public class ValidationMiddleware<T> implements  Middleware{
    private final Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;


    public ValidationMiddleware(Class<T> type) {
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        this.validator = factory.getValidator();
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
                response.setStatus(400);

                // Construir un mapa con los errores de validación
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<T> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
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
