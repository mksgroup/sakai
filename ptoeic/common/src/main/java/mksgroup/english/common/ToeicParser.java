package mksgroup.english.common;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;

import lombok.extern.slf4j.Slf4j;
import mksgroup.java.common.FileUtil;
import mksgroup.java.poi.PoiUtil;

@Slf4j
public class ToeicParser {
    private String folderPath;
    
    public ToeicParser(String folderPath) {
        this.folderPath = folderPath;
        
        String folderName = FileUtil.getFilename(folderPath);
        String lcTxtFilePath = FileUtil.buildPath(folderPath, folderName + "_LC.txt");
        String rcTxtFilePath = FileUtil.buildPath(folderPath, folderName + "_RC.txt");
        String outExcelPath = FileUtil.buildPath(folderPath, folderName + ".xlsx");
        
        try {
            String lcTxt = FileUtil.getContent(new File(lcTxtFilePath), "utf-8");
            String rcTxt = FileUtil.getContent(new File(rcTxtFilePath), "utf-8");
            ToeicData lcTd = new ToeicData(lcTxt);
            
            Workbook wb = AppUtility.write(null, null, lcTd);
//            PoiUtil.writeExcelFile(wb, outExcelPath);
            
            ToeicData rcTd = new ToeicDataPart56(rcTxt);
            // Write Part 5
            wb = AppUtility.write(wb, null, rcTd);
            
            // Write Part 6
            wb = AppUtility.writePart6(wb, (ToeicDataPart56) rcTd);
            
            PoiUtil.writeExcelFile(wb, outExcelPath);
        } catch (IOException e) {
            log.error("Could not read content of data.", e);
        }
    }
    
}
