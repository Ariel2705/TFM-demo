package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * VULNERABILIDAD: Insecure File Upload
 * 
 * OWASP Top 10 2021: A01:2021 - Broken Access Control
 * ISO/IEC 25010: Security - Integrity
 * 
 * Reglas SonarQube:
 * - S5145: File uploads should be restricted (BLOCKER)
 * - S2083: Path traversal (BLOCKER)
 * - S5131: File types should be validated (CRITICAL)
 */
@MultipartConfig
public class FileUploadVulnerabilities {
    
    private static final String UPLOAD_DIR = "/var/www/uploads/";
    
    /**
     * Vulnerabilidad S5145: Upload sin validación de tipo
     * Permite subir archivos ejecutables (shells)
     */
    public void uploadFile(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();
        
        // VULNERABLE: Sin validar extensión ni tipo MIME
        String filePath = UPLOAD_DIR + fileName;
        
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, Paths.get(filePath), 
                      StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Ataque: Subir shell.jsp, backdoor.php
    }
    
    /**
     * Vulnerabilidad: Path traversal en upload
     */
    public void uploadWithPath(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("file");
        String fileName = request.getParameter("filename");
        
        // VULNERABLE: Permite path traversal
        File uploadFile = new File(UPLOAD_DIR + fileName);
        
        try (InputStream input = filePart.getInputStream();
             FileOutputStream output = new FileOutputStream(uploadFile)) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        
        // Ataque: filename=../../../../../../etc/crontab
    }
    
    /**
     * Vulnerabilidad: Validación solo por extensión (bypasseable)
     */
    public void uploadWithWeakValidation(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();
        
        // VULNERABLE: Solo valida extensión en nombre
        if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
            String filePath = UPLOAD_DIR + fileName;
            filePart.write(filePath);
        }
        
        // Bypass: shell.jsp.jpg (doble extensión)
        // Bypass: shell.php%00.jpg (null byte)
    }
    
    /**
     * Vulnerabilidad: Sin límite de tamaño
     */
    public void uploadWithoutSizeLimit(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("file");
        
        // VULNERABLE: Sin validar tamaño del archivo
        String fileName = filePart.getSubmittedFileName();
        
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, Paths.get(UPLOAD_DIR + fileName));
        }
        
        // Ataque: DoS subiendo archivos gigantes
    }
    
    /**
     * Vulnerabilidad: Upload con nombre predecible
     */
    public String uploadWithPredictableName(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("file");
        
        // VULNERABLE: Nombre predecible permite acceso directo
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = "upload_" + timestamp + ".dat";
        
        String filePath = UPLOAD_DIR + fileName;
        filePart.write(filePath);
        
        return fileName; // Retorna nombre, atacante puede adivinarlo
    }
    
    /**
     * Vulnerabilidad: Upload en directorio web accesible
     */
    public void uploadToWebRoot(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();
        
        // VULNERABLE: Guarda en directorio público
        String webPath = "/var/www/html/public/uploads/" + fileName;
        
        filePart.write(webPath);
        
        // Atacante puede ejecutar: http://app.com/public/uploads/shell.jsp
    }
    
    /**
     * Vulnerabilidad: Validación solo por Content-Type (spoofeable)
     */
    public void uploadValidateContentType(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("file");
        String contentType = filePart.getContentType();
        
        // VULNERABLE: Content-Type puede ser manipulado
        if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
            String fileName = filePart.getSubmittedFileName();
            filePart.write(UPLOAD_DIR + fileName);
        }
        
        // Bypass: Cambiar Content-Type header a image/jpeg pero subir .jsp
    }
    
    /**
     * Vulnerabilidad: Unzip sin validar contenido
     */
    public void uploadAndExtractZip(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("zipfile");
        String tempZip = "/tmp/" + filePart.getSubmittedFileName();
        
        filePart.write(tempZip);
        
        // VULNERABLE: Extrae zip sin validar paths internos
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(tempZip);
        java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = 
            zipFile.entries();
        
        while (entries.hasMoreElements()) {
            java.util.zip.ZipEntry entry = entries.nextElement();
            
            // VULNERABLE: Path traversal en zip
            File outputFile = new File(UPLOAD_DIR + entry.getName());
            
            try (InputStream input = zipFile.getInputStream(entry);
                 FileOutputStream output = new FileOutputStream(outputFile)) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        }
        
        // Ataque: Zip con entry "../../../../../../etc/passwd"
    }
    
    /**
     * Vulnerabilidad: Upload de imagen sin re-encoding
     */
    public void uploadImageWithoutProcessing(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("image");
        String fileName = filePart.getSubmittedFileName();
        
        // VULNERABLE: Guarda imagen sin re-procesar
        // Puede contener código malicioso en metadatos EXIF
        filePart.write(UPLOAD_DIR + fileName);
        
        // Ataque: Imagen con código en EXIF + LFI = RCE
    }
    
    /**
     * Vulnerabilidad: Avatar upload sin sanitizar
     */
    public void uploadAvatar(HttpServletRequest request, 
                            HttpServletResponse response) throws Exception {
        Part avatarPart = request.getPart("avatar");
        String userId = request.getParameter("userId");
        
        // VULNERABLE: Nombre basado en input del usuario
        String avatarPath = UPLOAD_DIR + "avatar_" + userId + ".jpg";
        
        avatarPart.write(avatarPath);
        
        // Ataque: userId=../../config
        // Resultado: Sobrescribe /var/www/config.jpg
    }
}
