package com.github.angel.raa.modules.session;

import java.util.HashMap;
import java.util.Map;

/***
 * This class represents a user session.
 * It stores session-specific data and provides methods to manage the session.
 */
public class Session {
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    private final String sessionId;
    private final long creationTime;
    private final Map<String, Object> attributes = new HashMap<>();
    private long lastAccessTime;
    private boolean isValid;

    public Session(String sessionId) {
        this.sessionId = sessionId;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessTime = creationTime;
        this.isValid = true;
    }

    /***
     * Returns the session ID.
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    public void updateLastAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    /***
     * Checks if the session has expired.
     * @return true if the session has expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - lastAccessTime > SESSION_TIMEOUT;
    }

    /***
     * Invalidates the session.
     */
    public void invalidate() {
        this.isValid = false;
    }

    /***
     * Checks if the session is valid.
     * @return true if the session is valid, false otherwise
     */
    public boolean isValid() {
        return isValid;
    }

    /***
     * Sets an attribute in the session.
     * @param name The name of the attribute
     * @param value The value of the attribute
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /***
     * Gets an attribute from the session.
     * @param name The name of the attribute
     * @return The value of the attribute, or null if the attribute does not exist
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /***
     * Removes an attribute from the session.
     * @param name The name of the attribute
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

}
