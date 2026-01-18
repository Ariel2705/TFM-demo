#!/bin/bash

##############################################################################
# Script Simplificado - Demo TFM
# Ejecutar paso a paso con pausas para verificar
##############################################################################

set -e

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

SONAR_URL="http://localhost:9000"

echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║     Demo TFM - Análisis SAST SonarQube               ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""

##############################################################################
# PASO 1: Verificar que SonarQube esté corriendo
##############################################################################
echo -e "${YELLOW}═══ PASO 1: Verificando SonarQube ═══${NC}"
echo ""

if curl -s "${SONAR_URL}/api/system/status" | grep -q '"status":"UP"'; then
    echo -e "${GREEN}✓ SonarQube está operativo en ${SONAR_URL}${NC}"
else
    echo -e "${YELLOW}SonarQube no está corriendo. Levantando contenedores...${NC}"
    cd ../docker
    docker-compose up -d
    echo "Esperando 60 segundos para inicialización..."
    sleep 60
    cd ../scripts
fi

echo ""
echo -e "${BLUE}Por favor, completa los siguientes pasos manualmente:${NC}"
echo ""
echo -e "${YELLOW}1. Accede a ${SONAR_URL} en tu navegador${NC}"
echo -e "${YELLOW}2. Login con usuario: admin / password: admin${NC}"
echo -e "${YELLOW}3. Si te pide cambiar contraseña, usa: Admin123!${NC}"
echo -e "${YELLOW}4. Ve a: My Account → Security → Generate Token${NC}"
echo -e "${YELLOW}5. Nombre del token: tfm-demo${NC}"
echo -e "${YELLOW}6. Copia el token generado${NC}"
echo ""
read -p "Presiona ENTER cuando tengas el token copiado..."

echo ""
read -p "Pega el token aquí: " SONAR_TOKEN
echo ""

if [ -z "${SONAR_TOKEN}" ]; then
    echo -e "${YELLOW}No se ingresó token. Saliendo...${NC}"
    exit 1
fi

# Guardar token
mkdir -p ../results
echo "SONAR_TOKEN=${SONAR_TOKEN}" > ../results/.env
echo -e "${GREEN}✓ Token guardado${NC}"
echo ""

##############################################################################
# PASO 2: Importar Quality Profile
##############################################################################
echo -e "${YELLOW}═══ PASO 2: Importando Quality Profile ═══${NC}"
echo ""

PROFILE_XML="../quality-profiles/OWASP-ISO25010-SecurityProfile.xml"

if [ ! -f "${PROFILE_XML}" ]; then
    echo -e "${YELLOW}Error: Archivo de profile no encontrado${NC}"
    exit 1
fi

echo "Importando profile personalizado..."
RESTORE_RESPONSE=$(curl -s -u "${SONAR_TOKEN}:" -X POST \
    -F "backup=@${PROFILE_XML}" \
    "${SONAR_URL}/api/qualityprofiles/restore")

if echo "${RESTORE_RESPONSE}" | grep -q "error"; then
    echo -e "${YELLOW}⚠ Error o profile ya existe:${NC}"
    echo "${RESTORE_RESPONSE}"
else
    echo -e "${GREEN}✓ Profile 'OWASP-ISO25010-Security' importado${NC}"
fi

echo ""
read -p "Presiona ENTER para continuar con la compilación..."

##############################################################################
# PASO 3: Compilar proyecto
##############################################################################
echo ""
echo -e "${YELLOW}═══ PASO 3: Compilando proyecto vulnerable ═══${NC}"
echo ""

cd ../vulnerable-app

echo "Ejecutando: mvn clean compile test..."
mvn clean compile test -q

echo -e "${GREEN}✓ Proyecto compilado${NC}"
echo ""
read -p "Presiona ENTER para iniciar análisis con profile por defecto..."

##############################################################################
# PASO 4: Análisis con Profile Default
##############################################################################
echo ""
echo -e "${YELLOW}═══ PASO 4: Análisis con 'Sonar way' (default) ═══${NC}"
echo ""

mvn sonar:sonar \
    -Dsonar.projectKey=tfm-demo-default \
    -Dsonar.projectName="TFM Demo - Default Profile" \
    -Dsonar.qualityprofile="Sonar way" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo -e "${GREEN}✓ Análisis completado${NC}"
echo -e "${CYAN}Dashboard: ${SONAR_URL}/dashboard?id=tfm-demo-default${NC}"
echo ""
read -p "Presiona ENTER para iniciar análisis con profile personalizado..."

##############################################################################
# PASO 5: Análisis con Profile Custom
##############################################################################
echo ""
echo -e "${YELLOW}═══ PASO 5: Análisis con 'OWASP-ISO25010' (custom) ═══${NC}"
echo ""

mvn sonar:sonar \
    -Dsonar.projectKey=tfm-demo-custom \
    -Dsonar.projectName="TFM Demo - OWASP-ISO25010 Profile" \
    -Dsonar.qualityprofile="OWASP-ISO25010-Security" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo -e "${GREEN}✓ Análisis completado${NC}"
echo -e "${CYAN}Dashboard: ${SONAR_URL}/dashboard?id=tfm-demo-custom${NC}"
echo ""

##############################################################################
# PASO 6: Comparar resultados
##############################################################################
echo -e "${YELLOW}═══ PASO 6: Comparación de Resultados ═══${NC}"
echo ""

cd ../results

echo "Extrayendo métricas..."

# Default
curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/issues/search?componentKeys=tfm-demo-default&facets=severities&ps=1" \
    > default-severities.json

curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/issues/search?componentKeys=tfm-demo-default&facets=types&ps=1" \
    > default-types.json

# Custom
curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/issues/search?componentKeys=tfm-demo-custom&facets=severities&ps=1" \
    > custom-severities.json

curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/issues/search?componentKeys=tfm-demo-custom&facets=types&ps=1" \
    > custom-types.json

echo -e "${GREEN}✓ Métricas extraídas${NC}"
echo ""

# Mostrar resultados si jq está disponible
if command -v jq &> /dev/null; then
    DEFAULT_TOTAL=$(jq -r '.paging.total' default-severities.json)
    DEFAULT_BLOCKER=$(jq -r '.facets[0].values[] | select(.val=="BLOCKER") | .count' default-severities.json 2>/dev/null || echo "0")
    DEFAULT_VULN=$(jq -r '.facets[0].values[] | select(.val=="VULNERABILITY") | .count' default-types.json 2>/dev/null || echo "0")
    
    CUSTOM_TOTAL=$(jq -r '.paging.total' custom-severities.json)
    CUSTOM_BLOCKER=$(jq -r '.facets[0].values[] | select(.val=="BLOCKER") | .count' custom-severities.json 2>/dev/null || echo "0")
    CUSTOM_VULN=$(jq -r '.facets[0].values[] | select(.val=="VULNERABILITY") | .count' custom-types.json 2>/dev/null || echo "0")
    
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}           RESULTADOS COMPARATIVOS                 ${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo ""
    printf "%-30s %15s %15s\n" "Métrica" "Default" "Custom"
    printf "%-30s %15s %15s\n" "---" "---" "---"
    printf "%-30s %15s %15s\n" "Total Issues" "$DEFAULT_TOTAL" "$CUSTOM_TOTAL"
    printf "%-30s %15s %15s\n" "BLOCKER" "$DEFAULT_BLOCKER" "$CUSTOM_BLOCKER"
    printf "%-30s %15s %15s\n" "Vulnerabilidades" "$DEFAULT_VULN" "$CUSTOM_VULN"
    echo ""
    
    BLOCKER_DIFF=$((CUSTOM_BLOCKER - DEFAULT_BLOCKER))
    VULN_DIFF=$((CUSTOM_VULN - DEFAULT_VULN))
    
    echo -e "${GREEN}═══ VALOR AGREGADO ═══${NC}"
    echo -e "${GREEN}✓ ${BLOCKER_DIFF} issues BLOCKER adicionales${NC}"
    echo -e "${GREEN}✓ ${VULN_DIFF} vulnerabilidades adicionales${NC}"
    
    if [ $DEFAULT_VULN -gt 0 ]; then
        MEJORA=$((VULN_DIFF * 100 / DEFAULT_VULN))
        echo -e "${GREEN}✓ Mejora del ${MEJORA}% en detección${NC}"
    fi
else
    echo -e "${YELLOW}Instala jq para ver estadísticas: brew install jq${NC}"
fi

echo ""
echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"
echo -e "${CYAN}                DEMO COMPLETADO                     ${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}Dashboards para revisar:${NC}"
echo "  Default: ${SONAR_URL}/dashboard?id=tfm-demo-default"
echo "  Custom:  ${SONAR_URL}/dashboard?id=tfm-demo-custom"
echo ""
echo -e "${YELLOW}Archivos generados:${NC}"
echo "  $(pwd)/default-severities.json"
echo "  $(pwd)/custom-severities.json"
echo ""
