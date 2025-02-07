package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.Server;
import com.github.angel.raa.modules.middleware.LoggerMiddleware;
import com.github.angel.raa.modules.middleware.ValidationMiddleware;
import com.github.angel.raa.modules.test.User;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);

        // Middleware global para registrar solicitudes
        server.use((request, response, chain) -> {
            System.out.println("Solicitud: " + request.getMethod() + " " + request.getPath());

            System.out.println("Solicitud recibida desde IP: " + request.getClientIp());

            return chain.next(request, response);
        });

        // Middleware global para validar datos
        server.use(new LoggerMiddleware());

        server.get("/users/:id", request -> {
            Long userId = request.getPathParamAsLong("id");
            String name = request.getQueryParam("name");

            return new Response(200, new JSONObject()
                    .put("message", "Detalles del usuario")
                    .put("userId", userId)
                    .put("name", name));// Ruta GET para listar usuarios
        });

        server.get("/users", request -> {
            List<Map<String, String>> users = Arrays.asList(
                    Map.of("id", "1", "name", "John Doe", "email", "john@example.com"),
                    Map.of("id", "2", "name", "Jane Doe", "email", "jane@example.com")
            );

            return new Response(200, new JSONObject().put("users", users));
        });
        // Ruta GET para listar usuarios
        server.get("/user/:id", request -> {
            String userId = request.getPathParam("id"); // Obtiene el parámetro dinámico ":id"
            String name = request.getQueryParam("name"); // Obtiene el parámetro de consulta "name"

            return new Response(200, new JSONObject()
                    .put("message", "Usuario encontrado")
                    .put("userId", userId)
                    .put("name", name));
        });
        // Ruta POST para crear un usuario
        server.post("/user", request -> {
            JSONObject body = request.getBody();
            return new Response(200, new JSONObject()
                    .put("message", "Usuario creado")
                    .put("data", body));
        }, new ValidationMiddleware<>(User.class));


        // Ruta DELETE para eliminar un usuario
        server.delete("/user/:id", request -> {
            String userId = request.getParams().get("id");
            return new Response(200, new JSONObject()
                    .put("message", "Usuario eliminado")
                    .put("userId", userId));
        });


        server.post("/port", request -> new Response(200, new JSONObject().put("message", "Okey con POST")));
        server.delete("/delete", request -> new Response(200, new JSONObject().put("message", "Okey con DELETE")));
        server.put("/put", request -> new Response(200, new JSONObject().put("message", "Okey con put")));


        server.start();
    }
}
