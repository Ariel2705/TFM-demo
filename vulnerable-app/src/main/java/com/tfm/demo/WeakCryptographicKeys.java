package com.tfm.demo;

import javax.crypto.KeyGenerator;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import javax.crypto.SecretKey;

/**
 * VULNERABILIDAD: Weak Cryptographic Keys
 * 
 * OWASP Top 10 2021: A02:2021 - Cryptographic Failures
 * ISO/IEC 25010: Security - Confidentiality
 * 
 * Regla SonarQube:
 * - S4426: Cryptographic keys should be robust (CRITICAL)
 * 
 * Esta regla detecta llaves criptográficas débiles:
 * - RSA < 2048 bits
 * - AES < 128 bits
 * - DSA < 2048 bits
 */
public class WeakCryptographicKeys {
    
    /**
     * Vulnerabilidad S4426: RSA key de 1024 bits (débil)
     * Vulnerable a ataques de factorización
     */
    public KeyPair generateWeakRSAKey() throws Exception {
        // VULNERABLE: 1024 bits es insuficiente (recomendado: 2048+)
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024); // WEAK!
        
        KeyPair keyPair = keyGen.generateKeyPair();
        
        // Llaves débiles pueden ser rotas con recursos computacionales modernos
        return keyPair;
    }
    
    /**
     * Vulnerabilidad: RSA key de 512 bits (muy débil)
     */
    public KeyPair generateVeryWeakRSAKey() throws Exception {
        // VULNERABLE: 512 bits puede romperse en horas/días
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512); // EXTREMELY WEAK!
        
        return keyGen.generateKeyPair();
        
        // Ataque: RSA-512 fue roto públicamente en 1999
    }
    
    /**
     * Vulnerabilidad: AES key de 56 bits (DES equivalente)
     */
    public SecretKey generateWeakAESKey() throws Exception {
        // VULNERABLE: 56 bits es trivialmente débil
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(56); // WEAK! (Mínimo recomendado: 128 bits)
        
        SecretKey key = keyGen.generateKey();
        
        // Ataque: Brute force en minutos con hardware moderno
        return key;
    }
    
    /**
     * Vulnerabilidad: DSA key de 1024 bits
     */
    public KeyPair generateWeakDSAKey() throws Exception {
        // VULNERABLE: DSA 1024 bits ya no es seguro
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024); // WEAK! (Recomendado: 2048+)
        
        return keyGen.generateKeyPair();
        
        // Ataque: Vulnerable a discrete logarithm attacks
    }
    
    /**
     * Vulnerabilidad: Diffie-Hellman key exchange débil
     */
    public KeyPair generateWeakDHKey() throws Exception {
        // VULNERABLE: DH con 1024 bits
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
        keyGen.initialize(1024); // WEAK!
        
        return keyGen.generateKeyPair();
        
        // Ataque: Logjam attack (512-1024 bits DH)
    }
    
    /**
     * Vulnerabilidad: Key generator sin especificar tamaño
     */
    public KeyPair generateDefaultRSAKey() throws Exception {
        // VULNERABLE: Usa tamaño por defecto (puede ser débil)
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        // No especifica tamaño - usa default (varía por implementación)
        
        return keyGen.generateKeyPair();
        
        // Riesgo: Default podría ser 1024 bits en algunas JVMs
    }
    
    /**
     * Vulnerabilidad: EC (Elliptic Curve) con curva débil
     */
    public KeyPair generateWeakECKey() throws Exception {
        // VULNERABLE: EC con menos de 256 bits
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(192); // WEAK! (Recomendado: 256+)
        
        return keyGen.generateKeyPair();
        
        // Ataque: 192-bit EC equivale a ~96-bit symmetric
    }
    
    /**
     * Vulnerabilidad: SecureRandom con seed débil para keygen
     */
    public KeyPair generateKeyWithWeakSeed() throws Exception {
        // VULNERABLE: Seed predecible compromete la llave
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        
        // Seed basado en timestamp (predecible)
        SecureRandom weakRandom = new SecureRandom();
        weakRandom.setSeed(System.currentTimeMillis());
        
        keyGen.initialize(2048, weakRandom); // Tamaño OK, pero seed débil
        
        return keyGen.generateKeyPair();
        
        // Ataque: Adivinar seed permite reproducir llaves
    }
    
    /**
     * Vulnerabilidad: Llave simétrica muy corta
     */
    public SecretKey generateVeryShortKey() throws Exception {
        // VULNERABLE: 40 bits (exportable encryption de los 90s)
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(40); // EXTREMELY WEAK!
        
        return keyGen.generateKey();
        
        // Ataque: Brute force en segundos
    }
    
    /**
     * Vulnerabilidad: RC4 key (algoritmo obsoleto)
     */
    public SecretKey generateRC4Key() throws Exception {
        // VULNERABLE: RC4 tiene vulnerabilidades conocidas
        KeyGenerator keyGen = KeyGenerator.getInstance("RC4");
        keyGen.init(128); // Incluso con 128 bits, RC4 es inseguro
        
        return keyGen.generateKey();
        
        // Ataque: RC4 NOMORE attack, bias en keystream
    }
    
    /**
     * Vulnerabilidad: Llave hardcoded (peor que débil)
     */
    public byte[] getHardcodedKey() {
        // VULNERABLE: Llave estática en código fuente
        byte[] hardcodedKey = {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F
        };
        
        // Ataque: Llave extraíble con reverse engineering
        return hardcodedKey;
    }
    
    /**
     * Vulnerabilidad: Key derivation con pocas iteraciones
     */
    public SecretKey deriveKeyWeakly(String password) throws Exception {
        // VULNERABLE: PBKDF2 con solo 100 iteraciones (recomendado: 100,000+)
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
            password.toCharArray(),
            "salt".getBytes(),
            100,  // WEAK! Muy pocas iteraciones
            128   // Key length OK
        );
        
        javax.crypto.SecretKeyFactory factory = 
            javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        
        return factory.generateSecret(spec);
        
        // Ataque: Brute force acelerado con GPU
    }
}
