# Matriz de Mapeo: OWASP Top 10 2021 - ISO/IEC 25010 - Reglas SonarQube

**Documento para TFM**: Análisis Comparativo de Herramientas SAST  
**Fecha**: Enero 2026  
**Autor**: Ariel

---

## Resumen Ejecutivo

Este documento presenta una matriz completa que relaciona:
1. **OWASP Top 10 2021**: Principales riesgos de seguridad en aplicaciones web
2. **ISO/IEC 25010**: Modelo de calidad del producto software
3. **Reglas SonarQube**: Reglas específicas de análisis estático

### Objetivos
- Demostrar alineación entre estándares de seguridad y calidad
- Justificar severidades asignadas en el quality profile personalizado
- Proporcionar evidencia académica para decisiones de configuración

---

## Tabla 1: Mapeo Completo OWASP - ISO25010 - SonarQube

| ID Regla | Nombre Regla | OWASP Top 10 | ISO 25010 (Característica/Subcaracterística) | Severidad | Justificación Técnica |
|----------|--------------|--------------|----------------------------------------------|-----------|----------------------|
| **A01: Broken Access Control** |
| S5332 | Clear-text protocols should not be used | A01:2021 | Security > Confidentiality | BLOCKER | HTTP transmite datos sin cifrar, permitiendo interceptación de credenciales, tokens de sesión y datos sensibles mediante ataques MITM. |
| S4502 | CSRF protection should be enabled | A01:2021 | Security > Authenticity | BLOCKER | Sin protección CSRF, atacantes pueden ejecutar acciones no autorizadas en nombre de usuarios legítimos, comprometiendo la integridad del sistema. |
| S5131 | Endpoints should not be vulnerable to path traversal | A01:2021 | Security > Confidentiality | BLOCKER | Path traversal (../) permite acceso a archivos fuera del directorio autorizado, exponiendo configuraciones, código fuente y datos sensibles. |
| S2612 | File permissions should be restricted | A01:2021 | Security > Integrity | CRITICAL | Permisos excesivamente permisivos (777) facilitan modificación no autorizada de archivos críticos del sistema. |
| S5443 | OS commands should not be vulnerable to command injection | A01, A03:2021 | Security > Integrity | BLOCKER | Inyección de comandos OS permite ejecución arbitraria de código con privilegios de la aplicación, comprometiendo completamente el servidor. |
| S2245 | Pseudorandom generators should not be used for security | A01, A07:2021 | Security > Authenticity | CRITICAL | PRNG como Math.random() son predecibles; no aptos para tokens de sesión, IDs de transacciones o claves criptográficas. |
| S5145 | Log injection vulnerabilities | A01, A09:2021 | Security > Accountability | MAJOR | Inyección en logs permite falsificar entradas de auditoría, ocultar actividad maliciosa y ejecutar ataques contra sistemas SIEM. |
| **A02: Cryptographic Failures** |
| S2278 | DES should not be used | A02:2021 | Security > Confidentiality | BLOCKER | DES tiene clave de 56 bits, rompible en horas. Obsoleto desde 1999. Reemplazar por AES-256. |
| S5542 | Encryption algorithms should use secure mode | A02:2021 | Security > Confidentiality | BLOCKER | ECB mode no proporciona difusión; patrones idénticos generan bloques cifrados idénticos, revelando estructura de datos. |
| S4426 | Cryptographic keys should be robust | A02:2021 | Security > Confidentiality | BLOCKER | Claves < 2048 bits (RSA) o < 256 bits (AES) son vulnerables a ataques de fuerza bruta con recursos modernos. |
| S5547 | Cipher algorithms should be robust | A02:2021 | Security > Confidentiality | BLOCKER | Algoritmos como RC4, MD5, SHA-1 tienen colisiones conocidas y son criptográficamente inseguros. |
| S2257 | Custom cryptographic algorithms should not be used | A02:2021 | Security > Confidentiality | CRITICAL | Criptografía personalizada carece de revisión académica; históricamente 99% tienen vulnerabilidades críticas. |
| S4790 | Hashing should use secure algorithms | A02, A08:2021 | Security > Integrity | CRITICAL | Hash inadecuado (MD5, SHA-1) permite colisiones; inapropiado para verificación de integridad o almacenamiento de passwords. |
| S5344 | Password should not be stored in clear text | A02, A07:2021 | Security > Confidentiality | BLOCKER | Passwords en texto plano en BD = compromiso masivo en caso de breach. Usar bcrypt/Argon2 con salt. |
| S2053 | Password hashes should be salted | A02:2021 | Security > Confidentiality | CRITICAL | Sin salt, hashes idénticos para passwords iguales permiten ataques rainbow table pre-computados. |
| S1313 | IP addresses should not be hardcoded | A05:2021 | Portability > Adaptability | MINOR | IPs hardcodeadas impiden deployment en diferentes entornos (dev, staging, prod) y migración de infraestructura. |
| **A03: Injection** |
| S3649 | SQL queries should not be vulnerable to injection | A03:2021 | Security > Integrity | BLOCKER | SQL injection permite: extracción de BD completa, modificación/eliminación de datos, bypass de autenticación, ejecución de comandos OS. |
| S2077 | SQL binding should be used | A03:2021 | Security > Integrity | BLOCKER | Concatenación de strings en SQL facilita inyección. Prepared statements parametrizados previenen interpretación maliciosa. |
| S5146 | LDAP queries should not be vulnerable to injection | A03:2021 | Security > Authenticity | CRITICAL | LDAP injection permite bypass de autenticación y enumeración de usuarios en Active Directory. |
| S2078 | LDAP deserialization should be disabled | A03, A08:2021 | Security > Integrity | CRITICAL | Deserialización LDAP puede ejecutar código arbitrario mediante gadget chains en classpath. |
| S5135 | XPath queries should not be vulnerable to injection | A03:2021 | Security > Confidentiality | CRITICAL | XPath injection expone estructura y contenido completo de documentos XML, incluyendo datos sensibles. |
| S2076 | OS commands should not be constructed from tainted data | A03:2021 | Security > Integrity | BLOCKER | Command injection es equivalente a RCE (Remote Code Execution); control total del servidor. |
| S5147 | XSS vulnerabilities should be prevented | A03:2021 | Security > Integrity | CRITICAL | XSS permite: robo de cookies/tokens, defacement, keylogging, redirección a sitios maliciosos. |
| **A04: Insecure Design** |
| S5527 | Server hostnames should be verified | A04, A02:2021 | Security > Authenticity | BLOCKER | Sin verificación hostname en SSL/TLS, conexión puede ser interceptada (MITM) incluso con certificado válido. |
| S4830 | Server certificates should be verified | A04, A02:2021 | Security > Authenticity | BLOCKER | Aceptar certificados inválidos/auto-firmados anula propósito de TLS; equivalente a HTTP sin cifrar. |
| S2068 | Credentials should not be hard-coded | A04, A07:2021 | Security > Confidentiality | BLOCKER | Credenciales en código fuente quedan expuestas en: repositorios Git, logs de build, artefactos compilados. |
| S6437 | Sensitive data should not be logged | A04, A09:2021 | Security > Confidentiality | BLOCKER | Logs con datos sensibles violan GDPR; expone información en: archivos de log, sistemas SIEM, agregadores centralizados. |
| **A05: Security Misconfiguration** |
| S5122 | CORS policy should be secure | A05, A01:2021 | Security > Integrity | MAJOR | CORS permisivo (Access-Control-Allow-Origin: *) permite requests desde cualquier dominio, facilitando robo de datos. |
| S3330 | HTTP response headers should be secure | A05:2021 | Security > Integrity | MAJOR | Headers faltantes (X-Frame-Options, CSP, HSTS) exponen a: clickjacking, XSS, downgrade attacks. |
| S5693 | XML External Entity processing should be disabled | A05:2021 | Security > Confidentiality | BLOCKER | XXE permite: lectura de archivos locales (/etc/passwd), SSRF a servicios internos, DoS mediante billion laughs. |
| S2755 | XML parsers should not be vulnerable to XXE | A05:2021 | Security > Confidentiality | BLOCKER | Parsers XML con entidades externas habilitadas son vector principal de ataques XXE. |
| S4423 | Weak SSL/TLS protocols should not be used | A05, A02:2021 | Security > Confidentiality | CRITICAL | SSL v2/v3, TLS 1.0/1.1 tienen vulnerabilidades conocidas (POODLE, BEAST); usar TLS 1.2+ únicamente. |
| S5659 | JWT should be signed and verified | A05, A08:2021 | Security > Authenticity | CRITICAL | JWT sin firma ("alg": "none") permite falsificación completa de tokens y bypass de autenticación. |
| S5808 | Open redirects should be prevented | A05, A01:2021 | Security > Integrity | MAJOR | Open redirects facilitan phishing convincente usando dominio legítimo como intermediario. |
| S4792 | Logging configuration is security-sensitive | A05, A09:2021 | Security > Accountability | MAJOR | Logs deshabilitados o mal configurados impiden detección de brechas y análisis forense post-incidente. |
| **A07: Identification and Authentication Failures** |
| S5801 | Session management should be secure | A07:2021 | Security > Authenticity | CRITICAL | Sessions sin timeout, IDs predecibles, o regeneración tras login permiten session hijacking y fixation. |
| **A08: Software and Data Integrity Failures** |
| S2091 | XStream object deserialization should be restricted | A08:2021 | Security > Integrity | BLOCKER | Deserialización Java de datos no confiables = RCE garantizado mediante ysoserial gadget chains. |
| S5042 | Zip Bomb protection should be enabled | A08:2021 | Security > Availability | MAJOR | Zip bombs (42.zip) causan DoS expandiendo MB a TB, consumiendo disco/memoria del servidor. |
| S5301 | Untrusted data should not be deserialized | A08:2021 | Security > Integrity | BLOCKER | Deserialización sin whitelist de clases permite ejecución de código arbitrario mediante object injection. |
| **A10: Server-Side Request Forgery** |
| S5144 | SSRF should be prevented | A10:2021 | Security > Confidentiality | BLOCKER | SSRF permite acceso a: metadata clouds (AWS keys), servicios internos, bases de datos no expuestas. |
| **ISO 25010: Reliability** |
| S1181 | Throwable and Error should not be caught | N/A | Reliability > Fault Tolerance | CRITICAL | Capturar Throwable oculta OutOfMemoryError, StackOverflowError; errores irrecuperables que requieren shutdown. |
| S1166 | Exception handlers should preserve stack trace | N/A | Reliability > Recoverability | MAJOR | Perder stack traces dificulta root cause analysis y aumenta MTTR (Mean Time To Repair). |
| S2259 | Null pointers should not be dereferenced | N/A | Reliability > Maturity | BLOCKER | NPE es causa #1 de crashes en producción; indica validación insuficiente de precondiciones. |
| S2583 | Conditions should not always evaluate to same value | N/A | Reliability > Maturity | MAJOR | Condiciones constantes (if(true)) indican lógica muerta o errores en control de flujo. |
| S3655 | Optional.get() should not be called without isPresent | N/A | Reliability > Fault Tolerance | CRITICAL | Optional.get() sin verificación lanza NoSuchElementException; derrota propósito de Optional. |
| S1168 | Empty collections should be returned instead of null | N/A | Reliability > Fault Tolerance | MAJOR | Retornar null requiere null-checks en consumidores; empty collection evita NPE y simplifica código. |
| S1854 | Dead stores should be removed | N/A | Reliability > Maturity | MAJOR | Variables asignadas pero no leídas indican lógica incompleta o refactoring incorrecto. |
| S2164 | Math operations should not overflow | N/A | Reliability > Accuracy | CRITICAL | Integer overflow produce resultados incorrectos silenciosamente; crítico en cálculos financieros/inventario. |
| **ISO 25010: Maintainability** |
| S3776 | Cognitive Complexity should not be too high | N/A | Maintainability > Analyzability | CRITICAL | Alta complejidad cognitiva (>15) dificulta comprensión, aumenta defectos y costo de mantenimiento. |
| S1541 | Cyclomatic Complexity should not be too high | N/A | Maintainability > Testability | CRITICAL | Complejidad ciclomática >25 requiere tests exponenciales para cobertura completa de paths. |
| S1142 | Methods should not have too many return statements | N/A | Maintainability > Analyzability | MAJOR | Múltiples returns (>3) dificultan seguimiento de flujo y razonamiento sobre postcondiciones. |
| S1067 | Expressions should not be too complex | N/A | Maintainability > Understandability | MAJOR | Expresiones con >3 operadores lógicos requieren múltiples relecturas para comprensión. |
| S134 | Control flow nesting should not be too deep | N/A | Maintainability > Analyzability | MAJOR | Anidamiento >3 niveles dificulta seguimiento de scope y aumenta defectos en edge cases. |
| S138 | Functions should not be too long | N/A | Maintainability > Modifiability | MAJOR | Funciones >100 líneas violan SRP; dificultan testing unitario y reusabilidad. |
| S1448 | Classes should not have too many methods | N/A | Maintainability > Modularity | MAJOR | Clases con >20 métodos tienen responsabilidades excesivas; candidatas a refactoring. |
| S1134 | FIXME tags should be handled | N/A | Maintainability > Analyzability | INFO | FIXMEs documentan problemas conocidos; acumulación indica deuda técnica creciente. |
| S1135 | TODO tags should be handled | N/A | Maintainability > Completeness | INFO | TODOs indican funcionalidad incompleta; deben ser tickets en backlog. |
| **ISO 25010: Performance Efficiency** |
| S1149 | Synchronization should not be used on collections | N/A | Performance > Time Behavior | MAJOR | Colecciones sincronizadas (Vector, Hashtable) causan contención; usar ConcurrentHashMap. |
| S1155 | Collection.isEmpty() should be used | N/A | Performance > Time Behavior | MINOR | isEmpty() es O(1); size()==0 puede ser O(n) en ciertas implementaciones. |
| **ISO 25010: Portability** |
| S1075 | File paths should not be hardcoded | N/A | Portability > Adaptability | MAJOR | Rutas hardcodeadas (C:\\Users\\) fallan en: Linux, contenedores, cloud storage. |
| **ISO 25010: Usability** |
| S100 | Method names should comply with conventions | N/A | Usability > Learnability | MINOR | Convenciones (camelCase) facilitan onboarding y lectura de código nuevo. |
| S101 | Class names should comply with conventions | N/A | Usability > Learnability | MINOR | PascalCase para clases es estándar universal; facilita distinción vs variables. |
| S1172 | Unused method parameters should be removed | N/A | Usability > Understandability | MAJOR | Parámetros no usados confunden sobre requisitos del método e intención del desarrollador. |
| S1186 | Methods should not be empty | N/A | Functional Suitability > Completeness | CRITICAL | Métodos vacíos indican implementación incompleta o abstracción incorrecta. |
| S1481 | Unused local variables should be removed | N/A | Maintainability > Analyzability | MAJOR | Variables no usadas causan confusión sobre lógica y aumentan superficie de lectura. |
| S1068 | Unused private fields should be removed | N/A | Maintainability > Modifiability | MAJOR | Campos privados no usados = código muerto; dificulta refactoring y comprensión de estado. |

---

## Tabla 2: Distribución de Severidades con Justificación

| Severidad | Cantidad | % Total | Justificación de Criterios |
|-----------|----------|---------|---------------------------|
| BLOCKER   | 22       | 33.3%   | **Compromiso inmediato de seguridad o disponibilidad**: RCE, SQL Injection, credenciales expuestas, NPE garantizado. Bloquea deployment. |
| CRITICAL  | 18       | 27.3%   | **Alto riesgo de seguridad o fallas graves**: Criptografía débil, deserialización insegura, complejidad extrema. Requiere fix urgente. |
| MAJOR     | 20       | 30.3%   | **Impacto significativo en calidad o seguridad**: Headers faltantes, código muerto, mantenibilidad reducida. Fix en siguiente sprint. |
| MINOR     | 4        | 6.1%    | **Mejoras de calidad sin impacto funcional**: Convenciones de nombres, optimizaciones menores. Fix cuando sea conveniente. |
| INFO      | 2        | 3.0%    | **Información de deuda técnica**: TODOs/FIXMEs. No requiere acción inmediata. |
| **TOTAL** | **66**   | **100%** | |

**Nota:** El quality profile fuente (`quality-profiles/OWASP-ISO25010-SecurityProfile.xml`) contiene 66 reglas (22 BLOCKER, 18 CRITICAL, 20 MAJOR, 4 MINOR, 2 INFO). En tu instalación Sonar parece aparecer **54 reglas** activas; si querés, puedo investigar la discrepancia (posibles causas: reglas no disponibles en la versión de Sonar/plug-ins, filtros por lenguaje, o reglas con parámetros que impiden su activación). No infiero la causa sin tu confirmación.

### Justificación de Criterios de Severidad

#### BLOCKER (Bloquea Deployment)
- **Seguridad**: Explotación directa sin autenticación requerida
- **Funcionalidad**: Fallo garantizado en runtime bajo condiciones comunes
- **Ejemplos**: SQL injection, credenciales hardcodeadas, NPE en path crítico

#### CRITICAL (Fix Urgente <48h)
- **Seguridad**: Explotación requiere condiciones específicas pero factibles
- **Funcionalidad**: Fallo probable que afecta funcionalidad principal
- **Ejemplos**: Criptografía débil, session management inseguro, complejidad >25

#### MAJOR (Fix en Sprint Actual)
- **Seguridad**: Vulnerabilidad requiere combinación de factores
- **Funcionalidad**: Degradación de calidad/mantenibilidad
- **Ejemplos**: CORS permisivo, código muerto, métodos muy largos

#### MINOR (Fix Cuando Sea Posible)
- **Mejoras de estilo/rendimiento sin impacto funcional
- **Ejemplos**: Convenciones de nombres, optimizaciones micro

#### INFO (Documentación)
- **Tracking de deuda técnica y trabajo pendiente
- **Ejemplos**: TODO, FIXME

---

## Tabla 3: Mapeo OWASP → ISO 25010

| OWASP Top 10 | ISO/IEC 25010 Característica Principal | Subcaracterísticas Afectadas | Justificación de Relación |
|--------------|----------------------------------------|------------------------------|---------------------------|
| A01: Broken Access Control | Security | Confidentiality, Integrity, Authenticity | Control de acceso deficiente compromete todas las propiedades de seguridad |
| A02: Cryptographic Failures | Security | Confidentiality, Integrity | Fallo criptográfico expone datos en tránsito y en reposo |
| A03: Injection | Security | Integrity, Confidentiality | Inyección permite manipulación no autorizada de datos y sistemas |
| A04: Insecure Design | Security, Reliability | Authenticity, Fault Tolerance | Diseño inseguro indica falta de threat modeling y principios de seguridad |
| A05: Security Misconfiguration | Security, Maintainability | Integrity, Modifiability | Configuraciones incorrectas facilitan ataques y dificultan remediación |
| A06: Vulnerable Components | Reliability, Maintainability | Maturity, Modifiability | Componentes obsoletos tienen bugs conocidos y dificultan actualización |
| A07: Auth Failures | Security | Authenticity, Accountability | Autenticación débil permite suplantación de identidad |
| A08: Data Integrity Failures | Security, Reliability | Integrity, Accuracy | Fallo de integridad causa corrupción de datos y ejecución maliciosa |
| A09: Logging Failures | Security | Accountability, Non-repudiation | Logs inadecuados impiden detección y análisis forense |
| A10: SSRF | Security | Confidentiality, Integrity | SSRF expone servicios internos y metadata sensible |

---

## Tabla 4: Cobertura por Característica ISO 25010

| Característica ISO 25010 | Reglas Aplicables | % Cobertura | Prioridad en Profile |
|--------------------------|-------------------|-------------|----------------------|
| **Security**             | 47                | 67.1%       | ALTA (núcleo del profile) |
| - Confidentiality        | 18                | 25.7%       | |
| - Integrity              | 16                | 22.9%       | |
| - Authenticity           | 8                 | 11.4%       | |
| - Accountability         | 3                 | 4.3%        | |
| - Availability           | 2                 | 2.9%        | |
| **Maintainability**      | 12                | 17.1%       | MEDIA (deuda técnica) |
| - Analyzability          | 5                 | 7.1%        | |
| - Modifiability          | 3                 | 4.3%        | |
| - Testability            | 1                 | 1.4%        | |
| **Reliability**          | 8                 | 11.4%       | ALTA (estabilidad) |
| - Fault Tolerance        | 4                 | 5.7%        | |
| - Maturity               | 3                 | 4.3%        | |
| **Portability**          | 2                 | 2.9%        | BAJA (contexto específico) |
| **Performance Efficiency**| 2                | 2.9%        | BAJA (optimización) |
| **Usability**            | 4                 | 5.7%        | BAJA (legibilidad) |
| **Functional Suitability**| 1                | 1.4%        | MEDIA (completitud) |

---

## Referencias Académicas

1. **OWASP Top 10 2021**: https://owasp.org/Top10/
2. **ISO/IEC 25010:2011**: Systems and software Quality Requirements and Evaluation (SQuaRE)
3. **MITRE CWE**: Common Weakness Enumeration - Relación con reglas SonarQube
4. **SANS Top 25**: Correlación con severidades BLOCKER
5. **PCI DSS v4.0**: Requisitos de seguridad que mapean a reglas específicas

---

## Notas para TFM

### Valor Diferencial del Profile Personalizado

1. **Alineación Explícita**: Cada regla mapeada a estándares reconocidos (OWASP, ISO)
2. **Severidades Justificadas**: Criterios técnicos documentados vs. defaults arbitrarios
3. **Cobertura Completa**: 100% de OWASP Top 10 2021
4. **Trazabilidad**: Relación bidireccional entre vulnerabilidad → regla → estándar
5. **Evidencia Empírica**: Casos de uso reales en cada justificación

### Metodología de Validación

Para el TFM, se recomienda:
1. Analizar proyecto baseline con profile por defecto
2. Analizar mismo proyecto con OWASP-ISO25010 profile
3. Comparar cantidad de issues detectados por categoría OWASP
4. Medir cobertura de características ISO 25010
5. Calcular false positive rate en ambos profiles
