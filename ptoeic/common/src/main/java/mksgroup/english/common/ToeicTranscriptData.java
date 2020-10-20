package mksgroup.english.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.extern.slf4j.Slf4j;
import m.k.s.sakai.app.question.logic.QuestionData;
import mksgroup.java.poi.PoiUtil;

@Slf4j
public class ToeicTranscriptData extends ToeicData {

    private Workbook wb;

    public ToeicTranscriptData(ToeicData lcTd, String lcTranscriptTxt, Workbook wb) {
        this.mapQuestion = lcTd.mapQuestion;
        this.text = lcTranscriptTxt;
        this.wb = wb;
        extractCorrectedAnswer(1, 100);
        
        extractTranscriptPart12(1, 31);
        
        extractTranscriptPart3(32, 70);
    }

    private void extractTranscriptPart12(int startIdxQ, int endIdxQ) {
        QuestionData qd;
        List<String> feedbacks;
        for (int questionNo = startIdxQ; questionNo <= endIdxQ; questionNo++) {
            if (questionNo == 2) {
                log.debug("");
            }
            String regular = questionNo + "\\s[WwMm\n][-\\w\\W]";
            Pattern pattern = Pattern.compile(regular);

            Matcher matcher = pattern.matcher(text);
            
            if (matcher.find()) {
                qd = mapQuestion.get(questionNo);
                
                if (qd == null) {
                    qd = new QuestionData();
                }

                // Move to end of position of question no.
                if (questionNo > 1) {
                    curPos = matcher.end();
                }
                // Part 2
                if (6 < questionNo && questionNo < 32) {
                    feedbacks = extractAnswers('A', 'C');
                } else {
                    feedbacks = extractAnswers();
                }
                
                qd.setFeedbacks(feedbacks);
                
                // Update map
                mapQuestion.put(questionNo, qd);
            }
        }
    }
    
    /**
     * [Give the description for method].
     * @input wb workbook
     * @param startIdxQ
     * @param endIdxQ
     */
    private void extractTranscriptPart3(int startIdxQ, int endIdxQ) {
        Sheet sheet = wb.getSheet("Part3");
        Row row;
        Object questionNoObj;
        Integer questionNo;
        QuestionData questionData;
        int answerColIdx;

        String currentFeedback = null;
        int firstQuestionNo;
        
        // Scan question no in sheet Part3 of workbook
        int lastRowIdx = sheet.getLastRowNum();
        // Start at row 1
        for (int idxRow = 1; idxRow <= lastRowIdx; idxRow++) {
            row = sheet.getRow(idxRow);
            questionNoObj = PoiUtil.getValue(row, 0);
            
            if (questionNoObj == null) {
                // Write the main contain of question
                // Detect next sub questions
                List<Integer> nextQuestions = AppUtility.nextSubQuestions(sheet, idxRow);
                firstQuestionNo = nextQuestions.get(0);
                int lastNo = nextQuestions.get(nextQuestions.size() - 1);
                String mainQuestionKey = String.format("%s-%s", firstQuestionNo, lastNo);
                currentFeedback = extractTranscript(mainQuestionKey);
                
                if (currentFeedback != null) {
                    // Update the map
                    List<String> lstFeedback = new ArrayList<String>();
                    lstFeedback.add(currentFeedback);
                    mapQuestion.get(firstQuestionNo).setFeedbacks(lstFeedback);
                } else {
                    log.warn("Could not extract feedback for group question:" + mainQuestionKey);
                }

            } else if (questionNoObj instanceof Double) {
                questionNo = ((Double) questionNoObj).intValue();

                questionData = mapQuestion.get(questionNo);
                if (questionData != null) {
                    // Make sure the feedback of exact question
                    if (currentFeedback.contentEquals(questionData.getFeedback(0))) {
                        PoiUtil.setContent(row, 12, questionData.getQuestion());
                    } else {
                        log.warn("Unexact feedback at question " + questionNo);
                    }
                } else {
                    log.warn("Question not found at i = " + questionNo);
                }
            } 

        }

    }

    private String extractTranscript(String groupQuestionNo) {
        
        
//        String transcript = substring(groupQuestionNo, ".\n\n\n");
        String transcript = extractDialog(groupQuestionNo);
        
        // Detect to remove some special characters
//        transcript = transcript.replace('|', 'I');
//        String regex = "[MW]-";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(transcript);
        
//        if (matcher.find()) {
////            transcript = AppUtility.extractDialog(text, matcher.start());
//        }
//        
        return transcript;
    }

    private void extractCorrectedAnswer(int startIdxQ, int endIdxQ) {
        
        String answerKeyTable = substring("1(", "\n(A) ");
        
        answerKeyTable = answerKeyTable.trim();
        
        String remainText = answerKeyTable;
        int endPos = 0;
        int questionNo;
        while (remainText.length() > 0) {
            String regular = "(\\d{1,3})\\s*\\(([ABCD])\\)";
            Pattern pattern = Pattern.compile(regular);
            Matcher matcher = pattern.matcher(remainText);
            
            if (matcher.find()) {
                String strQuestionNo = matcher.group(1);
                questionNo = Integer.parseInt(strQuestionNo);
                String key = matcher.group(2);
                
                QuestionData qd = mapQuestion.get(questionNo);
                if (qd == null) {
                    qd = new QuestionData();
                }
                qd.setCorrectAnswer("" + key.charAt(0));
                // Update map
                mapQuestion.put(questionNo, qd);
                
                endPos = matcher.end();
                remainText = remainText.substring(endPos);
            } else {

                log.error("Could not extract answer key from position " + endPos + ": " + remainText);
                break;
            }
        }
    }

}
