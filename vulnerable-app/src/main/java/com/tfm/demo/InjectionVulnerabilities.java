package com.tfm.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A03:2021 – Injection
 * Ejemplos de SQL Injection y Command Injection
 */
public class InjectionVulnerabilities {

    // VULNERABILITY: SQL Injection mediante concatenación
    public String getUserData(String userId) throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/mydb", "root", "password"
        );
        
        // VULNERABLE: Concatenación directa sin prepared statement
        String query = "SELECT * FROM users WHERE id = '" + userId + "'";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        StringBuilder result = new StringBuilder();
        while (rs.next()) {
            result.append(rs.getString("username"));
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return result.toString();
    }
    
    // VULNERABILITY: SQL Injection en login
    public boolean login(String username, String password) throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/mydb", "root", "password"
        );
        
        // VULNERABLE: Permite bypass con ' OR '1'='1
        String query = "SELECT * FROM users WHERE username = '" + username + 
                      "' AND password = '" + password + "'";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        boolean authenticated = rs.next();
        
        rs.close();
        stmt.close();
        conn.close();
        
        return authenticated;
    }
    
    // VULNERABILITY: OS Command Injection
    public String executeCommand(String filename) throws Exception {
        // VULNERABLE: Permite inyección de comandos
        String command = "cat " + filename;
        
        Process process = Runtime.getRuntime().exec(command);
        
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(process.getInputStream())
        );
        
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        
        return output.toString();
    }
}
