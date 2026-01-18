package com.tfm.demo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * VULNERABILIDAD: Clear-Text Protocols (HTTP, FTP, Telnet)
 * 
 * OWASP Top 10 2021: A02:2021 - Cryptographic Failures
 * ISO/IEC 25010: Security - Confidentiality
 * 
 * Regla SonarQube:
 * - S5332: Using clear-text protocols is security-sensitive (BLOCKER)
 * 
 * Esta regla NO está en "Sonar way" pero SÍ en nuestro profile custom.
 * Demuestra que el custom detecta MÁS vulnerabilidades críticas.
 */
public class ClearTextProtocols {
    
    /**
     * Vulnerabilidad S5332: Conexión HTTP sin cifrar
     * Permite Man-in-the-Middle attacks
     */
    public String fetchDataOverHTTP(String endpoint) throws Exception {
        // VULNERABLE: HTTP transmite datos en texto plano
        URL url = new URL("http://api.example.com" + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        // Datos sensibles viajan sin cifrar
        conn.setRequestProperty("Authorization", "Bearer secret-token-12345");
        
        InputStream is = conn.getInputStream();
        byte[] buffer = new byte[1024];
        is.read(buffer);
        
        return new String(buffer);
        // Ataque: Wireshark captura "Bearer secret-token-12345"
    }
    
    /**
     * Vulnerabilidad: POST con credenciales sobre HTTP
     */
    public void loginOverHTTP(String username, String password) throws Exception {
        // VULNERABLE: Credenciales en texto plano
        URL url = new URL("http://myapp.com/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        
        String credentials = "user=" + username + "&pass=" + password;
        
        OutputStream os = conn.getOutputStream();
        os.write(credentials.getBytes());
        os.flush();
        
        // Ataque: Red WiFi pública captura username y password
    }
    
    /**
     * Vulnerabilidad: Download de archivo sensible por HTTP
     */
    public void downloadFileHTTP(String fileId) throws Exception {
        // VULNERABLE: HTTP para archivos confidenciales
        String downloadUrl = "http://files.company.com/confidential/" + fileId;
        
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        // Session cookie viaja sin cifrar
        conn.setRequestProperty("Cookie", "JSESSIONID=ABC123DEF456");
        
        InputStream in = conn.getInputStream();
        // Descarga archivo sin cifrar
        // Ataque: MITM intercepta archivo confidencial
    }
    
    /**
     * Vulnerabilidad: API call con API key sobre HTTP
     */
    public String callAPIWithKey(String resource) throws Exception {
        // VULNERABLE: API key expuesta en HTTP
        String apiKey = "sk_live_51H123456789abcdef";
        
        URL url = new URL("http://api.payments.com/" + resource);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestProperty("X-API-Key", apiKey);
        
        InputStream is = conn.getInputStream();
        byte[] data = new byte[4096];
        is.read(data);
        
        return new String(data);
        // Ataque: ISP o proxy captura API key
    }
    
    /**
     * Vulnerabilidad: Webhook callback sobre HTTP
     */
    public void registerWebhook(String callbackUrl) throws Exception {
        // VULNERABLE: Webhook URL sin HTTPS
        // callbackUrl = "http://myserver.com/webhook"
        
        URL url = new URL("http://service.com/register-webhook");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        
        String payload = "{\"callback\":\"" + callbackUrl + "\"}";
        
        OutputStream os = conn.getOutputStream();
        os.write(payload.getBytes());
        
        // Ataque: MITM cambia callback URL a servidor malicioso
    }
    
    /**
     * Vulnerabilidad: FTP connection para transferir datos
     */
    public void uploadViaFTP(String filename, byte[] data) throws Exception {
        // VULNERABLE: FTP transmite en claro (usuario, password, datos)
        URL ftpUrl = new URL("ftp://user:password@ftp.company.com/" + filename);
        
        java.net.URLConnection conn = ftpUrl.openConnection();
        OutputStream os = conn.getOutputStream();
        os.write(data);
        os.flush();
        
        // Ataque: Sniffer captura credenciales FTP y contenido del archivo
    }
    
    /**
     * Vulnerabilidad: Redirect a HTTP después de login
     */
    public String postLoginRedirect() {
        // VULNERABLE: Post-authentication redirect a HTTP
        // Usuario se autentica en HTTPS, luego redirigido a HTTP
        
        String sessionToken = generateSessionToken();
        
        // Cookie de sesión será enviada por HTTP
        return "http://dashboard.myapp.com?token=" + sessionToken;
        
        // Ataque: Session fixation/hijacking vía MITM
    }
    
    /**
     * Vulnerabilidad: WebSocket sin TLS
     */
    public void connectWebSocket(String wsUrl) {
        // VULNERABLE: ws:// en lugar de wss://
        // wsUrl = "ws://chat.example.com/socket"
        
        // WebSocket sin cifrar permite intercepción de mensajes
        // Ataque: MITM lee mensajes en tiempo real
    }
    
    /**
     * Vulnerabilidad: Email con link HTTP a reset password
     */
    public String generatePasswordResetLink(String userId, String token) {
        // VULNERABLE: Link de reset password sobre HTTP
        String resetLink = "http://myapp.com/reset-password?user=" + userId + "&token=" + token;
        
        // Email contiene link HTTP
        sendEmail(userId, "Reset your password: " + resetLink);
        
        return resetLink;
        // Ataque: MITM intercepta token de reset, cambia password
    }
    
    /**
     * Vulnerabilidad: Configuración mezclada HTTP/HTTPS
     */
    public void mixedContentRequest(boolean useSecure) throws Exception {
        // VULNERABLE: Lógica condicional permite HTTP
        String protocol = useSecure ? "https" : "http";
        
        URL url = new URL(protocol + "://api.company.com/data");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        // Si useSecure=false, datos viajan sin cifrar
        conn.setRequestProperty("Authorization", "Bearer token123");
        
        // Ataque: Forzar useSecure=false para downgrade attack
    }
    
    // Métodos auxiliares
    private String generateSessionToken() {
        return "SESSION_" + System.currentTimeMillis();
    }
    
    private void sendEmail(String userId, String message) {
        // Simula envío de email
    }
}
