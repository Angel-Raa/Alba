package com.github.angel.raa.modules.middleware;

import com.github.angel.raa.modules.core.Request;
import com.github.angel.raa.modules.core.Response;
import com.github.angel.raa.modules.session.Session;
import com.github.angel.raa.modules.session.SessionManager;


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
