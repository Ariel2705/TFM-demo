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
}
