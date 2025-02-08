package com.github.angel.raa.modules;

import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.core.Server;
import com.github.angel.raa.modules.middleware.BasicAuthMiddleware;
import com.github.angel.raa.modules.middleware.IpRestrictionMiddleware;
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


        server.addController(new PostController());
        server.get("/hey", req ->  new Response(200, new JSONObject().put("message", "Hello World")));



        server.start();
    }
}
