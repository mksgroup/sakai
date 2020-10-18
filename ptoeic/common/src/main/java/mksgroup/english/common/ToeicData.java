package mksgroup.english.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // final static String PATTERN_NUMBERS2 = ".*(\\d\\d\\.\r\n[\\d|\\.|\r|\n]*\\d\\d\\.\r\n).*";
    final static String PATTERN_NUMBERS2 = ".*(\\d\\d\\.\n[\\d\\.,\n\\s]*\\d\\d\\.\n).*";
    final static String PATTERN_NUMBERS3 = "(\\d\\d\\d\\.\n).*";
    
    Map<String, String> mapIntro = new HashMap<String, String>();
    Map<Integer, QuestionData> mapQuestion = new HashMap<Integer, QuestionData>();
    
	public ToeicData(String text) {
		this.text = text;
		
		// Step 1: Parse introductions from Part 1 to Part 3.
		extractIntros();
		
		// Step 2: Parse question in Part 3.
		extractQuestionPart34(32, 70);
		
		// Step 3: Parse question in Part 4.
		extractIntroPart4();
		
		extractQuestionPart34(71, 100);
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

        // Scan questions Part 3:  32 to 70
        // Scan questions Part 4:  71 to 100
        List<Integer> lstQuestionNo;
        int i = startIdxQ;
        int nextCurPos;
        while (i <= endIdxQ) {
  
            qd = extractNextQuestions(i);
            if (qd != null) {
                // Do nothing
                log.debug("Extracted question " + i + ": " + qd.getQuestion());
            } else {
                // Restore curPos as preCurCus
                log.debug("Could not extract question " + i + ". Try to extract by group.");

                curPos = preCurPos;

                log.debug("Reset current postion back to  " + curPos + ": " + text.substring(curPos, curPos + 20));
                lstQuestionNo = extractGroupOfQuestions(i);
                
                if (lstQuestionNo != null) {
                    log.debug("Prepare to parse group of questions: " + lstQuestionNo.toString());
                    // Update the current position at the last number
                    int lastQuestionNoOfGroup = lstQuestionNo.get(lstQuestionNo.size() - 1);
                    nextCurPos = text.indexOf(lastQuestionNoOfGroup + ".", curPos);
                    
                    if (nextCurPos == -1) {
                        log.error("Could not extract question " + lstQuestionNo + " at position " + curPos);
                    } else {
                        curPos = nextCurPos;
                        extractGroupOfQuestionContents(lstQuestionNo);
                    }
                    
                    // Update i
                    i = lastQuestionNoOfGroup;
                } else {
                    log.warn(String.format("Could not parse from position %d: %s...", curPos, text.substring(curPos, curPos + 20)));
                }
            }
            
            i++;
        }
        
    }

    /**
     * Extract list of question no from following text:</br>
     * ...
     * 44.
     * 45.
     * ...
     * <br/>
     * End of number maybe: . or ,
     * @param curQNo current question no.
     * @return
     */
    private List<Integer> extractGroupOfQuestions(int curQNo) {
        List<Integer> result;
        String nextText = text.substring(curPos);

        if (curQNo == 44 || curQNo == 100) {
            log.debug("");
        }
        
        // Extract list of question numbers
        String pattern = (curQNo < 100 ) ? PATTERN_NUMBERS2 : PATTERN_NUMBERS3;
        String linesOfQuestionNumbers = CommonUtil.parsePattern(nextText, pattern);


        if (linesOfQuestionNumbers == null) {
            return null;
        }

        linesOfQuestionNumbers = linesOfQuestionNumbers.replace(',', '.');
        

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
            if (i == 80) {
                log.debug("");
            }
            qd = extractNextQuestionFromGroup(i);
            if (qd != null) {
                mapQuestion.put(i, qd);
                log.debug("Extracted question " + i + ": " + qd.getQuestion());
            } else {
                log.error("Could not extract questions: " + lstQuestionNo + " from position " + curPos + ": " + text.substring(curPos, curPos + 20));
            }
        }
        
    }
    
    private QuestionData extractNextQuestionFromGroup(int questionNo) {
        QuestionData qd = null;
        List<String> answers;
        String question = AppUtility.clean(substring(null, "(A)"));
        
        if (questionNo == 80) {
            log.debug("Question=" + questionNo);
        }
        if (question != null && isValid(question)) {
            qd = new QuestionData();
            qd.setQuestion(question);
            
            // Update current position
            answers = extractAnswers();
            
            // Update answers
            qd.setAnswers(answers);
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
        String startText = questionNo + ".";
    
        final String endText = "(A) ";

        if (questionNo == 44) {
            log.debug("");
        }
        String question = substring(startText, endText);
        

        if (question != null && isValid(question)) {
            QuestionData qd;
            List<String> answers;

            // Set content of question
            qd = new QuestionData();
            qd.setQuestion(question);
            answers = extractAnswers();

            qd.setAnswers(answers);
            
            if (questionNo == 44) {
                log.debug("");
            }
            mapQuestion.put(questionNo, qd);
            
            return qd;
        } else {
            return null;
        }
    }

    /**
     * [Give the description for method].
     * @return
     */
    private List<String> extractAnswers() {
        List<String> answers;
        answers = new ArrayList<String>(4);
        String option;
        String startSign;
        String endSign;
        for (char j = 'A'; j <= 'D'; j++) {
            endSign = j < 'D' ? String.format("(%c)", j + 1) : "\n";
            startSign = String.format("(%c)", j);
            option = substring(startSign, endSign);

            // Remove the prefix: (A)..(D)
            option = option.substring(startSign.length()).trim();

            answers.add(option);
        }

        return answers;
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
        String pattern = PATTERN_NUMBERS2;
        String value = CommonUtil.parsePattern(question, pattern);
        
        return value == null;
    }

    public QuestionData getQuestion(Integer questionNo) {
        return mapQuestion.get(questionNo);
    }
    
    public Map<Integer, QuestionData> getQuestions() {
        return mapQuestion;
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
    	int posStart = (startText != null) ? text.indexOf(startText, curPos) : (curPos + 1);
    	
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
    	curPos = posEnd;

    	return subText;
    }


}
