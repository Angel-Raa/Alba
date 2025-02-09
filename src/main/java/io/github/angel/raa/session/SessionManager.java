package io.github.angel.raa.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/***
 * This class manages user sessions.
 * It provides methods to create, retrieve, invalidate, and remove sessions.
 */
public class SessionManager {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_EXPIRATION_TIME = 30 * 60 * 1000;

    /***
     * Creates a new session and returns the session ID.
     */
    public static String createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        sessions.put(sessionId, session);
        return sessionId;
    }

    public static Session getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        Session session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.updateLastAccessTime();
            return session;
        }
        sessions.remove(sessionId);
        return null;
    }

    public static Session getOrCreateSession(String sessionId) {
        if (sessionId == null || !sessions.containsKey(sessionId)) {
            sessionId = UUID.randomUUID().toString();
            Session newSession = new Session(sessionId);
            sessions.put(sessionId, newSession);
            return newSession;
        }
        return getSession(sessionId);
    }

    public static void invalidateSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.invalidate();
        }
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }


}
