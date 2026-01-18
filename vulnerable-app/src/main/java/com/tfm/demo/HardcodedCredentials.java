package com.tfm.demo;

/**
 * A01:2021 – Broken Access Control
 * Ejemplo: Credenciales hardcodeadas en el código
 */
public class HardcodedCredentials {

    // VULNERABILITY: Credenciales en texto plano
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "P@ssw0rd123!";
    private static final String API_KEY = "sk_live_51234567890abcdef";
    
    public boolean authenticateUser(String username, String password) {
        // VULNERABILITY: Comparación directa con credenciales hardcodeadas
        return DB_USERNAME.equals(username) && DB_PASSWORD.equals(password);
    }
    
    public String getApiKey() {
        // VULNERABILITY: Exponer API key
        return API_KEY;
    }
}
