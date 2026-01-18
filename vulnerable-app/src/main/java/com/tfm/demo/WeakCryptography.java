package com.tfm.demo;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * A02:2021 – Cryptographic Failures
 * Ejemplos de criptografía débil e insegura
 */
public class WeakCryptography {

    // VULNERABILITY: Uso de DES (algoritmo obsoleto)
    public byte[] encryptWithDES(byte[] data) throws Exception {
        String algorithm = "DES";
        byte[] keyBytes = "weakkey1".getBytes(); // 8 bytes para DES
        SecretKeySpec key = new SecretKeySpec(keyBytes, algorithm);
        
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        return cipher.doFinal(data);
    }
    
    // VULNERABILITY: Uso de ECB mode (inseguro)
    public byte[] encryptWithECB(byte[] data) throws Exception {
        String algorithm = "AES/ECB/PKCS5Padding"; // ECB no es seguro
        byte[] keyBytes = "1234567890123456".getBytes(); // 16 bytes
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        return cipher.doFinal(data);
    }
    
    // VULNERABILITY: Clave criptográfica débil (hardcoded)
    private static final String ENCRYPTION_KEY = "simplekey";
    
    public String getEncryptionKey() {
        return ENCRYPTION_KEY;
    }
}
