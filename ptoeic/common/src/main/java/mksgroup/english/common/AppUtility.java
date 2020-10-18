/**
 * Licensed to MKS Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * MKS Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package mksgroup.english.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;
import m.k.s.sakai.app.question.logic.QuestionData;
import mksgroup.english.common.AppUtility;
import mksgroup.java.common.CommonUtil;
import mksgroup.java.poi.PoiUtil;

/**
 * An example Utility.
 * 
 * @author Thach Ngoc Le (ThachLN@mks.com.vn)
 *
 */
@Slf4j
public class AppUtility {
    /** For logging. */
    private static final Logger LOG = Logger.getLogger(AppUtility.class);


    
    public static String substring(String text, String startText, String endText) {
    	// Determine the position of startTest
    	int posStart = text.indexOf(startText);
    	
    	if (posStart == -1) {
    		return null;
    	}
    	
    	int posEnd = text.indexOf(endText, posStart);
    	
    	if (posEnd == -1) {
    		return null;
    	}
    	
    	String subText = text.substring(posStart, posEnd);
    	
    	if (subText != null) {
    		subText = subText.trim();
    	}

    	return subText;
    }
    /**
     * Parse a XML File into a document model.
     * @param xmlFile file of xml.
     * @return Document model if no error.
     */
    public static Document parseXML(File xmlFile) {
        Document xmlDoc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
            xmlDoc = db.parse(xmlFile);
        } catch (ParserConfigurationException ex) {
            LOG.error("Could not parse the file.", ex);
        } catch (SAXException ex) {
            LOG.error("Error in XML file.", ex);
        } catch (IOException ex) {
            LOG.error("Could not open file.", ex);
        }
        
        return xmlDoc;
    }

    /**
     * Write Part3, Part4 to Excel.
     * @param toeicData
     * @param outExcelPath
     * @throws IOException
     */
    public static void write(ToeicData toeicData, String outExcelPath) throws IOException {
        Workbook wb = PoiUtil.loadWorkbookByResource("/Template_TOEIC.xlsx");
        final String[] PARTS = {"Part3", "Part4"};
        Sheet sheet;
        // Scan question no
        int lastRowIdx;
        Row row;;
        Object questionNoObj;
        Integer questionNo;
        QuestionData questionData;
        int answerColIdx;

        for (String part : PARTS) {
            sheet = wb.getSheet(part);
            lastRowIdx = sheet.getLastRowNum();
            
            for (int i = 2; i <= lastRowIdx; i++) {
                row = sheet.getRow(i);
                questionNoObj = PoiUtil.getValue(row, 0);
                
                if (questionNoObj instanceof Double) {
                    questionNo = ((Double) questionNoObj).intValue();
                    
                    if (31 < questionNo && questionNo < 101) {

                        questionData = toeicData.getQuestion(questionNo);
                        if (questionData != null) {
                            PoiUtil.setContent(row, 1, questionData.getQuestion());
                            
                            answerColIdx = 7; // Column index of A
                            for (String answer : questionData.getAnswers()) {
                                PoiUtil.setContent(row, answerColIdx, answer);
                                answerColIdx++;
                            }
                        } else {
                            log.warn("Question not found at i = " + questionNo);
                        }
                    } else {
                        log.warn("Has not processed question " + questionNo);
                    }
                } else if (questionNoObj != null) {
                    LOG.warn("Unknown data type:" + questionNoObj.getClass());
                }
   
            }
        }
        
        PoiUtil.writeExcelFile(wb, outExcelPath);
        
    }
    
    /**
     * Remove special character in prefix of question.<br/>
     * For example:
     * 9.

       26

       Where are the speakers?
     * @param substring
     * @return
     */
    public static String clean(String st) {
        String result;
        int i = 0;
        boolean hasSpecialChar = true;
        char c;
        int len = (st != null) ? st.length() : 0;
        do {
            c = st.charAt(i);
            hasSpecialChar = Character.isDigit(c) || c == '\n' || c == '.';
            i++;
        } while (i < len && hasSpecialChar);
        
        // Replace Enter character by Space
        result = (hasSpecialChar) ? st: st.substring(i - 1);
        result = result.replace("\r\n", " ");
        result = result.replace("\n", " ");

        return result;
    }
}
