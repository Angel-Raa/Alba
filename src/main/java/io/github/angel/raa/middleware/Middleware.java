package io.github.angel.raa.middleware;

import io.github.angel.raa.core.Request;
import io.github.angel.raa.core.Response;

/**
 * Middleware interface for handling requests and responses.
 * Middleware is a design pattern that allows for the chaining of multiple handlers,
 * each of which can perform operations on the request and response before passing control to the next handler.
 * This interface defines a single method, handle, which takes a Request, Response, and a MiddlewareChain as parameters.
 * The handle method should return a boolean indicating whether the request should be further processed or not.
 * If the handle method returns false, the request processing will stop and the response will be sent back to the client.
 * If the handle method returns true, the request processing will continue with the next middleware in the chain.
 *
 *
 * <p>
 * Example:
 * <pre>
 *     {@code
 *     public class LoggingMiddleware implements Middleware {
 *         @Override
 *         public boolean handle(Request request, Response response, MiddlewareChain chain) {
 *             System.out.println("Logging: " + request.getMethod() + " " + request.getPath());
 *             return chain.next(request, response);
 *         }
 *     }
 *     }
 *     </pre>
 * </p>
 */
@FunctionalInterface
public interface Middleware {


    /***
     * Handle the request and response.
     * If the handle method returns false, the request processing will stop and the response will be sent back to the client.
     * If the handle method returns true, the request processing will continue with the next middleware in the chain.
     */
    boolean handle(final Request request, final Response response, final MiddlewareChain chain);


}
