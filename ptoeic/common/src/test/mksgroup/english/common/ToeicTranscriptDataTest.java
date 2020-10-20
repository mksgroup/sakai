package mksgroup.english.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class ToeicTranscriptDataTest {

    @Test
    public void testToeicTranscriptData() {
        String lcTranscriptTxt = "\n" + 
                "\n" + 
                "1(B) 2(C) 3(D) 4(A) 5 (D)\n" + 
                "6 (C) 7 (A) 8 (C) 9 (B) 10 (C)\n" + 
                "11(C) 12(C) 13(B) 14(A)  15(A)\n" + 
                "16(C) 17(B) 18(C) 19(A) 20(D)\n" + 
                "21(B) 22(B) 23(B) 24(A)  25(C)\n" + 
                "26(B) 27(A) 28(C) 29(C) 30(C)\n" + 
                "31(B) 32(C) 33(B) 34(D) 35(D)\n" + 
                "36(B)  37(A) 38(B) 39(B) = 40(C)\n" + 
                "41(C) 42(B) 43(A) 44(D) 45(C).\n" + 
                "46(B) 47(D) 48(C) 49(A) — 50(A)\n" + 
                "51(B) 52(C)  53(D) 54(C)  55(A)\n" + 
                "56(B) 57(C)  58(D) 59(D) 60(C)\n" + 
                "61(A) 62(B) 63(D) 64(D)  65(B)\n" + 
                "66(C)  67(A) 68(C) 69(B) —_70(B)\n" + 
                "71(B) 72(C) 73(B) 74(D)  75(C)\n" + 
                "76(A)  77(B) 78(A) 79(C) — 80(D)\n" + 
                "81(C) 82(A) 83(B) 84(A) _ 85(A)\n" + 
                "86(A)  87(B)  88(D) 89(A) 90(C)\n" + 
                "91(B) 92(B) 93(D) 94(A)  95(C)\n" + 
                "96(B)  97(D) 98(B)  99(C)  100(D)\n" + 
                "\n" + 
                " \n" + 
                "\n" + 
                " \n" + 
                "\n" + 
                " \n" + 
                "\n" + 
                "(A) A woman is painting a house.";
        ToeicData lcTd = new ToeicData();
        ToeicTranscriptData ttd = new ToeicTranscriptData(lcTd , lcTranscriptTxt, null);
        
        assertNotNull(ttd.mapQuestion);
        assertEquals(100, ttd.mapQuestion.size());
        assertEquals('B', ttd.mapQuestion.get(1).getCorrectAnswer().charAt(0));
        assertEquals('C', ttd.mapQuestion.get(45).getCorrectAnswer().charAt(0));
        assertEquals('D', ttd.mapQuestion.get(100).getCorrectAnswer().charAt(0));
    }

}
