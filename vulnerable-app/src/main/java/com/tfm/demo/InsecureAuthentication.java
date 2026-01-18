package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.util.Base64;

/**
 * VULNERABILIDAD: Insecure Authentication Mechanisms
 * 
 * OWASP Top 10 2021: A07:2021 - Identification and Authentication Failures
 * ISO/IEC 25010: Security - Authenticity
 * 
 * Reglas SonarQube:
 * - S2647: Basic authentication should not be used (BLOCKER)
 * - S3330: HttpOnly cookies should be used (CRITICAL)
 * - S2092: Cookies should be "secure" (CRITICAL)
 * - S5122: CORS should be configured properly (BLOCKER)
 */
public class InsecureAuthentication {
    
    /**
     * Vulnerabilidad S2647: Basic Authentication sin HTTPS
     * Credenciales viajan en Base64 (fácilmente decodificable)
     */
    public boolean authenticateWithBasicAuth(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // VULNERABLE: Basic auth sin verificar HTTPS
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];
            
            return validateUser(username, password);
        }
        return false;
    }
    
    /**
     * Vulnerabilidad S3330: Cookie sin HttpOnly flag
     * Vulnerable a XSS que puede robar la cookie via JavaScript
     */
    public void createSessionCookieWithoutHttpOnly(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie("SESSIONID", sessionId);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        // VULNERABLE: No tiene setHttpOnly(true)
        // JavaScript puede acceder: document.cookie
        response.addCookie(cookie);
    }
    
    /**
     * Vulnerabilidad S2092: Cookie sin Secure flag
     * Cookie puede ser transmitida sobre HTTP no cifrado
     */
    public void createInsecureCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setMaxAge(3600);
        // VULNERABLE: No tiene setSecure(true)
        // Cookie se envía también en conexiones HTTP (sin cifrar)
        response.addCookie(cookie);
    }
    
    /**
     * Vulnerabilidad: Cookie de autenticación completamente insegura
     */
    public void createCompletelyInsecureCookie(HttpServletResponse response, 
                                               String username, String password) {
        // VULNERABLE: Múltiples problemas
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        
        Cookie cookie = new Cookie("USER_CREDS", encoded);
        cookie.setMaxAge(86400); // 24 horas
        // Sin HttpOnly - vulnerable a XSS
        // Sin Secure - se envía por HTTP
        // Sin SameSite - vulnerable a CSRF
        response.addCookie(cookie);
    }
    
    /**
     * Vulnerabilidad: Basic Auth header hardcoded
     */
    public String createBasicAuthHeader(String username, String password) {
        // VULNERABLE: Construye header Basic Auth manualmente
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded; // Se transmite sin cifrado si no es HTTPS
    }
    
    /**
     * Vulnerabilidad: Session fixation
     */
    public void login(HttpServletRequest request, HttpServletResponse response, 
                     String username, String password) {
        if (validateUser(username, password)) {
            // VULNERABLE: No regenera session ID después del login
            // Permite ataques de session fixation
            Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
            response.addCookie(cookie);
        }
    }
    
    /**
     * Vulnerabilidad: Token en cookie sin protección
     */
    public void storeJWTInInsecureCookie(HttpServletResponse response, String jwtToken) {
        Cookie cookie = new Cookie("JWT_TOKEN", jwtToken);
        cookie.setMaxAge(7200);
        cookie.setPath("/");
        // VULNERABLE: JWT en cookie sin HttpOnly ni Secure
        response.addCookie(cookie);
    }
    
    // Método auxiliar
    private boolean validateUser(String username, String password) {
        // Simulación de validación
        return username != null && password != null;
    }
}
