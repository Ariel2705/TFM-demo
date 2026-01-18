package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A03:2021 – Injection (XSS)
 * Ejemplos de Cross-Site Scripting
 */
public class XSSVulnerabilities {

    // VULNERABILITY: Reflected XSS
    public void displayUserInput(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String userInput = request.getParameter("name");
        
        PrintWriter out = response.getWriter();
        
        // VULNERABLE: Sin sanitización del input
        out.println("<html><body>");
        out.println("<h1>Welcome, " + userInput + "!</h1>");
        out.println("</body></html>");
    }
    
    // VULNERABILITY: Stored XSS
    public String formatComment(String comment) {
        // VULNERABLE: Retorna HTML sin escape
        return "<div class='comment'>" + comment + "</div>";
    }
    
    // VULNERABILITY: DOM-based XSS potencial
    public String generateJavaScript(String callback) {
        // VULNERABLE: Inyección en JavaScript
        return "<script>var callback = '" + callback + "'; callback();</script>";
    }
}
