# TFM Demo - Análisis SAST con SonarQube

Demo completo para Trabajo Fin de Máster que compara quality profiles de SonarQube.

## 🎯 Objetivo

Demostrar empíricamente que un **quality profile personalizado**, fundamentado en estándares (OWASP Top 10, ISO/IEC 25010), proporciona **valor agregado cuantificable** vs. configuraciones por defecto.

## 📁 Contenido

- **docker/** - Configuración SonarQube + PostgreSQL
- **vulnerable-app/** - Proyecto Java con 8 vulnerabilidades intencionales
- **quality-profiles/** - Profile personalizado con 70 reglas documentadas
- **scripts/** - Automatización del demo completo
- **results/** - Reportes comparativos generados

## 🚀 Quick Start

```bash
cd tfm-demo/scripts
./run-complete-demo.sh
```

**Tiempo**: 15-20 minutos  
**Resultado**: Reporte comparativo con valor agregado demostrado

## 📊 Resultados Esperados

| Métrica | Default | Custom | Mejora |
|---------|--------:|-------:|-------:|
| Issues BLOCKER | 2-3 | 8-12 | +300% |
| Vulnerabilidades | 4-6 | 12-18 | +150% |

## 📖 Documentación Completa

Ver [README.md](README.md) para guía paso a paso detallada.

## 🎓 Para tu TFM

Incluye:
- ✅ Evidencia cuantitativa de mejora
- ✅ Mapeo completo OWASP-ISO25010-Reglas
- ✅ Justificación técnica de cada regla
- ✅ Metodología reproducible

---

**Autor**: TFM - Análisis Comparativo SAST  
**Fecha**: Enero 2026
