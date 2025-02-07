package com.github.angel.raa.modules.core.router;

import com.github.angel.raa.modules.annotations.Delete;
import com.github.angel.raa.modules.annotations.Get;
import com.github.angel.raa.modules.annotations.Post;
import com.github.angel.raa.modules.annotations.Put;
import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.handler.Handler;
import com.github.angel.raa.modules.middleware.Middleware;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa un controlador en el enrutador.
 * Cada controlador maneja un prefijo de ruta y contiene rutas y middlewares específicos.
 *
 * @see Router
 * @see Handler
 */
public class Controller {
    private final String prefix;
    private final Map<String, Handler> routes = new HashMap<>();
    private final List<Middleware> middlewares = new ArrayList<>();

    public Controller(String prefix) {
        this.prefix = prefix;
        setupRoutesAutomatically();
    }


    public void addMiddleware(Middleware middleware) {
        middlewares.add(middleware);
    }


    public List<Middleware> getMiddlewares() {
        return middlewares;
    }

    public String getPrefix() {
        return prefix;
    }

    public void addRoute(String method, String path, Handler handler) {
        routes.put(method + prefix + path, handler);
    }

    public Map<String, Handler> getRoutes() {
        return routes;
    }

    private Handler createHandlerChain(List<Middleware> middlewares, Handler handler) {
        return request -> {
            Response response = new Response();
            for (Middleware middleware : middlewares) {
                if (!middleware.handle(request, response, null)) {
                    return response;
                }
            }
            return handler.handle(request);
        };
    }

    /**
     * Configura las rutas del controlador automáticamente basándose en los métodos anotados con @Get, @Post, @Put y @Delete.
     *
     * @see Get
     * @see Post
     * @see Put
     * @see Delete
     */

    private void setupRoutesAutomatically() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Get.class)) {
                Get get = method.getAnnotation(Get.class);
                String path = prefix + get.value();
                routes.put("GET " + path, createHandler(method));
            } else if (method.isAnnotationPresent(Post.class)) {
                Post post = method.getAnnotation(Post.class);
                String path = prefix + post.value();
                routes.put("POST " + path, createHandler(method));
            } else if (method.isAnnotationPresent(Put.class)) {
                Put put = method.getAnnotation(Put.class);
                String path = prefix + put.value();
                routes.put("PUT " + path, createHandler(method));
            } else if (method.isAnnotationPresent(Delete.class)) {
                Delete delete = method.getAnnotation(Delete.class);
                String path = prefix + delete.value();
                routes.put("DELETE " + path, createHandler(method));
            }
        }
    }

    /**
     * Crea un manejador para un método del controlador.
     *
     * @param method Método del controlador
     * @return Manejador creado
     */
    private Handler createHandler(Method method) {
        return request -> {
            try {
                return (Response) method.invoke(this, request);
            }
            catch (InvocationTargetException e){
                Throwable cause = e.getCause();
                if(cause instanceof  IllegalArgumentException){
                    return new Response(400, cause.getMessage()); // Bad Request
                }
                else {
                    return new Response(500, "Error interno del servidor: " + cause.getMessage());

                }
            }
            catch (Exception e) {
                return new Response(500, new JSONObject().put("Error", "Error al invocar el método del controlador"));
            }
        };
    }


}
