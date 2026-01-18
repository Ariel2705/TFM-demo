package com.tfm.demo;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests básicos para compilación
 */
public class BasicTest {
    
    @Test
    public void testHardcodedCredentials() {
        HardcodedCredentials hc = new HardcodedCredentials();
        assertTrue(hc.authenticateUser("admin", "P@ssw0rd123!"));
        assertNotNull(hc.getApiKey());
    }
    
    @Test
    public void testWeakRandomness() {
        WeakRandomness wr = new WeakRandomness();
        String token = wr.generateSessionToken();
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    public void testWeakHashing() {
        WeakHashing wh = new WeakHashing();
        String hash = wh.hashPasswordWithMD5("test");
        assertNotNull(hash);
    }
    
    @Test
    public void testRegexVulnerabilities() {
        RegexVulnerabilities rv = new RegexVulnerabilities();
        assertNotNull(rv);
    }
    
    @Test
    public void testExceptionHandlingVulnerabilities() {
        ExceptionHandlingVulnerabilities ehv = new ExceptionHandlingVulnerabilities();
        assertNotNull(ehv);
    }
    
    @Test
    public void testInsecureAuthentication() {
        InsecureAuthentication ia = new InsecureAuthentication();
        assertNotNull(ia);
    }
    
    @Test
    public void testLoggingVulnerabilities() {
        LoggingVulnerabilities lv = new LoggingVulnerabilities();
        assertNotNull(lv);
    }
}
