package com.github.angel.raa.modules.core;

import org.junit.jupiter.api.Test;
import com.github.angel.raa.modules.handler.Handler;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class RouterTest {


    private Router router;

    @BeforeEach
    void setUp() {
        router = new Router();
    }

    @BeforeEach
    void setUp_2() {
        router = new Router();
        router.addRoute("GET", "/users/:id", req -> new Response(200, "OK"));
    }

    /**
     * Test adding a route and verifying it can be retrieved
     */
    @Test
    public void testAddRouteAndRetrieveHandler() {
        Router router = new Router();
        String method = "GET";
        String path = "/test";
        Handler handler = request -> new Response(200, "OK");

        router.addRoute(method, path, handler);

        Handler retrievedHandler = router.getHandler(method, path);
        assertNotNull(retrievedHandler, "Handler should not be null");
        assertEquals(handler, retrievedHandler, "Retrieved handler should match the added handler");

        RouteMatch routeMatch = router.getRouteMatch(method, path);
        assertNotNull(routeMatch, "RouteMatch should not be null");
        assertEquals(handler, routeMatch.getHandler(), "RouteMatch handler should match the added handler");
    }

    @Test
    public void testAddRouteDuplicateRoute() {
        /**
         * Test adding a duplicate route.
         * This tests an edge case for the addRoute method.
         */
        Handler handler1 = request -> null;
        Handler handler2 = request -> null;
        router.addRoute("GET", "/test", handler1);
        router.addRoute("GET", "/test", handler2);
        
        // Verify that the second handler overwrites the first one
        assertEquals(handler2, router.getHandler("GET", "/test"));
    }







    /**
     * Test getHandler method when a matching route exists
     */
    @Test
    public void testGetHandlerWithMatchingRoute() {
        // Arrange
        String method = "GET";
        String path = "/users/123";
        Handler mockHandler = mock(Handler.class);
        router.addRoute(method, "/users/:id", mockHandler);

        // Act
        Handler result = router.getHandler(method, path);

        // Assert
        assertNotNull(result);
        assertEquals(mockHandler, result);
    }

    /**
     * Test getHandler method when no matching route exists
     */
    @Test
    public void testGetHandlerWithNoMatchingRoute() {
        // Arrange
        String method = "POST";
        String path = "/nonexistent";

        // Act
        Handler result = router.getHandler(method, path);

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetHandler_CaseSensitivity() {
        /**
         * Test getHandler method for case sensitivity in method and path
         */
        router.addRoute("GET", "/test", request -> new Response(200, "Test"));
        assertNull(router.getHandler("get", "/test"));
        assertNull(router.getHandler("GET", "/TEST"));
    }

    @Test
    public void testGetHandler_EmptyInput() {
        /**
         * Test getHandler method with empty input for both method and path
         */
        assertNull(router.getHandler("", ""));
    }

    @Test
    public void testGetHandler_InvalidMethod() {
        /**
         * Test getHandler method with an invalid HTTP method
         */
        router.addRoute("GET", "/test", request -> {
            return new Response(200," test");
        });
        assertNull(router.getHandler("INVALID", "/test"));
    }

    @Test
    public void testGetHandler_NonExistentPath() {
        /**
         * Test getHandler method with a non-existent path
         */
        router.addRoute("GET", "/test", request ->new Response(200, "test"));
        assertNull(router.getHandler("GET", "/nonexistent"));
    }


    @Test
    public void testGetHandler_PartialMatch() {
        /**
         * Test getHandler method with a partial path match
         */
        router.addRoute("GET", "/test/path", request -> {
            return new Response(200,"test");
        });
        assertNull(router.getHandler("GET", "/test"));
    }

    /**
     * Test getRouteMatch when no matching route is found
     */
    @Test
    public void testGetRouteMatchNoMatchingRoute() {
        Router router = new Router();
        Handler handler = request -> {
            // Handler implementation
            return new Response(200, "Test Response");
        };
        router.addRoute("GET", "/test", handler);

        RouteMatch result = router.getRouteMatch("POST", "/test");

        assertNull(result, "RouteMatch should be null when no matching route is found");
    }


    /**
     * Test getRouteMatch with empty method and path
     */
    @Test
    public void testGetRouteMatchWithEmptyInput() {
        RouteMatch match = router.getRouteMatch("", "");
        assertNull(match, "Route match should be null for empty input");
    }

    /**
     * Test getRouteMatch with incorrect number of path segments
     */
    @Test
    public void testGetRouteMatchWithIncorrectPathSegments() {
        RouteMatch match = router.getRouteMatch("GET", "/users/1/extra");
        assertNull(match, "Route match should be null for incorrect number of path segments");
    }

    /**
     * Test getRouteMatch with invalid method
     */
    @Test
    public void testGetRouteMatchWithInvalidMethod() {
        RouteMatch match = router.getRouteMatch("INVALID", "/users/1");
        assertNull(match, "Route match should be null for invalid method");
    }

    /**
     * Test getRouteMatch with mismatched path
     */
    @Test
    public void testGetRouteMatchWithMismatchedPath() {
        RouteMatch match = router.getRouteMatch("GET", "/invalid/path");
        assertNull(match, "Route match should be null for mismatched path");
    }




}