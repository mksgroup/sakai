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
        preprocess();
        this.wb = wb;
        extractCorrectedAnswer(1, 100);
        
        extractTranscriptPart12(1, 31);
        
        extractTranscriptPart34("Part3", 32, 70);
        extractTranscriptPart34("Part4", 71, 100);
    }

    private void extractTranscriptPart12(int startIdxQ, int endIdxQ) {
        QuestionData qd;
        List<String> feedbacks;
        for (int questionNo = startIdxQ; questionNo <= endIdxQ; questionNo++) {
            if (questionNo == 2) {
                log.debug("");
            }
            String regular = questionNo + "[\\s]++[WwMm\n]+[-\\w\\W]";
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
    private void extractTranscriptPart34(String partName, int startIdxQ, int endIdxQ) {
        Sheet sheet = wb.getSheet(partName);
        Row row;
        Object questionNoObj;
        Integer questionNo;
        QuestionData questionData;

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
                
                if ("44-46".contentEquals(mainQuestionKey)) {
                    log.debug("");
                }
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
        String transcript = extractDialog(groupQuestionNo);

        transcript = posprocess(transcript);
        return transcript;
    }

}
