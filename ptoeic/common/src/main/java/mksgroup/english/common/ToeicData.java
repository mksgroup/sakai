package mksgroup.english.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.CommunicationException;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import m.k.s.sakai.app.question.logic.QuestionData;
import mksgroup.java.common.CommonUtil;

@Slf4j
public class ToeicData {
    
	String text;
	
	int preCurPos = 0;
	/** Current position of processing. */
	int curPos = 0;
	
    final static String[][] POS_MARKS = {
            {"LISTENING_INTRO", "LISTENING TEST", "PART 1"}, 
            {"PART1_INTRO", "PART 1", "\n20\n"}, 
            {"PART2_INTRO", "PART 2", "\n \n\n", "\n\n7."},
            {"PART3_INTRO", "PART 3", "\n32. "}
    };
    
    /**
     * Pattern to detect following question number  
     * 44.

        45.
        
        46.
        
        47.
        
        48.
        
        49.
     */
    final static String PATTERN_NUMBERS2 = ".*(\\d\\d\\.\r\n[\\d|\\.|\r|\n]*\\d\\d\\.\r\n).*";
    final static String PATTERN_NUMBERS1 = ".*(\\d\\d\\.\n[\\d|\\.|\n]*\\d\\d\\.\n).*";
    
    Map<String, String> mapIntro = new HashMap<String, String>();
    Map<Integer, QuestionData> mapQuestionPart34 = new HashMap<Integer, QuestionData>();
    
	public ToeicData(String text) {
		this.text = text;
		
		// Step 1: Parse introductions from Part 1 to Part 3.
		extractIntros();
		
		// Step 2: Parse question in Part 3.
		extractQuestionPart34(32, 55);
		
		// Step 3: Parse question in Part 4.
//		extractIntroPart4();
		
//		extractQuestionPart34(71, 99);
	}
	
    /**
     * Parse content of text to extract introduction content of Part 1, Part 2, and Part 3.
     * @output mapIntro is updated.
     */
    private void extractIntros() {
        String content;

        for (String[] strings : POS_MARKS) {
            content = substring(strings[1], strings[2]);
            mapIntro.put(strings[0], content);
        }
    }
    
    private void extractQuestionPart34(int startIdxQ, int endIdxQ) {
        QuestionData qd;
        List<Integer> predetectQuestionNo;

        // Scan questions Part 3:  32 to 70
        // Scan questions Part 4:  71 to 100
        List<Integer> lstQuestionNo;
        for (int i = startIdxQ; i <= endIdxQ; i++) {
            
            qd = extractNextQuestions(i);
            if (qd != null) {
                // Do nothing
                log.debug("Extracted question " + i + ": " + qd.getQuestion());
            } else {
                // Restore curPos as preCurCus
                log.debug("Could not extract question " + i + ". Try to extract by group.");

                curPos = preCurPos;
                log.debug("Reset current postion back to  " + curPos + ": " + text.substring(curPos, curPos + 20));
                lstQuestionNo = extractGroupOfQuestions();
                
                if (lstQuestionNo != null) {
                    log.debug("Prepare to parse group of questions: " + lstQuestionNo.toString());
                    // Update the current position at the last number
                    int lastQuestionNoOfGroup = i + lstQuestionNo.size() - 1;
                    curPos = text.indexOf(lastQuestionNoOfGroup + ".", curPos);
                    extractGroupOfQuestionContents(lstQuestionNo);
                    
                    // Update i
                    i = lastQuestionNoOfGroup;
                } else {
                    log.warn(String.format("Could not parse from position %d: %s...", curPos, text.substring(curPos, curPos + 20)));
                }
            }

        }
        
    }

    /**
     * Extract list of question no from following text:</br>
     * ...
     * 44.
     * 45.
     * ...
     * @return
     */
    private List<Integer> extractGroupOfQuestions() {
        List<Integer> result;
        String nextText = text.substring(curPos);
        
        // Extract list of question numbers
        String linesOfQuestionNumbers = CommonUtil.parsePattern(nextText, PATTERN_NUMBERS1);
        
        if (linesOfQuestionNumbers == null) {
            return null;
        }
        result = new ArrayList<Integer>();
        String[] strQuestionNumbers = linesOfQuestionNumbers.split(".\n");
        
        // Scan to extract content of questions following number of question
        int questionNo;
        for (String strQuestionNumner: strQuestionNumbers) {
            questionNo = Integer.parseInt(strQuestionNumner.trim());
            result.add(questionNo);
        }
        
        return result;
    }

    private void extractGroupOfQuestionContents(List<Integer> lstQuestionNo) {
        QuestionData qd;
        for (Integer i : lstQuestionNo) {
            qd = extractNextQuestionFromGroup(i);
            if (qd != null) {
                mapQuestionPart34.put(i, qd);
                log.debug("Extracted question " + i + ": " + qd.getQuestion());
            } else {
                log.error("Could not extract questions: " + lstQuestionNo + " from position " + curPos + ": " + text.substring(curPos, curPos + 20));
            }
        }
        
    }
    
    private QuestionData extractNextQuestionFromGroup(int questionNo) {
        QuestionData qd = null;
        List<String> answers;
        // Get question
        String subText = text.substring(curPos);
        String regular = "(\\S.*)\n\n\\(A\\)";
        
        String question = CommonUtil.parsePattern(subText, regular);
        if (question != null && isValid(question)) {
            qd = new QuestionData();
            qd.setQuestion(question);
            
            // Update current position
            answers = new ArrayList<String>(4);
            String option;
            String endSign;
            for (char j = 'A'; j <= 'D'; j++) {
                endSign = j < 'D' ? String.format("(%c)", j + 1) : "\n";
                option = substring(String.format("(%c)", j), endSign);

                answers.add(option);

                // Update answers
                qd.setAnswers(answers);
            }
            
        } else {
            log.error("Could not extract question at " + curPos + ": " + text.substring(curPos, curPos + 20));
        }
     
        return qd;
    }

    /**
     * Extract question with given no from current position.
     * @input curPos
     * @param questionNo
     * @output mapQuestionPart34
     * @return QuestionData
     */
    private QuestionData extractNextQuestions(int questionNo) {
        String startText = questionNo + ". ";
    
        final String endText = "(A) ";
        String question = substring(startText, endText);
        
        if (question != null && isValid(question)) {
            QuestionData qd;
            List<String> answers;

            // Set content of question
            qd = new QuestionData();
            qd.setQuestion(question);
            answers = new ArrayList<String>(4);
            String option;
            String endSign;
            for (char j = 'A'; j <= 'D'; j++) {
              endSign = j < 'D' ? String.format("(%c)", j+1) : "\n";
              option = substring(String.format("(%c)", j), endSign);
              
              answers.add(option);
              
              // Update answers
              qd.setAnswers(answers);
            }
            
            mapQuestionPart34.put(questionNo, qd);
            
            return qd;
        } else {
            return null;
        }
    }

    /**
     * Make sure the question without content such as:<br/>
     * dd.
     * 
     * dd.
     * 
     * @param question
     * @return
     */
    private boolean isValid(String question) {
        String pattern = ".*(\\d\\d\\.\r\n).*";
        String value = CommonUtil.parsePattern(text, pattern);
        
        return value == null;
    }

    public QuestionData getQuestion(Integer questionNo) {
        return mapQuestionPart34.get(questionNo);
    }
    
    public Map<Integer, QuestionData> getQuestions() {
        return mapQuestionPart34;
    }

    private void extractIntroPart4() {
        String content = substring("PART 4", "\nL");
        mapIntro.put("PART4_INTRO", content);
    }
    
	/**
	 * Extract a substring with start text and end text.
	 * @param startText
	 * @param endText
	 * @return
	 */
	private String substring(String startText, String endText) {
    	// Determine the position of startTest
    	int posStart = text.indexOf(startText, curPos);
    	
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

    	// Keep pre-position
    	preCurPos = curPos;

    	// Update position
    	curPos = posEnd + endText.length();

    	return subText;
    }


}
