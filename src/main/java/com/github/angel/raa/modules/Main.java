package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.Server;
import com.github.angel.raa.modules.middleware.BasicAuthMiddleware;
import com.github.angel.raa.modules.middleware.LoggerMiddleware;
import com.github.angel.raa.modules.test.Login;
import com.github.angel.raa.modules.test.PostController;

import org.json.JSONObject;

import java.io.IOException;
;

public class Main {

    public static void main(String[] args) throws IOException {

        Server server = new Server(8080);
        //LanguageMiddleware languageMiddleware = new LanguageMiddleware(); TODO: Cannot invoke "String.replace(java.lang.CharSequence, java.lang.CharSequence)" because "ranges" is null
        BasicAuthMiddleware authMiddleware = new BasicAuthMiddleware();


        // Middleware global para validar datos
        server.use(new LoggerMiddleware());
        // Auth global

        server.use(authMiddleware);


        authMiddleware.addExcludedPath("/login");
        authMiddleware.addExcludedPathPattern("/sample/*");
        authMiddleware.setRealm("Error");

        server.addController(new PostController(), authMiddleware);
        server.post("/login", request -> {
            Login login = request.getBodyAs(Login.class);
            String username = login.getUsername();
            String password = login.getPassword();
            System.out.println("Username : "  + username + " " + "password " + password);
            authMiddleware.addCredential(username, password);
            return new Response(200, new JSONObject().put("message", "Hey"));
        });

        server.get("/public", request -> new Response(200, "Libre jeje"));
        server.get("/get", request -> new Response(200, new JSONObject().put("messge", "Hey")));
        server.post("/port", request -> new Response(200, new JSONObject().put("message", "Hey")));
        server.delete("/delete", request -> new Response(200, new JSONObject().put("message", "Hey")));
        server.put("/put", request -> new Response(200, new JSONObject().put("message", "Hey")));


        server.start();
    }
}
