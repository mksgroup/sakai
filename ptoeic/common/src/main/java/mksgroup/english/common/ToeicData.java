package mksgroup.english.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import m.k.s.sakai.app.question.logic.QuestionData;
import mksgroup.java.common.CommonUtil;

@Slf4j
public class ToeicData {
    
	public static final String WORD_SEPARATOR = " \t\n\r\f,.!?-—";

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
    final static String PATTERN_NUMBERS3 = ".*(\\d\\d\\d[\\.,]\n[\\d\\d\\.,\n\\s]*\\d\\d\\d[\\.,]\n).*";
    
    /** Detect question 100. in part 4. . */
    final static String PATTERN_NUMBERS_100 = "(\\d\\d\\d\\.\n).*";
    
    Map<String, String> mapIntro = new HashMap<String, String>();
    Map<Integer, QuestionData> mapQuestion = new HashMap<Integer, QuestionData>();
    
    public ToeicData() {
        
    }
	public ToeicData(String text) {
		this.text = text;
		
		preprocess();
        
		// Step 1: Parse introductions from Part 1 to Part 3.
		extractIntros(POS_MARKS);
		
		// Step 2: Parse question in Part 3.
		extractQuestionPart345(32, 70);
		
		// Step 3: Parse question in Part 4.
		extractIntroPart4();
		
		extractQuestionPart345(71, 100);
	}

    void preprocess() {

        // Pre-processing: replace \n \n by \n\n
		this.text = this.text.replace("\n \n", "\n\n");
		this.text = this.text.replace("\r", "");
		this.text = this.text.replace("w-Am", "W-Am");

    }
	
    /**
     * Parse content of text to extract introduction content of Part 1, Part 2, and Part 3.
     * @param posMarks array 2D describes the markers of sections.
     * @output mapIntro is updated.
     */
    void extractIntros(String[][] posMarks) {
        String content;

        for (String[] strings : posMarks) {
            content = substring(strings[1], strings[2]);
            mapIntro.put(strings[0], content);
        }
    }
    
    void extractQuestionPart345(int startIdxQ, int endIdxQ) {
        QuestionData qd;

        // Scan questions Part 3:  32 to 70
        // Scan questions Part 4:  71 to 100
        List<Integer> lstQuestionNo;
        int i = startIdxQ;
        int nextCurPos;
        while (i <= endIdxQ) {
  
            if (i ==  62) {
                log.debug("");
            }
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
                        // try to address the last significant by patter nnn,
                        nextCurPos = text.indexOf(lastQuestionNoOfGroup + ",", curPos);
                    }
                    
                    if (nextCurPos == -1) {
                        String msg = "Could not extract question " + lstQuestionNo + ";question no=" + i + " at position " + curPos;
                        log.error(msg);
                        throw new RuntimeException(msg);
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

        if (curQNo == 109) {
            log.debug("");
        }
        
        // Extract list of question numbers
        String pattern = (curQNo < 100 ) ? PATTERN_NUMBERS2 :
                        (curQNo == 100 ) ? PATTERN_NUMBERS_100 : PATTERN_NUMBERS3;
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
            if (i == 109) {
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
        
        if (questionNo == 109) {
            log.debug("Question=" + questionNo);
        }
        if (question != null && isValid(question, questionNo)) {
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
    QuestionData extractNextQuestions(int questionNo) {
        String startText = questionNo + ".";
    
        final String endText = "(A) ";

        if (questionNo > 100) {
            log.debug("");
        }
        String question = substring(startText, endText);
        

        if (question != null && isValid(question, questionNo)) {
            QuestionData qd;
            List<String> answers;
            
            // Remove prefix question no.
            question = AppUtility.removePrefixNo(question);
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

    List<String> extractAnswers() {
        return extractAnswers('A', 'D');
    }
    /**
     * [Give the description for method].
     * @return
     */
    List<String> extractAnswers(char startOption, char endOption) {
        List<String> answers;
        answers = new ArrayList<String>(endOption - startOption + 1);
        String option;
        String startSign;
        String endSign;
        
        for (char i = startOption; i <= endOption; i++) {
            endSign = i < endOption ? String.format("(%c)", i + 1) : "\n";
            startSign = String.format("(%c)", i);
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
     * @param content of question
     * @param questionNo order number of question
     * @return
     */
    private boolean isValid(String question, int questionNo) {
        if (questionNo == 109) {
            log.debug("");
        }
        String pattern = questionNo < 100 ? PATTERN_NUMBERS2: PATTERN_NUMBERS3;
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
        String content = substring("PART 4", ".\n\n");
        mapIntro.put("PART4_INTRO", content);
    }

    void extractCorrectedAnswer(int startIdxQ, int endIdxQ) {
        
        String answerKeyTable;
        
        // Try to address start of answer key table
        int startPosAnswerKeyTable = text.indexOf(startIdxQ + " (");
        if (-1 < startPosAnswerKeyTable && startPosAnswerKeyTable < 100) {
            answerKeyTable = substring(startIdxQ + " (", "\n(A) ");
        } else if (endIdxQ == 100) {
            answerKeyTable = substring(startIdxQ + "(", "\n(A) ");
        } else {
            // For Reading
            answerKeyTable = text;
        }
        
        if (answerKeyTable == null) {
            throw new RuntimeException(String.format("Could to extract answerkey table from question from %d to %d", startIdxQ, endIdxQ));
        }
        
        if (startIdxQ > 100 ) {
            log.debug("");
        }
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

	/**
	 * Extract a substring with start text and end text.
	 * @param startText
	 * @param endText
	 * @return
	 */
	String substring(String startText, String endText) {
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

    	subText = posprocess(subText);
    	
    	return subText;
    }

    String posprocess(String st) {
        // Post-preprocessing;
        st = st.replace(" | ", " I ");
    	st = st.replace("| ", "I ");
    	st = st.replace(" |", " I");

        return st;
    }

    String extractDialog(String groupQuestionNo) {
        StringBuffer sb = new StringBuffer();

        // Start at line of groupQuestion no such as: 38-40 39! CHS}
        int idxStart = text.indexOf(groupQuestionNo, curPos);
        
        if (idxStart > -1) {
            int idxNextEndline = text.indexOf('\n', idxStart);
            String line = text.substring(idxStart, idxNextEndline);

            boolean validLine = true;
            do {
                idxStart += line.length() + 1;
                idxNextEndline = text.indexOf('\n', idxStart);
                line = text.substring(idxStart, idxNextEndline);

                if (groupQuestionNo.contentEquals("62-64")) {
                    log.debug("");
                }
                if (line != null && (line.length() == 0 || line.charAt(0) == '\n') || " ".contentEquals(line)) {
                    if ("\n".contentEquals(line)) {
                        sb.append(line);
                    }
                    continue;
                }
                // Valid line contains one of the token: M-, W-, (\\d, \n, English word.
                if (isEnglish(line)) {
                    // Go next
                    sb.append(line).append("\n");
                } else {
                    validLine = false;
                }
//                String[] words = line.split(" ");
//                if (words != null && words.length > 0) {
//                    if (isValidTokenDialog(words[0])) {
//                        // Go next
//                        sb.append(line).append("\n");
//                    } else {
//                        // Stop
//                        validLine = false;
//                    }
//                } else {
//                    log.warn("Invalid line: " + line);
//                }
            } while (validLine);
        } else {
            log.warn("Could to detect line of group question: " + groupQuestionNo);
        }
        preCurPos = curPos;
        curPos += sb.length();

        return sb.toString();
    }

    private boolean isNoEnglish(String line) {
        StringTokenizer stzer = new StringTokenizer(line, WORD_SEPARATOR);
        
        String token;
        int ncount=0;
        while (stzer.hasMoreTokens()) {
            token = stzer.nextToken();
            if (!isValidTokenDialog(token)) {
                ncount++;
            }
            if (!isValidTokenDialog(token) && ncount > 1) {
                return true;
            }
        }
        
        return false;
    }
    private boolean isEnglish(String line) {
        StringTokenizer stzer = new StringTokenizer(line, WORD_SEPARATOR);
        int nEnglish = 0;
        int nNoEnglish = 0;
        int nTotal = stzer.countTokens();
        int index = 0;
        while (stzer.hasMoreTokens()) {
            String token = stzer.nextToken();
            if (!isValidTokenDialog(token)) {
                nNoEnglish++;
            } else {
                nEnglish++;
            }
            
//            if (nNoEnglish * 2 > nTotal) {
//                return false;
//            }
            if (index > 2 && nNoEnglish > 1) {
                return false;
            }
            
            if (nEnglish * 2 > nTotal) {
                return true;
            }
            index++;
        }
        
        return false;
    }

    private boolean isValidTokenDialog(String word) {
        final String[] PREFIX = {"M-", "W-", "(", "\n", "|"};
        
        for (String prefix : PREFIX) {
            if (word.startsWith(prefix)) {
                return true;
            }
        }
        
        // Check is numberic
        try {
            Double.parseDouble(word);
            return true;
        } catch (Exception ex) {
            // Do nothing
        }
        // Check English dictionary.
        
        return AppUtility.checkEnglishWord(word);
    }
}
