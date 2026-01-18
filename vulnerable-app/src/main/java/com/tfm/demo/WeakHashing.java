package com.tfm.demo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * VULNERABILIDAD: Weak Hashing Algorithms
 * 
 * OWASP Top 10 2021: A02:2021 - Cryptographic Failures
 * ISO/IEC 25010: Security - Confidentiality
 * 
 * Reglas SonarQube:
 * - S4790: Using weak hashing algorithms is security-sensitive (BLOCKER)
 * - S2070: SHA-1 and Message-Digest hash algorithms should not be used (BLOCKER)
 */
public class WeakHashing {
    
    /**
     * Vulnerabilidad S4790: MD5 hashing
     * MD5 está roto criptográficamente y es vulnerable a colisiones
     */
    public String hashPasswordWithMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // VULNERABLE: MD5
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }
    
    /**
     * Vulnerabilidad S2070: SHA-1 hashing
     * SHA-1 está deprecado y vulnerable a ataques de colisión
     */
    public String hashPasswordWithSHA1(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1"); // VULNERABLE: SHA-1
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not available", e);
        }
    }
    
    /**
     * Vulnerabilidad: Hash sin salt
     * Vulnerable a rainbow table attacks
     */
    public String hashWithoutSalt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // VULNERABLE
            return Base64.getEncoder().encodeToString(
                md.digest(input.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    /**
     * Vulnerabilidad: Usar hash débil para tokens de seguridad
     */
    public String generateSecurityToken(String userId) {
        try {
            String data = userId + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("SHA1"); // VULNERABLE: SHA-1
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
