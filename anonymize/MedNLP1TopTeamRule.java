package anonymize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author kkajiyama
 *
 * テストファイル 形態素分割単位ごとに改行されたテキストファイル
 *
 */

public class MedNLP1TopTeamRule {

	static File inputFileName = new File("~/Documents/crossVali/Byori2/crossValiRuleK4.txt"); // crf's predict data
	static File outputFileName = new File("~/Documents/result20190430/ruleK4.txt"); // for output
	
	// main
	public static void main(String[] args) throws IOException {
		// Reader
		BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
		// Writer
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName, false));
		ArrayList<String> predictTagList = new ArrayList<String>();
		ArrayList<String> testingList = new ArrayList<String>();
		String testWord = null;
		String line = null; // for test and predict
		String rawWord = null;
		String[] words = null;
		String word = null;
		String[] spl = null;
		
		
		// 
		while ((line = reader.readLine()) != null) {

			if (line.startsWith("  ")) {
				/**
				 *  when word is space.
				 *  
				 *  In processing of RuluAnoymizeFormat.java, 
				 *  if word is " ", " " replace "\s".
				 *  "\s" is not regular expression.
				 *  
				 *  So, " " may be nothing.
				 *  
				 */
				
				predictTagList.add("O");
				testingList.add("O");

			} else if (line.matches("") || line.equals(" ")) {
				// when nothing
				predictTagList.add("");
				testingList.add("");
			} else if (line.matches("\n")) {
				predictTagList.add("\n");
				predictTagList.add("\n");
				
			} else {
				spl = line.split(" ");
				if (spl.length < 1) {
					System.out.println("line = (" + line + ")"); // line = ( )
				}
				testWord = String.join(" ", spl[0], spl[1], spl[2], spl[3]);
				testingList.add(testWord);
				predictTagList.add(spl[4]);
			}
		}
		reader.close();

		System.out.println("testingList.size() = " + testingList.size());
		System.out.println("predictList.size() = " + predictTagList.size());
		
		TopTeamRule rule = new TopTeamRule();
		
		
		// upgrade
		for (int i = 0; i < testingList.size(); i++ ) {
			// upgrade

			rawWord = testingList.get(i);

			if (rawWord.startsWith("  ") || rawWord.startsWith("   ")) {
				continue;
			}
			words = testingList.get(i).toString().split(" ");

			if (words.length < 2) {continue;}
			word = words[0];

			// check method

			// sex
			rule.ruleSex(word, testingList, predictTagList, i);

			// time
			rule.ruleTime(word, testingList, predictTagList, i);

			// age
			rule.ruleAge(word, testingList, predictTagList, i);
			
			// time and age
			rule.ruleTimeAndAge(word, testingList, predictTagList, i);

			// hospital
			rule.ruleHospital(word, testingList, predictTagList, i);

		}

		// write
		String[] tests = null;
		String[] currentTag = null;

		String[] postCurrentPredict = null;

		System.out.println("testingList.size() = " + testingList.size());
		System.out.println("predictList.size() = " + predictTagList.size());
		
		for (int i = 0; i < testingList.size(); i++) {
			if (!testingList.get(i).toString().equals("")|| !testingList.get(i).matches("\n")){
				tests = testingList.get(i).split(" ");
				bw.write(tests[0]);
			}
			bw.write(" ");
			bw.write(tests[tests.length - 1]);

			//
			if (i < testingList.size() - 2 &&
					(predictTagList.get(i).startsWith("O") || predictTagList.get(i).equals("")) 
					&& (predictTagList.get(i + 1).startsWith("I-") 
							&& predictTagList.get(i + 2).startsWith("I-"))) {
				/*
				 *  "O(null), I, I" is not correct
				 */
				predictTagList.set(i + 1, "O");
				predictTagList.set(i + 2, "O");
			} else if (i < testingList.size() - 1 &&
					(predictTagList.get(i).startsWith("O") || predictTagList.get(i).equals(""))
					&& predictTagList.get(i + 1).startsWith("I-")) {
				/*
				 *  "O(null), I" is not correct
				 */
				predictTagList.set(i + 1, "O");
			}
			

			/*
			 * B-time
			 * B-time
			 * I-time
			 *   or
			 * B-time
			 * E-time, I-time, B-time
			 * B-time
			 *
			 * these pattern fix.
			 */

			if ( i < testingList.size() - 2 ){
				currentTag = predictTagList.get(i).split("-"); // i
				postCurrentPredict = predictTagList.get(i + 1).split("-"); // i+1
				String[] afterPostCurrentPredict = predictTagList.get(i + 2).split("-"); // i+2

				if (1 < currentTag.length && 1 < postCurrentPredict.length && 1 < afterPostCurrentPredict.length) {

					// In tag of i = i+1 = i+2
					if (currentTag[1].equals(postCurrentPredict[1]) && afterPostCurrentPredict[1].equals(postCurrentPredict[1])) {

						if (currentTag[0].equals("B") && postCurrentPredict[0].equals("B") && afterPostCurrentPredict[0].equals("I")) {
							predictTagList.set(i + 1, "I-" + afterPostCurrentPredict[1]);

						} else if (currentTag[0].equals("B") && postCurrentPredict[0].equals("I") && afterPostCurrentPredict[0].equals("B")) {
							predictTagList.set(i + 2, "I-" + postCurrentPredict[1]);
							
						} else if (currentTag[0].equals("B") && postCurrentPredict[0].equals("B") && afterPostCurrentPredict[0].equals("B")) {
							predictTagList.set(i + 1, "I-" + afterPostCurrentPredict[1]);
							predictTagList.set(i + 2, "I-" + postCurrentPredict[1]);
						}
					}
				}
			}
			
			/*
			 * EX:
			 *
			 * E-time
			 * B-time
			 *
			 * fix it
			 *
			 * I-time
			 * I-time
			 */
			if (i < testingList.size() - 1  &&
					(predictTagList.get(i).startsWith("I-") && predictTagList.get(i + 1).startsWith("B-"))) {
				currentTag = predictTagList.get(i).split("-");
				if (predictTagList.get(i + 1).endsWith(currentTag[1])) {
					predictTagList.set(i + 1, "I-" + currentTag[1]);
				}
			}

			bw.write(" ");
			bw.write(predictTagList.get(i));
			bw.newLine();

		}
		bw.close();
	}
}
