package com.github.angel.raa.modules.core.router;

import com.github.angel.raa.modules.core.Server;

/**
 * Interfaz funcional para definir un grupo de rutas.
 *
 * <p>Permite agrupar rutas relacionadas y registrarlas en un servidor HTTP.</p>
 *
 * @see Server
 */

public interface RouteGroup {
    /**
     * Registra las rutas definidas en el grupo.
     *
     * @param server El servidor HTTP al que se van a registrar las rutas.
     * @param prefix El prefijo de ruta para las rutas del grupo.
     */
    void registerRoutes(Server server, String prefix);

    default void registerRoutes(Server server) {
        registerRoutes(server, "");
    }
}
