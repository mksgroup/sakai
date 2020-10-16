package mksgroup.english.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import mksgroup.java.common.FileUtil;

public class ToeicDataTest {

    @Test
    public void testToeicData() {
        String inputTextFile = "D:/Temp/TOEIC1/ETS_2020_LC_#1.txt";
        String text;
        try {
            text = FileUtil.getContent(new File(inputTextFile), "utf-8");
            ToeicData td = new ToeicData(text);
            
            assertNotNull(td);
            assertEquals(td.getQuestions().size(), 99);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
    }

}
