package com.tfm.demo;

import java.util.logging.Logger;

/**
 * VULNERABILIDAD: Sensitive Data in Logs
 * 
 * OWASP Top 10 2021: A09:2021 - Security Logging and Monitoring Failures
 * ISO/IEC 25010: Security - Confidentiality, Accountability
 * 
 * Reglas SonarQube:
 * - S5145: Logging arguments should not require evaluation (MAJOR)
 * - S2629: Logging should not be vulnerable to injection attacks (CRITICAL)
 * - S5542: Encryption algorithms should be used with secure mode (BLOCKER - relacionado)
 */
public class LoggingVulnerabilities {
    
    private static final Logger logger = Logger.getLogger(LoggingVulnerabilities.class.getName());
    
    /**
     * Vulnerabilidad: Logging de credenciales
     * Expone información sensible en archivos de log
     */
    public void loginUser(String username, String password) {
        // VULNERABLE: Password en logs
        logger.info("User login attempt: username=" + username + ", password=" + password);
        
        if (authenticate(username, password)) {
            logger.info("User " + username + " authenticated successfully");
        } else {
            // VULNERABLE: Información sensible en logs de error
            logger.severe("Failed login for user: " + username + " with password: " + password);
        }
    }
    
    /**
     * Vulnerabilidad: Logging de tokens de sesión
     */
    public void createSession(String userId, String sessionToken) {
        // VULNERABLE: Session token en logs (puede ser reutilizado)
        logger.info("Created session for user " + userId + " with token: " + sessionToken);
    }
    
    /**
     * Vulnerabilidad: Logging de datos de tarjetas de crédito
     */
    public void processPayment(String cardNumber, String cvv, double amount) {
        // VULNERABLE: PCI-DSS violation - datos de tarjeta en logs
        logger.info("Processing payment: card=" + cardNumber + ", CVV=" + cvv + ", amount=" + amount);
        
        // Procesar pago...
        
        logger.info("Payment processed successfully for card ending in " + 
                   cardNumber.substring(cardNumber.length() - 4));
    }
    
    /**
     * Vulnerabilidad: Logging de PII (Personally Identifiable Information)
     */
    public void updateUserProfile(String userId, String email, String ssn, String phoneNumber) {
        // VULNERABLE: PII en logs (SSN, email, teléfono)
        logger.info("Updating profile for user " + userId + 
                   ": email=" + email + 
                   ", SSN=" + ssn + 
                   ", phone=" + phoneNumber);
    }
    
    /**
     * Vulnerabilidad S2629: Log injection
     * Input del usuario no sanitizado puede inyectar líneas falsas en logs
     */
    public void logUserAction(String username, String action) {
        // VULNERABLE: Si username contiene '\n', puede crear líneas falsas en log
        logger.info("User action: " + username + " performed " + action);
        // Ejemplo ataque: username = "admin\nUser action: attacker performed ADMIN_DELETE"
    }
    
    /**
     * Vulnerabilidad: Logging de errores con stack trace
     */
    public void handleSensitiveOperation(String apiKey, String data) {
        try {
            // Operación que usa API key
            callExternalAPI(apiKey, data);
        } catch (Exception e) {
            // VULNERABLE: Stack trace puede contener el API key
            logger.severe("API call failed with key: " + apiKey + ", error: " + e.getMessage());
        }
    }
    
    /**
     * Vulnerabilidad: Logging de queries SQL con datos sensibles
     */
    public void executeQuery(String userId, String searchTerm) {
        String query = "SELECT * FROM users WHERE id = '" + userId + 
                      "' AND search = '" + searchTerm + "'";
        
        // VULNERABLE: SQL query puede contener datos sensibles
        logger.info("Executing query: " + query);
    }
    
    /**
     * Vulnerabilidad: Logging en nivel DEBUG en producción
     */
    public void debugUserData(String username, String token, String email) {
        // VULNERABLE: Debug logs pueden estar activos en producción
        logger.fine("DEBUG: Processing user - username:" + username + 
                   ", token:" + token + 
                   ", email:" + email);
    }
    
    // Métodos auxiliares
    private boolean authenticate(String username, String password) {
        return true;
    }
    
    private void callExternalAPI(String apiKey, String data) throws Exception {
        // Simula llamada a API
    }
}
