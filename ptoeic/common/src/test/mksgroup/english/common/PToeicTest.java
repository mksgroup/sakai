package mksgroup.english.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class PToeicTest {

    @Test
    public void testMain() {
        String folderPath = "D:\\Temp\\TOEIC1\\ETS_2020_#1";

        PToeic.main(new String[] {folderPath});
    }

}
