package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.Server;
import com.github.angel.raa.modules.middleware.LoggerMiddleware;
import com.github.angel.raa.modules.test.PostController;

import java.io.IOException;
public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);

        server.use(new LoggerMiddleware());

        server.addController(new PostController());

        server.get("/hello", res -> {
            Response response = new Response(200, "Hello World");
            response.addHeader("Content-Type", "text/plain");
            return response;
        });

        server.start();
    }
}
