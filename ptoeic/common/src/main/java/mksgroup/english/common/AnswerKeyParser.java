package mksgroup.english.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnswerKeyParser extends ToeicData {

    public AnswerKeyParser(ToeicData td, String text) {
        this.mapQuestion = td.mapQuestion;
        this.text = text;
        extractCorrectedAnswer(101, 200);
    }

}
