# Guía Completa Paso a Paso - TFM Demo SAST

**Objetivo**: Demostrar el valor agregado de un quality profile personalizado vs configuración por defecto  
**Tiempo estimado**: 15-20 minutos  
**Nivel**: Principiante

---

## 📋 Tabla de Contenidos

1. [Prerrequisitos](#prerrequisitos)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Opción 1: Demo Automatizado (Recomendado)](#opción-1-demo-automatizado)
4. [Opción 2: Paso a Paso Manual](#opción-2-paso-a-paso-manual)
5. [Interpretación de Resultados](#interpretación-de-resultados)
6. [Troubleshooting](#troubleshooting)

---

## Prerrequisitos

### Software Necesario

- ✅ **Docker Desktop** (versión 20.10+)
  ```bash
  docker --version
  # Output esperado: Docker version 20.10.x o superior
  ```

- ✅ **Docker Compose** (versión 2.0+)
  ```bash
  docker-compose --version
  # Output esperado: Docker Compose version 2.x.x
  ```

- ✅ **Maven** (versión 3.6+)
  ```bash
  mvn --version
  # Output esperado: Apache Maven 3.6.x o superior
  ```

- ⚠️ **jq** (opcional pero recomendado para parsear JSON)
  ```bash
  # macOS
  brew install jq
  
  # Linux
  sudo apt-get install jq
  ```

### Recursos del Sistema

- **RAM**: Mínimo 4GB disponibles (8GB recomendado)
- **Disco**: 5GB libres
- **CPU**: 2 cores mínimo

### Verificar Puertos Disponibles

```bash
# Verificar que el puerto 9000 esté libre
lsof -i :9000

# Si está ocupado, detener el proceso o cambiar puerto en docker-compose.yml
```

---

## Estructura del Proyecto

```
tfm-demo/
├── docker/                              # Configuración de SonarQube
│   ├── Dockerfile                       # Imagen personalizada de SonarQube
│   ├── docker-compose.yml               # Orquestación de contenedores
│   └── .env.example                     # Variables de entorno
│
├── vulnerable-app/                      # Proyecto Java con vulnerabilidades
│   ├── pom.xml                          # Configuración Maven
│   └── src/main/java/com/tfm/demo/     # Código fuente vulnerable
│       ├── HardcodedCredentials.java    # A04: Credenciales hardcodeadas
│       ├── WeakCryptography.java        # A02: DES, ECB
│       ├── InjectionVulnerabilities.java# A03: SQL Injection
│       ├── XSSVulnerabilities.java      # A03: Cross-Site Scripting
│       ├── InsecureSSL.java             # A02: SSL bypass
│       ├── PathTraversal.java           # A01: Path Traversal
│       ├── WeakRandomness.java          # A02: PRNG débil
│       └── InsecureDeserialization.java # A08: Deserialización insegura
│
├── quality-profiles/                    # Configuración de quality profiles
│   ├── OWASP-ISO25010-SecurityProfile.xml  # Profile personalizado
│   └── documentation/                   # Documentación completa
│       ├── matriz-mapeo-completa.md     # Mapeo reglas-estándares
│       └── justificacion-detallada-reglas.md  # Justificación técnica
│
├── scripts/                             # Scripts de automatización
│   └── run-complete-demo.sh             # Script automatizado completo
│
└── results/                             # Resultados de análisis
    └── comparison-report.md             # Reporte comparativo generado
```

---

## Opción 1: Demo Automatizado (Recomendado)

### 🚀 Ejecución con un Solo Comando

```bash
cd /Users/ariel/PersonalDevs/sast-architecture/tfm-demo/scripts
./run-complete-demo.sh
```

### ¿Qué hace el script?

El script ejecuta automáticamente todos los pasos:

1. ✅ Verifica prerrequisitos (Docker, Maven, jq)
2. ✅ Levanta SonarQube con PostgreSQL
3. ✅ Espera inicialización completa (1-2 minutos)
4. ✅ Genera token de autenticación
5. ✅ Importa quality profile personalizado
6. ✅ Compila el proyecto vulnerable
7. ✅ Ejecuta análisis con profile por defecto
8. ✅ Ejecuta análisis con profile personalizado
9. ✅ Genera reporte comparativo
10. ✅ Muestra resumen de resultados

### Output Esperado

```
╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║        TFM - DEMO COMPLETO DE ANÁLISIS SAST                   ║
║        Comparación de Quality Profiles                        ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝

═══════════════════════════════════════════════════════════════
PASO 1: Verificando prerrequisitos
═══════════════════════════════════════════════════════════════

✓ Docker instalado
✓ Docker Compose instalado
✓ Maven instalado

... (continúa con todos los pasos)

╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║                  DEMO COMPLETADO EXITOSAMENTE                  ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝

═══ Valor Agregado ═══
✓ 8 issues BLOCKER adicionales detectados
✓ 12 vulnerabilidades más identificadas
✓ Mejora del 150% en detección de vulnerabilidades
```

### Acceder a los Resultados

Después de la ejecución:

1. **SonarQube Dashboard**: http://localhost:9000
   - Usuario: `admin`
   - Password: `admin`

2. **Proyecto con Profile Default**: http://localhost:9000/dashboard?id=tfm-demo-default

3. **Proyecto con Profile Custom**: http://localhost:9000/dashboard?id=tfm-demo-custom

4. **Reporte Comparativo**: `tfm-demo/results/comparison-report.md`

---

## Opción 2: Paso a Paso Manual

Si prefieres ejecutar cada paso manualmente para entender el proceso:

### PASO 1: Levantar SonarQube

```bash
# Navegar al directorio de Docker
cd /Users/ariel/PersonalDevs/sast-architecture/tfm-demo/docker

# Levantar contenedores
docker-compose up -d --build

# Verificar estado
docker-compose ps

# Esperar inicialización (1-2 minutos)
# Monitorear logs
docker-compose logs -f sonarqube
```

**Señal de éxito**: Cuando veas en los logs:
```
SonarQube is operational
```

### PASO 2: Acceder a SonarQube

```bash
# Abrir en navegador
open http://localhost:9000

# O verificar con curl
curl http://localhost:9000/api/system/status
```

**Login inicial**:
- Usuario: `admin`
- Password: `admin`
- (Te pedirá cambiar la contraseña en el primer login)

### PASO 3: Generar Token de API

**Opción A - Via UI**:
1. Login en http://localhost:9000
2. Click en tu avatar (arriba derecha) → "My Account"
3. Tab "Security"
4. "Generate Token"
5. Name: `tfm-demo`
6. Click "Generate"
7. **Copiar el token** (solo se muestra una vez)

**Opción B - Via API**:
```bash
curl -u admin:admin -X POST \
  "http://localhost:9000/api/user_tokens/generate?name=tfm-demo"
```

Guardar el token en variable:
```bash
export SONAR_TOKEN="sqp_xxxxxxxxxxxxxxxxxx"
```

### PASO 4: Importar Quality Profile Personalizado

```bash
cd /Users/ariel/PersonalDevs/sast-architecture/tfm-demo/quality-profiles

# Importar profile
curl -u "${SONAR_TOKEN}:" -X POST \
  -F "backup=@OWASP-ISO25010-SecurityProfile.xml" \
  "http://localhost:9000/api/qualityprofiles/restore"
```

**Verificar en UI**:
- Quality Profiles → Java
- Deberías ver "OWASP-ISO25010-Security" en la lista

### PASO 5: Compilar Proyecto Vulnerable

```bash
cd /Users/ariel/PersonalDevs/sast-architecture/tfm-demo/vulnerable-app

# Compilar
mvn clean compile test
```

**Output esperado**:
```
[INFO] BUILD SUCCESS
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

### PASO 6: Análisis con Profile Por Defecto

```bash
mvn sonar:sonar \
  -Dsonar.projectKey=tfm-demo-default \
  -Dsonar.projectName="TFM Demo - Default Profile" \
  -Dsonar.qualityprofile="Sonar way" \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login="${SONAR_TOKEN}"
```

**Tiempo estimado**: 30-60 segundos

**Verificar**:
- Abrir: http://localhost:9000/dashboard?id=tfm-demo-default
- Revisar cantidad de issues detectados

### PASO 7: Análisis con Profile Personalizado

```bash
mvn sonar:sonar \
  -Dsonar.projectKey=tfm-demo-custom \
  -Dsonar.projectName="TFM Demo - OWASP-ISO25010 Profile" \
  -Dsonar.qualityprofile="OWASP-ISO25010-Security" \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login="${SONAR_TOKEN}"
```

**Verificar**:
- Abrir: http://localhost:9000/dashboard?id=tfm-demo-custom
- Comparar con el proyecto anterior

### PASO 8: Extraer Métricas

```bash
mkdir -p /Users/ariel/PersonalDevs/sast-architecture/tfm-demo/results

# Métricas del profile por defecto
curl -u "${SONAR_TOKEN}:" \
  "http://localhost:9000/api/issues/search?componentKeys=tfm-demo-default&facets=severities&ps=1" \
  > results/default-severities.json

curl -u "${SONAR_TOKEN}:" \
  "http://localhost:9000/api/issues/search?componentKeys=tfm-demo-default&facets=types&ps=1" \
  > results/default-types.json

# Métricas del profile personalizado
curl -u "${SONAR_TOKEN}:" \
  "http://localhost:9000/api/issues/search?componentKeys=tfm-demo-custom&facets=severities&ps=1" \
  > results/custom-severities.json

curl -u "${SONAR_TOKEN}:" \
  "http://localhost:9000/api/issues/search?componentKeys=tfm-demo-custom&facets=types&ps=1" \
  > results/custom-types.json
```

### PASO 9: Analizar Resultados

```bash
# Si tienes jq instalado
echo "=== Profile Por Defecto ==="
echo "Total Issues: $(jq -r '.paging.total' results/default-severities.json)"
echo "BLOCKER: $(jq -r '.facets[0].values[] | select(.val=="BLOCKER") | .count' results/default-severities.json 2>/dev/null || echo "0")"

echo ""
echo "=== Profile Personalizado ==="
echo "Total Issues: $(jq -r '.paging.total' results/custom-severities.json)"
echo "BLOCKER: $(jq -r '.facets[0].values[] | select(.val=="BLOCKER") | .count' results/custom-severities.json 2>/dev/null || echo "0")"
```

---

## Interpretación de Resultados

### Dashboards de SonarQube

Cuando accedas a los dashboards, presta atención a:

#### 1. **Overview Tab**
- **Bugs**: Errores en el código
- **Vulnerabilities**: Problemas de seguridad
- **Security Hotspots**: Código sensible a revisar
- **Code Smells**: Deuda técnica

#### 2. **Issues Tab**
- Filtrar por **Severity**:
  - BLOCKER (rojo): Bloquea deployment
  - CRITICAL (naranja): Alta prioridad
  - MAJOR (amarillo): Debe ser corregido
  
- Filtrar por **Type**:
  - VULNERABILITY: Problema de seguridad
  - BUG: Error de lógica
  - CODE_SMELL: Calidad del código

#### 3. Comparación Esperada

**Profile Por Defecto (Sonar way)**:
- Total Issues: ~15-20
- BLOCKER: 2-3
- Vulnerabilities: 4-6

**Profile Personalizado (OWASP-ISO25010)**:
- Total Issues: ~25-35
- BLOCKER: 8-12 ⬆️ **(Valor Agregado)**
- Vulnerabilities: 12-18 ⬆️ **(Valor Agregado)**

### Issues Específicos a Buscar

En el **profile personalizado** deberías ver detectados:

| Archivo | Issue | Regla SonarQube | Severidad |
|---------|-------|-----------------|-----------|
| HardcodedCredentials.java | Hard-coded credentials | S2068 | BLOCKER |
| WeakCryptography.java | DES is insecure | S2278 | BLOCKER |
| WeakCryptography.java | ECB mode is insecure | S5542 | BLOCKER |
| InjectionVulnerabilities.java | SQL injection | S3649 | BLOCKER |
| InjectionVulnerabilities.java | Command injection | S2076 | BLOCKER |
| InsecureSSL.java | SSL verification disabled | S4830 | BLOCKER |
| PathTraversal.java | Path traversal | S5131 | BLOCKER |
| InsecureDeserialization.java | Unsafe deserialization | S5301 | BLOCKER |
| WeakRandomness.java | Weak PRNG | S2245 | CRITICAL |

### Valor Agregado Demostrado

Si el profile personalizado detecta **8+ issues BLOCKER** mientras que el default detecta **2-3**, eso es un:

```
Mejora = ((8 - 2) / 2) × 100 = 300% más detección
```

**Para el TFM**: Este es el dato clave que demuestra el valor agregado.

---

## Troubleshooting

### Problema: SonarQube no inicia

**Síntoma**: 
```bash
docker-compose logs sonarqube
# Error: "max virtual memory areas vm.max_map_count [65530] is too low"
```

**Solución**:
```bash
# macOS (Docker Desktop settings)
# Docker Desktop → Preferences → Resources → Advanced
# Aumentar Memory a 4GB o más

# Linux
sudo sysctl -w vm.max_map_count=262144
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf
```

### Problema: Puerto 9000 ocupado

**Síntoma**:
```
Error: port is already allocated
```

**Solución**:
```bash
# Opción 1: Detener proceso existente
lsof -ti:9000 | xargs kill -9

# Opción 2: Cambiar puerto en docker-compose.yml
# Editar línea ports: - "9001:9000"
```

### Problema: Maven no encuentra SonarQube

**Síntoma**:
```
Error: Connection refused to http://localhost:9000
```

**Solución**:
```bash
# Verificar que SonarQube esté UP
curl http://localhost:9000/api/system/status

# Esperar más tiempo (a veces tarda 2-3 minutos en primera inicialización)
```

### Problema: Token no funciona

**Síntoma**:
```
Error: Not authorized. Please check the user token
```

**Solución**:
```bash
# Regenerar token
curl -u admin:admin -X POST \
  "http://localhost:9000/api/user_tokens/generate?name=tfm-demo-new"

# Actualizar variable
export SONAR_TOKEN="nuevo_token_aqui"
```

### Problema: Profile no importa

**Síntoma**:
```
Error importing profile
```

**Solución**:
```bash
# Verificar que el archivo XML existe
ls -lh quality-profiles/OWASP-ISO25010-SecurityProfile.xml

# Verificar sintaxis XML
xmllint --noout quality-profiles/OWASP-ISO25010-SecurityProfile.xml

# Importar manualmente via UI
# Quality Profiles → Restore (botón arriba derecha) → Seleccionar archivo XML
```

### Problema: Análisis Maven falla

**Síntoma**:
```
BUILD FAILURE
Compilation error
```

**Solución**:
```bash
# Limpiar proyecto
cd vulnerable-app
mvn clean

# Verificar versión de Java
java -version
# Debe ser Java 11+

# Compilar sin tests primero
mvn compile -DskipTests

# Luego ejecutar análisis
mvn sonar:sonar ...
```

---

## Comandos Útiles

### Ver logs en tiempo real
```bash
cd docker
docker-compose logs -f sonarqube
```

### Reiniciar SonarQube
```bash
docker-compose restart sonarqube
```

### Detener todo
```bash
docker-compose down
```

### Detener y limpiar volúmenes (reset completo)
```bash
docker-compose down -v
```

### Ver consumo de recursos
```bash
docker stats
```

### Backup de BD PostgreSQL
```bash
docker exec tfm-sonarqube-db pg_dump -U sonar sonarqube > backup.sql
```

---

## Siguiente Paso: Documentar para TFM

Una vez completado el demo, tienes evidencia para el TFM:

### Capturas de Pantalla Necesarias

1. ✅ Dashboard del proyecto con profile default
2. ✅ Dashboard del proyecto con profile custom
3. ✅ Comparación lado a lado de issues BLOCKER
4. ✅ Ejemplo de issue específico (e.g., S2068 credenciales hardcodeadas)
5. ✅ Quality Profile configuration mostrando 70 reglas activas

### Datos para Tablas del TFM

```bash
# Generar tabla comparativa
cat results/comparison-report.md
```

Incluir en tu documento:
- Tabla de métricas comparativas
- Gráfico de distribución de severidades
- Lista de vulnerabilidades detectadas por categoría OWASP

### Citas y Referencias

- SonarQube Community Edition 10.4
- OWASP Top 10 2021
- ISO/IEC 25010:2011
- Proyecto vulnerable intencional (código ético para investigación)

---

## ¿Necesitas Ayuda?

### Verificar Estado General

```bash
cd /Users/ariel/PersonalDevs/sast-architecture/tfm-demo

# Check 1: Contenedores corriendo
docker-compose -f docker/docker-compose.yml ps

# Check 2: SonarQube responde
curl -s http://localhost:9000/api/system/status | jq

# Check 3: Proyecto compila
cd vulnerable-app && mvn compile && cd ..

# Check 4: Profile importado
curl -s -u admin:admin \
  "http://localhost:9000/api/qualityprofiles/search?language=java" | \
  jq '.profiles[] | select(.name | contains("OWASP"))'
```

Si todos los checks pasan, estás listo para ejecutar el demo! 🚀

---

**Tiempo Total Estimado**: 15-20 minutos  
**Dificultad**: ⭐⭐☆☆☆ (Principiante-Intermedio)  
**Resultado**: Evidencia cuantificable de valor agregado para TFM
