package mksgroup.english.common;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import mksgroup.java.common.CommonUtil;

public class TestPattern {

    @Test
    public void testPattern1() {
        String text = "(C) To correct an error on a map\r\n" + 
                "\r\n" + 
                "(D) To complain about traffic noise\r\n" + 
                "\r\n" + 
                " \r\n" + 
                "\r\n" + 
                "GO ON TO THE NEXT PAGE\r\n" + 
                "\r\n" + 
                "TEST 1 25\r\n" + 
                "\r\n" + 
                "L-Lsal\r\n" + 
                "\f44.\r\n" + 
                "\r\n" + 
                "45.\r\n" + 
                "\r\n" + 
                "46.\r\n" + 
                "\r\n" + 
                "47.\r\n" + 
                "\r\n" + 
                "48.\r\n" + 
                "\r\n" + 
                "49.\r\n" + 
                "\r\n" + 
                "26\r\n" + 
                "\r\n" + 
                "Where are the speakers?";
        String pattern = ".*(\\d\\d\\.\r\n[\\d|\\.|\r|\n]*\\d\\d\\.\r\n).*";
        String value = CommonUtil.parsePattern(text, pattern);
        
        System.out.println(value);
        
//        assertTrue(isValid(text));
    }
    
    @Test
    public void testPattern2() {
        String text = "(D) A printing shop \n" + 
                " \n" + 
                "Why does the woman say, “We actually \n" + 
                "have a new company logo”? \n" + 
                " \n" + 
                "(A) To compliment a colleague \n" + 
                " \n" + 
                "";
        String regular = "(.*)\\(A\\)";
        Pattern pattern = Pattern.compile(regular);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String value = matcher.group(1);
            
            System.out.println(value);
        }
        
//        assertTrue(isValid(text));
    }
    public static String parsePattern(String text, String regular) {
        Pattern pattern = Pattern.compile(regular);
        Matcher matcher = pattern.matcher(text);
        String key = null;

        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                System.out.println("Group i=" + matcher.group(i));
            }
            key = matcher.group(1);
        }

        return key;
    }

    private boolean isValid(String question) {
        String listOfQuestionNo = CommonUtil.parsePattern(question, ".*([\\d\\d\\.\r\n]{2, 4}).*");

        return listOfQuestionNo != null;
    }
}
