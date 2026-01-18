#!/usr/bin/env python3
"""
Generador de Reporte Comparativo TFM
Compara resultados de análisis SAST entre profiles DEFAULT y CUSTOM
"""

import json
import sys
from pathlib import Path
from datetime import datetime

def load_json(filepath):
    """Carga archivo JSON"""
    with open(filepath) as f:
        return json.load(f)

def print_banner():
    """Imprime banner del reporte"""
    print("\n" + "="*80)
    print(" "*25 + "REPORTE COMPARATIVO TFM")
    print(" "*15 + "Análisis SAST: SonarQube Default vs OWASP-ISO25010")
    print("="*80 + "\n")

def print_metrics_table(default_measures, custom_measures):
    """Imprime tabla comparativa de métricas"""
    print("📊 MÉTRICAS COMPARATIVAS")
    print("-" * 80)
    print(f"{'Métrica':<30} {'DEFAULT':<20} {'CUSTOM':<20} {'Diferencia':>10}")
    print("-" * 80)
    
    metrics_map = {
        'ncloc': 'Lines of Code',
        'vulnerabilities': 'Vulnerabilities',
        'bugs': 'Bugs',
        'code_smells': 'Code Smells',
        'security_hotspots': 'Security Hotspots',
        'sqale_index': 'Technical Debt (min)',
        'security_rating': 'Security Rating',
        'reliability_rating': 'Reliability Rating'
    }
    
    default_dict = {m['metric']: m.get('value', '0') for m in default_measures}
    custom_dict = {m['metric']: m.get('value', '0') for m in custom_measures}
    
    for key, label in metrics_map.items():
        def_val = default_dict.get(key, 'N/A')
        cust_val = custom_dict.get(key, 'N/A')
        
        # Calcular diferencia si son números
        try:
            def_num = float(def_val) if def_val != 'N/A' else 0
            cust_num = float(cust_val) if cust_val != 'N/A' else 0
            diff = cust_num - def_num
            diff_str = f"+{diff:.0f}" if diff > 0 else f"{diff:.0f}"
            if diff == 0:
                diff_str = "="
        except:
            diff_str = "-"
        
        print(f"{label:<30} {str(def_val):<20} {str(cust_val):<20} {diff_str:>10}")
    
    print("-" * 80 + "\n")

def print_profile_distribution():
    """Imprime distribución de reglas del profile custom"""
    print("📋 DISTRIBUCIÓN DEL PROFILE OWASP-ISO25010-Security")
    print("-" * 80)
    
    distribution = {
        'BLOCKER': 25,
        'CRITICAL': 18,
        'MAJOR': 21,
        'MINOR': 4,
        'INFO': 2
    }
    
    total = sum(distribution.values())
    
    for severity, count in distribution.items():
        percentage = (count / total) * 100
        bar_length = int(percentage / 2)
        bar = "█" * bar_length
        print(f"{severity:<12} {count:>3} reglas ({percentage:>5.1f}%)  {bar}")
    
    print(f"\nTotal: {total} reglas activas\n")

def print_owasp_coverage():
    """Imprime cobertura OWASP Top 10"""
    print("🎯 COBERTURA OWASP TOP 10 2021")
    print("-" * 80)
    
    categories = [
        ("A01: Broken Access Control", 8),
        ("A02: Cryptographic Failures", 12),
        ("A03: Injection", 11),
        ("A04: Insecure Design", 7),
        ("A05: Security Misconfiguration", 6),
        ("A06: Vulnerable Components", 5),
        ("A07: Authentication Failures", 6),
        ("A08: Data Integrity Failures", 8),
        ("A09: Logging Failures", 4),
        ("A10: SSRF", 3)
    ]
    
    total_rules = sum(count for _, count in categories)
    
    for category, count in categories:
        percentage = (count / total_rules) * 100
        bar_length = int(count / 2)
        bar = "▓" * bar_length
        print(f"{category:<40} {count:>3} reglas  {bar}")
    
    print(f"\nCobertura: 10/10 categorías (100%)\n")

def print_value_proposition():
    """Imprime propuesta de valor del profile custom"""
    print("💡 VALOR AGREGADO DEL PROFILE CUSTOM")
    print("-" * 80)
    
    benefits = [
        ("Alineación Estándar", "Mapeo directo con OWASP Top 10 2021 e ISO/IEC 25010"),
        ("Trazabilidad", "Cada regla justificada y documentada"),
        ("Reducción de Ruido", "70 reglas focalizadas vs ~200 del default"),
        ("Severidades Calibradas", "25 BLOCKER para vulnerabilidades críticas"),
        ("Facilita Auditorías", "Reportes alineados con frameworks reconocidos"),
        ("Compliance", "Evidencia para certificaciones y auditorías")
    ]
    
    for i, (title, desc) in enumerate(benefits, 1):
        print(f"  {i}. {title}")
        print(f"     → {desc}\n")
    
    print("-" * 80 + "\n")

def generate_summary(default_data, custom_data):
    """Genera resumen ejecutivo"""
    def_measures = default_data['component']['measures']
    cust_measures = custom_data['component']['measures']
    
    def_dict = {m['metric']: m.get('value', '0') for m in def_measures}
    cust_dict = {m['metric']: m.get('value', '0') for m in cust_measures}
    
    print("📝 RESUMEN EJECUTIVO")
    print("-" * 80)
    
    # Comparación de vulnerabilidades
    def_vuln = int(def_dict.get('vulnerabilities', 0))
    cust_vuln = int(cust_dict.get('vulnerabilities', 0))
    
    if def_vuln == cust_vuln:
        print(f"✓ Ambos profiles detectaron {def_vuln} vulnerabilidades")
        print("✓ La diferencia está en la granularidad y alineación con estándares\n")
    elif cust_vuln > def_vuln:
        mejora = cust_vuln - def_vuln
        percentage = (mejora / def_vuln * 100) if def_vuln > 0 else 0
        print(f"✓ Profile custom detectó {mejora} vulnerabilidades adicionales (+{percentage:.1f}%)")
        print(f"✓ Total: {cust_vuln} vs {def_vuln}\n")
    
    # Rating de seguridad
    def_rating = def_dict.get('security_rating', 'N/A')
    cust_rating = cust_dict.get('security_rating', 'N/A')
    
    rating_map = {'1.0': 'A', '2.0': 'B', '3.0': 'C', '4.0': 'D', '5.0': 'E'}
    def_letter = rating_map.get(def_rating, 'N/A')
    cust_letter = rating_map.get(cust_rating, 'N/A')
    
    print(f"Security Rating:")
    print(f"  DEFAULT: {def_letter} ({def_rating})")
    print(f"  CUSTOM:  {cust_letter} ({cust_rating})")
    
    if def_letter == cust_letter == 'E':
        print("\n⚠️  Ambos proyectos tienen rating E (peor rating)")
        print("    Esto es esperado: la aplicación tiene vulnerabilidades intencionales\n")
    
    print("-" * 80 + "\n")

def main():
    """Función principal"""
    # Directorio de resultados (ajustar según desde dónde se ejecuta)
    results_dir = Path.cwd()
    if not (results_dir / 'default-measures.json').exists():
        results_dir = Path(__file__).parent.parent / 'results'
    
    # Cargar datos (intentar versión v2 primero)
    try:
        default_file = 'default-measures-v2.json' if (results_dir / 'default-measures-v2.json').exists() else 'default-measures.json'
        custom_file = 'custom-measures-v2.json' if (results_dir / 'custom-measures-v2.json').exists() else 'custom-measures.json'
        
        with open(results_dir / default_file) as f:
            default_data = json.load(f)
        with open(results_dir / custom_file) as f:
            custom_data = json.load(f)
            
        print(f"📁 Usando archivos: {default_file} y {custom_file}\n")
    except FileNotFoundError as e:
        print(f"Error: Archivos de métricas no encontrados en {results_dir}")
        print(f"Detalle: {e}")
        print("Ejecuta primero el análisis de SonarQube")
        sys.exit(1)
    
    # Generar reporte
    print_banner()
    generate_summary(default_data, custom_data)
    print_metrics_table(
        default_data['component']['measures'],
        custom_data['component']['measures']
    )
    print_profile_distribution()
    print_owasp_coverage()
    print_value_proposition()
    
    # Footer
    print("="*80)
    print(f"Reporte generado: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("="*80 + "\n")
    
    print("🔗 ENLACES ÚTILES:")
    print("  • Dashboard Default: http://localhost:9000/dashboard?id=tfm-demo-default")
    print("  • Dashboard Custom:  http://localhost:9000/dashboard?id=tfm-demo-custom")
    print("  • Quality Profiles:  http://localhost:9000/profiles\n")

if __name__ == '__main__':
    main()
