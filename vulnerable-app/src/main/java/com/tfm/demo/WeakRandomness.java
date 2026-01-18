package com.tfm.demo;

import java.util.Random;

/**
 * A02:2021 – Cryptographic Failures
 * Uso de generadores de números aleatorios débiles
 */
public class WeakRandomness {

    // VULNERABILITY: Uso de Random en lugar de SecureRandom
    private static final Random random = new Random();
    
    // VULNERABILITY: Generar token de sesión con Random débil
    public String generateSessionToken() {
        // VULNERABLE: Predecible
        long token = random.nextLong();
        return Long.toHexString(token);
    }
    
    // VULNERABILITY: Generar password reset token
    public String generateResetToken() {
        // VULNERABLE: Math.random() es predecible
        return String.valueOf(Math.random() * 1000000);
    }
    
    // VULNERABILITY: Generar CSRF token
    public String generateCSRFToken() {
        // VULNERABLE: Seed predecible
        Random r = new Random(System.currentTimeMillis());
        return String.valueOf(r.nextInt());
    }
}
