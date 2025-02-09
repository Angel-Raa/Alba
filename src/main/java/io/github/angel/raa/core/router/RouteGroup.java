package io.github.angel.raa.core.router;

import io.github.angel.raa.core.Server;

/**
 * Interfaz funcional para definir un grupo de rutas.
 *
 * <p>Permite agrupar rutas relacionadas y registrarlas en un servidor HTTP.</p>
 *
 */

public interface RouteGroup {
    /**
     * Registra las rutas definidas en el grupo.
     *

     */
    void registerRoutes(Server server, String prefix);

    default void registerRoutes(Server server) {
        registerRoutes(server, "");
    }
}
