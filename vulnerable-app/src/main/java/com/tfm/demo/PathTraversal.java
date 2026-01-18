package com.tfm.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A01:2021 – Broken Access Control
 * Ejemplo de Path Traversal
 */
public class PathTraversal {

    private static final String BASE_DIR = "/var/www/uploads/";
    
    // VULNERABILITY: Path Traversal
    public byte[] readFile(String filename) throws IOException {
        // VULNERABLE: No validación de path, permite ../../../etc/passwd
        File file = new File(BASE_DIR + filename);
        
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        
        return data;
    }
    
    // VULNERABILITY: Directory Listing
    public String[] listFiles(String directory) {
        // VULNERABLE: Permite listar cualquier directorio
        File dir = new File(BASE_DIR + directory);
        return dir.list();
    }
}
