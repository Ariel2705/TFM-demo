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
