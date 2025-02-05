package com.github.angel.raa.modules.handler;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;

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
     * @param request
     * @return Response
     */
    Response handle(Request request);
}
