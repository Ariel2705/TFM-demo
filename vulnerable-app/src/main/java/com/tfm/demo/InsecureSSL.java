package com.tfm.demo;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * A02:2021 & A05:2021 – Cryptographic Failures & Security Misconfiguration
 * Ejemplos de SSL/TLS inseguro
 */
public class InsecureSSL {

    // VULNERABILITY: Deshabilitar verificación de certificados SSL
    public void disableSSLVerification() throws Exception {
        // VULNERABLE: Trust all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // No verification
                }
                
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // No verification
                }
            }
        };
        
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        // VULNERABLE: Deshabilitar hostname verification
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true; // Accept all hostnames
            }
        };
        
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
