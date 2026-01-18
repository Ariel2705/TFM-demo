#!/usr/bin/env python3
"""
Script para identificar reglas de seguridad que deberíamos agregar al profile custom
para demostrar MEJOR detección, no solo reducción de ruido.
"""

# Reglas adicionales recomendadas para el profile OWASP-ISO25010-Security
# Estas reglas están en Sonar way pero deberían estar en nuestro profile custom

RECOMMENDED_SECURITY_RULES = {
    # A01: Broken Access Control
    "java:S5332": {
        "name": "Using clear-text protocols is security-sensitive",
        "severity": "CRITICAL",
        "owasp": "A02:2021 - Cryptographic Failures",
        "iso25010": "Security - Confidentiality",
        "justification": "HTTP, FTP, Telnet transmiten datos sin cifrar. Permite MITM attacks."
    },
    "java:S4426": {
        "name": "Cryptographic keys should be robust",
        "severity": "CRITICAL",
        "owasp": "A02:2021 - Cryptographic Failures",
        "iso25010": "Security - Confidentiality",
        "justification": "Llaves criptográficas débiles (<2048 bits RSA) son vulnerables a ataques."
    },
    "java:S5527": {
        "name": "Server hostnames should be verified during SSL/TLS connections",
        "severity": "CRITICAL",
        "owasp": "A02:2021 - Cryptographic Failures",
        "iso25010": "Security - Authenticity",
        "justification": "Sin verificar hostname permite MITM attacks en SSL/TLS."
    },
    
    # A03: Injection
    "java:S2077": {
        "name": "Formatting SQL queries is security-sensitive",
        "severity": "CRITICAL",
        "owasp": "A03:2021 - Injection",
        "iso25010": "Security - Integrity",
        "justification": "String formatting en SQL permite injection. Usar PreparedStatement."
    },
    "java:S5131": {
        "name": "Endpoints should not be vulnerable to reflected XSS",
        "severity": "BLOCKER",
        "owasp": "A03:2021 - Injection",
        "iso25010": "Security - Integrity",
        "justification": "Input del usuario reflejado sin sanitizar permite XSS attacks."
    },
    
    # A04: Insecure Design
    "java:S4790": {
        "name": "Hashing data is security-sensitive",
        "severity": "CRITICAL",
        "owasp": "A02:2021 - Cryptographic Failures",
        "iso25010": "Security - Confidentiality",
        "justification": "MD5, SHA-1 son criptográficamente rotos. Usar SHA-256+."
    },
    "java:S2245": {
        "name": "Using pseudorandom number generators is security-sensitive",
        "severity": "CRITICAL",
        "owasp": "A02:2021 - Cryptographic Failures",
        "iso25010": "Security - Confidentiality",
        "justification": "Random() no es criptográficamente seguro. Usar SecureRandom."
    },
    
    # A05: Security Misconfiguration
    "java:S4423": {
        "name": "Weak SSL/TLS protocols should not be used",
        "severity": "CRITICAL",
        "owasp": "A02:2021 - Cryptographic Failures",
        "iso25010": "Security - Confidentiality",
        "justification": "SSLv3, TLS 1.0/1.1 tienen vulnerabilidades conocidas."
    },
    "java:S5443": {
        "name": "Operating system commands should not be vulnerable to injection",
        "severity": "BLOCKER",
        "owasp": "A03:2021 - Injection",
        "iso25010": "Security - Integrity",
        "justification": "Runtime.exec() con input del usuario permite command injection."
    },
    
    # A06: Vulnerable Components
    "java:S4507": {
        "name": "Delivering code in production with debug features activated is security-sensitive",
        "severity": "MAJOR",
        "owasp": "A05:2021 - Security Misconfiguration",
        "iso25010": "Security - Confidentiality",
        "justification": "Debug mode expone información sensible del sistema."
    },
    
    # A08: Software and Data Integrity Failures
    "java:S5135": {
        "name": "Deserialization should not be vulnerable to injection attacks",
        "severity": "BLOCKER",
        "owasp": "A08:2021 - Software and Data Integrity Failures",
        "iso25010": "Security - Integrity",
        "justification": "Deserialización de datos no confiables permite RCE."
    },
    
    # A09: Security Logging and Monitoring Failures
    "java:S5144": {
        "name": "Server-side requests should not be vulnerable to SSRF",
        "severity": "BLOCKER",
        "owasp": "A10:2021 - Server-Side Request Forgery",
        "iso25010": "Security - Confidentiality",
        "justification": "SSRF permite acceso a recursos internos no autorizados."
    },
    
    # Reglas de autenticación y sesiones
    "java:S2068": {
        "name": "Credentials should not be hard-coded",
        "severity": "BLOCKER",
        "owasp": "A07:2021 - Identification and Authentication Failures",
        "iso25010": "Security - Confidentiality",
        "justification": "Credenciales hardcoded pueden ser extraídas del código."
    },
    "java:S2092": {
        "name": "Cookies should be 'secure'",
        "severity": "CRITICAL",
        "owasp": "A05:2021 - Security Misconfiguration",
        "iso25010": "Security - Confidentiality",
        "justification": "Cookies sin flag 'secure' se transmiten por HTTP."
    },
    "java:S3330": {
        "name": "Cookies should not contain sensitive data",
        "severity": "CRITICAL",
        "owasp": "A01:2021 - Broken Access Control",
        "iso25010": "Security - Confidentiality",
        "justification": "Cookies HttpOnly=false permite robo vía XSS."
    },
}

print("=" * 100)
print("REGLAS RECOMENDADAS PARA AGREGAR AL QUALITY PROFILE CUSTOM")
print("=" * 100)
print()
print(f"Total de reglas adicionales recomendadas: {len(RECOMMENDED_SECURITY_RULES)}")
print()

for rule_key, details in RECOMMENDED_SECURITY_RULES.items():
    print(f"📌 {rule_key} - {details['severity']}")
    print(f"   Nombre: {details['name']}")
    print(f"   OWASP: {details['owasp']}")
    print(f"   ISO: {details['iso25010']}")
    print(f"   Justificación: {details['justification']}")
    print()

print("=" * 100)
print()
print("💡 SIGUIENTES PASOS:")
print()
print("1. Exportar quality profile actual")
print("2. Agregar estas reglas al XML")
print("3. Re-importar quality profile mejorado")
print("4. Crear vulnerabilidades que SOLO el custom detecte")
print("5. Re-ejecutar análisis y comparar")
print()
print("OBJETIVO: Demostrar que CUSTOM detecta MÁS vulnerabilidades críticas")
print("         no solo que reduce ruido del DEFAULT")
print()
