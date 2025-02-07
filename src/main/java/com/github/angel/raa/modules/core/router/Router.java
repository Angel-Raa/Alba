package com.github.angel.raa.modules.core.router;

import com.github.angel.raa.modules.exceptions.RouteException;
import com.github.angel.raa.modules.handler.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase `Router`: Maneja las rutas y sus correspondientes manejadores en la aplicación.
 *
 * <p>Esta clase permite registrar rutas por método HTTP, buscar rutas eficientes y extraer
 * parámetros dinámicos en las solicitudes. Utiliza una estructura de datos optimizada para
 * realizar búsquedas rápidas y asignar los manejadores de forma eficiente.</p>
 *
 * <h2>Características:</h2>
 * <ul>
 *     <li>Almacena rutas en un mapa estructurado por método HTTP.</li>
 *     <li>Permite definir rutas con parámetros dinámicos (ej. `/users/:id`).</li>
 *     <li>Ofrece una búsqueda eficiente de rutas mediante `getRouteMatch`.</li>
 *     <li>Facilita la extracción automática de parámetros dinámicos con `extractParams`.</li>
 * </ul>
 *
 * <h2>Métodos Principales:</h2>
 * <ul>
 *     <li><b>addRoute(String method, String path, Handler handler)</b>: Agrega una nueva ruta al enrutador.</li>
 *     <li><b>getRouteMatch(String method, String path)</b>: Obtiene el manejador correspondiente a una ruta específica.</li>
 *     <li><b>extractParams(String route, String path)</b>: Extrae los parámetros dinámicos de una ruta.</li>
 * </ul>
 *
 * <h2>Ejemplo de Uso:</h2>
 * <pre>{@code
 * // Crear instancia del enrutador
 * Router router = new Router();
 *
 * // Agregar una ruta con parámetro dinámico
 * router.addRoute("GET", "/users/:id", request -> {
 *     // Manejar la solicitud con el ID del usuario
 * });
 *
 * // Buscar una ruta coincidente con un valor dinámico
 * RouteMatch match = router.getRouteMatch("GET", "/users/123");
 * if (match != null) {
 *     Handler handler = match.getHandler();
 *     // Procesar la solicitud con el handler correspondiente
 * }
 * }</pre>
 */
public class Router {
    // Mapa para almacenar las rutas por método HTTP
    private final Map<String, Map<String, Handler>> routes = new HashMap<>();

    /**
     * Extrae la ruta sin parámetros de consulta.
     *
     * @param fullPath Ruta completa, incluyendo parámetros de consulta
     * @return Ruta sin parámetros de consulta
     */
    public static String extractPathWithoutQueryParams(String fullPath) {
        int queryIndex = fullPath.indexOf('?');

        return queryIndex != -1 ? fullPath.substring(0, queryIndex) : fullPath;
    }

    /**
     * Agrega una nueva ruta al enrutador.
     *
     * @param method  Método HTTP (GET, POST, PUT, DELETE, etc.)
     * @param path    Ruta (puede incluir parámetros dinámicos, ej.: /users/:id)
     * @param handler Manejador asociado a la ruta
     */
    public void addRoute(String method, String path, Handler handler) {
        routes.computeIfAbsent(method.toUpperCase(), k -> new HashMap<>()).put(path, handler);
    }

    /**
     * Obtiene el manejador correspondiente a una ruta específica.
     *
     * @param method   Método HTTP
     * @param fullPath Ruta solicitada
     * @return Un objeto RouteMatch si se encuentra una coincidencia, o null si no.
     */
    public RouteMatch getRouteMatch(String method, String fullPath) {
        String path = extractPathWithoutQueryParams(fullPath);

        Map<String, Handler> methodRoutes = routes.get(method.toUpperCase());
        if (methodRoutes == null) {
            return null;
        }

        for (Map.Entry<String, Handler> entry : methodRoutes.entrySet()) {
            String routePath = entry.getKey();
            Handler handler = entry.getValue();

            Map<String, String> params = extractParams(routePath, path);
            if (params != null) {
                return new RouteMatch(params, handler);
            }
        }

        return null;
    }

    /**
     * Agrega un controlador al enrutador.
     *
     * @param controller
     * @throws RouteException Si hay un error al agregar las rutas del controlador
     */
    public void addController(Controller controller) {
        for (Map.Entry<String, Handler> entry : controller.getRoutes().entrySet()) {
            String methodAndPath = entry.getKey();
            Handler handler = entry.getValue();
            String[] parts = methodAndPath.split(" ", 2);
            if (parts.length == 2) {
                String method = parts[0]; // GET, POST, etc.
                String path = parts[1];  // /post/posts
                addRoute(method, path, handler);
            } else {
                throw new RouteException("Invalid controller route: " + methodAndPath);
            }

        }
    }

    /**
     * Extrae los parámetros dinámicos de una ruta.
     *
     * @param routePath Ruta definida en el enrutador (puede incluir parámetros dinámicos)
     * @param path      Ruta solicitada
     * @return Un mapa con los parámetros extraídos, o null si no hay coincidencia.
     */
    private Map<String, String> extractParams(String routePath, String path) {
        String[] routeParts = routePath.split("/");
        String[] pathParts = path.split("/");

        if (routeParts.length != pathParts.length) {
            return null; // Las rutas no coinciden en longitud
        }

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < routeParts.length; i++) {
            String routePart = routeParts[i];
            String pathPart = pathParts[i];

            if (routePart.startsWith(":")) {
                // Es un parámetro dinámico
                String paramName = routePart.substring(1); // Eliminar el ":"
                params.put(paramName, pathPart);
            } else if (!routePart.equals(pathPart)) {
                return null; // No coincide con la ruta estática
            }
        }

        return params;
    }

    /**
     * Agrega una ruta GET al enrutador.
     *
     * @param path
     * @param handler
     */
    public void get(String path, Handler handler) {
        addRoute("GET", path, handler);
    }

    /**
     * Agrega una ruta POST al enrutador.
     *
     * @param path
     * @param handler
     */
    public void post(String path, Handler handler) {
        addRoute("POST", path, handler);
    }

    /**
     * Agrega una ruta PUT al enrutador.
     *
     * @param path
     * @param handler
     */
    public void put(String path, Handler handler) {
        addRoute("PUT", path, handler);
    }

    /**
     * Agrega una ruta DELETE al enrutador.
     *
     * @param path
     * @param handler
     */
    public void delete(String path, Handler handler) {
        addRoute("DELETE", path, handler);
    }

    /**
     * Agrega una ruta PATCH al enrutador.
     *
     * @param path
     * @param handler
     */
    public void patch(String path, Handler handler) {
        addRoute("PATCH", path, handler);
    }

}
