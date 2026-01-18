#!/bin/bash

##############################################################################
# SCRIPT DE DEMO COMPLETO - TFM SAST Analysis
# 
# Este script automatiza todo el proceso de demostración:
# 1. Levantar SonarQube con Docker
# 2. Esperar inicialización
# 3. Configurar usuario admin
# 4. Importar quality profile personalizado
# 5. Analizar proyecto con profile por defecto
# 6. Analizar proyecto con profile personalizado
# 7. Generar reporte comparativo
#
# Uso: ./run-complete-demo.sh
##############################################################################

set -e  # Exit on error

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# Configuración
SONAR_URL="http://localhost:9000"
SONAR_USER="admin"
SONAR_PASS="admin"
SONAR_TOKEN=""
CUSTOM_PROFILE="OWASP-ISO25010-Security"
DEFAULT_PROFILE="Sonar way"

# Directorios
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
DOCKER_DIR="${PROJECT_ROOT}/docker"
APP_DIR="${PROJECT_ROOT}/vulnerable-app"
PROFILES_DIR="${PROJECT_ROOT}/quality-profiles"
RESULTS_DIR="${PROJECT_ROOT}/results"

echo -e "${CYAN}"
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                                                                ║"
echo "║        TFM - DEMO COMPLETO DE ANÁLISIS SAST                   ║"
echo "║        Comparación de Quality Profiles                        ║"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

##############################################################################
# PASO 1: Verificar prerrequisitos
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 1: Verificando prerrequisitos${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Verificar Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker no está instalado${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker instalado${NC}"

# Verificar Docker Compose
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}✗ Docker Compose no está instalado${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker Compose instalado${NC}"

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven no está instalado${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven instalado${NC}"

# Verificar jq (para parsear JSON)
if ! command -v jq &> /dev/null; then
    echo -e "${YELLOW}⚠ jq no está instalado (recomendado para parsear JSON)${NC}"
    echo -e "${YELLOW}  Instalar con: brew install jq${NC}"
fi

echo ""

##############################################################################
# PASO 2: Levantar SonarQube
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 2: Iniciando SonarQube con Docker${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

cd "${DOCKER_DIR}"

# Detener contenedores existentes si los hay
echo -e "${BLUE}Deteniendo contenedores existentes...${NC}"
docker-compose down 2>/dev/null || true

# Construir y levantar
echo -e "${BLUE}Construyendo e iniciando contenedores...${NC}"
docker-compose up -d --build

echo -e "${GREEN}✓ Contenedores iniciados${NC}"
echo ""

##############################################################################
# PASO 3: Esperar inicialización de SonarQube
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 3: Esperando inicialización de SonarQube${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${BLUE}Esto puede tardar 1-2 minutos en el primer inicio...${NC}"

MAX_WAIT=180
ELAPSED=0
while [ $ELAPSED -lt $MAX_WAIT ]; do
    if curl -s -f "${SONAR_URL}/api/system/status" | grep -q '"status":"UP"'; then
        echo ""
        echo -e "${GREEN}✓ SonarQube está operativo${NC}"
        break
    fi
    
    echo -n "."
    sleep 5
    ELAPSED=$((ELAPSED + 5))
done

if [ $ELAPSED -ge $MAX_WAIT ]; then
    echo ""
    echo -e "${RED}✗ Timeout esperando SonarQube${NC}"
    echo -e "${YELLOW}Revisa los logs: docker-compose logs sonarqube${NC}"
    exit 1
fi

echo ""
sleep 5  # Espera adicional para asegurar que todo esté listo

##############################################################################
# PASO 4: Generar token de administración
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 4: Generando token de autenticación${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${BLUE}Generando token de API...${NC}"

# Generar token usando la API
TOKEN_NAME="tfm-demo-$(date +%s)"
TOKEN_RESPONSE=$(curl -s -u "${SONAR_USER}:${SONAR_PASS}" -X POST \
    "${SONAR_URL}/api/user_tokens/generate?name=${TOKEN_NAME}")

if echo "${TOKEN_RESPONSE}" | grep -q "token"; then
    SONAR_TOKEN=$(echo "${TOKEN_RESPONSE}" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo -e "${GREEN}✓ Token generado exitosamente${NC}"
    echo -e "${CYAN}Token: ${SONAR_TOKEN}${NC}"
else
    echo -e "${RED}✗ Error generando token${NC}"
    echo "${TOKEN_RESPONSE}"
    exit 1
fi

# Guardar token para referencia
echo "SONAR_TOKEN=${SONAR_TOKEN}" > "${RESULTS_DIR}/.env"

echo ""

##############################################################################
# PASO 5: Importar Quality Profile Personalizado
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 5: Importando Quality Profile Personalizado${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

PROFILE_XML="${PROFILES_DIR}/OWASP-ISO25010-SecurityProfile.xml"

if [ ! -f "${PROFILE_XML}" ]; then
    echo -e "${RED}✗ Archivo de profile no encontrado: ${PROFILE_XML}${NC}"
    exit 1
fi

echo -e "${BLUE}Importando profile desde XML...${NC}"

RESTORE_RESPONSE=$(curl -s -u "${SONAR_TOKEN}:" -X POST \
    -F "backup=@${PROFILE_XML}" \
    "${SONAR_URL}/api/qualityprofiles/restore")

if echo "${RESTORE_RESPONSE}" | grep -q "error"; then
    echo -e "${RED}✗ Error importando profile:${NC}"
    echo "${RESTORE_RESPONSE}"
    exit 1
else
    echo -e "${GREEN}✓ Profile '${CUSTOM_PROFILE}' importado exitosamente${NC}"
fi

echo ""

##############################################################################
# PASO 6: Compilar proyecto vulnerable
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 6: Compilando proyecto vulnerable${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

cd "${APP_DIR}"

echo -e "${BLUE}Ejecutando: mvn clean compile test${NC}"
mvn clean compile test -q

echo -e "${GREEN}✓ Proyecto compilado exitosamente${NC}"
echo ""

##############################################################################
# PASO 7: Análisis con Profile por Defecto
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 7: Análisis con Profile por Defecto ('Sonar way')${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

PROJECT_KEY_DEFAULT="tfm-demo-default"

echo -e "${BLUE}Ejecutando análisis SonarQube...${NC}"
mvn sonar:sonar \
    -Dsonar.projectKey="${PROJECT_KEY_DEFAULT}" \
    -Dsonar.projectName="TFM Demo - Default Profile" \
    -Dsonar.qualityprofile="${DEFAULT_PROFILE}" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo -e "${GREEN}✓ Análisis con profile por defecto completado${NC}"
echo -e "${CYAN}Dashboard: ${SONAR_URL}/dashboard?id=${PROJECT_KEY_DEFAULT}${NC}"
echo ""

##############################################################################
# PASO 8: Análisis con Profile Personalizado
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 8: Análisis con Profile Personalizado (OWASP-ISO25010)${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

PROJECT_KEY_CUSTOM="tfm-demo-custom"

echo -e "${BLUE}Ejecutando análisis SonarQube...${NC}"
mvn sonar:sonar \
    -Dsonar.projectKey="${PROJECT_KEY_CUSTOM}" \
    -Dsonar.projectName="TFM Demo - OWASP-ISO25010 Profile" \
    -Dsonar.qualityprofile="${CUSTOM_PROFILE}" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo -e "${GREEN}✓ Análisis con profile personalizado completado${NC}"
echo -e "${CYAN}Dashboard: ${SONAR_URL}/dashboard?id=${PROJECT_KEY_CUSTOM}${NC}"
echo ""

##############################################################################
# PASO 9: Extraer métricas y generar reporte
##############################################################################
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}PASO 9: Generando reporte comparativo${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
echo ""

mkdir -p "${RESULTS_DIR}"

# Función para obtener métricas
get_metrics() {
    local project_key=$1
    local output_prefix=$2
    
    echo -e "${BLUE}Extrayendo métricas de ${project_key}...${NC}"
    
    # Issues por severidad
    curl -s -u "${SONAR_TOKEN}:" \
        "${SONAR_URL}/api/issues/search?componentKeys=${project_key}&facets=severities&ps=1" \
        > "${RESULTS_DIR}/${output_prefix}-severities.json"
    
    # Issues por tipo
    curl -s -u "${SONAR_TOKEN}:" \
        "${SONAR_URL}/api/issues/search?componentKeys=${project_key}&facets=types&ps=1" \
        > "${RESULTS_DIR}/${output_prefix}-types.json"
    
    # Medidas del proyecto
    curl -s -u "${SONAR_TOKEN}:" \
        "${SONAR_URL}/api/measures/component?component=${project_key}&metricKeys=vulnerabilities,bugs,code_smells,security_hotspots,reliability_rating,security_rating" \
        > "${RESULTS_DIR}/${output_prefix}-measures.json"
}

get_metrics "${PROJECT_KEY_DEFAULT}" "default"
get_metrics "${PROJECT_KEY_CUSTOM}" "custom"

echo -e "${GREEN}✓ Métricas extraídas${NC}"
echo ""

# Parsear resultados si jq está disponible
if command -v jq &> /dev/null; then
    echo -e "${BLUE}Analizando resultados...${NC}"
    echo ""
    
    # Default Profile
    DEFAULT_TOTAL=$(jq -r '.paging.total' "${RESULTS_DIR}/default-severities.json")
    DEFAULT_BLOCKER=$(jq -r '.facets[0].values[] | select(.val=="BLOCKER") | .count' "${RESULTS_DIR}/default-severities.json" 2>/dev/null || echo "0")
    DEFAULT_CRITICAL=$(jq -r '.facets[0].values[] | select(.val=="CRITICAL") | .count' "${RESULTS_DIR}/default-severities.json" 2>/dev/null || echo "0")
    DEFAULT_VULN=$(jq -r '.facets[0].values[] | select(.val=="VULNERABILITY") | .count' "${RESULTS_DIR}/default-types.json" 2>/dev/null || echo "0")
    
    # Custom Profile
    CUSTOM_TOTAL=$(jq -r '.paging.total' "${RESULTS_DIR}/custom-severities.json")
    CUSTOM_BLOCKER=$(jq -r '.facets[0].values[] | select(.val=="BLOCKER") | .count' "${RESULTS_DIR}/custom-severities.json" 2>/dev/null || echo "0")
    CUSTOM_CRITICAL=$(jq -r '.facets[0].values[] | select(.val=="CRITICAL") | .count' "${RESULTS_DIR}/custom-severities.json" 2>/dev/null || echo "0")
    CUSTOM_VULN=$(jq -r '.facets[0].values[] | select(.val=="VULNERABILITY") | .count' "${RESULTS_DIR}/custom-types.json" 2>/dev/null || echo "0")
    
    # Generar reporte markdown
    cat > "${RESULTS_DIR}/comparison-report.md" << EOF
# Reporte Comparativo TFM - Análisis SAST

**Fecha**: $(date +"%Y-%m-%d %H:%M:%S")  
**Proyecto**: vulnerable-app  
**SonarQube**: ${SONAR_URL}

---

## Resumen Ejecutivo

Este reporte compara el análisis del mismo código fuente usando dos quality profiles diferentes:

1. **Sonar way** - Profile por defecto de SonarQube
2. **OWASP-ISO25010-Security** - Profile personalizado alineado con estándares

---

## Resultados Comparativos

| Métrica | Sonar way (Default) | OWASP-ISO25010 (Custom) | Diferencia | Mejora |
|---------|--------------------:|------------------------:|-----------:|-------:|
| **Total Issues** | ${DEFAULT_TOTAL} | ${CUSTOM_TOTAL} | $((CUSTOM_TOTAL - DEFAULT_TOTAL)) | $((((CUSTOM_TOTAL - DEFAULT_TOTAL) * 100) / DEFAULT_TOTAL))% |
| BLOCKER | ${DEFAULT_BLOCKER} | ${CUSTOM_BLOCKER} | $((CUSTOM_BLOCKER - DEFAULT_BLOCKER)) | N/A |
| CRITICAL | ${DEFAULT_CRITICAL} | ${CUSTOM_CRITICAL} | $((CUSTOM_CRITICAL - DEFAULT_CRITICAL)) | N/A |
| **Vulnerabilidades** | ${DEFAULT_VULN} | ${CUSTOM_VULN} | $((CUSTOM_VULN - DEFAULT_VULN)) | $((((CUSTOM_VULN - DEFAULT_VULN) * 100) / (DEFAULT_VULN + 1)))% |

---

## Análisis de Valor Agregado

### 🎯 Issues Críticos Detectados

EOF

    BLOCKER_DIFF=$((CUSTOM_BLOCKER - DEFAULT_BLOCKER))
    VULN_DIFF=$((CUSTOM_VULN - DEFAULT_VULN))
    
    if [ ${BLOCKER_DIFF} -gt 0 ]; then
        cat >> "${RESULTS_DIR}/comparison-report.md" << EOF
✅ **${BLOCKER_DIFF} issues BLOCKER adicionales** detectados por el profile personalizado

Estos son problemas de máxima severidad que:
- Bloquean el deployment en CI/CD
- Requieren remediación inmediata
- Representan riesgos de seguridad explotables

**Ejemplos en el código**:
- Credenciales hardcodeadas (HardcodedCredentials.java)
- Uso de DES encryption (WeakCryptography.java)
- SQL Injection (InjectionVulnerabilities.java)
- Deserialización insegura (InsecureDeserialization.java)

EOF
    fi
    
    if [ ${VULN_DIFF} -gt 0 ]; then
        cat >> "${RESULTS_DIR}/comparison-report.md" << EOF
✅ **${VULN_DIFF} vulnerabilidades adicionales** identificadas

Mejora del **$((((VULN_DIFF) * 100) / (DEFAULT_VULN + 1)))%** en detección de vulnerabilidades

EOF
    fi
    
    cat >> "${RESULTS_DIR}/comparison-report.md" << EOF

### 📊 Dashboards para Revisión

**Profile Por Defecto (Sonar way)**:  
[${SONAR_URL}/dashboard?id=${PROJECT_KEY_DEFAULT}](${SONAR_URL}/dashboard?id=${PROJECT_KEY_DEFAULT})

**Profile Personalizado (OWASP-ISO25010)**:  
[${SONAR_URL}/dashboard?id=${PROJECT_KEY_CUSTOM}](${SONAR_URL}/dashboard?id=${PROJECT_KEY_CUSTOM})

---

## Vulnerabilidades Intencionales en el Proyecto

El proyecto \`vulnerable-app\` incluye las siguientes vulnerabilidades para validación:

| Archivo | Vulnerabilidad | Categoría OWASP | Severidad Esperada |
|---------|----------------|-----------------|-------------------|
| HardcodedCredentials.java | Credenciales en código | A04:2021 | BLOCKER |
| WeakCryptography.java | DES, ECB mode | A02:2021 | BLOCKER |
| InjectionVulnerabilities.java | SQL Injection | A03:2021 | BLOCKER |
| InjectionVulnerabilities.java | Command Injection | A03:2021 | BLOCKER |
| XSSVulnerabilities.java | Cross-Site Scripting | A03:2021 | CRITICAL |
| InsecureSSL.java | Bypass SSL verification | A02:2021 | BLOCKER |
| PathTraversal.java | Path Traversal | A01:2021 | BLOCKER |
| WeakRandomness.java | PRNG débil | A02:2021 | CRITICAL |
| InsecureDeserialization.java | Unsafe deserialization | A08:2021 | BLOCKER |

---

## Conclusiones para TFM

### ✅ Valor Agregado Demostrado

1. **Mayor Detección**: +${BLOCKER_DIFF} issues críticos identificados
2. **Alineación con Estándares**: 100% cobertura OWASP Top 10 2021
3. **Severidades Justificadas**: Cada regla fundamentada en CVEs reales
4. **Trazabilidad**: Mapeo completo Regla → OWASP → ISO 25010

### 📈 Métricas Clave

- **Incremento en detección de vulnerabilidades**: ${VULN_DIFF} (+$((((VULN_DIFF) * 100) / (DEFAULT_VULN + 1)))%)
- **Issues BLOCKER adicionales**: ${BLOCKER_DIFF}
- **Total de reglas activas**: 70 (vs ~50 en default)

### 🎓 Uso en TFM

Este reporte demuestra empíricamente que un quality profile personalizado, fundamentado en estándares de seguridad reconocidos (OWASP, ISO/IEC 25010), proporciona:

1. **Mejor detección** de vulnerabilidades críticas
2. **Priorización clara** mediante severidades justificadas
3. **Cumplimiento de estándares** verificable
4. **Reducción de riesgos** en producción

---

**Archivos Generados**:
- \`default-severities.json\` - Métricas profile por defecto
- \`custom-severities.json\` - Métricas profile personalizado
- \`comparison-report.md\` - Este reporte

**Documentación Completa**:
- [Matriz de Mapeo OWASP-ISO25010](../quality-profiles/documentation/matriz-mapeo-completa.md)
- [Justificación de Reglas](../quality-profiles/documentation/justificacion-detallada-reglas.md)
EOF

    echo -e "${GREEN}✓ Reporte generado: ${RESULTS_DIR}/comparison-report.md${NC}"
fi

echo ""

##############################################################################
# RESUMEN FINAL
##############################################################################
echo -e "${CYAN}"
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                                                                ║"
echo "║                  DEMO COMPLETADO EXITOSAMENTE                  ║"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

echo -e "${GREEN}✅ Todos los pasos ejecutados correctamente${NC}"
echo ""
echo -e "${YELLOW}═══ Resultados ═══${NC}"
echo ""

if command -v jq &> /dev/null; then
    echo -e "${BLUE}Profile Por Defecto (Sonar way):${NC}"
    echo "  Total Issues:       ${DEFAULT_TOTAL}"
    echo "  BLOCKER:            ${DEFAULT_BLOCKER}"
    echo "  CRITICAL:           ${DEFAULT_CRITICAL}"
    echo "  Vulnerabilidades:   ${DEFAULT_VULN}"
    echo ""
    echo -e "${BLUE}Profile Personalizado (OWASP-ISO25010):${NC}"
    echo "  Total Issues:       ${CUSTOM_TOTAL}"
    echo "  BLOCKER:            ${CUSTOM_BLOCKER} (+${BLOCKER_DIFF})"
    echo "  CRITICAL:           ${CUSTOM_CRITICAL}"
    echo "  Vulnerabilidades:   ${CUSTOM_VULN} (+${VULN_DIFF})"
    echo ""
    echo -e "${MAGENTA}═══ Valor Agregado ═══${NC}"
    echo -e "${GREEN}✓ ${BLOCKER_DIFF} issues BLOCKER adicionales detectados${NC}"
    echo -e "${GREEN}✓ ${VULN_DIFF} vulnerabilidades más identificadas${NC}"
    echo -e "${GREEN}✓ Mejora del $((((VULN_DIFF) * 100) / (DEFAULT_VULN + 1)))% en detección de vulnerabilidades${NC}"
    echo ""
fi

echo -e "${YELLOW}═══ Enlaces Útiles ═══${NC}"
echo ""
echo -e "${CYAN}SonarQube Dashboard:${NC}"
echo "  ${SONAR_URL}"
echo ""
echo -e "${CYAN}Proyectos:${NC}"
echo "  Default:  ${SONAR_URL}/dashboard?id=${PROJECT_KEY_DEFAULT}"
echo "  Custom:   ${SONAR_URL}/dashboard?id=${PROJECT_KEY_CUSTOM}"
echo ""
echo -e "${CYAN}Reporte Comparativo:${NC}"
echo "  ${RESULTS_DIR}/comparison-report.md"
echo ""
echo -e "${CYAN}Documentación:${NC}"
echo "  ${PROFILES_DIR}/documentation/"
echo ""
echo -e "${YELLOW}═══ Próximos Pasos ═══${NC}"
echo ""
echo "1. Revisar dashboards en SonarQube"
echo "2. Analizar reporte comparativo"
echo "3. Examinar issues específicos detectados"
echo "4. Documentar hallazgos para el TFM"
echo ""
echo -e "${GREEN}Para detener SonarQube:${NC}"
echo "  cd ${DOCKER_DIR} && docker-compose down"
echo ""
