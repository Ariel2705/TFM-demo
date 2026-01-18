package com.tfm.demo;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * VULNERABILIDAD: Debug Mode in Production
 * 
 * OWASP Top 10 2021: A05:2021 - Security Misconfiguration
 * ISO/IEC 25010: Security - Confidentiality
 * 
 * Regla SonarQube:
 * - S4507: Delivering code with debug features activated is security-sensitive (MAJOR)
 * 
 * Debug mode en producción expone:
 * - Stack traces completos
 * - Variables internas
 * - Rutas del sistema
 * - Configuración sensible
 */
public class DebugModeProduction {
    
    // VULNERABLE: Flag de debug público y mutable
    public static boolean DEBUG_MODE = true;
    
    /**
     * Vulnerabilidad S4507: Debug mode habilitado
     * Expone información sensible en producción
     */
    public void processRequest(HttpServletRequest request, 
                               HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        
        if (DEBUG_MODE) {
            // VULNERABLE: Expone información interna en producción
            System.out.println("DEBUG: Processing request for user: " + userId);
            System.out.println("DEBUG: Session ID: " + request.getSession().getId());
            System.out.println("DEBUG: Client IP: " + request.getRemoteAddr());
            System.out.println("DEBUG: Request headers: " + request.getHeaderNames());
        }
        
        try {
            // Procesar request
            processUser(userId);
        } catch (Exception e) {
            if (DEBUG_MODE) {
                // VULNERABLE: Stack trace completo en respuesta
                e.printStackTrace(response.getWriter());
                
                // Expone rutas internas, librerías, código fuente
            }
        }
    }
    
    /**
     * Vulnerabilidad: Logging verbose en producción
     */
    public void debugLogging(String action, Object data) {
        // VULNERABLE: Log level DEBUG en producción
        if (DEBUG_MODE) {
            System.out.println("[DEBUG] Action: " + action);
            System.out.println("[DEBUG] Data: " + data.toString());
            System.out.println("[DEBUG] Stack trace:");
            Thread.currentThread().getStackTrace();
            
            // Logs pueden contener passwords, tokens, PII
        }
    }
    
    /**
     * Vulnerabilidad: Debug endpoint accesible
     */
    public void debugEndpoint(HttpServletRequest request, 
                             HttpServletResponse response) throws Exception {
        // VULNERABLE: Endpoint /debug sin autenticación
        if (request.getRequestURI().contains("/debug")) {
            response.setContentType("text/html");
            
            // Expone configuración completa
            response.getWriter().println("<h1>Debug Information</h1>");
            response.getWriter().println("<h2>System Properties</h2>");
            response.getWriter().println("<pre>" + System.getProperties() + "</pre>");
            
            response.getWriter().println("<h2>Environment Variables</h2>");
            response.getWriter().println("<pre>" + System.getenv() + "</pre>");
            
            response.getWriter().println("<h2>Active Sessions</h2>");
            // Listar todas las sesiones activas
            
            // Ataque: Acceso directo a /debug expone todo el sistema
        }
    }
    
    /**
     * Vulnerabilidad: Assertions habilitadas en producción
     */
    public void processPayment(double amount, String userId) {
        // VULNERABLE: Assertions en producción
        assert amount > 0 : "Amount must be positive, user: " + userId;
        assert userId != null : "User ID is null";
        
        // Si assertions están habilitadas, lanza AssertionError
        // exponiendo datos internos
        
        executePayment(amount, userId);
    }
    
    /**
     * Vulnerabilidad: Test mode flag en producción
     */
    private boolean isTestMode() {
        // VULNERABLE: Flag de test puede ser true en producción
        String testMode = System.getProperty("test.mode");
        return testMode != null && testMode.equals("true");
    }
    
    public void sensitiveOperation() {
        if (isTestMode()) {
            System.out.println("TEST MODE: Skipping validation");
            System.out.println("TEST MODE: Using test database");
            System.out.println("TEST MODE: Bypassing security checks");
        }
        
        // Riesgo: test.mode=true en producción bypasea seguridad
    }
    
    /**
     * Vulnerabilidad: Verbose error messages
     */
    public void loginUser(String username, String password) throws Exception {
        if (DEBUG_MODE) {
            System.out.println("DEBUG: Attempting login for: " + username);
            System.out.println("DEBUG: Password hash: " + hashPassword(password));
        }
        
        boolean authenticated = authenticate(username, password);
        
        if (!authenticated) {
            if (DEBUG_MODE) {
                // VULNERABLE: Detalle excesivo en errores
                throw new Exception(
                    "Authentication failed for user '" + username + "' " +
                    "with password '" + password + "' " +
                    "at " + new java.util.Date() +
                    " from IP " + getCurrentIP()
                );
            }
        }
        
        // Ataque: Error messages revelan si usuario existe
    }
    
    /**
     * Vulnerabilidad: Debug config file en producción
     */
    public void loadConfiguration() throws Exception {
        Properties config = new Properties();
        
        // VULNERABLE: Carga config de debug en producción
        String configFile = DEBUG_MODE ? 
            "/config/debug-config.properties" : 
            "/config/production-config.properties";
        
        config.load(new FileInputStream(configFile));
        
        if (DEBUG_MODE) {
            // Imprime TODA la configuración incluyendo secrets
            System.out.println("DEBUG: Configuration loaded:");
            config.list(System.out);
        }
        
        // Ataque: Debug config puede tener credenciales débiles
    }
    
    /**
     * Vulnerabilidad: Console access en producción
     */
    public void enableDebugConsole(ServletContext context) {
        if (DEBUG_MODE) {
            // VULNERABLE: Habilita consola remota de debugging
            context.setAttribute("debugConsole", "enabled");
            
            // Permite ejecución arbitraria de código vía consola
            System.out.println("Debug console enabled at /console");
        }
    }
    
    /**
     * Vulnerabilidad: Source code exposure vía debug
     */
    public void showSourceCode(String className, HttpServletResponse response) 
            throws Exception {
        if (DEBUG_MODE) {
            // VULNERABLE: Expone código fuente
            String sourcePath = "/src/main/java/" + 
                className.replace(".", "/") + ".java";
            
            response.setContentType("text/plain");
            java.nio.file.Files.copy(
                java.nio.file.Paths.get(sourcePath),
                response.getOutputStream()
            );
            
            // Ataque: Ver lógica de negocio y encontrar vulnerabilidades
        }
    }
    
    /**
     * Vulnerabilidad: Performance metrics exposing internal data
     */
    public void logPerformanceMetrics(String operation, long duration) {
        if (DEBUG_MODE) {
            // VULNERABLE: Métricas revelan comportamiento interno
            System.out.println("PERF: " + operation + " took " + duration + "ms");
            System.out.println("PERF: Memory: " + 
                Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
            System.out.println("PERF: Active threads: " + 
                Thread.activeCount());
            System.out.println("PERF: DB connections: " + 
                getActiveConnections());
            
            // Ataque: Timing attacks, resource exhaustion info
        }
    }
    
    /**
     * Vulnerabilidad: Swagger/OpenAPI enabled in production
     */
    public boolean isSwaggerEnabled() {
        // VULNERABLE: API documentation en producción
        return DEBUG_MODE; // Swagger habilitado si DEBUG_MODE=true
        
        // Ataque: /swagger-ui.html expone TODOS los endpoints
        // incluyendo los no documentados públicamente
    }
    
    // Métodos auxiliares
    private void processUser(String userId) throws Exception {
        // Simula procesamiento
        if (userId == null) {
            throw new NullPointerException("User ID is null");
        }
    }
    
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
    
    private boolean authenticate(String username, String password) {
        return username != null && password != null;
    }
    
    private String getCurrentIP() {
        return "192.168.1.100";
    }
    
    private void executePayment(double amount, String userId) {
        // Simula pago
    }
    
    private int getActiveConnections() {
        return 42;
    }
}
