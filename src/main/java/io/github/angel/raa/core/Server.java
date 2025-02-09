package io.github.angel.raa.core;

import io.github.angel.raa.core.router.Controller;
import io.github.angel.raa.core.router.RouteMatch;
import io.github.angel.raa.core.router.Router;
import io.github.angel.raa.exceptions.HttpException;
import io.github.angel.raa.exceptions.RouteException;
import io.github.angel.raa.handler.Handler;
import io.github.angel.raa.middleware.Middleware;
import io.github.angel.raa.middleware.MiddlewareChain;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
 *
 */
public class Server {
    private static final System.Logger logger = System.getLogger(Server.class.getName());
    private final int port;
    private final String host;
    private final Router router = new Router();
    private final List<Middleware> globalMiddlewares = new ArrayList<>();
    private final ExecutorService threadPool;
    private volatile boolean running = true;

    /**
     * Constructor de la clase Server.
     * Inicializa el servidor con el puerto, host y tamaño del pool de hilos especificados.
     *
     */
    public Server(int port, String host, int threadPoolSize) {
        this.port = port;
        this.host = host;
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Constructor de la clase Server.
     * Inicializa el servidor con el puerto y host especificados.
     * Tamaño del pool de hilos por defecto: 10
     *
     */
    public Server(int port, String host) {
        this.port = port;
        this.host = host;
        this.threadPool = Executors.newFixedThreadPool(10); // Pool de hilos
    }

    /**
     * Constructor de la clase Server.
     * Inicializa el servidor con el puerto especificado.
     * Host por defecto: "localhost"
     * Tamaño del pool de hilos por defecto: 10
     *
     */
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

    public void addController(Controller controller) {
        for (Map.Entry<String, Handler> entry : controller.getRoutes().entrySet()) {
            String methodAndPath = entry.getKey();
            Handler handler = entry.getValue();
            String[] parts = methodAndPath.split(" ", 2);
            if (parts.length == 2) {
                String method = parts[0]; // GET, POST, etc.
                String path = parts[1];  // /post/posts
                addRouteWithMiddleware(method, path, handler, controller.getMiddlewares().toArray(new Middleware[0]));
            } else {
                throw new RouteException("Invalid controller route: " + methodAndPath);
            }
        }

    }

    /**
     * Agrega un controlador con middlewares específicos.
     *
     */
    public void addController(Controller controller, Middleware... middlewares) {

        for (Map.Entry<String, Handler> entry : controller.getRoutes().entrySet()) {
            String methodAndPath = entry.getKey();
            Handler handler = entry.getValue();

            // Extraer el método y la ruta
            String[] parts = methodAndPath.split(" ", 2);
            if (parts.length == 2) {
                String method = parts[0];
                String path = parts[1];

                // Registrar la ruta con los middlewares específicos
                router.addRoute(method, path, handler, middlewares);
            }
        }

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
     *
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

                // Ejecutar middlewares globales
                MiddlewareChain globalChain = new MiddlewareChain(globalMiddlewares.iterator());
                if (!globalChain.next(request, response)) {
                    sendResponse(out, response); // Respuesta generada por un middleware global
                    return;
                }

                // Ejecutar middlewares específicos de la ruta
                List<Middleware> routeMiddlewares = routeMatch.getMiddlewares();
                if (routeMiddlewares != null && !routeMiddlewares.isEmpty()) {
                    MiddlewareChain routeChain = new MiddlewareChain(routeMiddlewares.iterator());
                    if (!routeChain.next(request, response)) {
                        sendResponse(out, response); // Respuesta generada por un middleware de ruta
                        return;
                    }
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
        String httpResponse = response.build();
        out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
