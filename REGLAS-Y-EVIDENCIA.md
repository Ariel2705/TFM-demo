# Reglas aplicadas y evidencia consolidada

Este documento consolida las reglas aplicadas en el quality profile `OWASP-ISO25010-Security` y los resultados experimentales publicados en `EVIDENCIA-COMPLETA.md`.

**Reglas Aplicadas (clave — prioridad — justificación breve)**

- `S5332` — BLOCKER — HTTP sin cifrar expone datos sensibles (OWASP A01, ISO: Confidentiality).
- `S4502` — BLOCKER — CSRF permite acciones no autorizadas (OWASP A01, ISO: Authenticity).
- `S5131` — BLOCKER — Path traversal permite acceso no autorizado a archivos (OWASP A01, ISO: Confidentiality).
- `S2612` — CRITICAL — Permisos de archivos incorrectos facilitan acceso no autorizado (OWASP A01, ISO: Integrity).
- `S5443` — BLOCKER — Comandos OS sin validación = ejecución arbitraria (OWASP A01/A03, ISO: Integrity).
- `S2245` — CRITICAL — PRNG débiles comprometen tokens/sesiones (OWASP A01/A07, ISO: Authenticity).
- `S5145` — MAJOR — Log injection puede ocultar actividad maliciosa (OWASP A01/A09, ISO: Accountability).

- `S2278` — BLOCKER — DES/algoritmos débiles son rompibles (OWASP A02, ISO: Confidentiality).
- `S5542` — BLOCKER — Modos de cifrado inseguros (ECB) no protegen patrones (OWASP A02, ISO: Confidentiality).
- `S4426` — BLOCKER — Claves criptográficas débiles facilitan ataques (OWASP A02, ISO: Confidentiality).
- `S5547` — BLOCKER — Algoritmos criptográficos débiles son vulnerables (OWASP A02, ISO: Confidentiality).
- `S2257` — CRITICAL — Criptografía personalizada suele tener vulnerabilidades (OWASP A02, ISO: Confidentiality).
- `S4790` — CRITICAL — Hash inadecuado compromete integridad de datos (OWASP A02/A08, ISO: Integrity).
- `S5344` — BLOCKER — Contraseñas en texto plano = compromiso total (OWASP A02/A07, ISO: Confidentiality).
- `S2053` — CRITICAL — Hash sin salt facilita ataques rainbow table (OWASP A02, ISO: Confidentiality).
- `S1313` — MINOR — IPs hardcodeadas dificultan portabilidad (OWASP A05, ISO: Portability).

- `S3649` — BLOCKER — SQL injection = acceso/modificación no autorizada de BD (OWASP A03, ISO: Integrity).
- `S2077` — BLOCKER — Concatenación SQL facilita inyección (OWASP A03, ISO: Integrity).
- `S5146` — CRITICAL — LDAP injection compromete autenticación (OWASP A03, ISO: Authenticity).
- `S2078` — CRITICAL — Deserialización LDAP puede ejecutar código arbitrario (OWASP A03/A08, ISO: Integrity).
- `S5135` — CRITICAL — XPath injection expone datos XML sensibles (OWASP A03, ISO: Confidentiality).
- `S2076` — BLOCKER — OS command injection = ejecución remota de código (OWASP A03, ISO: Integrity).
- `S5147` — CRITICAL — XSS compromete usuarios y sesiones (OWASP A03, ISO: Integrity).

- `S5527` — BLOCKER — Sin verificación hostname = Man-in-the-Middle (OWASP A04/A02, ISO: Authenticity).
- `S4830` — BLOCKER — Certificados no verificados = conexiones inseguras (OWASP A04/A02, ISO: Authenticity).
- `S2068` — BLOCKER — Credenciales hardcodeadas = compromiso en repositorios (OWASP A04/A07, ISO: Confidentiality).
- `S6437` — BLOCKER — Datos sensibles en logs = exposición información (OWASP A04/A09, ISO: Confidentiality).

- `S5122` — MAJOR — CORS permisivo permite acceso no autorizado (OWASP A05/A01, ISO: Integrity).
- `S3330` — MAJOR — Headers HTTP inseguros facilitan ataques (OWASP A05, ISO: Integrity).
- `S5693` — BLOCKER — XXE permite lectura archivos/SSRF (OWASP A05, ISO: Confidentiality).
- `S2755` — BLOCKER — Parsers XML vulnerables a XXE (OWASP A05, ISO: Confidentiality).
- `S4423` — CRITICAL — Protocolos SSL/TLS débiles son interceptables (OWASP A05/A02, ISO: Confidentiality).
- `S5659` — CRITICAL — JWT sin firma = tokens falsificables (OWASP A05/A08, ISO: Authenticity).
- `S5808` — MAJOR — Open redirects facilitan phishing (OWASP A05/A01, ISO: Integrity).
- `S4792` — MAJOR — Logs mal configurados afectan auditoría (OWASP A05/A09, ISO: Accountability).

- `S5801` — CRITICAL — Session management débil facilita secuestro (OWASP A07, ISO: Authenticity).

- `S2091` — BLOCKER — Deserialización insegura = ejecución código remoto (OWASP A08, ISO: Integrity).
- `S5042` — MAJOR — Zip bombs causan DoS (OWASP A08, ISO: Availability).
- `S5301` — BLOCKER — Deserialización de datos no confiables = RCE (OWASP A08, ISO: Integrity).

- `S5144` — BLOCKER — SSRF permite acceso recursos internos (OWASP A10, ISO: Confidentiality).

- `S1181` — CRITICAL — Capturar Throwable oculta errores críticos (ISO: Reliability).
- `S1166` — MAJOR — Perder stack traces dificulta debugging (ISO: Reliability).
- `S2259` — BLOCKER — NullPointerException causa fallos en runtime (ISO: Reliability).
- `S2583` — MAJOR — Condiciones constantes indican lógica errónea (ISO: Reliability).
- `S3655` — CRITICAL — Optional.get() sin check causa excepciones (ISO: Reliability).
- `S1168` — MAJOR — Retornar null genera NPE en consumidores (ISO: Reliability).
- `S1854` — MAJOR — Dead stores indican lógica no utilizada/errónea (ISO: Reliability).
- `S2164` — CRITICAL — Overflow en matemáticas causa resultados incorrectos (ISO: Reliability/Accuracy).

- `S3776` — CRITICAL — Alta complejidad cognitiva dificulta mantenimiento (ISO: Maintainability). (threshold=15)
- `S1541` — CRITICAL — Complejidad ciclomática alta = difícil testing (ISO: Maintainability). (threshold=25)
- `S1142` — MAJOR — Múltiples returns dificultan seguimiento (ISO: Maintainability).
- `S1067` — MAJOR — Expresiones complejas reducen legibilidad (ISO: Maintainability).
- `S134` — MAJOR — Anidamiento excesivo dificulta comprensión (ISO: Maintainability).
- `S138` — MAJOR — Funciones largas violan Single Responsibility (ISO: Maintainability). (max=100)
- `S1448` — MAJOR — Muchos métodos indican responsabilidad excesiva (ISO: Maintainability).
- `S1134` — INFO — FIXMEs indican problemas pendientes (ISO: Maintainability).
- `S1135` — INFO — TODOs indican funcionalidad incompleta (ISO: Maintainability).

- `S1149` — MAJOR — Colecciones ineficientes afectan performance (ISO: Performance).
- `S1155` — MINOR — isEmpty() más eficiente que size()==0 (ISO: Performance).

- `S1075` — MAJOR — Rutas hardcodeadas no son portables entre sistemas (ISO: Portability).

- `S100` — MINOR — Nombres de métodos claros mejoran comprensión (ISO: Usability).
- `S101` — MINOR — Nombres de clases estándar facilitan lectura (ISO: Usability).
- `S1172` — MAJOR — Parámetros no usados confunden intención (ISO: Usability).
- `S1186` — CRITICAL — Métodos vacíos indican implementación incompleta (ISO: Functional Suitability).
- `S1481` — MAJOR — Variables no usadas causan confusión (ISO: Maintainability).
- `S1068` — MAJOR — Campos privados no usados = código muerto (ISO: Maintainability).


**Resultados (extraídos de `EVIDENCIA-COMPLETA.md`)**

## Resumen Ejecutivo

La hipótesis: "Un profile enfocado en seguridad crítica (54 reglas) puede reducir el ruido significativamente sin sacrificar la detección de vulnerabilidades" fue confirmada en 2 proyectos.

### Experimento 1: tfm-demo (Aplicación Vulnerable Sintética)

Perfiles comparados:
- `tfm-demo-default-final`: Sonar way (479 reglas)
- `tfm-demo-custom-final`: OWASP-ISO25010-Security (54 reglas)

Resultados principales:
- Bugs: DEFAULT 32 → CUSTOM 1 (**-96.9%**)
- Vulnerabilities: DEFAULT 36 → CUSTOM 36 (**0% pérdida**)
- Code Smells: DEFAULT 201 → CUSTOM 55 (**-72.6%**)
- Security Hotspots: DEFAULT 44 → CUSTOM 28 (**-36.4%**)
- Technical Debt: 2,373 min → 460 min (**-80.6%**)
- Reliability Rating: 5.0 (E) → 3.0 (C) (mejora)

Conclusiones: reducción masiva de ruido sin pérdida de detección, ahorro estimado 31.9 horas ($1,594.17).

### Experimento 2: WebGoat (Aplicación Web Real)

Perfiles comparados:
- `test1`: Sonar way DEFAULT (479 reglas)
- `test2`: OWASP-ISO25010-Security CUSTOM (54 reglas)

Resultados principales:
- Bugs: DEFAULT 35 → CUSTOM 4 (**-88.6%**)
- Vulnerabilities: DEFAULT 8 → CUSTOM 8 (**0% pérdida**)
- Code Smells: DEFAULT 502 → CUSTOM 155 (**-69.1%**)
- Security Hotspots: DEFAULT 69 → CUSTOM 48 (**-30.4%**)
- Technical Debt: 2,476 min → 1,820 min (**-26.5%**)
- Security & Reliability Rating: ambos E (sin cambio)

Conclusiones: reducción consistente de ruido manteniendo la detección de vulnerabilidades; ahorro aproximado 10.9 horas ($546.67).


## Conclusión Consolidada

- El profile `OWASP-ISO25010-Security` mantiene la detección de vulnerabilities críticas (0% pérdida) en ambos experimentos.
- Reduce el ruido (bugs y code smells) entre 69% y 97% según proyecto.
- Aporta ahorro de tiempo y reducción de deuda técnica medible.


## Referencias y anexos

- Quality Profile XML fuente: [quality-profiles/OWASP-ISO25010-SecurityProfile.xml](quality-profiles/OWASP-ISO25010-SecurityProfile.xml)
- Evidencia completa: [EVIDENCIA-COMPLETA.md](EVIDENCIA-COMPLETA.md)
- Reglas recomendadas: [docs/reglas-recomendadas.txt](docs/reglas-recomendadas.txt)
- Scripts de comparación: [results/compare-final.py](results/compare-final.py) y [results/compare-webgoat.py](results/compare-webgoat.py)

---

Fecha: Enero 2026
Fuente: `quality-profiles/OWASP-ISO25010-SecurityProfile.xml` + `EVIDENCIA-COMPLETA.md`
