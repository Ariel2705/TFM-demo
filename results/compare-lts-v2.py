#!/usr/bin/env python3
"""
Comparación de métricas entre profile DEFAULT y CUSTOM (versión v2 con más vulnerabilidades web)
"""

import json

# Métricas del análisis DEFAULT
default_metrics = {
    "security_hotspots": 33,
    "code_smells": 116,
    "bugs": 17,
    "sqale_index": 1270,  # minutos
    "reliability_rating": 5.0,
    "ncloc": 1025,
    "vulnerabilities": 18
}

# Métricas del análisis CUSTOM
custom_metrics = {
    "bugs": 1,
    "reliability_rating": 3.0,
    "sqale_index": 300,  # minutos
    "ncloc": 1025,
    "security_hotspots": 18,
    "vulnerabilities": 18,
    "code_smells": 39
}

print("=" * 80)
print("COMPARACIÓN: PROFILE DEFAULT vs CUSTOM (LTS v2 - Con vulnerabilidades web)")
print("=" * 80)
print()

print(f"{'MÉTRICA':<30} {'DEFAULT':>12} {'CUSTOM':>12} {'DIFERENCIA':>15}")
print("-" * 80)

metrics_order = [
    ("Lines of Code", "ncloc", False),
    ("Bugs", "bugs", True),
    ("Vulnerabilities", "vulnerabilities", True),
    ("Code Smells", "code_smells", True),
    ("Security Hotspots", "security_hotspots", True),
    ("Technical Debt (min)", "sqale_index", True),
    ("Reliability Rating", "reliability_rating", True),
]

for name, key, show_diff in metrics_order:
    default_val = default_metrics.get(key, 0)
    custom_val = custom_metrics.get(key, 0)
    
    if not show_diff:
        diff_str = "="
    else:
        diff = custom_val - default_val
        if isinstance(default_val, float):
            diff_str = f"{diff:+.1f}"
        else:
            if default_val > 0:
                pct = (diff / default_val) * 100
                diff_str = f"{diff:+d} ({pct:+.1f}%)"
            else:
                diff_str = f"{diff:+d}"
    
    print(f"{name:<30} {default_val:>12} {custom_val:>12} {diff_str:>15}")

print("=" * 80)
print()

# Calcular mejoras
bugs_improvement = ((default_metrics["bugs"] - custom_metrics["bugs"]) / default_metrics["bugs"]) * 100
smells_improvement = ((default_metrics["code_smells"] - custom_metrics["code_smells"]) / default_metrics["code_smells"]) * 100
hotspots_improvement = ((default_metrics["security_hotspots"] - custom_metrics["security_hotspots"]) / default_metrics["security_hotspots"]) * 100
debt_improvement = ((default_metrics["sqale_index"] - custom_metrics["sqale_index"]) / default_metrics["sqale_index"]) * 100

print("🎯 MEJORAS DEL PROFILE CUSTOM:")
print(f"  • Bugs: {bugs_improvement:.1f}% reducción ({default_metrics['bugs']} → {custom_metrics['bugs']})")
print(f"  • Code Smells: {smells_improvement:.1f}% reducción ({default_metrics['code_smells']} → {custom_metrics['code_smells']})")
print(f"  • Security Hotspots: {hotspots_improvement:.1f}% reducción ({default_metrics['security_hotspots']} → {custom_metrics['security_hotspots']})")
print(f"  • Technical Debt: {debt_improvement:.1f}% reducción ({default_metrics['sqale_index']} → {custom_metrics['sqale_index']} min)")
print(f"  • Reliability Rating: Mejoró de {default_metrics['reliability_rating']} (E) → {custom_metrics['reliability_rating']} (C)")
print()

# Rating interpretation
def get_rating_label(rating):
    if rating <= 1.0:
        return "A (Best)"
    elif rating <= 2.0:
        return "B (Good)"
    elif rating <= 3.0:
        return "C (Acceptable)"
    elif rating <= 4.0:
        return "D (Poor)"
    else:
        return "E (Worst)"

print("📊 INTERPRETACIÓN:")
print(f"  • DEFAULT Reliability: {get_rating_label(default_metrics['reliability_rating'])}")
print(f"  • CUSTOM Reliability: {get_rating_label(custom_metrics['reliability_rating'])}")
print()

print("✅ CONCLUSIÓN:")
print("  El profile CUSTOM (OWASP-ISO25010-Security) demuestra VALOR AGREGADO significativo:")
print(f"  - Reduce ruido ({smells_improvement:.0f}% menos code smells)")
print(f"  - Mejora confiabilidad ({bugs_improvement:.0f}% menos bugs)")
print(f"  - Reduce deuda técnica ({debt_improvement:.0f}% menos minutos)")
print(f"  - Enfoque específico en seguridad web (mantiene {custom_metrics['vulnerabilities']} vulnerabilities detectadas)")
print()
print("=" * 80)
