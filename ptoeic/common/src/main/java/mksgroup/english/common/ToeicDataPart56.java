package mksgroup.english.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import m.k.s.sakai.app.question.logic.QuestionData;
import mksgroup.java.common.CommonUtil;

@Slf4j
public class ToeicDataPart56 extends ToeicData {
    Map<Object, QuestionData> mapQPart6 = new HashMap<Object, QuestionData>();
    
    final static String[][] POS_MARKS = {
            {"READING_TEST", "READING TEST", "PART 5"}, 
            {"PART5_INTRO", "PART 5", ".\n\n"}
    };

    
    public ToeicDataPart56() {
        super();
    }

	public ToeicDataPart56(String text) {
		this.text = text;
		
		// Step 1: Parse introduction.
		extractIntros(POS_MARKS);
		
		// Step 2: Parse question in Part 5.
		extractQuestionPart345(101, 130);
		
		// Step 3: Parse question in Part 6.
		extractIntroPart6();
		
		extractQuestionPart6(131, 146);
	}
	

    private void extractIntroPart6() {
        String content = substring("PART 6", ".\n\n");
        mapIntro.put("PART6_INTRO", content);
    }


    private void extractQuestionPart6(int startIdxQ, int endIdxQ) {
        // Scan questions Part 3:  32 to 70
        // Scan questions Part 4:  71 to 100
        List<Integer> lstQuestionNo;
        int i = startIdxQ;
        int nextCurPos;
        int endQuestionNo;
        while (i <= endIdxQ) {
  
            if (i == 109) {
                log.debug("");
            }
            i = extractGroupQuestion();

            i++;
        }
    }

    private int extractGroupQuestion() {
        QuestionData qd = new QuestionData();
        String groupQuestion = substring("Questions 1", "\n\n1");
        
        // Part question indices of sub questions. Ex: Questions 131-134
        Pattern pattern = Pattern.compile("Questions (\\d{3})-(\\d{3})");
        Matcher matcher = pattern.matcher(groupQuestion);

        qd.setQuestion(groupQuestion);

        if (matcher.find()) {
            int startNo = Integer.parseInt(matcher.group(1));
            int endNo = Integer.parseInt(matcher.group(2));

            mapQPart6.put(String.format("%s-%s", startNo, endNo), qd);
            
            for (int i=startNo; i <= endNo; i++) {
                log.debug("Prepare to extract sub question of " + i);
                qd = extractNextQuestions(i);
                
                if (qd != null) {
                    mapQPart6.put(i, qd);
                } else {
                    log.error("Could not extract sub question " + i);
                }
            }
            
            return endNo;
        }
        
        return -1;
    }
}
