package com.tfm.demo;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * VULNERABILIDAD: Improper Exception Handling
 * 
 * OWASP Top 10 2021: A09:2021 - Security Logging and Monitoring Failures
 * ISO/IEC 25010: Security - Accountability
 * 
 * Reglas SonarQube:
 * - S5164: Throwable.printStackTrace() should not be called (CRITICAL)
 * - S1148: Throwable.printStackTrace(...) should not be called (CRITICAL)
 * - S2139: Exceptions should be either logged or rethrown but not both (MAJOR)
 * - S1181: Throwable and Error should not be caught (BLOCKER)
 */
public class ExceptionHandlingVulnerabilities {
    
    /**
     * Vulnerabilidad S5164: printStackTrace expone información sensible
     * Stack traces pueden revelar estructura interna, paths, versiones
     */
    public void connectToDatabaseWithStackTrace(String url, String user, String password) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace(); // VULNERABLE: Expone información en logs/consola
        }
    }
    
    /**
     * Vulnerabilidad S1148: printStackTrace en producción
     */
    public String readConfigFile(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            byte[] data = new byte[1024];
            fis.read(data);
            fis.close();
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace(); // VULNERABLE: Stack trace en producción
            return null;
        }
    }
    
    /**
     * Vulnerabilidad S2139: Log y throw (información duplicada)
     */
    public void processPayment(double amount) throws Exception {
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Invalid amount");
            }
            // Procesar pago...
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage()); // VULNERABLE
            throw e; // Logged AND rethrown
        }
    }
    
    /**
     * Vulnerabilidad S1181: Catch Throwable/Error
     * Captura errores que no deberían ser manejados
     */
    public void dangerousCatchAll() {
        try {
            // Operación crítica
            performCriticalOperation();
        } catch (Throwable t) { // VULNERABLE: Captura OutOfMemoryError, StackOverflowError, etc
            System.out.println("Something went wrong");
            // Continúa ejecutándose en estado inválido
        }
    }
    
    /**
     * Vulnerabilidad: Múltiples printStackTrace
     */
    public void multipleStackTraces() {
        try {
            riskyOperation1();
        } catch (Exception e1) {
            e1.printStackTrace(); // VULNERABLE
            try {
                riskyOperation2();
            } catch (Exception e2) {
                e2.printStackTrace(); // VULNERABLE
            }
        }
    }
    
    /**
     * Vulnerabilidad: printStackTrace con información sensible
     */
    public boolean authenticateUser(String username, String password) {
        try {
            // Autenticación...
            if (!validateCredentials(username, password)) {
                throw new SecurityException("Invalid credentials for user: " + username);
            }
            return true;
        } catch (SecurityException e) {
            e.printStackTrace(); // VULNERABLE: Expone username en stack trace
            return false;
        }
    }
    
    // Métodos auxiliares
    private void performCriticalOperation() {
        // Simula operación crítica
    }
    
    private void riskyOperation1() throws Exception {
        throw new Exception("Risk 1");
    }
    
    private void riskyOperation2() throws Exception {
        throw new Exception("Risk 2");
    }
    
    private boolean validateCredentials(String username, String password) {
        return false;
    }
}
