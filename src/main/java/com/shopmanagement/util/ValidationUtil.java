package com.shopmanagement.util;

import java.util.regex.Pattern;

/**
 * Input validation and sanitization utilities.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");

    /**
     * Checks if a string is not null and not blank.
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Sanitizes input by trimming whitespace and stripping HTML tags.
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return HTML_TAG_PATTERN.matcher(input.trim()).replaceAll("");
    }

    /**
     * Safely parses an integer, returning a default value on failure.
     */
    public static int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parses a double, returning a default value on failure.
     */
    public static double parseDoubleSafe(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Validates an email address format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // optional field
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates string length within bounds.
     */
    public static boolean isValidLength(String value, int min, int max) {
        if (value == null) return min == 0;
        int len = value.trim().length();
        return len >= min && len <= max;
    }
}
