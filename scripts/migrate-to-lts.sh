#!/bin/bash

##############################################################################
# Script de Migración a SonarQube LTS
# Re-ejecuta análisis con mismos quality profiles
##############################################################################

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

SONAR_URL="http://localhost:9000"

echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║     Migración a SonarQube LTS - TFM Demo             ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""

##############################################################################
# PASO 1: Verificar SonarQube LTS
##############################################################################
echo -e "${YELLOW}═══ PASO 1: Verificando SonarQube LTS ═══${NC}"
echo ""

RESPONSE=$(curl -s "${SONAR_URL}/api/system/status")
if echo "$RESPONSE" | grep -q '"status":"UP"'; then
    VERSION=$(echo "$RESPONSE" | grep -o '"version":"[^"]*"' | cut -d'"' -f4)
    echo -e "${GREEN}✓ SonarQube está operativo${NC}"
    echo -e "${GREEN}✓ Versión: ${VERSION}${NC}"
    echo -e "${GREEN}✓ URL: ${SONAR_URL}${NC}"
else
    echo -e "${YELLOW}Error: SonarQube no está disponible${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}Por favor, completa los siguientes pasos:${NC}"
echo ""
echo -e "${YELLOW}1. Accede a ${SONAR_URL} en tu navegador${NC}"
echo -e "${YELLOW}2. Login con usuario: admin / password: admin${NC}"
echo -e "${YELLOW}3. Si te pide cambiar contraseña, usa: Admin123!${NC}"
echo -e "${YELLOW}4. Ve a: My Account → Security → Generate Token${NC}"
echo -e "${YELLOW}5. Nombre del token: tfm-demo-lts${NC}"
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
echo -e "${YELLOW}═══ PASO 2: Importando Quality Profile OWASP-ISO25010 ═══${NC}"
echo ""

PROFILE_XML="../quality-profiles/OWASP-ISO25010-SecurityProfile.xml"

echo "Importando profile personalizado..."
RESTORE_RESPONSE=$(curl -s -u "${SONAR_TOKEN}:" -X POST \
    -F "backup=@${PROFILE_XML}" \
    "${SONAR_URL}/api/qualityprofiles/restore")

if echo "${RESTORE_RESPONSE}" | grep -q "error"; then
    echo -e "${YELLOW}⚠ Profile ya existe o error:${NC}"
    echo "${RESTORE_RESPONSE}"
else
    echo -e "${GREEN}✓ Profile 'OWASP-ISO25010-Security' importado${NC}"
fi

echo ""
read -p "Presiona ENTER para continuar con compilación..."

##############################################################################
# PASO 3: Compilar proyecto
##############################################################################
echo ""
echo -e "${YELLOW}═══ PASO 3: Compilando proyecto (13 archivos Java) ═══${NC}"
echo ""

cd ../vulnerable-app

echo "Ejecutando: mvn clean compile test..."
mvn clean compile test -q

echo -e "${GREEN}✓ Proyecto compilado (13 archivos)${NC}"
echo ""
read -p "Presiona ENTER para iniciar análisis con profile DEFAULT..."

##############################################################################
# PASO 4: Análisis con Profile DEFAULT
##############################################################################
echo ""
echo -e "${YELLOW}═══ PASO 4: Análisis con 'Sonar way' (DEFAULT) ═══${NC}"
echo ""

source ../results/.env

mvn sonar:sonar \
    -Dsonar.projectKey=tfm-demo-default-lts \
    -Dsonar.projectName="TFM Demo LTS - Default Profile" \
    -Dsonar.qualityprofile="Sonar way" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo -e "${GREEN}✓ Análisis DEFAULT completado${NC}"
echo -e "${CYAN}Dashboard: ${SONAR_URL}/dashboard?id=tfm-demo-default-lts${NC}"
echo ""
read -p "Presiona ENTER para iniciar análisis con profile CUSTOM..."

##############################################################################
# PASO 5: Asignar y Analizar con Profile CUSTOM
##############################################################################
echo ""
echo -e "${YELLOW}═══ PASO 5: Análisis con 'OWASP-ISO25010' (CUSTOM) ═══${NC}"
echo ""

# Crear proyecto y asignar profile
curl -s -u "${SONAR_TOKEN}:" -X POST \
    "${SONAR_URL}/api/projects/create?project=tfm-demo-custom-lts&name=TFM+Demo+LTS+-+OWASP-ISO25010+Profile" \
    > /dev/null 2>&1

sleep 2

# Asignar quality profile
curl -s -u "${SONAR_TOKEN}:" -X POST \
    "${SONAR_URL}/api/qualityprofiles/add_project?project=tfm-demo-custom-lts&qualityProfile=OWASP-ISO25010-Security&language=java" \
    > /dev/null 2>&1

echo "Profile OWASP-ISO25010-Security asignado al proyecto"
echo ""

# Ejecutar análisis
mvn sonar:sonar \
    -Dsonar.projectKey=tfm-demo-custom-lts \
    -Dsonar.projectName="TFM Demo LTS - OWASP-ISO25010 Profile" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo -e "${GREEN}✓ Análisis CUSTOM completado${NC}"
echo -e "${CYAN}Dashboard: ${SONAR_URL}/dashboard?id=tfm-demo-custom-lts${NC}"
echo ""

##############################################################################
# PASO 6: Extraer métricas y comparar
##############################################################################
echo -e "${YELLOW}═══ PASO 6: Extrayendo métricas y generando reporte ═══${NC}"
echo ""

cd ../results

echo "Esperando procesamiento de SonarQube (10 segundos)..."
sleep 10

# Extraer métricas
curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/measures/component?component=tfm-demo-default-lts&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots,ncloc,sqale_index,reliability_rating,security_rating" \
    > default-measures-lts.json

curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/measures/component?component=tfm-demo-custom-lts&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots,ncloc,sqale_index,reliability_rating,security_rating" \
    > custom-measures-lts.json

echo -e "${GREEN}✓ Métricas extraídas${NC}"
echo ""

# Mostrar comparación
if command -v jq &> /dev/null; then
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}           RESULTADOS EN SONARQUBE LTS              ${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo ""
    
    echo "DEFAULT (Sonar way):"
    jq -r '.component.measures[] | "\(.metric): \(.value)"' default-measures-lts.json | column -t
    
    echo ""
    echo "CUSTOM (OWASP-ISO25010-Security):"
    jq -r '.component.measures[] | "\(.metric): \(.value)"' custom-measures-lts.json | column -t
    
    echo ""
else
    echo -e "${YELLOW}Instala jq para ver estadísticas: brew install jq${NC}"
    echo "Ver archivos:"
    echo "  - default-measures-lts.json"
    echo "  - custom-measures-lts.json"
fi

echo ""
echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"
echo -e "${CYAN}          MIGRACIÓN A LTS COMPLETADA                ${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}Dashboards para revisar:${NC}"
echo "  Default: ${SONAR_URL}/dashboard?id=tfm-demo-default-lts"
echo "  Custom:  ${SONAR_URL}/dashboard?id=tfm-demo-custom-lts"
echo ""
echo -e "${YELLOW}Archivos generados:${NC}"
echo "  $(pwd)/default-measures-lts.json"
echo "  $(pwd)/custom-measures-lts.json"
echo ""
