package com.github.angel.raa.modules.core;

import com.github.angel.raa.modules.exceptions.HttpException;
import com.github.angel.raa.modules.handler.Handler;
import com.github.angel.raa.modules.middleware.Middleware;
import com.github.angel.raa.modules.middleware.MiddlewareChain;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.lang.System.out;

/**
 * Clase principal del servidor HTTP de Alba.
 *
 * <p>Proporciona un servidor ligero y eficiente para manejar solicitudes HTTP.
 * Permite definir rutas, middlewares y manejar múltiples solicitudes concurrentes
 * mediante un pool de hilos.</p>
 *
 * <h2>Características:</h2>
 * <ul>
 *     <li>Soporte para rutas estáticas y dinámicas con parámetros.</li>
 *     <li>Manejo de métodos HTTP específicos (GET, POST, PUT, DELETE, etc.).</li>
 *     <li>Registro de eventos y errores mediante un sistema de logging integrado.</li>
 *     <li>Configuración flexible de host y puerto.</li>
 *     <li>Compatibilidad con middlewares globales y específicos por ruta.</li>
 *     <li>Gestión eficiente de concurrencia mediante un pool de hilos.</li>
 *     <li>Facilidad para iniciar y detener el servidor de manera controlada.</li>
 * </ul>
 *
 * <h2>Ejemplo de Uso:</h2>
 * <pre>{@code
 * // Crear una instancia del servidor en el puerto 8080
 * Server server = new Server(8080);
 *
 * // Definir rutas para distintos métodos HTTP
 * server.get("/get", request ->
 *     new Response(200, new JSONObject().put("message", "Hey"))
 * );
 *
 * server.post("/post", request ->
 *     new Response(200, new JSONObject().put("message", "Hey"))
 * );
 *
 * server.delete("/delete", request ->
 *     new Response(200, new JSONObject().put("message", "Hey"))
 * );
 *
 * server.put("/put", request ->
 *     new Response(200, new JSONObject().put("message", "Hey"))
 * );
 *
 * // Iniciar el servidor
 * server.start();
 *
 * // Detener el servidor cuando sea necesario
 * server.stop();
 * }</pre>
 */
public class Server {
    private static final System.Logger logger = System.getLogger(Server.class.getName());
    private final int port;
    private final String host;
    private final Router router = new Router();
    private final List<Middleware> globalMiddlewares = new ArrayList<>();
    private final ExecutorService threadPool;
    private volatile boolean running = true;

    public Server(int port, String host) {
        this.port = port;
        this.host = host;
        this.threadPool = Executors.newFixedThreadPool(10); // Pool de hilos
    }

    public Server(int port) {
        this(port, "localhost");
    }

    /**
     * Agrega un middleware global.
     */
    public void use(Middleware middleware) {
        globalMiddlewares.add(middleware);
    }

    /**
     * Métodos abreviados para registrar rutas.
     */
    public void get(String path, Handler handler, Middleware... middlewares) {
        addRouteWithMiddleware("GET", path, handler, middlewares);
    }

    public void post(String path, Handler handler, Middleware... middlewares) {
        addRouteWithMiddleware("POST", path, handler, middlewares);
    }

    public void put(String path, Handler handler, Middleware... middlewares) {
        addRouteWithMiddleware("PUT", path, handler, middlewares);
    }

    public void delete(String path, Handler handler, Middleware... middlewares) {
        addRouteWithMiddleware("DELETE", path, handler, middlewares);
    }

    /**
     * Método auxiliar para agregar una ruta con middlewares específicos.
     */
    private void addRouteWithMiddleware(String method, String path, Handler handler, Middleware[] middlewares) {
        router.addRoute(method, path, request -> {
            Response response = new Response();

            // Crear una cadena de middlewares específica para esta ruta
            MiddlewareChain chain = new MiddlewareChain(
                    Stream.concat(Arrays.stream(middlewares), globalMiddlewares.stream()).iterator()
            );

            // Ejecutar la cadena de middlewares
            if (!chain.next(request, response)) {
                return response; // Devolver la respuesta si un middleware falla
            }

            // Si no hay errores, ejecutar el manejador
            return handler.handle(request);
        });
    }

    /**
     * Detiene el servidor.
     */
    public void stop() {
        running = false;
        threadPool.shutdown();
        logger.log(System.Logger.Level.INFO, "Servidor detenido");
    }

    /**
     * Inicia el servidor.
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logger.log(System.Logger.Level.INFO, "[" + timestamp + "] Servidor iniciado en el puerto " + this.port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> {
                    try {
                        handleRequest(clientSocket);
                    } catch (IOException e) {
                        logger.log(System.Logger.Level.ERROR, "Error al manejar la solicitud", e);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al iniciar el servidor", e);
        }
    }

    /**
     * Maneja una solicitud HTTP entrante.
     */
    private void handleRequest(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // Parsear la línea de solicitud
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                sendResponse(out, new Response(400, new JSONObject().put("error", "Solicitud inválida")));
                return;
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendResponse(out, new Response(400, new JSONObject().put("error", "Solicitud inválida")));
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];

            // Leer cabeceras
            Map<String, String> headers = new HashMap<>();
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            // Leer cuerpo
            StringBuilder body = new StringBuilder();
            if (headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] buffer = new char[contentLength];
                in.read(buffer, 0, contentLength);
                body.append(buffer);
            }

            // Crear objetos Request y Response
            Request request = new Request(clientSocket, method, path, headers, !body.isEmpty() ? new JSONObject(body.toString()) : new JSONObject());
            Response response = new Response();

            // Buscar la ruta coincidente
            RouteMatch routeMatch = router.getRouteMatch(method, path);
            if (routeMatch != null) {
                request.setParams(routeMatch.getParams()); // Almacenar parámetros dinámicos
                Handler handler = routeMatch.getHandler();

                // Crear la cadena de middlewares
                MiddlewareChain chain = new MiddlewareChain(globalMiddlewares.iterator());

                // Ejecutar middlewares globales
                if (!chain.next(request, response)) {
                    sendResponse(out, response); // Respuesta generada por un middleware
                    return;
                }

                // Ejecutar el manejador
                response = handler.handle(request);
                sendResponse(out, response);
            } else {
                sendResponse(out, new Response(404, new JSONObject().put("error", "Ruta no encontrada")));
            }
        } catch (HttpException e) {
            sendResponse(out, new Response(e.getStatusCode(), new JSONObject().put("error", e.getMessage())));
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR, "Error interno del servidor", e);
            sendResponse(out, new Response(500, new JSONObject().put("error", "Error interno del servidor")));
        }
    }

    /**
     * Envía una respuesta HTTP al cliente.
     */
    private void sendResponse(OutputStream out, Response response) throws IOException {
        out.write(response.build().getBytes());
    }
}
