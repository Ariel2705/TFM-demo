package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;

/**
 * VULNERABILIDAD: Open Redirect & SSRF
 * 
 * OWASP Top 10 2021: A01:2021 - Broken Access Control
 * ISO/IEC 25010: Security - Confidentiality
 * 
 * Reglas SonarQube:
 * - S5146: Open redirects should be avoided (BLOCKER)
 * - S5145: Unvalidated redirects (CRITICAL)
 * - S5131: Endpoints should not forward requests to untrusted servers (BLOCKER)
 */
public class OpenRedirectVulnerabilities {
    
    /**
     * Vulnerabilidad S5146: Open Redirect sin validación
     * Permite phishing dirigiendo a sitios maliciosos
     */
    public void redirectToUserInput(HttpServletRequest request, 
                                    HttpServletResponse response) throws IOException {
        String redirectUrl = request.getParameter("url");
        
        // VULNERABLE: Redirect sin validar dominio
        response.sendRedirect(redirectUrl);
        // Ataque: ?url=http://malicious.com/fake-login
    }
    
    /**
     * Vulnerabilidad: Redirect con concatenación de URL
     */
    public void redirectWithConcatenation(HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        String page = request.getParameter("page");
        
        // VULNERABLE: Concatenación permite bypass
        response.sendRedirect("/app/" + page);
        // Ataque: ?page=../../../evil.com
    }
    
    /**
     * Vulnerabilidad: Forward sin validación (SSRF)
     */
    public void forwardToExternalURL(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        String targetUrl = request.getParameter("target");
        
        // VULNERABLE: SSRF - puede acceder recursos internos
        URL url = new URL(targetUrl);
        response.getWriter().write(url.openStream().toString());
        // Ataque: ?target=http://localhost:8080/admin
        // Ataque: ?target=http://169.254.169.254/latest/meta-data (AWS metadata)
    }
    
    /**
     * Vulnerabilidad: Redirect con validación insuficiente
     */
    public void weakRedirectValidation(HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        String url = request.getParameter("returnUrl");
        
        // VULNERABLE: Validación débil, bypasseable
        if (url.contains("myapp.com")) {
            response.sendRedirect(url);
        }
        // Ataque: ?returnUrl=http://evil.com?fake=myapp.com
    }
    
    /**
     * Vulnerabilidad: Session fixation con redirect
     */
    public void loginWithRedirect(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String username, String password) throws IOException {
        if (authenticate(username, password)) {
            // VULNERABLE: No regenera session ID
            HttpSession session = request.getSession();
            session.setAttribute("user", username);
            
            String redirect = request.getParameter("redirect");
            response.sendRedirect(redirect); // Y redirect sin validar
        }
    }
    
    /**
     * Vulnerabilidad: JavaScript redirect injection
     */
    public void javascriptRedirect(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        String target = request.getParameter("goto");
        
        // VULNERABLE: Inyección en JavaScript redirect
        response.getWriter().write(
            "<script>window.location.href='" + target + "';</script>"
        );
        // Ataque: ?goto=javascript:alert(document.cookie)
    }
    
    /**
     * Vulnerabilidad: Meta refresh redirect
     */
    public void metaRefreshRedirect(HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        String url = request.getParameter("next");
        
        // VULNERABLE: Meta refresh sin validar
        response.getWriter().write(
            "<meta http-equiv='refresh' content='0;url=" + url + "'>"
        );
    }
    
    /**
     * Vulnerabilidad: Header injection vía redirect
     */
    public void headerInjectionRedirect(HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        String url = request.getParameter("url");
        
        // VULNERABLE: Permite inyectar headers adicionales
        response.setHeader("Location", url);
        response.setStatus(302);
        // Ataque: ?url=http://evil.com%0d%0aSet-Cookie:session=hacked
    }
    
    // Método auxiliar
    private boolean authenticate(String username, String password) {
        return username != null && password != null;
    }
}
