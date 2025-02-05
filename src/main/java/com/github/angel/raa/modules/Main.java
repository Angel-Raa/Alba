package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.Server;
import com.github.angel.raa.modules.middleware.CorsMiddleware;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> allowedOrigins = Arrays.asList("https://example.com", "https://api.example.com");
        List<String> allowedMethods = Arrays.asList("GET", "POST");
        List<String> allowedHeaders = Arrays.asList("Content-Type", "Authorization");

        Server server = new Server(8080);

        server.use(new CorsMiddleware());
        server.use(((request, response, chain) -> {
            System.out.println(" Mi Middleware ");
            return chain.next(request, response);
        }));

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



        server.post("/port", request ->  new Response(200, new JSONObject().put("message", "Okey con POST")));
        server.delete("/delete", request ->  new Response(200, new JSONObject().put("message", "Okey con DELETE")));
        server.put("/put", request ->  new Response(200, new JSONObject().put("message", "Okey con put")));


        server.start();
    }
}