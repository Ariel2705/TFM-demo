package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * VULNERABILIDAD: CORS Misconfiguration
 * 
 * OWASP Top 10 2021: A05:2021 - Security Misconfiguration
 * ISO/IEC 25010: Security - Confidentiality
 * 
 * Reglas SonarQube:
 * - S5122: CORS should be restricted (BLOCKER)
 * - S5131: Origins should be verified (CRITICAL)
 * - S2647: Authentication credentials should not be exposed (BLOCKER)
 */
public class CORSVulnerabilities {
    
    /**
     * Vulnerabilidad S5122: CORS permite cualquier origen
     * Permite robo de datos desde sitios maliciosos
     */
    public void allowAllOrigins(HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        // VULNERABLE: Acepta peticiones de cualquier origen
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        
        // Procesa request con credenciales
        String sensitiveData = getUserData(request);
        response.getWriter().write(sensitiveData);
        
        // Ataque: Sitio malicioso hace XHR y roba datos
    }
    
    /**
     * Vulnerabilidad: Refleja origen sin validar
     */
    public void reflectOrigin(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        String origin = request.getHeader("Origin");
        
        // VULNERABLE: Refleja cualquier origen
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        
        String apiData = getApiData();
        response.getWriter().write(apiData);
        
        // Ataque: evil.com hace request y recibe datos
    }
    
    /**
     * Vulnerabilidad: Validación débil de origen
     */
    public void weakOriginValidation(HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        String origin = request.getHeader("Origin");
        
        // VULNERABLE: Validación bypasseable
        if (origin != null && origin.contains("myapp.com")) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        
        response.getWriter().write(getSensitiveInfo());
        
        // Bypass: http://evil.com?fake=myapp.com
        // Bypass: http://myapp.com.evil.com
    }
    
    /**
     * Vulnerabilidad: CORS + credenciales expuestas
     */
    public void corsWithCredentials(HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        // VULNERABLE: Permite credenciales con wildcard (mal implementado)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, X-API-Key");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE");
        
        // Expone API key en header
        String apiKey = request.getHeader("X-API-Key");
        processWithApiKey(apiKey);
    }
    
    /**
     * Vulnerabilidad: Pre-flight sin validación
     */
    public void handlePreflight(HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        if ("OPTIONS".equals(request.getMethod())) {
            String origin = request.getHeader("Origin");
            
            // VULNERABLE: Pre-flight acepta cualquier origen
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "86400");
            
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
    
    /**
     * Vulnerabilidad: JSONP callback sin validar
     */
    public void jsonpEndpoint(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        String callback = request.getParameter("callback");
        String data = getUserSensitiveData(request);
        
        // VULNERABLE: JSONP sin validar callback
        response.setContentType("application/javascript");
        response.getWriter().write(callback + "(" + data + ");");
        
        // Ataque: ?callback=alert(document.cookie)
        // Ataque: ?callback=stealData
    }
    
    /**
     * Vulnerabilidad: CORS permite subdominios maliciosos
     */
    public void allowSubdomains(HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        String origin = request.getHeader("Origin");
        
        // VULNERABLE: Acepta todos los subdominios sin verificar
        if (origin != null && origin.endsWith(".myapp.com")) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        
        // Ataque: Atacante registra evil.myapp.com
    }
    
    /**
     * Vulnerabilidad: Filter CORS global inseguro
     */
    public static class InsecureCORSFilter implements Filter {
        @Override
        public void doFilter(javax.servlet.ServletRequest request,
                           javax.servlet.ServletResponse response,
                           FilterChain chain) throws IOException, ServletException {
            
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // VULNERABLE: Filter global acepta todo
            String origin = httpRequest.getHeader("Origin");
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Methods", "*");
            httpResponse.setHeader("Access-Control-Allow-Headers", "*");
            
            chain.doFilter(request, response);
        }
    }
    
    /**
     * Vulnerabilidad: API expone datos privados sin CORS adecuado
     */
    public void privateDataApi(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        // VULNERABLE: Sin configuración CORS = navegadores antiguos permiten
        response.setContentType("application/json");
        
        String userId = (String) request.getSession().getAttribute("userId");
        String privateData = "{\"ssn\":\"123-45-6789\",\"salary\":150000}";
        
        response.getWriter().write(privateData);
        
        // Ataque: Flash/Java applet puede leer sin CORS en navegadores viejos
    }
    
    /**
     * Vulnerabilidad: Whitelist de orígenes mal implementada
     */
    public void whitelistMisconfigured(HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        String origin = request.getHeader("Origin");
        
        // VULNERABLE: Lista permite HTTP (no HTTPS)
        String[] allowedOrigins = {
            "http://app.mycompany.com",  // HTTP vulnerable a MITM
            "http://admin.mycompany.com"
        };
        
        for (String allowed : allowedOrigins) {
            if (allowed.equals(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                break;
            }
        }
        
        // Ataque: MITM en HTTP intercepta y roba credenciales
    }
    
    // Métodos auxiliares
    private String getUserData(HttpServletRequest request) {
        return "{\"user\":\"john\",\"balance\":5000}";
    }
    
    private String getApiData() {
        return "{\"api_secret\":\"sk_live_123456\"}";
    }
    
    private String getSensitiveInfo() {
        return "{\"credit_card\":\"4111-1111-1111-1111\"}";
    }
    
    private void processWithApiKey(String apiKey) {
        // Procesa con API key
    }
    
    private String getUserSensitiveData(HttpServletRequest request) {
        return "{\"email\":\"user@example.com\"}";
    }
}
