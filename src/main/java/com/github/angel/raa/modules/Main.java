package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.Server;

import com.github.angel.raa.modules.middleware.ValidationMiddleware;
import com.github.angel.raa.modules.test.User;
import org.json.JSONObject;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {



        Server server = new Server(8080);
        server.use((request, response, chain) -> {
            System.out.println("Entrando al middleware");
            return chain.next(request, response);
        });
       server.use(new ValidationMiddleware<>(User.class));

        server.get("/hey", request -> {
            Response response = new Response(200, new JSONObject().put("message", "Hola Mundo desde JSON"));

            response.addHeader("Content-Type", "application/json");
            return response;

        });

        server.get("/hello", request -> {
            Response response = new Response(200, "Hola Mundo en texto plano");
            response.addHeader("Content-Type", "text/plain"); // Especificamos el tipo de contenido
            return response;
        });


        // Ruta POST para crear un usuario
        server.post("/user", request -> {
            // Recuperar el cuerpo de la solicitud
            JSONObject body = request.getBody();
            System.out.println(body);

            // Devolver una respuesta exitosa
            return new Response(200, new JSONObject()
                    .put("message", "Usuario creado")
                    .put("data", body));
        });

        server.post("/port", request ->  new Response(200, new JSONObject().put("message", "Okey con POST")));
        server.delete("/delete", request ->  new Response(200, new JSONObject().put("message", "Okey con DELETE")));
        server.put("/put", request ->  new Response(200, new JSONObject().put("message", "Okey con put")));


        server.start();
    }
}