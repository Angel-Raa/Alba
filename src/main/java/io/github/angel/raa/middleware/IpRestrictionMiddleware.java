package io.github.angel.raa.middleware;

import io.github.angel.raa.core.Request;
import io.github.angel.raa.core.Response;
import org.apache.commons.net.util.SubnetUtils;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Middleware para la restricción de direcciones IP.
 * <p>
 * Permite definir listas de direcciones IP permitidas y bloqueadas, además de configurar si se permite el acceso por defecto.
 * Utiliza la librería Apache Commons Net para la validación de direcciones IP y subredes, y permite definir restricciones
 * utilizando la clase {@code SubnetUtils}.
 * </p>
 *
 * <h2>Características:</h2>
 * <ul>
 *     <li>Permite agregar direcciones IP específicas a las listas de permitidos y bloqueados.</li>
 *     <li>Soporta la configuración de rangos de IP mediante notación CIDR.</li>
 *     <li>Posibilidad de definir un comportamiento por defecto cuando una IP no está en ninguna lista.</li>
 * </ul>
 *
 * <h2>Ejemplo de uso:</h2>
 * <pre>{@code
 * // Crear el middleware de restricción de IPs
 * IpRestrictionMiddleware ipRestrictionMiddleware = new IpRestrictionMiddleware()
 *         .addAllowedIps(Arrays.asList("192.168.1.10", "192.168.1.20")) // Permitir IPs específicas
 *         .addBlockedIps(Collections.singletonList("192.168.1.15")) // Bloquear una IP específica
 *         .setAllowAllByDefault(false); // Bloquear todas las IPs por defecto
 *
 * // Registrar el middleware en el servidor
 * server.use(ipRestrictionMiddleware);
 * }</pre>
 *
 * @author Angel Aguero
 * @version 1.0
 */
public class IpRestrictionMiddleware implements Middleware {
    private final Set<String> allowedIps = new HashSet<>();
    private final Set<String> blockedIps = new HashSet<>();
    private boolean allowAllByDefault = true;

    /**
     * Constructor de la clase IpRestrictionMiddleware.
     * Inicializa las listas de IPs permitidas y bloqueadas, y establece si se permite todo por defecto o no.
     */
    public IpRestrictionMiddleware() {
    }

    /**
     * Agrega una IP permitida al middleware.
     *
     * @return IpRestrictionMiddleware
     */
    public IpRestrictionMiddleware addAllowedIp(String ip) {
        this.allowedIps.add(ip);
        return this;
    }

    /**
     * Agrega varias IPs permitidas al middleware.
     *
     */
    public IpRestrictionMiddleware addAllowedIp(String... ips) {
        this.allowedIps.addAll(Arrays.asList(ips));
        return this;
    }

    /**
     * Agrega una IP bloqueada al middleware.
     *
     */
    public IpRestrictionMiddleware addBlockedIp(String ip) {
        this.blockedIps.add(ip);
        return this;
    }

    /**
     * Agrega varias IPs bloqueadas al middleware.
     *
     */
    public IpRestrictionMiddleware addBlockedIp(String... ips) {
        this.blockedIps.addAll(Arrays.asList(ips));
        return this;
    }

    /**
     * Establece si se permite todo por defecto o no.
     *
     */
    public IpRestrictionMiddleware setAllowAllByDefault(boolean allowAllByDefault) {
        this.allowAllByDefault = allowAllByDefault;
        return this;
    }

    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        String clientIp = request.getClientIp();
        if (!isIpAllowed(clientIp)) {
            response.setStatus(403);

            response.setBody(new JSONObject().put("error", "Acceso denegado: IP no permitida"));
            response.addHeader("Content-Type", "application/json");

            return false;
        }
        return chain.next(request, response);
    }


    /**
     * Verifica si una IP está permitida.
     *
     */
    private boolean isIpAllowed(String ip) {
        // Si hay IPs bloqueadas y la IP está en la lista negra, denegar
        if (!blockedIps.isEmpty() && blockedIps.contains(ip)) return false;
        // Si hay IPs permitidas, verificar si la IP está en la lista blanca
        if (!allowedIps.isEmpty() && allowedIps.contains(ip)) {
            return allowedIps.contains(ip) || matchesIpRange(ip, allowedIps);
        }

        return allowAllByDefault;
    }

    /**
     * Verifica si una IP coincide con algún rango en la lista.
     *
     */
    private boolean matchesIpRange(String ip, Set<String> ipSet) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            for (String range : ipSet) {
                if (range.contains("/")) {
                    if (isInRange(address, range)) return true;
                } else {
                    if (address.getHostAddress().equals(range)) return true;
                }
            }

        } catch (UnknownHostException ignored) {
        }
        return false;
    }

    /**
     * Verifica si una dirección IP está dentro de un rango CIDR.
     *
     */
    private boolean isInRange(InetAddress address, String cidr) {
        try {
            SubnetUtils subnetUtils = new SubnetUtils(cidr);
            subnetUtils.setInclusiveHostCount(true);
            return subnetUtils.getInfo().isInRange(address.getHostAddress());
        } catch (IllegalArgumentException e) {
            return false;
        }

    }
}
