#!/bin/bash
# Script para implementar mejoras al TFM Demo
# Ejecutar desde: tfm-demo/scripts/

set -e

echo "======================================================================"
echo "  MEJORA DEL TFM DEMO - Implementación Automática"
echo "======================================================================"
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Exportar quality profile actual
echo -e "${YELLOW}[1/6]${NC} Exportando quality profile actual..."
curl -s -u 'admin:Admin123!' \
  "http://localhost:9000/api/qualityprofiles/backup?language=java&qualityProfile=OWASP-ISO25010-Security" \
  > ../quality-profiles/OWASP-ISO25010-SecurityProfile-backup.xml
echo -e "${GREEN}✓${NC} Profile exportado"
echo ""

# 2. Contar reglas actuales
CURRENT_RULES=$(grep -c "<rule>" ../quality-profiles/OWASP-ISO25010-SecurityProfile-backup.xml || echo "0")
echo -e "${YELLOW}[2/6]${NC} Reglas actuales en el profile: ${CURRENT_RULES}"
echo ""

# 3. Generar reporte de reglas faltantes
echo -e "${YELLOW}[3/6]${NC} Generando lista de reglas recomendadas..."
python3 recommended-rules.py > ../docs/reglas-recomendadas.txt
echo -e "${GREEN}✓${NC} Lista generada en docs/reglas-recomendadas.txt"
echo ""

# 4. Crear estructura de documentación mejorada
echo -e "${YELLOW}[4/6]${NC} Creando estructura de documentación..."
mkdir -p ../docs/academic
mkdir -p ../docs/visualizations
mkdir -p ../docs/case-studies
echo -e "${GREEN}✓${NC} Directorios creados"
echo ""

# 5. Generar métricas detalladas por severidad
echo -e "${YELLOW}[5/6]${NC} Generando análisis de severidad..."
cd ../results
cat > analyze-severity.py << 'EOF'
#!/usr/bin/env python3
import json

# Leer métricas DEFAULT
with open('default-measures-lts-v2.json', 'r') as f:
    default_data = json.load(f)

# Leer métricas CUSTOM  
with open('custom-measures-lts-v2.json', 'r') as f:
    custom_data = json.load(f)

def get_metric(data, metric_key):
    for m in data['component']['measures']:
        if m['metric'] == metric_key:
            return m.get('value', '0')
    return '0'

print("=" * 80)
print("ANÁLISIS DETALLADO POR SEVERIDAD")
print("=" * 80)
print()
print(f"{'Métrica':<30} {'DEFAULT':>15} {'CUSTOM':>15} {'Mejora':>15}")
print("-" * 80)

metrics = [
    ('Bugs', 'bugs'),
    ('Vulnerabilities', 'vulnerabilities'),
    ('Code Smells', 'code_smells'),
    ('Security Hotspots', 'security_hotspots'),
    ('Technical Debt (min)', 'sqale_index'),
    ('Reliability Rating', 'reliability_rating'),
]

for name, key in metrics:
    default_val = get_metric(default_data, key)
    custom_val = get_metric(custom_data, key)
    
    try:
        if key == 'reliability_rating':
            diff = float(custom_val) - float(default_val)
            improvement = f"{diff:+.1f}"
        else:
            diff = int(custom_val) - int(default_val)
            if int(default_val) > 0:
                pct = (diff / int(default_val)) * 100
                improvement = f"{diff:+d} ({pct:+.1f}%)"
            else:
                improvement = f"{diff:+d}"
    except:
        improvement = "N/A"
    
    print(f"{name:<30} {default_val:>15} {custom_val:>15} {improvement:>15}")

print("=" * 80)
EOF

python3 analyze-severity.py
cd ../scripts
echo -e "${GREEN}✓${NC} Análisis completado"
echo ""

# 6. Resumen de próximos pasos
echo -e "${YELLOW}[6/6]${NC} Resumen de mejoras implementadas"
echo ""
echo -e "${GREEN}✓${NC} Quality profile exportado para revisión"
echo -e "${GREEN}✓${NC} Lista de 15 reglas recomendadas generada"
echo -e "${GREEN}✓${NC} Estructura de documentación creada"
echo -e "${GREEN}✓${NC} Análisis de severidad generado"
echo ""
echo "======================================================================"
echo "  PRÓXIMOS PASOS MANUALES"
echo "======================================================================"
echo ""
echo "1. Revisar reglas recomendadas en: docs/reglas-recomendadas.txt"
echo "2. Agregar reglas al quality profile XML"
echo "3. Re-importar profile mejorado a SonarQube"
echo "4. Crear 5 clases nuevas con vulnerabilidades exclusivas"
echo "5. Re-ejecutar análisis y comparar"
echo "6. Generar visualizaciones con Python/matplotlib"
echo ""
echo "Ver plan completo en: docs/PROPUESTA-MEJORA.md"
echo ""
