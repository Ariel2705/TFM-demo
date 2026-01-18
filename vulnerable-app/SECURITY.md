# Vulnerabilidades Intencionales - Proyecto Demo

⚠️ **ADVERTENCIA**: Este código contiene vulnerabilidades **INTENCIONALES** para propósitos educativos.  
❌ **NO USAR EN PRODUCCIÓN**

## Propósito

Este proyecto fue creado específicamente para:
1. Demostrar capacidades de detección de herramientas SAST
2. Validar quality profiles personalizados
3. Investigación académica (TFM)
4. Formación en seguridad de aplicaciones

## Vulnerabilidades Incluidas

### A01:2021 – Broken Access Control

**PathTraversal.java**
- ❌ Path Traversal sin validación
- ❌ Directory Listing sin restricciones
- 📌 CWE-22, CWE-23

### A02:2021 – Cryptographic Failures

**WeakCryptography.java**
- ❌ DES encryption (obsoleto desde 1999)
- ❌ ECB mode (no proporciona confidencialidad)
- ❌ Claves criptográficas hardcodeadas
- 📌 CWE-327, CWE-326

**InsecureSSL.java**
- ❌ Bypass de verificación de certificados SSL
- ❌ Deshabilitación de hostname verification
- 📌 CWE-295

**WeakRandomness.java**
- ❌ Uso de Math.random() para tokens de sesión
- ❌ PRNG con seed predecible
- 📌 CWE-338

### A03:2021 – Injection

**InjectionVulnerabilities.java**
- ❌ SQL Injection por concatenación de strings
- ❌ Command Injection en Runtime.exec()
- 📌 CWE-89, CWE-78

**XSSVulnerabilities.java**
- ❌ Reflected XSS
- ❌ Stored XSS potencial
- ❌ DOM-based XSS en JavaScript
- 📌 CWE-79

### A04:2021 – Insecure Design

**HardcodedCredentials.java**
- ❌ Credenciales de BD en código fuente
- ❌ API keys hardcodeadas
- 📌 CWE-798

### A08:2021 – Software and Data Integrity Failures

**InsecureDeserialization.java**
- ❌ Deserialización de datos no confiables
- ❌ Sin whitelist de clases permitidas
- 📌 CWE-502

## Detección Esperada

### Con Profile Por Defecto (Sonar way)
- 2-3 issues BLOCKER
- ~15-20 issues totales
- Algunas vulnerabilidades **NO detectadas**

### Con Profile Personalizado (OWASP-ISO25010)
- 8-12 issues BLOCKER ✅
- ~25-35 issues totales ✅
- **Todas** las vulnerabilidades críticas detectadas ✅

## Mapeo a Reglas SonarQube

| Vulnerabilidad | Archivo | Regla | Severidad |
|----------------|---------|-------|-----------|
| Hard-coded credentials | HardcodedCredentials.java | S2068 | BLOCKER |
| DES encryption | WeakCryptography.java | S2278 | BLOCKER |
| ECB mode | WeakCryptography.java | S5542 | BLOCKER |
| SQL Injection | InjectionVulnerabilities.java | S3649 | BLOCKER |
| Command Injection | InjectionVulnerabilities.java | S2076 | BLOCKER |
| SSL bypass | InsecureSSL.java | S4830 | BLOCKER |
| Path Traversal | PathTraversal.java | S5131 | BLOCKER |
| Unsafe deserialization | InsecureDeserialization.java | S5301 | BLOCKER |
| Weak PRNG | WeakRandomness.java | S2245 | CRITICAL |
| XSS | XSSVulnerabilities.java | S5147 | CRITICAL |

## Uso Ético

✅ **Permitido**:
- Investigación académica
- Formación en seguridad
- Testing de herramientas SAST
- Demos controlados

❌ **Prohibido**:
- Uso en aplicaciones de producción
- Deployment en servidores públicos
- Cualquier uso malicioso

## Remediación

Para cada vulnerabilidad, la remediación correcta sería:

### Credenciales Hardcodeadas
```java
// ❌ VULNERABLE
private static final String PASSWORD = "P@ssw0rd123!";

// ✅ CORRECTO
private String password = System.getenv("DB_PASSWORD");
```

### Criptografía Débil
```java
// ❌ VULNERABLE
Cipher cipher = Cipher.getInstance("DES");

// ✅ CORRECTO
Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
SecretKeySpec key = new SecretKeySpec(keyBytes, "AES"); // 256 bits
```

### SQL Injection
```java
// ❌ VULNERABLE
String query = "SELECT * FROM users WHERE id = '" + userId + "'";

// ✅ CORRECTO
PreparedStatement pstmt = conn.prepareStatement(
    "SELECT * FROM users WHERE id = ?"
);
pstmt.setString(1, userId);
```

### Path Traversal
```java
// ❌ VULNERABLE
File file = new File(BASE_DIR + userInput);

// ✅ CORRECTO
Path basePath = Paths.get(BASE_DIR).toRealPath();
Path requestedPath = basePath.resolve(userInput).normalize();
if (!requestedPath.startsWith(basePath)) {
    throw new SecurityException("Path traversal attempt");
}
```

## Referencias

- [OWASP Top 10 2021](https://owasp.org/Top10/)
- [CWE/SANS Top 25](https://cwe.mitre.org/top25/)
- [SonarQube Java Rules](https://rules.sonarsource.com/java/)

---

**Disclaimer**: Este código es deliberadamente inseguro para propósitos educativos.  
No somos responsables del mal uso de este código.
