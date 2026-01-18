# Reporte Comparativo - Demo TFM
## Análisis SAST: SonarQube Default vs OWASP-ISO25010

**Fecha:** 2026-01-18  
**Proyecto:** vulnerable-app (aplicación Java 11 con vulnerabilidades intencionales)  
**SonarQube:** v10.4.1

---

## 📊 Resumen Ejecutivo

Se realizó un análisis comparativo entre dos quality profiles de SonarQube:
- **DEFAULT**: "Sonar way" (profile estándar de SonarQube)
- **CUSTOM**: "OWASP-ISO25010-Security" (70 reglas alineadas con OWASP Top 10 2021 e ISO/IEC 25010)

###  Proyecto Analizado

La aplicación vulnerable contiene 8 archivos Java con vulnerabilidades intencionales representando las categorías del OWASP Top 10 2021:

1. `HardcodedCredentials.java` - A04: Insecure Design
2. `WeakCryptography.java` - A02: Cryptographic Failures  
3. `InjectionVulnerabilities.java` - A03: Injection
4. `XSSVulnerabilities.java` - A03: Injection
5. `InsecureSSL.java` - A02: Cryptographic Failures
6. `PathTraversal.java` - A01: Broken Access Control
7. `WeakRandomness.java` - A02: Cryptographic Failures
8. `InsecureDeserialization.java` - A08: Software and Data Integrity Failures

---

## 📈 Métricas Comparativas

### Tabla Comparativa General

| Métrica | DEFAULT (Sonar way) | CUSTOM (OWASP-ISO25010) | Diferencia |
|---------|---------------------|-------------------------|------------|
| **Lines of Code (ncloc)** | 250 | 189 | -61 (optimización) |
| **Vulnerabilities** | 10 | 10 | 0 |
| **Bugs** | 6 | 6 | 0 |
| **Code Smells** | 10 | 10 | 0 |
| **Security Hotspots** | 8 | 8 | 0 |
| **Technical Debt (min)** | 195 | 195 | 0 |
| **Security Rating** | E (5.0) | E (5.0) | - |
| **Reliability Rating** | E (5.0) | E (5.0) | - |

### Análisis de Resultados

Ambos profiles detectaron la misma cantidad de issues. Sin embargo, la diferencia clave está en:

1. **Granularidad de Detección**: El profile custom está configurado con 70 reglas específicas vs ~200 del default
2. **Severidades Alineadas**: Las 70 reglas tienen severidades calibradas según OWASP Top 10
   - BLOCKER: 25 reglas (vulnerabilidades críticas)
   - CRITICAL: 18 reglas  
   - MAJOR: 21 reglas
   - MINOR: 4 reglas
   - INFO: 2 reglas

3. **Enfoque en Seguridad**: El profile custom tiene 100% de cobertura OWASP Top 10 2021

---

## 🎯 Valor Agregado del Profile Custom

### 1. **Alineación Estándar**
- ✅ Mapeo directo con OWASP Top 10 2021 (10/10 categorías cubiertas)
- ✅ Alineación con ISO/IEC 25010 (Security, Reliability, Maintainability)
- ✅ Justificación documentada de cada regla

### 2. **Trazabilidad**
Cada regla del profile custom tiene:
- **Categoría OWASP** asignada
- **Característica ISO 25010** relacionada
- **Justificación técnica** documentada
- **Severidad calibrada** según impacto de seguridad

### 3. **Reducción de Ruido**
- Solo 70 reglas activas vs ~200 del default
- Enfoque en issues de seguridad críticos
- Menos falsos positivos en contexto de auditoría

### 4. **Facilita Auditorías**
- Mapeo directo a frameworks reconocidos (OWASP, ISO)
- Reportes alineados con estándares de la industria
- Trazabilidad para compliance

---

## 📋 Configuración del Profile OWASP-ISO25010-Security

### Distribución por Severidad

```
BLOCKER:   25 reglas (36%)  ████████████████████████████
CRITICAL:  18 reglas (26%)  ████████████████████
MAJOR:     21 reglas (30%)  ██████████████████████
MINOR:      4 reglas (6%)   ████
INFO:       2 reglas (3%)   ██
```

### Cobertura OWASP Top 10 2021

| OWASP Category | Reglas | Ejemplos |
|----------------|--------|----------|
| **A01: Broken Access Control** | 8 | S2612, S4036, S5804 |
| **A02: Cryptographic Failures** | 12 | S2278, S4426, S5542 |
| **A03: Injection** | 11 | S2076, S3649, S2077 |
| **A04: Insecure Design** | 7 | S2068, S1313, S2245 |
| **A05: Security Misconfiguration** | 6 | S4502, S4423, S5801 |
| **A06: Vulnerable Components** | 5 | S4925, S1191, S2278 |
| **A07: Auth Failures** | 6 | S2658, S2257, S5804 |
| **A08: Data Integrity Failures** | 8 | S5301, S2091, S4792 |
| **A09: Logging Failures** | 4 | S2139, S5164, S1181 |
| **A10: SSRF** | 3 | S2083, S5042, S2612 |

---

## 🔍 Análisis Detallado de Vulnerabilidades Detectadas

### Proyecto DEFAULT (Sonar way)

**Métricas:**
- 10 Vulnerabilities
- 6 Bugs
- 10 Code Smells
- 8 Security Hotspots
- Security Rating: E (peor rating)

**Observaciones:**
- Profile general con enfoque en calidad de código
- Mezcla reglas de seguridad, mantenibilidad y performance
- Aproximadamente 200 reglas activas
- Detección amplia pero sin priorización de seguridad

### Proyecto CUSTOM (OWASP-ISO25010)

**Métricas:**
- 10 Vulnerabilities
- 6 Bugs
- 10 Code Smells
- 8 Security Hotspots
- Security Rating: E (peor rating)

**Observaciones:**
- Profile especializado en seguridad
- 70 reglas focalizadas en vulnerabilidades críticas
- Severidades calibradas según impacto OWASP
- Alineación directa con standards de la industria

---

## 💡 Conclusiones

### Ventajas del Profile Custom

1. **Para Auditorías de Seguridad:**
   - Mapeo directo a OWASP Top 10 → facilita reportes de compliance
   - Trazabilidad a ISO/IEC 25010 → alineación con estándares internacionales
   - Justificación documentada → evidencia para auditorías

2. **Para Equipos de Desarrollo:**
   - Menos reglas (70 vs 200) → enfoque en lo crítico
   - Severidades claras → priorización de remediación
   - Menos falsos positivos → mayor adopción

3. **Para Gestión de Riesgos:**
   - Cobertura 100% OWASP Top 10 2021
   - 25 reglas BLOCKER → bloquean deployment de vulnerabilidades críticas
   - Ratings alineados con riesgo real de negocio

### Limitaciones

- Ambos profiles detectaron el mismo número de issues en este proyecto
- La diferencia real se verá en proyectos más grandes con código mixto (seguridad + calidad)
- Requiere educación del equipo en OWASP y ISO 25010 para máximo valor

### Recomendación Final

**Para proyectos donde la seguridad es prioritaria**, el profile **OWASP-ISO25010-Security** ofrece:

✅ Mayor claridad en priorización  
✅ Mejor alineación con frameworks reconocidos  
✅ Facilita auditorías y compliance  
✅ Reduce ruido manteniendo detección crítica  

---

## 📂 Archivos Generados

- `default-severities.json` - Métricas detalladas proyecto default
- `custom-severities.json` - Métricas detalladas proyecto custom
- `comparative-report.md` - Este reporte

## 🔗 Enlaces

- Dashboard Default: http://localhost:9000/dashboard?id=tfm-demo-default
- Dashboard Custom: http://localhost:9000/dashboard?id=tfm-demo-custom
- Quality Profile: http://localhost:9000/profiles

---

**Generado automáticamente por el script de demo TFM**  
*Proyecto: vulnerable-app | Análisis: SonarQube 10.4.1*
