package mksgroup.english.common;

import static org.junit.Assert.*;

import org.junit.Test;

import mksgroup.java.common.FileUtil;

public class AppUtilityTest {

	String text = FileUtil.getContent("/ETS_2020_LC.txt", true, "utf-8");
	
	@Test
	public void testExtractListening() {
		
		String startText = "LISTENING TEST";
		
		String endText = "PART 1";
		
		String listeningIntro = AppUtility.substring(text, startText, endText);
		
		assertNotNull(listeningIntro);
		
		System.out.println(listeningIntro);
	}
	
	@Test
	public void testExactPart1Intro() {
		String startText = "PART 1";
		
		String endText = "\n20\n";
		
		String part1Intro = AppUtility.substring(text, startText, endText);
		
		assertNotNull(part1Intro);
		
		System.out.println(part1Intro);
	}

	@Test
	public void testExactPart2Intro() {
		String startText = "PART 2";
		
		String endText = "\n \n\n";
		
		String part2Intro = AppUtility.substring(text, startText, endText);
		
		assertNotNull(part2Intro);
		
		System.out.println(part2Intro);
	}
	
	@Test
	public void testExactPart3Intro() {
		String startText = "PART 3";
		
		String endText = "\nL";
		
		String part3Intro = AppUtility.substring(text, startText, endText);
		
		assertNotNull(part3Intro);
		
		System.out.println(part3Intro);
	}
	
	@Test
	public void testExactQuestion() {
		String startText = "32. ";
		
		String endText = "(A) ";
		
		String question32 = AppUtility.substring(text, startText, endText);
		
		assertNotNull(question32);
		
		System.out.println(question32);
	}
}
