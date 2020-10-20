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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import mksgroup.java.common.FileUtil;
import mksgroup.java.poi.PoiUtil;

/**
 * An example Utility.
 * 
 * @author Thach Ngoc Le (ThachLN@mks.com.vn)
 *
 */
@Slf4j
public class AppUtility {
    private static final int IDX_COL_ANSWER_A = 7;
    private static final int IDX_COL_FEEDBACK_A = 12;
    /** For logging. */
    private static final Logger LOG = Logger.getLogger(AppUtility.class);


    public static String ENGLISH_WORDS;
    static {
        try {
            ENGLISH_WORDS = FileUtil.getContent("/words.txt", "utf-8");
        } catch (IOException e) {
            log.error("Could not read English work file words.txt", e);
        }
    }

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
    public static Workbook write(Workbook currentWb, String templateFilePath, ToeicData toeicData) throws IOException {
        Workbook wb;
        
        if (currentWb != null) {
            wb = currentWb;
        } else {
            wb = (templateFilePath == null) ? PoiUtil.loadWorkbookByResource("/Template_TOEIC.xlsx"):
                                                       PoiUtil.loadWorkbook(new FileInputStream(templateFilePath));
        }

        final String[] PARTS = {"Part1", "Part2", "Part3", "Part4", "Part5"};
        Sheet sheet;
        // Scan question no
        int lastRowIdx;
        Row row;;
        Object questionNoObj;
        Integer questionNo;
        QuestionData questionData;
        int answerColIdx;
        int feedbackColIdx;

        for (String part : PARTS) {
            sheet = wb.getSheet(part);
            lastRowIdx = sheet.getLastRowNum();
            
            // Start at row 1
            for (int idxRow = 1; idxRow <= lastRowIdx; idxRow++) {
                row = sheet.getRow(idxRow);
                questionNoObj = PoiUtil.getValue(row, 0);
                
                if (questionNoObj instanceof Double) {
                    questionNo = ((Double) questionNoObj).intValue();
                    
                    questionData = toeicData.getQuestion(questionNo);
                    if (questionData != null) {
                        // Write the content of question
                        if (questionData.getQuestion() != null) {
                            PoiUtil.setContent(row, 1, questionData.getQuestion());
                        }
                        
                        // Write the answer key
                        if (questionData.getAnswers() != null) {
                            answerColIdx = IDX_COL_ANSWER_A; // Column index of A
                            for (String answer : questionData.getAnswers()) {
                                PoiUtil.setContent(row, answerColIdx++, answer);
                            }
                        }
                        
                        // write feedback
                        if (questionData.getFeedbacks() != null) {
                            feedbackColIdx = IDX_COL_FEEDBACK_A;
                            for (String feedback: questionData.getFeedbacks()) {
                                PoiUtil.setContent(row, feedbackColIdx++, feedback);
                            }
                        }
                    } else {
                        log.warn("Question not found at i = " + questionNo);
                    }
                } else if (questionNoObj != null) {
                    LOG.warn("Unknown data type:" + questionNoObj.getClass());
                }
   
            }
        }
        
//        PoiUtil.writeExcelFile(wb, outExcelPath);
        return wb;
        
    }
    
    public static Workbook writePart6(Workbook wb, ToeicDataPart56 toeicData) throws IOException {
        Sheet sheet;
        Row row;
        Object questionNoObj;
        Integer questionNo;
        QuestionData questionData;
        int answerColIdx;

        int lastRowIdx;

        final String[] PARTS = {"Part6", "Part7"};
        
        for (String part : PARTS) {
            sheet = wb.getSheet(part);
            lastRowIdx = sheet.getLastRowNum();
            
            // Start at row 1
            for (int idxRow = 1; idxRow <= lastRowIdx; idxRow++) {
                row = sheet.getRow(idxRow);
                questionNoObj = PoiUtil.getValue(row, 0);
                
                if (questionNoObj instanceof Double) {
                    questionNo = ((Double) questionNoObj).intValue();
    
                    questionData = toeicData.getQuestion(questionNo);
                    if (questionData != null) {
                        // Don't write the content of sub question
                        if ("Part6".contentEquals(part) ) {
                            // Do nothing
                        } else if ("Part7".contentEquals(part)) {
                            PoiUtil.setContent(row, 1, questionData.getQuestion());
                        }
                        
                        answerColIdx = 7; // Column index of A
                        for (String answer : questionData.getAnswers()) {
                            PoiUtil.setContent(row, answerColIdx, answer);
                            answerColIdx++;
                        }
                    } else {
                        log.warn("Question not found at i = " + questionNo);
                    }
                } else if (questionNoObj == null && "Part6".contentEquals(part) ) {
                    // Write the main contain of question
                    // Detect next sub questions
                    List<Integer> nextQuestions = nextSubQuestions(sheet, idxRow);
                    int firstNo = nextQuestions.get(0);
                    int lastNo = nextQuestions.get(nextQuestions.size() - 1);
                    String mainQuestionKey = String.format("%s-%s", firstNo, lastNo);
                    questionData = toeicData.mapQPart6.get(mainQuestionKey);
                    
                    // Write to Excel
                    PoiUtil.setContent(row, 1, questionData.getQuestion());
                } else if (questionNoObj != null) {
                    LOG.warn("Unknown data type:" + questionNoObj.getClass());
                } else {
                    // Do nothing.
                }
    
            }
        }
        
        return wb;
    }
    
    public static Workbook writeAnswerKeys(Workbook wb, ToeicData transcriptData) {
        final String[] PARTS = {"Part1", "Part2", "Part3", "Part4"};
        Sheet sheet;
        // Scan question no
        int lastRowIdx;
        Row row;;
        Object questionNoObj;
        Integer questionNo;
        QuestionData qd;

        for (String part : PARTS) {
            sheet = wb.getSheet(part);
            lastRowIdx = sheet.getLastRowNum();
            
            // Start at row 1
            for (int idxRow = 1; idxRow <= lastRowIdx; idxRow++) {
                row = sheet.getRow(idxRow);
                questionNoObj = PoiUtil.getValue(row, 0);
                
                if (questionNoObj instanceof Double) {
                    questionNo = ((Double) questionNoObj).intValue();
                    
                    qd = transcriptData.mapQuestion.get(questionNo);
                    if (qd != null) {
                        PoiUtil.setContent(row, 6, qd.getCorrectAnswer());
                    } else {
                        log.warn("Not found key anwser of question " + questionNo);
                    }
                } else if (questionNoObj != null) {
                    // Do nothing
                }
   
            }
        }
        
        return wb;
    }
    
    static List<Integer> nextSubQuestions(Sheet sheet, int idxCurRow) {
        List<Integer> listQuesionNo = new ArrayList<Integer>();
        Row row;
        Object questionNoObj = null;
        int questionNo;

        // Net row
        do {
            idxCurRow++;
            row = sheet.getRow(idxCurRow);
            
            if (row != null) {
                questionNoObj = PoiUtil.getValue(row, 0);
    
                if (questionNoObj instanceof Double) {
                    questionNo = ((Double) questionNoObj).intValue();
                    listQuesionNo.add(questionNo);
                }
            } else {
                log.error(String.format("Could not get row at %d of sheet '%s'", idxCurRow, sheet.getSheetName()));
            }
        } while ((questionNoObj != null) && (questionNoObj instanceof Double) && (row != null));
        
        return listQuesionNo;
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
    public static String removePrefixNo(String question) {
        Pattern p = Pattern.compile("(\\d{2,3}\\.\\s)");
        return p.matcher(question).replaceAll("");
    }

    public static boolean checkEnglishWord(String word) {
        boolean valid;

        if (word.startsWith("OK?")) {
            log.debug("");
        }
        // Void special charaters: ' ? 
        String[] subwords = word.split("[’'\\?\\.;,\\(\\)]");
        
        if (subwords != null && subwords.length > 0) {
            valid = ENGLISH_WORDS.contains(subwords[0].toLowerCase() + '\n');
        } else {
            valid = ENGLISH_WORDS.contains(word.toLowerCase()  + '\n');
        }

        return valid;
    }

}
