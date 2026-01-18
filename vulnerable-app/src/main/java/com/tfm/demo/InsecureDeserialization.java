package com.tfm.demo;

import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

/**
 * A08:2021 – Software and Data Integrity Failures
 * Deserialización insegura
 */
public class InsecureDeserialization {

    // VULNERABILITY: Deserialización de datos no confiables
    public Object deserializeUntrustedData(byte[] data) throws Exception {
        // VULNERABLE: Permite ejecución de código arbitrario
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        Object obj = ois.readObject();
        
        ois.close();
        bis.close();
        
        return obj;
    }
    
    // VULNERABILITY: Deserialización sin validación
    public Object loadObject(byte[] serializedData) throws Exception {
        // VULNERABLE: Sin whitelist de clases permitidas
        ObjectInputStream ois = new ObjectInputStream(
            new ByteArrayInputStream(serializedData)
        );
        
        return ois.readObject();
    }
}
