package com.tfm.demo;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

/**
 * VULNERABILIDAD: SQL Formatting / String Concatenation in SQL
 * 
 * OWASP Top 10 2021: A03:2021 - Injection
 * ISO/IEC 25010: Security - Integrity
 * 
 * Regla SonarQube:
 * - S2077: Formatting SQL queries is security-sensitive (CRITICAL)
 * 
 * Usar String.format(), concatenación o StringBuilder para SQL
 * permite SQL Injection incluso sin parametrización.
 */
public class FormattedSQLQueries {
    
    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/mydb", "user", "pass"
        );
    }
    
    /**
     * Vulnerabilidad S2077: String.format() en SQL
     * Permite SQL Injection vía format string
     */
    public void queryWithFormat(String userId) throws Exception {
        Connection conn = getConnection();
        
        // VULNERABLE: String.format() no previene SQL injection
        String sql = String.format(
            "SELECT * FROM users WHERE id = '%s'",
            userId
        );
        
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql);
        
        // Ataque: userId = "1' OR '1'='1"
        // SQL: SELECT * FROM users WHERE id = '1' OR '1'='1'
    }
    
    /**
     * Vulnerabilidad: StringBuilder con input del usuario
     */
    public void queryWithStringBuilder(HttpServletRequest request) throws Exception {
        String username = request.getParameter("username");
        
        // VULNERABLE: StringBuilder concatena input sin sanitizar
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT email, role FROM users WHERE username = '");
        sql.append(username); // User input!
        sql.append("'");
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql.toString());
        
        // Ataque: username = "admin' --"
        // SQL: SELECT email, role FROM users WHERE username = 'admin' --'
    }
    
    /**
     * Vulnerabilidad: Concatenación directa
     */
    public void queryWithConcatenation(String email) throws Exception {
        // VULNERABLE: Operador + concatena sin escapar
        String sql = "DELETE FROM sessions WHERE user_email = '" + email + "'";
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        
        // Ataque: email = "' OR 1=1 --"
        // SQL: DELETE FROM sessions WHERE user_email = '' OR 1=1 --'
        // Borra TODAS las sesiones
    }
    
    /**
     * Vulnerabilidad: String concatenation en UPDATE
     */
    public void updateWithConcatenation(String userId, String newEmail) throws Exception {
        // VULNERABLE: Ambos parámetros sin sanitizar
        String sql = "UPDATE users SET email = '" + newEmail + 
                    "' WHERE id = " + userId;
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        
        // Ataque: newEmail = "hacker@evil.com' WHERE '1'='1"
        // SQL: UPDATE users SET email = 'hacker@evil.com' WHERE '1'='1' WHERE id = 1
        // Cambia email de TODOS los usuarios
    }
    
    /**
     * Vulnerabilidad: String.format() con múltiples parámetros
     */
    public void insertWithFormat(String name, String role, int salary) throws Exception {
        // VULNERABLE: Format string permite injection
        String sql = String.format(
            "INSERT INTO employees (name, role, salary) VALUES ('%s', '%s', %d)",
            name, role, salary
        );
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        
        // Ataque: name = "John', 'admin', 999999); DROP TABLE employees; --"
    }
    
    /**
     * Vulnerabilidad: ORDER BY dinámico con concatenación
     */
    public void queryWithDynamicOrderBy(String sortColumn) throws Exception {
        // VULNERABLE: Columna de ordenamiento sin validar
        String sql = "SELECT * FROM products ORDER BY " + sortColumn;
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql);
        
        // Ataque: sortColumn = "price; DROP TABLE products; --"
    }
    
    /**
     * Vulnerabilidad: LIKE clause con concatenación
     */
    public void searchWithLike(String searchTerm) throws Exception {
        // VULNERABLE: LIKE pattern sin escapar
        String sql = "SELECT * FROM articles WHERE title LIKE '%" + searchTerm + "%'";
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql);
        
        // Ataque: searchTerm = "%' UNION SELECT password FROM users WHERE '1'='1"
    }
    
    /**
     * Vulnerabilidad: IN clause con String.join()
     */
    public void queryWithInClause(String[] ids) throws Exception {
        // VULNERABLE: String.join() no sanitiza
        String idsStr = String.join(",", ids);
        String sql = "SELECT * FROM orders WHERE id IN (" + idsStr + ")";
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql);
        
        // Ataque: ids = ["1", "2); DROP TABLE orders; --"]
    }
    
    /**
     * Vulnerabilidad: Batch operations con format
     */
    public void batchInsertWithFormat(String[][] data) throws Exception {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        
        for (String[] row : data) {
            // VULNERABLE: Loop con String.format()
            String sql = String.format(
                "INSERT INTO logs (user, action, timestamp) VALUES ('%s', '%s', '%s')",
                row[0], row[1], row[2]
            );
            
            stmt.addBatch(sql);
        }
        
        stmt.executeBatch();
        
        // Ataque: row[1] = "login'); DELETE FROM logs WHERE ('1'='1"
    }
    
    /**
     * Vulnerabilidad: Subquery con concatenación
     */
    public void queryWithSubquery(String department) throws Exception {
        // VULNERABLE: Subquery dinámico
        String sql = "SELECT name FROM employees WHERE dept_id IN " +
                    "(SELECT id FROM departments WHERE name = '" + department + "')";
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql);
        
        // Ataque: department = "IT') OR dept_id IN (SELECT id FROM departments) --"
    }
    
    /**
     * Vulnerabilidad: LIMIT/OFFSET con concatenación
     */
    public void paginationQuery(String page, String pageSize) throws Exception {
        // VULNERABLE: Parámetros numéricos sin validar
        String sql = "SELECT * FROM users LIMIT " + pageSize + " OFFSET " + page;
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery(sql);
        
        // Ataque: pageSize = "10; DROP TABLE users; --"
    }
    
    /**
     * Vulnerabilidad: Stored procedure call con format
     */
    public void callProcedureWithFormat(String userId, String action) throws Exception {
        // VULNERABLE: Llamada a SP con parámetros sin escapar
        String sql = String.format(
            "CALL log_user_action('%s', '%s')",
            userId, action
        );
        
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        
        // Ataque: action = "logout'); DROP PROCEDURE log_user_action; --"
    }
}
