#!/usr/bin/env python3
"""Comparación de resultados en SonarQube LTS"""
import json
from pathlib import Path

results_dir = Path(__file__).parent

with open(results_dir / 'default-measures-lts.json') as f:
    default = json.load(f)
with open(results_dir / 'custom-measures-lts.json') as f:
    custom = json.load(f)

def_measures = {m['metric']: m.get('value', 'N/A') for m in default['component']['measures']}
cust_measures = {m['metric']: m.get('value', 'N/A') for m in custom['component']['measures']}

print("="*80)
print("           COMPARACIÓN EN SONARQUBE LTS 9.9.8")
print("="*80)
print(f"\n{'Métrica':<30} {'DEFAULT':>15} {'CUSTOM':>15} {'Diferencia':>15}")
print("-"*80)

metrics = [
    ('ncloc', 'Lines of Code'),
    ('bugs', 'Bugs'),
    ('vulnerabilities', 'Vulnerabilities'),
    ('code_smells', 'Code Smells'),
    ('security_hotspots', 'Security Hotspots'),
    ('sqale_index', 'Technical Debt (min)'),
    ('reliability_rating', 'Reliability Rating'),
    ('security_rating', 'Security Rating'),
]

for key, label in metrics:
    d_val = def_measures.get(key, 'N/A')
    c_val = cust_measures.get(key, 'N/A')
    
    if d_val != 'N/A' and c_val != 'N/A':
        try:
            d_num = float(d_val)
            c_num = float(c_val)
            diff = c_num - d_num
            if diff > 0:
                diff_str = f"+{diff:.0f}"
            elif diff < 0:
                diff_str = f"{diff:.0f}"
            else:
                diff_str = "="
        except:
            diff_str = "-"
    else:
        diff_str = "-"
    
    print(f"{label:<30} {str(d_val):>15} {str(c_val):>15} {diff_str:>15}")

print("-"*80)

# Calcular mejoras específicas
try:
    hotspots_def = int(def_measures.get('security_hotspots', 0))
    hotspots_cust = int(cust_measures.get('security_hotspots', 0))
    bugs_def = int(def_measures.get('bugs', 0))
    bugs_cust = int(cust_measures.get('bugs', 0))
    
    print("\n" + "="*80)
    print("                      ANÁLISIS DE VALOR AGREGADO")
    print("="*80)
    print()
    
    if hotspots_cust > hotspots_def:
        hotspot_diff = hotspots_cust - hotspots_def
        hotspot_pct = (hotspot_diff / hotspots_def * 100) if hotspots_def > 0 else 0
        print(f"✅ Security Hotspots: +{hotspot_diff} (+{hotspot_pct:.1f}%)")
        print(f"   El profile custom detectó MÁS riesgos de seguridad")
    elif hotspots_cust == hotspots_def:
        print(f"➖ Security Hotspots: {hotspots_cust} (igual cobertura)")
    else:
        hotspot_diff = hotspots_def - hotspots_cust
        print(f"⚠️  Security Hotspots: -{hotspot_diff}")
        print(f"   El profile custom detectó MENOS hotspots")
    
    print()
    
    if bugs_cust < bugs_def:
        bug_diff = bugs_def - bugs_cust
        bug_pct = (bug_diff / bugs_def * 100) if bugs_def > 0 else 0
        print(f"✅ Bugs: -{bug_diff} (-{bug_pct:.1f}%)")
        print(f"   Reducción de ruido - enfoque en seguridad")
    elif bugs_cust == bugs_def:
        print(f"➖ Bugs: {bugs_cust} (sin cambio)")
    else:
        bug_diff = bugs_cust - bugs_def
        print(f"📊 Bugs: +{bug_diff}")
        print(f"   El profile custom reporta más bugs")
    
    print()
    
    rel_def = def_measures.get('reliability_rating', 'N/A')
    rel_cust = cust_measures.get('reliability_rating', 'N/A')
    
    if rel_def != 'N/A' and rel_cust != 'N/A':
        rel_diff = float(rel_def) - float(rel_cust)
        if rel_diff > 0:
            rating_map = {1.0: 'A', 2.0: 'B', 3.0: 'C', 4.0: 'D', 5.0: 'E'}
            from_rating = rating_map.get(float(rel_def), rel_def)
            to_rating = rating_map.get(float(rel_cust), rel_cust)
            print(f"✅ Reliability Rating: {from_rating} → {to_rating} (mejorado)")
        elif rel_diff < 0:
            print(f"⚠️  Reliability Rating: empeorado")
        else:
            print(f"➖ Reliability Rating: {rel_cust} (sin cambio)")
    
    print()
    print("="*80)
    
except Exception as e:
    print(f"\nError al calcular diferencias: {e}")

print()
print("🔗 DASHBOARDS:")
print("   DEFAULT: http://localhost:9000/dashboard?id=tfm-demo-default-lts")
print("   CUSTOM:  http://localhost:9000/dashboard?id=tfm-demo-custom-lts")
print()
print("📁 ARCHIVOS:")
print(f"   {results_dir}/default-measures-lts.json")
print(f"   {results_dir}/custom-measures-lts.json")
print()
print("="*80)
print()
