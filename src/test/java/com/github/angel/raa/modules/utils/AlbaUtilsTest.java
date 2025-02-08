package com.github.angel.raa.modules.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlbaUtilsTest {

    private static class TestObject {
        private String name;
        private int age;

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    private static class TestObject_2 {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    /**
     * Test case for daysBetween method with two different dates
     */
    @Test
    public void testDaysBetweenWithDifferentDates() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 5, 0, 0);
        
        long days = AlbaUtils.daysBetween(start, end);
        
        assertEquals(4, days, "The number of days between 2023-01-01 and 2023-01-05 should be 4");
    }

    @Test
    public void testDaysBetweenWithExtremeDates() {
        /**
         * Test daysBetween method with extreme dates
         * Expected: Large number of days
         */
        LocalDateTime start = LocalDateTime.MIN;
        LocalDateTime end = LocalDateTime.MAX;
        long days = AlbaUtils.daysBetween(start, end);
        assertTrue(days > 0, "Expected positive number of days, but got: " + days);
        assertTrue(days > 365 * 1000000, "Expected very large number of days, but got: " + days);
    }

    @Test
    public void testDaysBetweenWithFutureDateAsStart() {
        /**
         * Test daysBetween method with future date as start
         * Expected: Negative number of days
         */
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = LocalDateTime.now();
        long days = AlbaUtils.daysBetween(start, end);
        assertTrue(days < 0, "Expected negative number of days, but got: " + days);
    }

    @Test
    public void testDaysBetweenWithNullInputs() {
        /**
         * Test daysBetween method with null inputs
         * Expected: NullPointerException
         */
        assertThrows(NullPointerException.class, () -> AlbaUtils.daysBetween(null, LocalDateTime.now()));
        assertThrows(NullPointerException.class, () -> AlbaUtils.daysBetween(LocalDateTime.now(), null));
        assertThrows(NullPointerException.class, () -> AlbaUtils.daysBetween(null, null));
    }

    @Test
    public void testDaysBetweenWithSameDates() {
        /**
         * Test daysBetween method with same start and end dates
         * Expected: 0 days
         */
        LocalDateTime date = LocalDateTime.now();
        assertEquals(0, AlbaUtils.daysBetween(date, date));
    }



    @Test
    public void testFromJsonWithEmptyInput() {
        /**
         * Test fromJson method with empty input
         */
        String emptyJson = "";
        assertNull(AlbaUtils.fromJson(emptyJson, Object.class));
    }

    @Test
    public void testFromJsonWithIncorrectType() {
        /**
         * Test fromJson method with incorrect type
         */
        String jsonWithWrongType = "{\"name\": 123, \"age\": \"thirty\"}";
        assertNull(AlbaUtils.fromJson(jsonWithWrongType, TestObject_2.class));
    }

    @Test
    public void testFromJsonWithInvalidJson() {
        /**
         * Test fromJson method with invalid JSON input
         */
        String invalidJson = "{\"name\":\"John\", \"age\":30,}"; // Extra comma
        assertNull(AlbaUtils.fromJson(invalidJson, TestObject_2.class));
    }

    @Test
    public void testFromJsonWithMissingFields() {
        /**
         * Test fromJson method with missing fields in JSON
         */
        String jsonWithMissingFields = "{\"name\":\"John\"}";
        TestObject_2 result = AlbaUtils.fromJson(jsonWithMissingFields, TestObject_2.class);
        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(0, result.getAge()); // Default value for int
    }

    @Test
    public void testFromJsonWithNullInput() {
        /**
         * Test fromJson method with null input
         */
        assertNull(AlbaUtils.fromJson(null, Object.class));
    }

    @Test
    public void testGenerateSecurePasswordConsistency() {
        /**
         * Test generateSecurePassword for consistency in length and character set.
         * This tests the edge case of ensuring the generated password adheres to the specified length
         * and only contains characters from the defined character set.
         */
        int length = 20;
        String password = AlbaUtils.generateSecurePassword(length);
        assertEquals(length, password.length(), "Generated password should have the specified length");

        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
        for (char c : password.toCharArray()) {
            assertTrue(validChars.indexOf(c) != -1, "Generated password should only contain valid characters");
        }
    }

    /**
     * Test that generateSecurePassword creates a password of the specified length
     * and contains only valid characters.
     */
    @Test
    public void testGenerateSecurePasswordLengthAndCharacters() {
        int expectedLength = 12;
        String password = AlbaUtils.generateSecurePassword(expectedLength);
        
        assertNotNull(password);
        assertEquals(expectedLength, password.length());
        
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
        for (char c : password.toCharArray()) {
            assertTrue(validChars.indexOf(c) != -1, "Password contains invalid character: " + c);
        }
    }

    @Test
    public void testGenerateSecurePasswordWithLargeLength() {
        /**
         * Test generateSecurePassword with a very large length input.
         * This tests the scenario where the input is outside typical bounds (very large).
         */
        int largeLength = Integer.MAX_VALUE;
        assertThrows(OutOfMemoryError.class, () -> AlbaUtils.generateSecurePassword(largeLength),
                "Should throw OutOfMemoryError for extremely large length");
    }

    @Test
    public void testGenerateSecurePasswordWithNegativeLength() {
        /**
         * Test generateSecurePassword with negative length input.
         * This tests the scenario where the input is outside accepted bounds (negative).
         */
        String password = AlbaUtils.generateSecurePassword(-1);
        assertTrue(password.isEmpty(), "Password should be empty for negative length");
    }

    @Test
    public void testGenerateSecurePasswordWithZeroLength() {
        /**
         * Test generateSecurePassword with zero length input.
         * This tests the scenario where the input is invalid (zero).
         */
        String password = AlbaUtils.generateSecurePassword(0);
        assertTrue(password.isEmpty(), "Password should be empty for zero length");
    }

    @Test
    public void testGetCurrentTimestampFormat() {
        String timestamp = AlbaUtils.getCurrentTimestamp();
        assertNotNull(timestamp, "Timestamp should not be null");
        assertTrue(timestamp.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"), 
                   "Timestamp should match the format dd/MM/yyyy HH:mm:ss");
    }

    @Test
    public void testGetCurrentTimestampLength() {
        String timestamp = AlbaUtils.getCurrentTimestamp();
        assertEquals(19, timestamp.length(), "Timestamp should be exactly 19 characters long");
    }

    @Test
    public void testGetCurrentTimestampNotEmpty() {
        String timestamp = AlbaUtils.getCurrentTimestamp();
        assertFalse(timestamp.isEmpty(), "Timestamp should not be empty");
    }

    @Test
    public void testGetCurrentTimestampParseable() {
        String timestamp = AlbaUtils.getCurrentTimestamp();
        assertDoesNotThrow(() -> {
            LocalDateTime.parse(timestamp, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }, "Timestamp should be parseable as a valid LocalDateTime");
    }

    /**
     * Test that getCurrentTimestamp() returns the current timestamp in the correct format
     */
    @Test
    public void testGetCurrentTimestampReturnsCorrectFormat() {
        String timestamp = AlbaUtils.getCurrentTimestamp();
        assertNotNull(timestamp);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        // Attempt to parse the timestamp
        assertDoesNotThrow(() -> LocalDateTime.parse(timestamp, formatter),
                "The timestamp should be in the format dd/MM/yyyy HH:mm:ss");
        
        // Check if the parsed time is close to the current time
        LocalDateTime parsedTime = LocalDateTime.parse(timestamp, formatter);
        LocalDateTime now = LocalDateTime.now();
        
        assertTrue(parsedTime.isAfter(now.minusSeconds(1)) && parsedTime.isBefore(now.plusSeconds(1)),
                "The timestamp should be within 1 second of the current time");
    }





    @Test
    public void testGetCurrentTimestampWithInvalidFormat() {
        /**
         * Test getCurrentTimestamp with invalid format input
         */
        LocalDateTime date = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> {
            AlbaUtils.getCurrentTimestamp(date, "invalid-format");
        });
    }

    @Test
    public void testGetCurrentTimestampWithNullDate() {
        /**
         * Test getCurrentTimestamp with null date input
         */
        assertThrows(NullPointerException.class, () -> {
            AlbaUtils.getCurrentTimestamp(null, "dd/MM/yyyy HH:mm:ss");
        });
    }

    @Test
    public void testGetCurrentTimestampWithNullFormat() {
        /**
         * Test getCurrentTimestamp with null format input
         */
        LocalDateTime date = LocalDateTime.now();
        assertThrows(NullPointerException.class, () -> {
            AlbaUtils.getCurrentTimestamp(date, null);
        });
    }

    /**
     * Test case for getCurrentTimestamp method with a specific date and format
     */
    @Test
    public void testGetCurrentTimestampWithSpecificDateAndFormat() {
        LocalDateTime testDate = LocalDateTime.of(2023, 5, 15, 10, 30, 0);
        String format = "dd/MM/yyyy HH:mm:ss";
        String expected = "15/05/2023 10:30:00";
        
        String result = AlbaUtils.getCurrentTimestamp(testDate, format);
        
        assertEquals(expected, result, "The formatted date should match the expected string");
    }

    /**
     * Test case for isNotBlank method with blank string
     */
    @Test
    public void testIsNotBlankWithBlankString() {
        String blankString = "   ";
        assertFalse(AlbaUtils.isNotBlank(blankString), "Should return false for blank string");
    }

    /**
     * Test case for isNotBlank method with empty string
     */
    @Test
    public void testIsNotBlankWithEmptyString() {
        String emptyString = "";
        assertFalse(AlbaUtils.isNotBlank(emptyString), "Should return false for empty string");
    }

    @Test
    public void testIsNotBlankWithEmptyString_2() {
        /**
         * Test case for isNotBlank method with an empty string.
         * This tests the scenario where the input is empty.
         */
        assertFalse(AlbaUtils.isNotBlank(""));
    }

    @Test
    public void testIsNotBlankWithMixedWhitespace() {
        /**
         * Test case for isNotBlank method with a string containing mixed whitespace characters.
         * This tests a combination of different types of whitespace.
         */
        assertFalse(AlbaUtils.isNotBlank(" \n\t "));
    }

    @Test
    public void testIsNotBlankWithNewlineOnly() {
        /**
         * Test case for isNotBlank method with a string containing only a newline character.
         * This tests another scenario where the input is effectively empty.
         */
        assertFalse(AlbaUtils.isNotBlank("\n"));
    }

    /**
     * Test case for isNotBlank method with non-blank string
     */
    @Test
    public void testIsNotBlankWithNonBlankString() {
        String nonBlankString = "Hello, World!";
        assertTrue(AlbaUtils.isNotBlank(nonBlankString), "Should return true for non-blank string");
    }

    @Test
    public void testIsNotBlankWithNull() {
        /**
         * Test case for isNotBlank method with null input.
         * This tests the scenario where the input is invalid (null).
         */
        assertFalse(AlbaUtils.isNotBlank(null));
    }

    /**
     * Test case for isNotBlank method with null string
     */
    @Test
    public void testIsNotBlankWithNullString() {
        String nullString = null;
        assertFalse(AlbaUtils.isNotBlank(nullString), "Should return false for null string");
    }

    @Test
    public void testIsNotBlankWithTabOnly() {
        /**
         * Test case for isNotBlank method with a string containing only a tab character.
         * This tests another scenario where the input is effectively empty.
         */
        assertFalse(AlbaUtils.isNotBlank("\t"));
    }

    @Test
    public void testIsNotBlankWithWhitespaceOnly() {
        /**
         * Test case for isNotBlank method with a string containing only whitespace.
         * This tests the scenario where the input is effectively empty (only whitespace).
         */
        assertFalse(AlbaUtils.isNotBlank("   "));
    }

    /**
     * Test case for a valid credit card number where the alternating digits
     * are doubled but do not exceed 9 after doubling.
     */
    @Test
    public void testIsValidCreditCardNumberWithAlternatingDigitsDoubledButNotExceeding9() {
        // This credit card number is valid and follows the Luhn algorithm
        // The alternating digits (from right to left) are doubled but don't exceed 9
        String validCardNumber = "4111111111111111";
        
        assertTrue(AlbaUtils.isValidCreditCardNumber(validCardNumber));
    }

    /**
     * Test case for a valid credit card number using the Luhn algorithm
     */
    @Test
    public void testIsValidCreditCardNumberWithValidNumber() {
        String validCardNumber = "4532015112830366";
        assertTrue(AlbaUtils.isValidCreditCardNumber(validCardNumber));
    }

    /**
     * Test case for isValidCreditCardNumber method with a valid credit card number
     * that passes all checks and has a sum divisible by 10.
     */
    @Test
    public void testIsValidCreditCardNumber_ValidNumber() {
        // Arrange
        String validCardNumber = "4532015112830366"; // This is a valid Visa card number

        // Act
        boolean result = AlbaUtils.isValidCreditCardNumber(validCardNumber);

        // Assert
        assertTrue(result, "The credit card number should be considered valid");
    }

    /**
     * Test case for isValidCreditCardNumber method with a valid credit card number
     * This test checks if the method correctly validates a credit card number
     * where the sum of digits (after applying Luhn algorithm) is divisible by 10
     */
    @Test
    public void testIsValidCreditCardNumber_ValidNumber_2() {
        // Valid credit card number (Visa)
        String validCardNumber = "4532015112830366";
        
        boolean result = AlbaUtils.isValidCreditCardNumber(validCardNumber);
        
        assertTrue(result, "The credit card number should be valid");
    }

    @Test
    public void testIsValidCreditCardNumber_emptyInput() {
        /**
         * Test that the method returns false when the input is an empty string.
         */
        assertFalse(AlbaUtils.isValidCreditCardNumber(""));
    }

    @Test
    public void testIsValidCreditCardNumber_invalidChecksum() {
        /**
         * Test that the method returns false when the input is a numeric string of valid length but fails the Luhn algorithm check.
         */
        assertFalse(AlbaUtils.isValidCreditCardNumber("1234567890123456"));
    }

    @Test
    public void testIsValidCreditCardNumber_invalidLength() {
        /**
         * Test that the method returns false when the input is too short (less than 13 digits).
         */
        assertFalse(AlbaUtils.isValidCreditCardNumber("123456789012"));
    }

    @Test
    public void testIsValidCreditCardNumber_nonNumericInput() {
        /**
         * Test that the method returns false when the input contains non-numeric characters.
         */
        assertFalse(AlbaUtils.isValidCreditCardNumber("4111111111111ABC"));
    }

    @Test
    public void testIsValidCreditCardNumber_nullInput() {
        /**
         * Test that the method returns false when the input is null.
         */
        assertFalse(AlbaUtils.isValidCreditCardNumber(null));
    }

    @Test
    public void testIsValidCreditCardNumber_withSpacesAndDashes() {
        /**
         * Test that the method correctly handles input with spaces and dashes.
         */
        assertTrue(AlbaUtils.isValidCreditCardNumber("4111-1111-1111-1111"));
        assertTrue(AlbaUtils.isValidCreditCardNumber("4111 1111 1111 1111"));
    }

    /**
     * Test case for isValidEmail method with invalid email addresses
     */
    @Test
    public void testIsValidEmailWithInvalidAddresses() {
        assertFalse(AlbaUtils.isValidEmail(null));
        assertFalse(AlbaUtils.isValidEmail(""));
        assertFalse(AlbaUtils.isValidEmail("userexample.com"));
        assertFalse(AlbaUtils.isValidEmail("user@.com"));
        assertFalse(AlbaUtils.isValidEmail("user@example"));
        assertFalse(AlbaUtils.isValidEmail("user@exam ple.com"));
    }

    /**
     * Test case for isValidEmail method with invalid email address
     */
    @Test
    public void testIsValidEmailWithInvalidEmail() {
        String invalidEmail = "invalid.email@";
        assertFalse(AlbaUtils.isValidEmail(invalidEmail));
    }

    /**
     * Test case for isValidEmail method with valid email addresses
     */
    @Test
    public void testIsValidEmailWithValidAddresses() {
        assertTrue(AlbaUtils.isValidEmail("user@example.com"));
        assertTrue(AlbaUtils.isValidEmail("user.name@example.co.uk"));
        assertTrue(AlbaUtils.isValidEmail("user+tag@example.org"));
        assertTrue(AlbaUtils.isValidEmail("user123@example.net"));
    }

    /**
     * Test case for isValidEmail method with valid email address
     */
    @Test
    public void testIsValidEmailWithValidEmail() {
        String validEmail = "test@example.com";
        assertTrue(AlbaUtils.isValidEmail(validEmail));
    }

    @Test
    public void testIsValidEmail_EmptyInput() {
        /**
         * Test case for empty input in isValidEmail method.
         * This test verifies that the method returns false for an empty string.
         */
        assertFalse(AlbaUtils.isValidEmail(""));
    }

    @Test
    public void testIsValidEmail_InvalidCharacters() {
        /**
         * Test case for invalid characters in email address for isValidEmail method.
         * This test verifies that the method returns false for email addresses containing invalid characters.
         */
        assertFalse(AlbaUtils.isValidEmail("user name@domain.com"));
        assertFalse(AlbaUtils.isValidEmail("user<>@domain.com"));
        assertFalse(AlbaUtils.isValidEmail("user()@domain.com"));
    }

    @Test
    public void testIsValidEmail_InvalidDomainPart() {
        /**
         * Test case for invalid domain part in email address for isValidEmail method.
         * This test verifies that the method returns false for email addresses with invalid domain parts.
         */
        assertFalse(AlbaUtils.isValidEmail("user@domain"));
        assertFalse(AlbaUtils.isValidEmail("user@.com"));
        assertFalse(AlbaUtils.isValidEmail("user@domain..com"));
    }

    @Test
    public void testIsValidEmail_InvalidFormat() {
        /**
         * Test case for invalid email format in isValidEmail method.
         * This test verifies that the method returns false for various invalid email formats.
         */
        assertFalse(AlbaUtils.isValidEmail("user@.com"));
        assertFalse(AlbaUtils.isValidEmail("user@com"));
        assertFalse(AlbaUtils.isValidEmail("user.com"));
        assertFalse(AlbaUtils.isValidEmail("@domain.com"));
    }

    @Test
    public void testIsValidEmail_NullInput() {
        /**
         * Test case for null input in isValidEmail method.
         * This test verifies that the method returns false for a null input.
         */
        assertFalse(AlbaUtils.isValidEmail(null));
    }



    /**
     * Test successful conversion of an object to JSON string
     */
    @Test
    public void testToJsonSuccessfulConversion() {
        // Arrange
        TestObject testObject = new TestObject("Test Name", 25);
        String expectedJson = "{\"name\":\"Test Name\",\"age\":25}";

        // Act
        String actualJson = AlbaUtils.toJson(testObject);

        // Assert
        assertNotNull(actualJson);
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testToJsonWithCircularReference() {
        /**
         * Test toJson method with an object containing a circular reference.
         * This tests the exception handling for objects that can't be serialized.
         */
        class CircularReference {
            CircularReference ref = this;
        }
        
        String result = AlbaUtils.toJson(new CircularReference());
        assertNull(result, "toJson should return null for objects with circular references");
    }



    @Test
    public void testToJsonWithLargeObject() {
        /**
         * Test toJson method with a large object.
         * This tests the behavior when the input is at the bounds of what can be processed.
         */
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 10_000_000; i++) {
            largeString.append("a");
        }
        
        String result = AlbaUtils.toJson(largeString.toString());
        assertNotNull(result, "toJson should handle large objects without returning null");
    }

    @Test
    public void testToJsonWithNonSerializableObject() {
        /**
         * Test toJson method with a non-serializable object.
         * This tests the exception handling for objects that can't be serialized to JSON.
         */
        class NonSerializable {
            private final Object nonSerializableField = new Object();
        }
        
        String result = AlbaUtils.toJson(new NonSerializable());
        assertNull(result, "toJson should return null for non-serializable objects");
    }


    @Test
    public void testToJsonWithSpecialCharacters() {
        /**
         * Test toJson method with special characters.
         * This tests the correct handling of special characters in JSON encoding.
         */
        Map<String, String> specialChars = Map.of("key", "value with \"quotes\" and \\ backslash");
        String result = AlbaUtils.toJson(specialChars);
        assertNotNull(result, "toJson should handle special characters without returning null");
        assertTrue(result.contains("\\\"quotes\\\""), "toJson should properly escape quotes");
        assertTrue(result.contains("\\\\"), "toJson should properly escape backslashes");
    }





    @Test
    public void testToPrettyJsonWithLargeObject() {
        /**
         * Test toPrettyJson with a large object
         * This tests the scenario where the input might be outside accepted bounds
         */
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 1000000; i++) {
            largeString.append("a");
        }
        String result = AlbaUtils.toPrettyJson(largeString.toString());
        assertNotNull(result, "toPrettyJson should not return null for large objects");
        assertTrue(result.length() > 1000000, "toPrettyJson result should be a large string");
    }

    @Test
    public void testToPrettyJsonWithNonSerializableObject() {
        /**
         * Test toPrettyJson with a non-serializable object
         * This tests the scenario where the input is of an incorrect type
         */
        class NonSerializable {
            private final Object circular = this;
        }
        String result = AlbaUtils.toPrettyJson(new NonSerializable());
        assertNull(result, "toPrettyJson should return null for non-serializable objects");
    }


    /**
     * Test case for toPrettyJson method with a simple object
     */
    @Test
    public void testToPrettyJsonWithSimpleObject() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode testObject = mapper.createObjectNode();
        testObject.put("name", "John Doe");
        testObject.put("age", 30);

        // Act
        String result = AlbaUtils.toPrettyJson(testObject);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("{\n"));
        assertTrue(result.contains("  \"name\" : \"John Doe\",\n"));
        assertTrue(result.contains("  \"age\" : 30\n"));
        assertTrue(result.contains("}"));
    }

    /**
     * Test valid credit card number with alternating digits
     * This test checks if a valid credit card number with alternating digits is correctly identified
     */
    @Test
    public void test_isValidCreditCardNumber_withAlternatingDigits() {
        // Arrange
        String validCardNumber = "4111111111111111"; // This is a valid Visa test number

        // Act
        boolean result = AlbaUtils.isValidCreditCardNumber(validCardNumber);

        // Assert
        assertTrue(result, "The credit card number should be valid");
    }

}