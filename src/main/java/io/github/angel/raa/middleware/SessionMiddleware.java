package io.github.angel.raa.middleware;

import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;
import io.github.angel.raa.session.Session;
import io.github.angel.raa.session.SessionManager;


public class SessionMiddleware implements Middleware {
    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        String sessionId = request.getHeader("Session-Id");

        // Si no hay sessionId o la sesión no existe, crea una nueva
        Session session = SessionManager.getOrCreateSession(sessionId);

        // Asigna sessionId a la request
        request.setAttribute("sessionId", session.getSessionId());

        chain.next(request, response);


        return chain.next(request, response);
    }
}
