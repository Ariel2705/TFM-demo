#!/usr/bin/env python3
"""
Visualizaciones para Defensa de TFM
Genera gráficos comparativos DEFAULT vs CUSTOM
"""

import matplotlib.pyplot as plt
import numpy as np
from pathlib import Path

# Crear directorio para gráficos
output_dir = Path("visualizations")
output_dir.mkdir(exist_ok=True)

# Configuración de estilo
plt.style.use('seaborn-v0_8-darkgrid')
colors_default = '#e74c3c'  # Rojo
colors_custom = '#27ae60'   # Verde

# ============================================================================
# GRÁFICO 1: Comparación de Métricas - WebGoat
# ============================================================================

fig, ax = plt.subplots(figsize=(12, 6))

categories = ['Bugs', 'Vulnerabilities', 'Code Smells', 'Security\nHotspots']
default_values = [35, 8, 502, 69]
custom_values = [4, 8, 155, 48]

x = np.arange(len(categories))
width = 0.35

bars1 = ax.bar(x - width/2, default_values, width, label='DEFAULT (479 reglas)', 
               color=colors_default, alpha=0.8)
bars2 = ax.bar(x + width/2, custom_values, width, label='CUSTOM (54 reglas)', 
               color=colors_custom, alpha=0.8)

# Añadir valores en las barras
for bars in [bars1, bars2]:
    for bar in bars:
        height = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2., height,
                f'{int(height)}',
                ha='center', va='bottom', fontsize=10, fontweight='bold')

# Añadir porcentajes de reducción
reductions = ['-89%', '0%', '-69%', '-30%']
for i, reduction in enumerate(reductions):
    if reduction != '0%':
        color = colors_custom if '-' in reduction else colors_default
        ax.text(i, max(default_values[i], custom_values[i]) + 20,
                reduction, ha='center', va='bottom', 
                fontsize=11, fontweight='bold', color=color)

ax.set_xlabel('Métricas', fontsize=12, fontweight='bold')
ax.set_ylabel('Cantidad de Issues', fontsize=12, fontweight='bold')
ax.set_title('WebGoat (12,702 LOC): Comparación DEFAULT vs CUSTOM\n' +
             'MISMA detección de vulnerabilidades, MENOS ruido',
             fontsize=14, fontweight='bold', pad=20)
ax.set_xticks(x)
ax.set_xticklabels(categories, fontsize=11)
ax.legend(fontsize=11, loc='upper right')
ax.grid(axis='y', alpha=0.3)

plt.tight_layout()
plt.savefig(output_dir / 'webgoat_comparison.png', dpi=300, bbox_inches='tight')
print(f"✅ Guardado: {output_dir / 'webgoat_comparison.png'}")
plt.close()

# ============================================================================
# GRÁFICO 2: Technical Debt - Comparación
# ============================================================================

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 6))

# Proyecto 1: tfm-demo
projects = ['tfm-demo\n(1,552 LOC)', 'WebGoat\n(12,702 LOC)']
default_debt = [2373/60, 2476/60]  # En horas
custom_debt = [460/60, 1820/60]    # En horas

x = np.arange(len(projects))
width = 0.35

bars1 = ax1.bar(x - width/2, default_debt, width, label='DEFAULT', 
                color=colors_default, alpha=0.8)
bars2 = ax1.bar(x + width/2, custom_debt, width, label='CUSTOM', 
                color=colors_custom, alpha=0.8)

# Valores en barras
for bars in [bars1, bars2]:
    for bar in bars:
        height = bar.get_height()
        ax1.text(bar.get_x() + bar.get_width()/2., height,
                f'{height:.1f}h',
                ha='center', va='bottom', fontsize=10, fontweight='bold')

ax1.set_ylabel('Horas de Remediación', fontsize=12, fontweight='bold')
ax1.set_title('Technical Debt por Proyecto', fontsize=13, fontweight='bold')
ax1.set_xticks(x)
ax1.set_xticklabels(projects, fontsize=11)
ax1.legend(fontsize=11)
ax1.grid(axis='y', alpha=0.3)

# Pie chart - Ahorro
savings = [31.9, 10.9]  # Horas ahorradas
labels = ['tfm-demo\n31.9h ahorro\n($1,594)', 'WebGoat\n10.9h ahorro\n($547)']
colors = ['#3498db', '#9b59b6']

wedges, texts, autotexts = ax2.pie(savings, labels=labels, autopct='%1.1f%%',
                                     colors=colors, startangle=90,
                                     textprops={'fontsize': 11, 'fontweight': 'bold'})

ax2.set_title('Distribución de Ahorro de Tiempo', fontsize=13, fontweight='bold')

plt.tight_layout()
plt.savefig(output_dir / 'technical_debt_comparison.png', dpi=300, bbox_inches='tight')
print(f"✅ Guardado: {output_dir / 'technical_debt_comparison.png'}")
plt.close()

# ============================================================================
# GRÁFICO 3: Reducción de Ruido - Consistencia
# ============================================================================

fig, ax = plt.subplots(figsize=(10, 6))

# Datos de reducción
metrics = ['Bugs', 'Code Smells', 'Security\nHotspots', 'Technical\nDebt']
tfm_demo = [96.9, 72.6, 36.4, 80.6]
webgoat = [88.6, 69.1, 30.4, 26.5]

x = np.arange(len(metrics))
width = 0.35

bars1 = ax.bar(x - width/2, tfm_demo, width, label='tfm-demo (1.5K LOC)', 
               color='#3498db', alpha=0.8)
bars2 = ax.bar(x + width/2, webgoat, width, label='WebGoat (12.7K LOC)', 
               color='#9b59b6', alpha=0.8)

# Valores en barras
for bars in [bars1, bars2]:
    for bar in bars:
        height = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2., height,
                f'{height:.1f}%',
                ha='center', va='bottom', fontsize=9, fontweight='bold')

ax.set_ylabel('% Reducción', fontsize=12, fontweight='bold')
ax.set_title('Consistencia de Resultados: CUSTOM Profile\n' +
             'Reducción de ruido en proyectos de diferentes tamaños',
             fontsize=14, fontweight='bold', pad=20)
ax.set_xticks(x)
ax.set_xticklabels(metrics, fontsize=11)
ax.legend(fontsize=11, loc='upper right')
ax.grid(axis='y', alpha=0.3)
ax.set_ylim(0, 110)

# Línea de referencia
ax.axhline(y=50, color='gray', linestyle='--', alpha=0.5, linewidth=1)
ax.text(len(metrics)-0.5, 52, '50% reducción', fontsize=9, color='gray')

plt.tight_layout()
plt.savefig(output_dir / 'consistency_results.png', dpi=300, bbox_inches='tight')
print(f"✅ Guardado: {output_dir / 'consistency_results.png'}")
plt.close()

# ============================================================================
# GRÁFICO 4: ROI Anual - Extrapolación
# ============================================================================

fig, ax = plt.subplots(figsize=(10, 6))

# Cálculo de ROI anual (análisis semanal, 52 semanas, equipo 10 devs)
projects_roi = ['tfm-demo', 'WebGoat']
weekly_savings = [1594.17, 546.67]
annual_savings = [x * 52 for x in weekly_savings]

bars = ax.bar(projects_roi, annual_savings, color=['#2ecc71', '#27ae60'], alpha=0.8, width=0.6)

# Valores en barras
for bar in bars:
    height = bar.get_height()
    ax.text(bar.get_x() + bar.get_width()/2., height,
            f'${height:,.0f}',
            ha='center', va='bottom', fontsize=12, fontweight='bold')

# Añadir detalles
for i, (project, weekly) in enumerate(zip(projects_roi, weekly_savings)):
    ax.text(i, annual_savings[i] * 0.5,
            f'${weekly:.2f}/semana\n52 semanas',
            ha='center', va='center', fontsize=10, color='white', fontweight='bold')

ax.set_ylabel('Ahorro Anual (USD)', fontsize=12, fontweight='bold')
ax.set_title('ROI Anual: Profile CUSTOM\n' +
             'Equipo de 10 desarrolladores, análisis semanal',
             fontsize=14, fontweight='bold', pad=20)
ax.set_ylim(0, max(annual_savings) * 1.2)
ax.grid(axis='y', alpha=0.3)

# Formato de eje Y con separadores de miles
ax.yaxis.set_major_formatter(plt.FuncFormatter(lambda x, p: f'${x:,.0f}'))

plt.tight_layout()
plt.savefig(output_dir / 'roi_annual.png', dpi=300, bbox_inches='tight')
print(f"✅ Guardado: {output_dir / 'roi_annual.png'}")
plt.close()

# ============================================================================
# GRÁFICO 5: Quality over Quantity
# ============================================================================

fig, ax = plt.subplots(figsize=(8, 8))

# Datos
profiles = ['DEFAULT\n(479 reglas)', 'CUSTOM\n(54 reglas)']
vulnerabilities = [8, 8]
noise = [502 + 35 + 69, 155 + 4 + 48]  # code smells + bugs + hotspots

x = np.arange(len(profiles))
width = 0.4

# Stacked bar
p1 = ax.bar(x, vulnerabilities, width, label='Vulnerabilities (críticas)', 
            color='#e74c3c', alpha=0.9)
p2 = ax.bar(x, noise, width, bottom=vulnerabilities, label='Ruido (bugs + smells + hotspots)', 
            color='#95a5a6', alpha=0.7)

# Valores
for i, (v, n) in enumerate(zip(vulnerabilities, noise)):
    # Vulnerabilities
    ax.text(i, v/2, f'{v}', ha='center', va='center', 
            fontsize=14, fontweight='bold', color='white')
    # Ruido
    ax.text(i, v + n/2, f'{n}', ha='center', va='center', 
            fontsize=14, fontweight='bold', color='white')
    # Total
    ax.text(i, v + n + 20, f'Total: {v + n}', ha='center', va='bottom',
            fontsize=11, fontweight='bold')

ax.set_ylabel('Cantidad de Issues', fontsize=12, fontweight='bold')
ax.set_title('Quality over Quantity: WebGoat\n' +
             'MISMA detección, MENOS ruido',
             fontsize=14, fontweight='bold', pad=20)
ax.set_xticks(x)
ax.set_xticklabels(profiles, fontsize=12, fontweight='bold')
ax.legend(fontsize=11, loc='upper right')
ax.grid(axis='y', alpha=0.3)

# Anotación
ax.annotate('425 reglas adicionales\nNO detectaron más\nvulnerabilities',
            xy=(0, vulnerabilities[0] + noise[0]), xytext=(0.5, 400),
            arrowprops=dict(arrowstyle='->', color='red', lw=2),
            fontsize=11, fontweight='bold', color='red',
            ha='center', bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.8))

plt.tight_layout()
plt.savefig(output_dir / 'quality_over_quantity.png', dpi=300, bbox_inches='tight')
print(f"✅ Guardado: {output_dir / 'quality_over_quantity.png'}")
plt.close()

# ============================================================================
# RESUMEN
# ============================================================================

print("\n" + "="*70)
print("✅ VISUALIZACIONES GENERADAS EXITOSAMENTE")
print("="*70)
print(f"\nDirectorio: {output_dir.absolute()}/\n")
print("Archivos creados:")
print("  1. webgoat_comparison.png - Comparación de métricas WebGoat")
print("  2. technical_debt_comparison.png - Comparación de deuda técnica")
print("  3. consistency_results.png - Consistencia de resultados")
print("  4. roi_annual.png - ROI anual")
print("  5. quality_over_quantity.png - Quality over Quantity")
print("\nEstas gráficas están listas para tu presentación de TFM! 🎓")
print("="*70)
