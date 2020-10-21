package mksgroup.sakai.app.toeicbuilder.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import mksgroup.java.common.CommonUtil;
import mksgroup.java.poi.PoiUtil;

class ExamBuilderServiceTest {

    @Test
    void testParseExam1() {
        String folder = "D:\\Projects\\TOEIC-tools-templates\\TOEIC_DATA_TEMPLATE";
        String rootUrl = "/access/content/group/c508c733-cbd9-4163-9709-e46f338306f6/TOEIC/ETS_2020_N1";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
            
            // Check replacing ${rootURL}
            Workbook wb = PoiUtil.loadWorkbook(new FileInputStream(outputExcelFilePath));
            checkOutput(wb);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private void checkOutput(Workbook wb) {

        Object[] location = search("${rootURL}", wb, 1);
        if (location != null) {
            System.err.println(String.format("Pattern ${rootURL} found at (sheetname, row, col): (%s, %d, %d)", location[0], location[1], location[2]));
        }
        assertNull(location);
    }
    
    /**
     * Search cell contain a given text.
     * @param text string will searched.
     * @param wb Workbook of Spreadsheet
     * @param searchType 0: equal search; 1: containing search.
     * @return If found array with [0] is sheet name, [1] is row index, [2] is column index.<br/>
     * Otherwise, null.
     */
    public static Object[] search(String text, Workbook wb, int searchType) {
        int nSheet = wb.getNumberOfSheets();

        Sheet sheet;
        String strCellVal;
        for (int i = 0; i < nSheet; i++) {
            sheet = wb.getSheetAt(i);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING) {
                        strCellVal = cell.getRichStringCellValue().getString().trim();
                        switch (searchType) {
                            case 0 :
                                if (strCellVal.contentEquals(text)) {
                                    return new Object[] {sheet.getSheetName(), row.getRowNum() + 1, cell.getColumnIndex() + 1};  
                                }
                                break;
                            case 1:
                                if (strCellVal.contains(text)) {
                                    return new Object[] {sheet.getSheetName(), row.getRowNum() + 1, cell.getColumnIndex() + 1};  
                                }
                            default :
                                break;
                        }
                        
                    }
                }
            }               

        }

        return null;        
    }

    @Test
    void testParseExam2() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 2-20180918T131630Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%202-20180918T131630Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam3() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 3-20180918T132714Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%203-20180918T132714Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam4() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 4-20180918T133655Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%204-20180918T133655Z-001%20-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam5() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 5-20180918T134523Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%205-20180918T134523Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    
    @Test
    void testParseExam6() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 6-20180918T134937Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%206-20180918T134937Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam7() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 7-20180918T141039Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%207-20180918T141039Z-001_Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    
    @Test
    void testParseExam8() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 8-20180920T024459Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%208-20180920T024459Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam9() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 9-20180920T024626Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%209-20180920T024626Z-001%20-%20Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam10() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 10-20180920T024657Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%2010-20180920T024657Z-001_Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
