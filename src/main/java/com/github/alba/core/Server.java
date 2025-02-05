package com.github.alba.core;

import com.github.alba.exceptions.HttpException;
import com.github.alba.exceptions.NotFoundException;
import com.github.alba.handler.Handler;
import com.github.alba.middleware.LoggerMiddleware;
import com.github.alba.middleware.Middleware;
import com.github.alba.middleware.MiddlewareChain;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;

public class Server {
    private static final System.Logger logger = System.getLogger(Server.class.getName());
    private final int port;
    private final String host;
    private final Router router = new Router();
    private final List<Middleware> globalMiddlewares = new ArrayList<>();
    private ExecutorService threadPool;
    private volatile boolean running = true;

    public Server(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public Server(int port) {
        this(port, "localhost");
        this.threadPool = Executors.newFixedThreadPool(10);

    }

    public void use(Middleware middleware) {
        globalMiddlewares.add(middleware);
    }

    public void get(String path, Handler handler) {
        router.addRoute("GET", path, handler);
    }

    public void post(String path, Handler handler) {
        router.addRoute("POST", path, handler);
    }

    public void put(String path, Handler handler) {
        router.addRoute("PUT", path, handler);
    }

    public void delete(String path, Handler handler) {
        router.addRoute("DELETE", path, handler);
    }

    public void stop() {
        running = false;
        threadPool.shutdown();
        logger.log(System.Logger.Level.INFO, "Server stopped");
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logger.log(System.Logger.Level.INFO, "[" + timestamp + "] Server started on port " + this.port);

            globalMiddlewares.add(new LoggerMiddleware());
            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        handleRequest(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void handleRequest(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // Leer la primera línea de la solicitud HTTP
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                return;
            }
            String method = requestParts[0];
            String path = requestParts[1];

            // Leer cabeceras y cuerpo
            Map<String, String> headers = new HashMap<>();
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                String[] headerParts = line.split(": ");
                headers.put(headerParts[0], headerParts[1]);
            }

            StringBuilder body = new StringBuilder();
            if (headers.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] buffer = new char[contentLength];
                in.read(buffer, 0, contentLength);
                body.append(buffer);
            }


            Request request = new Request(method, path, headers, !body.isEmpty() ? new JSONObject(body.toString()) : new JSONObject());

            MiddlewareChain chain = new MiddlewareChain(globalMiddlewares.iterator());
            // Ejecutar middlewares globales
            if (!chain.next(request, new Response())) {
                Response response = new Response(403, new JSONObject().put("error", "Acceso denegado"));
                out.write(response.build().getBytes());
                return;

            }

            // Buscar el manejador
            Handler handler = router.getHandler(method, path);
            if (handler != null) {
                Response response = handler.handle(request);
                out.write(response.build().getBytes());
            } else {
                throw new NotFoundException("Ruta no encontrada");
            }

        } catch (HttpException e) {
            Response response = new Response(e.getStatusCode(), new JSONObject().put("error", e.getMessage()));
            out.write(response.build().getBytes());
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error al manejar la solicitud", e);
            Response response = new Response(500, new JSONObject().put("error", "Error interno del servidor"));
            out.write(response.build().getBytes());
        }
    }
}
