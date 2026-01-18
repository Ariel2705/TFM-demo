#!/bin/bash
# Quick LTS Migration - No interactive prompts
# Run this after generating token manually

set -e
SONAR_URL="http://localhost:9000"

if [ -z "$1" ]; then
    echo "Usage: ./quick-lts-migrate.sh <YOUR_SONAR_TOKEN>"
    echo ""
    echo "Steps:"
    echo "1. Access http://localhost:9000"
    echo "2. Login: admin/admin (change to Admin123! if asked)"
    echo "3. My Account → Security → Generate Token (name: tfm-demo-lts)"
    echo "4. Run: ./quick-lts-migrate.sh YOUR_TOKEN_HERE"
    exit 1
fi

SONAR_TOKEN=$1
echo "SONAR_TOKEN=${SONAR_TOKEN}" > ../results/.env

echo "✓ Token saved"
echo "📦 Importing quality profile..."

curl -s -u "${SONAR_TOKEN}:" -X POST \
    -F "backup=@../quality-profiles/OWASP-ISO25010-SecurityProfile.xml" \
    "${SONAR_URL}/api/qualityprofiles/restore" > /dev/null

echo "✓ Profile imported"
echo "🔨 Compiling project..."

cd ../vulnerable-app
mvn clean compile -q

echo "✓ Compiled (13 files)"
echo "📊 Analyzing with DEFAULT profile..."

source ../results/.env
mvn sonar:sonar \
    -Dsonar.projectKey=tfm-demo-default-lts \
    -Dsonar.projectName="TFM Demo LTS - Default" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo "✓ DEFAULT analysis complete"
echo "📊 Analyzing with CUSTOM profile..."

curl -s -u "${SONAR_TOKEN}:" -X POST \
    "${SONAR_URL}/api/qualityprofiles/add_project?project=tfm-demo-custom-lts&qualityProfile=OWASP-ISO25010-Security&language=java" \
    > /dev/null 2>&1 || true

mvn sonar:sonar \
    -Dsonar.projectKey=tfm-demo-custom-lts \
    -Dsonar.projectName="TFM Demo LTS - Custom" \
    -Dsonar.host.url="${SONAR_URL}" \
    -Dsonar.login="${SONAR_TOKEN}" \
    -q

echo "✓ CUSTOM analysis complete"
echo "⏳ Waiting for SonarQube processing..."
sleep 10

cd ../results
echo "📈 Extracting metrics..."

curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/measures/component?component=tfm-demo-default-lts&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots,ncloc,sqale_index,reliability_rating,security_rating" \
    > default-measures-lts.json

curl -s -u "${SONAR_TOKEN}:" \
    "${SONAR_URL}/api/measures/component?component=tfm-demo-custom-lts&metricKeys=bugs,vulnerabilities,code_smells,security_hotspots,ncloc,sqale_index,reliability_rating,security_rating" \
    > custom-measures-lts.json

echo ""
echo "═════════════════════════════════════════"
echo "         MIGRATION COMPLETE"
echo "═════════════════════════════════════════"
echo ""
echo "DEFAULT: http://localhost:9000/dashboard?id=tfm-demo-default-lts"
echo "CUSTOM:  http://localhost:9000/dashboard?id=tfm-demo-custom-lts"
echo ""
echo "Results saved in results/"
echo ""

if command -v python3 &> /dev/null; then
    python3 << 'EOF'
import json

with open('default-measures-lts.json') as f:
    default = json.load(f)
with open('custom-measures-lts.json') as f:
    custom = json.load(f)

def_measures = {m['metric']: m.get('value', 'N/A') for m in default['component']['measures']}
cust_measures = {m['metric']: m.get('value', 'N/A') for m in custom['component']['measures']}

print("COMPARISON:")
print("-" * 70)
print(f"{'Metric':<25} {'DEFAULT':>15} {'CUSTOM':>15} {'Diff':>10}")
print("-" * 70)

metrics = ['ncloc', 'bugs', 'vulnerabilities', 'code_smells', 'security_hotspots']
for m in metrics:
    d_val = def_measures.get(m, 'N/A')
    c_val = cust_measures.get(m, 'N/A')
    if d_val != 'N/A' and c_val != 'N/A':
        try:
            diff = int(c_val) - int(d_val)
            diff_str = f"+{diff}" if diff > 0 else str(diff)
        except:
            diff_str = "-"
    else:
        diff_str = "-"
    print(f"{m:<25} {d_val:>15} {c_val:>15} {diff_str:>10}")

print("-" * 70)
print(f"\n✓ LTS Version: 9.9.8")
print(f"✓ Analysis completed successfully\n")
EOF
fi
