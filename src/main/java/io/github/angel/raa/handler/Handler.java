package io.github.angel.raa.handler;

import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;

/**
 * Handler interface for handling requests and responses.
 * This interface defines a single method, handle, which takes a Request as a parameter and returns a Response.
 * The handle method should be implemented by classes that need to handle requests and generate responses.
 */
@FunctionalInterface
public interface Handler {
    /**
     * Handle the request and return a response.
     *
     */
    Response handle(Request request);
}
