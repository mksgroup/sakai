package mksgroup.english.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppUtilityTest3 {

    @Test
    public void testRemovePrefixNo() {
        String result = AppUtility.removePrefixNo("123. What is your name?");
        
        assertEquals("What is your name?", result);
    }

    
    @Test
    public void testRemovePrefixNo2() {
        String result = AppUtility.removePrefixNo("12. What is your name?");
        
        assertEquals("What is your name?", result);
    }
    
    @Test
    public void testRemovePrefixNo3() {
        String result = AppUtility.removePrefixNo("1. What is your name?");
        
        assertEquals("What is your name?", result);
    }
}
