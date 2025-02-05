package com.github.alba.middleware;

import com.github.alba.core.Request;
import com.github.alba.core.Response;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class StaticFileMiddleware implements Middleware {


    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        String staticFolder = "public";
        String filePath = staticFolder + request.getPath();
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            try {
                response.addHeader("Content-Type", Files.probeContentType(Path.of(filePath)));
                response.setBody(new JSONObject(Files.readAllBytes(Path.of(filePath))));
                response.setStatus(200);
                return false; // No continuar, respuesta servida
            } catch (IOException e) {
                response.setStatus(500);
                response.setBody(new JSONObject("{\"error\": \"Error al servir archivo\"}"));
                return false; // No continuar, respuesta servida
            }
        }
        return chain.next(request, response); // Continuar con el siguiente middleware si no es archivo estático
    }
}
