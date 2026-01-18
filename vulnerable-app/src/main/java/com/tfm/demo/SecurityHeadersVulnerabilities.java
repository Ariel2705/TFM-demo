package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * VULNERABILIDAD: Missing Security Headers
 * 
 * OWASP Top 10 2021: A05:2021 - Security Misconfiguration
 * ISO/IEC 25010: Security - Integrity
 * 
 * Reglas SonarQube:
 * - S5122: Security headers should be set (CRITICAL)
 * - S5131: Clickjacking protection (BLOCKER)
 * - S5146: Security policies should be enforced (CRITICAL)
 */
public class SecurityHeadersVulnerabilities {
    
    /**
     * Vulnerabilidad: Sin X-Frame-Options (Clickjacking)
     * Permite ataques de clickjacking
     */
    public void missingXFrameOptions(HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        // VULNERABLE: Sin X-Frame-Options header
        // Página puede ser embebida en iframe malicioso
        
        response.setContentType("text/html");
        response.getWriter().write(
            "<html><body>" +
            "<h1>Login</h1>" +
            "<form action='/login' method='POST'>" +
            "  <input type='text' name='username'/>" +
            "  <input type='password' name='password'/>" +
            "  <input type='submit' value='Login'/>" +
            "</form>" +
            "</body></html>"
        );
        
        /* Ataque:
        <iframe src="http://bank.com/transfer" style="opacity:0"></iframe>
        <button style="position:absolute">Click to win!</button>
        */
    }
    
    /**
     * Vulnerabilidad: Sin Content-Security-Policy
     */
    public void missingCSP(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        // VULNERABLE: Sin CSP = permite inline scripts y cualquier origen
        
        String userInput = request.getParameter("message");
        
        response.setContentType("text/html");
        response.getWriter().write(
            "<html><body>" +
            "<div>" + userInput + "</div>" +
            "</body></html>"
        );
        
        // Ataque XSS: ?message=<script>alert(document.cookie)</script>
    }
    
    /**
     * Vulnerabilidad: Sin X-Content-Type-Options
     */
    public void missingXContentTypeOptions(HttpServletRequest request,
                                          HttpServletResponse response) throws IOException {
        // VULNERABLE: Navegador puede interpretar mal el tipo MIME
        
        String fileContent = readUserFile(request.getParameter("file"));
        
        response.setContentType("text/plain");
        // Falta: response.setHeader("X-Content-Type-Options", "nosniff");
        
        response.getWriter().write(fileContent);
        
        // Ataque: Navegador ejecuta HTML como script si contiene código
    }
    
    /**
     * Vulnerabilidad: Sin Strict-Transport-Security (HSTS)
     */
    public void missingHSTS(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        // VULNERABLE: Sin HSTS permite downgrade a HTTP
        
        // Falta: response.setHeader("Strict-Transport-Security", 
        //                          "max-age=31536000; includeSubDomains");
        
        String sensitiveData = getUserAccountInfo();
        response.getWriter().write(sensitiveData);
        
        // Ataque: MITM downgrade HTTPS → HTTP y roba datos
    }
    
    /**
     * Vulnerabilidad: Sin X-XSS-Protection
     */
    public void missingXSSProtection(HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        // VULNERABLE: Sin X-XSS-Protection en navegadores viejos
        
        String search = request.getParameter("q");
        
        response.setContentType("text/html");
        response.getWriter().write(
            "<html><body>" +
            "<h1>Search results for: " + search + "</h1>" +
            "</body></html>"
        );
        
        // Reflected XSS: ?q=<script>alert(1)</script>
    }
    
    /**
     * Vulnerabilidad: Referrer-Policy débil
     */
    public void weakReferrerPolicy(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        // VULNERABLE: Referrer expone URLs con tokens
        
        // Falta configurar: Referrer-Policy: no-referrer
        // Default: unsafe-url (expone full URL)
        
        String resetToken = request.getParameter("token");
        
        response.getWriter().write(
            "<html><body>" +
            "<a href='https://external-analytics.com'>Learn more</a>" +
            "</body></html>"
        );
        
        // Leak: Referer header expone /reset?token=secret123
    }
    
    /**
     * Vulnerabilidad: Permissions-Policy sin configurar
     */
    public void missingPermissionsPolicy(HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        // VULNERABLE: Sin Permissions-Policy (antes Feature-Policy)
        
        // Permite acceso a camera, microphone, geolocation por defecto
        
        response.setContentType("text/html");
        response.getWriter().write(
            "<html><body>" +
            "<h1>Welcome</h1>" +
            "<script src='https://cdn.example.com/widget.js'></script>" +
            "</body></html>"
        );
        
        // Ataque: Script externo accede a camera/microphone
    }
    
    /**
     * Vulnerabilidad: Cache-Control inseguro para datos sensibles
     */
    public void insecureCacheControl(HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        // VULNERABLE: Datos sensibles pueden ser cacheados
        
        String userId = (String) request.getSession().getAttribute("userId");
        String accountInfo = getAccountDetails(userId);
        
        // Falta: response.setHeader("Cache-Control", "no-store, private");
        // Default: puede cachear en proxy/navegador
        
        response.setContentType("text/html");
        response.getWriter().write(accountInfo);
        
        // Riesgo: Siguiente usuario en PC compartida ve datos cacheados
    }
    
    /**
     * Vulnerabilidad: CSP permisivo con 'unsafe-inline'
     */
    public void weakCSP(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        // VULNERABLE: CSP permite inline scripts
        response.setHeader("Content-Security-Policy", 
            "default-src 'self' 'unsafe-inline' 'unsafe-eval' *");
        
        String userContent = request.getParameter("content");
        
        response.setContentType("text/html");
        response.getWriter().write(
            "<html><body><div>" + userContent + "</div></body></html>"
        );
        
        // unsafe-inline permite XSS
    }
    
    /**
     * Vulnerabilidad: Múltiples headers de seguridad faltantes
     */
    public void completelyInsecureHeaders(HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        // VULNERABLE: Sin ningún header de seguridad
        
        String userInput = request.getParameter("data");
        
        // Faltan TODOS los headers de seguridad:
        // - X-Frame-Options
        // - Content-Security-Policy
        // - X-Content-Type-Options
        // - Strict-Transport-Security
        // - X-XSS-Protection
        // - Referrer-Policy
        // - Permissions-Policy
        
        response.setContentType("text/html");
        response.getWriter().write(
            "<html>" +
            "<head><title>Vulnerable Page</title></head>" +
            "<body>" +
            "<script>var data = '" + userInput + "';</script>" +
            "<h1>Data: " + userInput + "</h1>" +
            "</body>" +
            "</html>"
        );
        
        // Vulnerable a: XSS, Clickjacking, MITM, MIME confusion, etc.
    }
    
    // Métodos auxiliares
    private String readUserFile(String filename) {
        return "<html><script>alert('xss')</script></html>";
    }
    
    private String getUserAccountInfo() {
        return "Account balance: $10,000";
    }
    
    private String getAccountDetails(String userId) {
        return "<html><body>SSN: 123-45-6789</body></html>";
    }
}
