package mksgroup.english.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppUtilityTest2 {

    @Test
    public void testRemovePrefixNo1() {
        String result = AppUtility.removePrefixNo("1. How are you?");
        
        assertEquals("How are you?", result);
        
    }

    @Test
    public void testRemovePrefixNo2() {
        String result = AppUtility.removePrefixNo("12. How are you?");
        
        assertEquals("How are you?", result);
        
    }
}
