package com.tfm.demo;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

/**
 * VULNERABILIDAD: SSL/TLS Hostname Verification Bypass
 * 
 * OWASP Top 10 2021: A02:2021 - Cryptographic Failures  
 * A07:2021 - Identification and Authentication Failures
 * ISO/IEC 25010: Security - Authenticity
 * 
 * Regla SonarQube:
 * - S5527: Server hostnames should be verified during SSL/TLS connections (CRITICAL)
 * 
 * Sin verificar hostname, atacante con certificado válido para
 * otro dominio puede hacer MITM attack.
 */
public class SSLHostnameVerification {
    
    /**
     * Vulnerabilidad S5527: Deshabilitar hostname verification
     * Permite Man-in-the-Middle con certificado válido de otro dominio
     */
    public void disableHostnameVerification() throws Exception {
        // VULNERABLE: Acepta cualquier hostname
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true; // INSECURE! Acepta cualquier hostname
            }
        };
        
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        
        // Ahora todas las conexiones HTTPS ignoran hostname mismatch
        URL url = new URL("https://api.example.com/data");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        
        // Ataque: Atacante con cert válido para evil.com intercepta
        // conexión a api.example.com
    }
    
    /**
     * Vulnerabilidad: Trust all certificates + skip hostname
     */
    public void trustAllCertificates() throws Exception {
        // VULNERABLE: Confía en TODOS los certificados
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // No valida
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // No valida - INSECURE!
                }
            }
        };
        
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        
        // VULNERABLE: También deshabilita hostname verification
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(
            (hostname, session) -> true
        );
        
        // Ataque: Certificado auto-firmado del atacante es aceptado
    }
    
    /**
     * Vulnerabilidad: Usar ALLOW_ALL_HOSTNAME_VERIFIER
     */
    public void useAllowAllVerifier() throws Exception {
        // VULNERABLE: Constante deprecated pero aún usada
        URL url = new URL("https://secure.bank.com/api");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        
        // Acepta cualquier hostname
        conn.setHostnameVerifier((hostname, session) -> true);
        
        conn.connect();
        
        // Ataque: evil.com con cert válido intercepta secure.bank.com
    }
    
    /**
     * Vulnerabilidad: Verificación personalizada incorrecta
     */
    public void customWeakVerifier() throws Exception {
        // VULNERABLE: Verificación que solo chequea substring
        HostnameVerifier weakVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                // Solo verifica que contenga "mycompany"
                return hostname.contains("mycompany.com");
            }
        };
        
        HttpsURLConnection.setDefaultHostnameVerifier(weakVerifier);
        
        // Ataque: evil-mycompany.com pasa la verificación
    }
    
    /**
     * Vulnerabilidad: Deshabilitar solo para desarrollo (pero queda en prod)
     */
    public void developmentModeInsecure() throws Exception {
        // VULNERABLE: Flag "isDevelopment" puede ser true en producción
        boolean isDevelopment = System.getProperty("env") != null;
        
        if (isDevelopment) {
            // Deshabilita verificación en "dev"
            HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, session) -> true
            );
        }
        
        URL url = new URL("https://api.prod.company.com/data");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        
        // Riesgo: Variable env puede filtrarse a producción
    }
    
    /**
     * Vulnerabilidad: Ignorar errores de verificación
     */
    public void ignoreVerificationErrors() {
        try {
            URL url = new URL("https://untrusted.example.com");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.connect();
        } catch (SSLException e) {
            // VULNERABLE: Catch SSL errors y continuar sin verificar
            System.out.println("SSL error ignorado, continuando...");
            
            // Retry sin verificación
            try {
                HttpsURLConnection.setDefaultHostnameVerifier(
                    (hostname, session) -> true
                );
                URL url = new URL("https://untrusted.example.com");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.connect();
            } catch (Exception ex) {
                // Ignore
            }
        } catch (Exception e) {
            // Ignore
        }
    }
    
    /**
     * Vulnerabilidad: SSLSocketFactory custom sin hostname check
     */
    public void customSSLSocketFactory() throws Exception {
        // VULNERABLE: Factory custom que no verifica hostname
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
        
        SSLSocketFactory factory = sslContext.getSocketFactory();
        
        URL url = new URL("https://api.company.com");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(factory);
        
        // No establece HostnameVerifier - usa default que podría ser inseguro
        conn.connect();
    }
    
    /**
     * Vulnerabilidad: Verificación solo para subdominios
     */
    public void subdomainOnlyVerifier() throws Exception {
        // VULNERABLE: Acepta cualquier subdominio
        HostnameVerifier subdomainVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                // Acepta *.mycompany.com
                return hostname.endsWith(".mycompany.com") || 
                       hostname.equals("mycompany.com");
            }
        };
        
        HttpsURLConnection.setDefaultHostnameVerifier(subdomainVerifier);
        
        // Ataque: Registrar evil.mycompany.com o subdominio typosquatting
    }
    
    /**
     * Vulnerabilidad: Bypass temporal que se vuelve permanente
     */
    public void temporaryBypassBecomesPermament() throws Exception {
        // VULNERABLE: "Temporal" bypass sin revertir
        HostnameVerifier originalVerifier = 
            HttpsURLConnection.getDefaultHostnameVerifier();
        
        // Deshabilita para una operación
        HttpsURLConnection.setDefaultHostnameVerifier(
            (hostname, session) -> true
        );
        
        // Operación que requería bypass
        performSensitiveOperation();
        
        // OLVIDA restaurar el verifier original
        // HttpsURLConnection.setDefaultHostnameVerifier(originalVerifier);
        
        // Todas las subsecuentes conexiones quedan inseguras
    }
    
    /**
     * Vulnerabilidad: Test code con verificación deshabilitada
     */
    public void testCodeWithDisabledVerification() throws Exception {
        // VULNERABLE: Código de test que deshabilita seguridad
        // @Test
        // public void testSSLConnection() {
        
        HttpsURLConnection.setDefaultHostnameVerifier(
            (hostname, session) -> true // Para facilitar testing
        );
        
        URL url = new URL("https://test-server.local");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.connect();
        
        // }
        
        // Riesgo: Código de test se ejecuta en producción
    }
    
    // Método auxiliar
    private void performSensitiveOperation() throws Exception {
        URL url = new URL("https://internal-dev-server.local");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.connect();
    }
}
