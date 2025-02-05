package com.github.alba;

import com.github.alba.core.Response;
import com.github.alba.core.Server;
import org.json.JSONObject;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);


        server.get("/hey", request -> {
            Response response =new Response(200, new JSONObject().put("message", "Hola Mundo desde JSON"));

            response.addHeader("Content-Type", "application/json");
            return response;

        });

        server.get("/hello", request -> {
            Response response = new Response(200, "Hola Mundo en texto plano");
            response.addHeader("Content-Type", "text/plain"); // Especificamos el tipo de contenido
            return response;
        });


        server.start();
    }
}