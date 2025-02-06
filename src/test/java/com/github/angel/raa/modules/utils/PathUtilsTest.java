package com.github.angel.raa.modules.utils;

import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PathUtilsTest {

    @Test
    public void testExtractParamsWithEmptyInput() {
        /**
         * Test extractParams with empty input strings.
         * This tests scenario 1: input is empty and/or invalid.
         */
        Map<String, String> result = PathUtils.extractParams("", "");
        assertTrue(result.isEmpty(), "Result should be an empty map for empty inputs");
    }


    @Test
    public void testExtractParamsWithInvalidFormat() {
        /**
         * Test extractParams with invalid format in routePath.
         * This tests scenario 3: input is incorrect format.
         */
        Map<String, String> result = PathUtils.extractParams("/users/id", "/users/123");
        assertTrue(result.isEmpty(), "Should not extract any parameters when routePath doesn't contain ':' prefix");
    }

    @Test
    public void testExtractParamsWithMismatchedPaths() {
        /**
         * Test extractParams with mismatched path lengths.
         * This tests scenario 2: input is outside accepted bounds.
         */
        Map<String, String> result = PathUtils.extractParams("/users/:id", "/users/123/posts");
        assertEquals(1, result.size(), "Should only extract parameters up to the length of the shorter path");
        assertEquals("123", result.get("id"), "Should correctly extract the 'id' parameter");
    }

    /**
     * Test extractParams when route path does not contain any parameters
     */
    @Test
    public void testExtractParamsWithNoParameters() {
        String routePath = "/users/profile";
        String requestPath = "/users/profile";
        
        Map<String, String> result = PathUtils.extractParams(routePath, requestPath);
        
        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractParamsWithNullInput() {
        /**
         * Test extractParams with null input.
         * This tests scenario 1: input is empty and/or invalid, and scenario 4: exceptions are tested.
         */
        assertThrows(NullPointerException.class, () -> {
            PathUtils.extractParams(null, null);
        }, "Method should throw NullPointerException for null inputs");
    }

    /**
     * Test case for extractParams method when route path contains parameters
     */
    @Test
    public void testExtractParamsWithParameters() {
        String routePath = "/users/:id/posts/:postId";
        String requestPath = "/users/123/posts/456";

        Map<String, String> result = PathUtils.extractParams(routePath, requestPath);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("123", result.get("id"));
        assertEquals("456", result.get("postId"));
    }

}