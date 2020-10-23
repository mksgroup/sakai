package mksgroup.english.common;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import mksgroup.java.common.CommonUtil;
import mksgroup.java.common.FileUtil;
import mksgroup.java.poi.PoiUtil;

public class PToeicTest {

    private static final int IDX_COL_ANSWER = 6;
    private static final int IDX_COL_FEEDBACK_A = 12;
    private static final int IDX_COL_FEEDBACK_B = 13;
    private static final int IDX_COL_FEEDBACK_C = 14;
    private static final int IDX_COL_FEEDBACK_D = 15;

    @Test
    public void testMain() {
        String folderPath = "D:\\Temp\\TOEIC3\\ETS_2020_N3";

        PToeic.main(new String[] {folderPath});
        
        // Read the result again
        try {
            String excelFileName = FileUtil.getFilename(folderPath);
            String outputExcelPath = FileUtil.buildPath(folderPath, excelFileName);
            Workbook wb = PoiUtil.loadWorkbook(new FileInputStream(outputExcelPath));
            Sheet sheet;
            Row row;
            final String[] PARTS = {"Part1", "Part2", "Part3", "Part4"};
            int firstRowIdx;
            int lastRowIdx;
            Object cellVal;
            String answerkey;
            int questionNo;
            for (String part: PARTS) {
                sheet = wb.getSheet(part);
                firstRowIdx = sheet.getFirstRowNum();
                lastRowIdx = sheet.getLastRowNum();
                
                for (int rowIdx = firstRowIdx; rowIdx <= lastRowIdx; rowIdx++) {
                    row = sheet.getRow(rowIdx);
                    cellVal = PoiUtil.getValue(row, 0);
                    
                    if (cellVal instanceof Double) {
                        questionNo = ((Double) cellVal).intValue();
                        
                        // Check answer key
                        answerkey = (String) PoiUtil.getValue(row, IDX_COL_ANSWER);
                        
                        if (answerkey == null) {
                            System.err.println("Answer key not found at question " + questionNo);
                        }
                        assertNotNull(answerkey);
                        
                        int endColumnFeedback = IDX_COL_FEEDBACK_D;
                        // Check feedback
                        if ("Part2".contentEquals(part)) {
                            endColumnFeedback = IDX_COL_FEEDBACK_C;
                        }
                        for (int idxFeedback = IDX_COL_FEEDBACK_A; idxFeedback <= endColumnFeedback; idxFeedback++) {
                            cellVal = PoiUtil.getValue(row, idxFeedback);

                            if (cellVal == null) {
                                System.err.println("Feedback not found at question " + questionNo);
                            }
                            assertNotNull(cellVal);
                        }
                    }
                }
            }
            
        } catch (FileNotFoundException e) {
            assertFalse(e.getMessage(), false);
        }
    }

}
