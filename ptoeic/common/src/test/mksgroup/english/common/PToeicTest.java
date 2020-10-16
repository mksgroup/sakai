package mksgroup.english.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class PToeicTest {

    @Test
    public void testMain() {
        PToeic.main(new String[] {"D:/Temp/TOEIC1/ETS_2020_LC_#1.txt", "D:/Temp/TOEIC1/ETS_2020_LC_#1.xlsx"});
    }

}
