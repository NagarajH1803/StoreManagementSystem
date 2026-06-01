package com.shopmanagement.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password utility using BCrypt for industry-standard password hashing.
 * Supports lazy migration from legacy SHA-256 hashes.
 */
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hashes a password using BCrypt with auto-generated salt.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verifies a password against a stored hash.
     * Supports both BCrypt (starts with "$2a$") and legacy SHA-256 hashes.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || password == null) return false;

        // BCrypt hashes start with "$2a$" or "$2b$"
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$")) {
            return BCrypt.checkpw(password, storedHash);
        }

        // Legacy SHA-256 fallback for migration
        return hashSHA256(password).equals(storedHash);
    }

    /**
     * Checks if a stored hash is using the legacy SHA-256 format
     * and needs migration to BCrypt.
     */
    public static boolean needsMigration(String storedHash) {
        if (storedHash == null) return false;
        return !storedHash.startsWith("$2a$") && !storedHash.startsWith("$2b$");
    }

    /**
     * Legacy SHA-256 hash — only used for verifying old passwords during migration.
     */
    private static String hashSHA256(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            java.math.BigInteger number = new java.math.BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
