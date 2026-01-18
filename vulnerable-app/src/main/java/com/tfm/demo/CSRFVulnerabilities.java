package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * VULNERABILIDAD: CSRF (Cross-Site Request Forgery)
 * 
 * OWASP Top 10 2021: A01:2021 - Broken Access Control
 * ISO/IEC 25010: Security - Integrity
 * 
 * Reglas SonarQube:
 * - S4502: CSRF protection should be enabled (BLOCKER)
 * - S5131: State-changing methods should be protected against CSRF (CRITICAL)
 * - S3330: HttpOnly cookies (relacionado)
 */
public class CSRFVulnerabilities {
    
    /**
     * Vulnerabilidad S4502: Operación crítica sin CSRF token
     * Permite que sitios externos ejecuten acciones en nombre del usuario
     */
    public void transferMoney(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        String toAccount = request.getParameter("to");
        String amount = request.getParameter("amount");
        
        // VULNERABLE: Sin validación de CSRF token
        executeTransfer(toAccount, Double.parseDouble(amount));
        
        response.getWriter().write("Transfer completed");
        // Ataque: <img src="http://bank.com/transfer?to=attacker&amount=1000">
    }
    
    /**
     * Vulnerabilidad: Cambio de contraseña sin CSRF protection
     */
    public void changePassword(HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        String newPassword = request.getParameter("password");
        
        // VULNERABLE: Sin CSRF token
        // Usuario autenticado pero sin validar origen de la petición
        String userId = (String) request.getSession().getAttribute("userId");
        updatePassword(userId, newPassword);
        
        response.getWriter().write("Password updated");
        // Ataque: Form auto-submit desde sitio malicioso
    }
    
    /**
     * Vulnerabilidad: Eliminación de cuenta sin protección
     */
    public void deleteAccount(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        // VULNERABLE: Acepta GET para operación destructiva
        String userId = (String) request.getSession().getAttribute("userId");
        
        if (request.getParameter("confirm") != null) {
            removeUserAccount(userId);
            response.getWriter().write("Account deleted");
        }
        // Ataque: <img src="http://app.com/delete?confirm=yes">
    }
    
    /**
     * Vulnerabilidad: Cambio de email sin CSRF
     */
    public void updateEmail(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        String newEmail = request.getParameter("email");
        String userId = (String) request.getSession().getAttribute("userId");
        
        // VULNERABLE: Sin CSRF validation
        updateUserEmail(userId, newEmail);
        
        response.getWriter().write("Email updated to: " + newEmail);
        // Ataque: Cambia email y luego resetea password
    }
    
    /**
     * Vulnerabilidad: Agregar administrador sin protección
     */
    public void addAdmin(HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        String newAdminId = request.getParameter("userId");
        
        // VULNERABLE: Operación privilegiada sin CSRF token
        grantAdminRole(newAdminId);
        
        response.getWriter().write("Admin role granted");
        // Ataque: Admin visita página maliciosa que ejecuta esto
    }
    
    /**
     * Vulnerabilidad: API endpoint sin CSRF protection
     */
    public void apiUpdateSettings(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        // VULNERABLE: API acepta requests sin validar origen
        String setting = request.getParameter("setting");
        String value = request.getParameter("value");
        
        updateSetting(setting, value);
        
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"success\"}");
        // Ataque: XHR desde sitio malicioso (si no hay CORS bien configurado)
    }
    
    /**
     * Vulnerabilidad: Usar referer como única protección
     */
    public void transferWithWeakProtection(HttpServletRequest request,
                                          HttpServletResponse response) throws IOException {
        String referer = request.getHeader("Referer");
        
        // VULNERABLE: Referer es bypasseable
        if (referer != null && referer.contains("myapp.com")) {
            String to = request.getParameter("to");
            String amount = request.getParameter("amount");
            executeTransfer(to, Double.parseDouble(amount));
        }
        // Bypass: Referer puede ser manipulado o nulo
    }
    
    /**
     * Vulnerabilidad: Form processing sin validación
     */
    public void processForm(HttpServletRequest request,
                           HttpServletResponse response) 
                           throws ServletException, IOException {
        // VULNERABLE: POST sin CSRF token
        if ("POST".equals(request.getMethod())) {
            String action = request.getParameter("action");
            
            switch (action) {
                case "delete":
                    deleteResource(request.getParameter("id"));
                    break;
                case "update":
                    updateResource(request.getParameter("id"), 
                                 request.getParameter("data"));
                    break;
            }
        }
    }
    
    // Métodos auxiliares
    private void executeTransfer(String to, double amount) {
        // Simula transferencia
    }
    
    private void updatePassword(String userId, String newPassword) {
        // Simula actualización
    }
    
    private void removeUserAccount(String userId) {
        // Simula eliminación
    }
    
    private void updateUserEmail(String userId, String email) {
        // Simula actualización
    }
    
    private void grantAdminRole(String userId) {
        // Simula grant de permisos
    }
    
    private void updateSetting(String setting, String value) {
        // Simula actualización de configuración
    }
    
    private void deleteResource(String id) {
        // Simula eliminación
    }
    
    private void updateResource(String id, String data) {
        // Simula actualización
    }
}
