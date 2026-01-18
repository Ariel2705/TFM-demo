package com.tfm.demo;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * VULNERABILIDAD: Regular Expression Denial of Service (ReDoS)
 * 
 * OWASP Top 10 2021: A01:2021 - Broken Access Control
 * ISO/IEC 25010: Security - Integrity
 * 
 * Reglas SonarQube:
 * - S5852: Slow regular expressions should not be used (CRITICAL)
 * - S6437: Regular expressions should not be vulnerable to Denial of Service attacks (BLOCKER)
 * - S5843: Regular expressions should not be too complicated (MAJOR)
 */
public class RegexVulnerabilities {
    
    /**
     * Vulnerabilidad S5852: ReDoS - Catastrophic backtracking
     * Pattern: (a+)+ con input "aaaa...b" causa explosión exponencial
     */
    public boolean validateEmailWithReDoS(String email) {
        // VULNERABLE: Regex con backtracking catastrófico
        String regex = "^([a-zA-Z0-9]+)*@([a-zA-Z0-9]+)*\\.com$";
        return email.matches(regex);
    }
    
    /**
     * Vulnerabilidad S6437: IP validation incompleta
     * Permite valores fuera del rango válido (0-255)
     */
    public boolean validateIPAddress(String ip) {
        // VULNERABLE: No valida rangos 0-255, acepta "999.999.999.999"
        String regex = "^\\d+\\.\\d+\\.\\d+\\.\\d+$";
        return ip.matches(regex);
    }
    
    /**
     * Vulnerabilidad S5852: Nested quantifiers
     * Pattern (a*)*b puede causar ReDoS
     */
    public boolean validateUsername(String username) {
        // VULNERABLE: Quantifiers anidados
        Pattern pattern = Pattern.compile("^(a*)*b$");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
    
    /**
     * Vulnerabilidad S5843: Regex excesivamente compleja
     */
    public boolean validateComplexPassword(String password) {
        // VULNERABLE: Regex demasiado compleja, difícil de mantener
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])" +
                      "(?=.*[a-z]{2,})(?=.*[A-Z]{2,})(?=.*\\d{2,})" +
                      "(?=.*[@$!%*?&]{2,})[A-Za-z\\d@$!%*?&]{16,}$";
        return password.matches(regex);
    }
    
    /**
     * Vulnerabilidad: Alternation con muchas opciones
     */
    public boolean matchesPattern(String input) {
        // VULNERABLE: Alternation puede causar backtracking
        String regex = "(a|a)*b";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).matches();
    }
    
    /**
     * Vulnerabilidad: URL validation vulnerable a ReDoS
     */
    public boolean validateURL(String url) {
        // VULNERABLE: Pattern complejo con nested quantifiers
        String regex = "^(https?://)?(www\\.)?([a-zA-Z0-9]+)*\\.([a-zA-Z]{2,})+$";
        return url.matches(regex);
    }
}
