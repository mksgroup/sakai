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
        
        // Transcript of Listening and Answer key of Listening
        String lcTranscriptTxtFilePath = FileUtil.buildPath(folderPath, folderName + "_LC_Transcript.txt");
        String rcTxtFilePath = FileUtil.buildPath(folderPath, folderName + "_RC.txt");
        
        // Answer key of reading
        String rcKeyTxtFilePath = FileUtil.buildPath(folderPath, folderName + "_RC_AnswserKey.txt");
        
        String outExcelPath = FileUtil.buildPath(folderPath, folderName + ".xlsx");
        
        try {
            String lcTxt = FileUtil.getContent(new File(lcTxtFilePath), "utf-8");
            String lcTranscriptTxt = FileUtil.getContent(new File(lcTranscriptTxtFilePath), "utf-8");
            String rcTxt = FileUtil.getContent(new File(rcTxtFilePath), "utf-8");
            String rcKeyTxt = FileUtil.getContent(new File(rcKeyTxtFilePath), "utf-8");
            ToeicData lcTd = new ToeicData(lcTxt);
            
            Workbook wb = AppUtility.write(null, null, lcTd);
            
            ToeicData rcTd = new ToeicDataPart567(rcTxt);
            // Write Part 5
            wb = AppUtility.write(wb, null, rcTd);
            
            // Write Part 6
            wb = AppUtility.writePart6(wb, (ToeicDataPart567) rcTd);

            // Write Corrected Answer for Listening
            ToeicTranscriptData transcriptData = new ToeicTranscriptData(lcTd, lcTranscriptTxt, wb);

            // Parse AnswerKey Table for reading
            AnswerKeyParser akParser = new AnswerKeyParser(transcriptData, rcKeyTxt);
            
            wb = AppUtility.writeAnswerKeys(wb, akParser);
            

            
            wb = AppUtility.write(wb, null, transcriptData);
            
            // Part Corrected Answer for Reading
            
            PoiUtil.writeExcelFile(wb, outExcelPath);
        } catch (IOException e) {
            log.error("Could not read content of data.", e);
        }
    }
    
}
