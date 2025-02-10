package io.github.angel.raa.api;

import io.github.angel.raa.http.Response;
import io.github.angel.raa.core.Server;
import io.github.angel.raa.middleware.LoggerMiddleware;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9090);
        server.use(new LoggerMiddleware());

        server.get("/hey", res -> Response.Ok("Hola Mundo"));
        server.start();

    }



}

