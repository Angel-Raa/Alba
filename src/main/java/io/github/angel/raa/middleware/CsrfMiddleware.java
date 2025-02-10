package io.github.angel.raa.middleware;

import io.github.angel.raa.http.Request;
import io.github.angel.raa.http.Response;
import org.json.JSONObject;

/**
 * Middleware para manejar la protección CSRF (Cross-Site Request Forgery).
 *
 *Ejemplo de uso:
 *<pre>{@code
 *    CsrfMiddleware csrfMiddleware = new CsrfMiddleware();
 *    server.use(csrfMiddleware);
 *}</pre>
 */
public class CsrfMiddleware implements Middleware{
    @Override
    public boolean handle(Request request, Response response, MiddlewareChain chain) {
        if(request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("DELETE")){
            String csrfToken = request.getParameter("_csrf");
            String sessionToken = request.getCookie("_csrf");
            if(csrfToken == null || !csrfToken.equals(sessionToken)){
                response.setStatus(403);
                response.setBody(new JSONObject().put("message", "Invalid CSRF token"
                        + " " + csrfToken + " " + sessionToken));
                return false;
            }
        }
        return chain.next(request, response);
    }
}
