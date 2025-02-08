package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.Server;

import java.io.IOException;
public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);


        server.get("/", res -> {
            res.getHeaders().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });
            return Response.Ok("Hello World");

        });

        server.get("/hello", res -> {
            Response response = new Response(200, "Hello World");
            response.addHeader("Content-Type", "text/plain");
            response.redirect("https://www.google.com/");
            return response;
        });
        server.start();
    }
}
