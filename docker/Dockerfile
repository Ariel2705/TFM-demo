# Dockerfile para SonarQube - TFM Demo
# Versión Community Edition con configuraciones para análisis de seguridad

FROM sonarqube:10.4-community

# Metadata
LABEL maintainer="TFM SAST Analysis"
LABEL description="SonarQube configurado para análisis OWASP Top 10 e ISO 25010"
LABEL version="1.0"

# Variables de entorno para optimización
ENV SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true
ENV SONAR_JDBC_USERNAME=sonar
ENV SONAR_JDBC_PASSWORD=sonar

# Usuario por defecto de SonarQube
USER sonarqube

# Exponer puerto
EXPOSE 9000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
  CMD curl -f http://localhost:9000/api/system/status | grep -q '"status":"UP"' || exit 1

# El comando por defecto ya está definido en la imagen base
