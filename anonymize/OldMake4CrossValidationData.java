package anonymize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OldMake4CrossValidationData {
	
	/**
	 * 
	 * 
	 * 
	 * @param args
	 * @throws IOException
	 */

	static int K = 4; // k-cross-validation's "k"

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String testData = "~/Documents/formatedData/medNLP1/noTagData.txt";
		String evaluateData = "~/Documents/formatedData/medNLP1/trainData.txt";
		
		int numberOfSentence = 0; // count sentence
		int countLine = 0;
		int numberOfASplit = 0; // for k-cross-varidation
		 
		ArrayList<String> testList = new ArrayList<String>(); // add tagged BIO sentence
		ArrayList<String> withTagList = new ArrayList<String>();
		
		BufferedReader testReader = Files.newBufferedReader(Paths.get(testData), Charset.defaultCharset());
		BufferedReader withTagdataReader = Files.newBufferedReader(Paths.get(evaluateData), Charset.defaultCharset());
		
		/*
		 * Add text data to List.
		 * Count sentence.
		 * Define "numberOfASplit" value.
		 */
		String str = "";
		while ((str = testReader.readLine()) != null) {
			testList.add(str);
			countLine++;
			if (str.length() < 1) {
				numberOfSentence++;
			}
		}
		testReader.close();
		
		while ((str = withTagdataReader.readLine()) != null) {
			withTagList.add(str);
		}
		withTagdataReader.close();
				
		// check validity of List
		if (testList.size() < 1) {
			System.out.println("=== List is not validate. ===");
			System.out.println("Program END.");
			return;
		}
		
		// determine numberOfASplit
		numberOfASplit = (numberOfSentence / K);
		
		/*
		 * Make data
		 */
		
		int counter = 0;
		
		// for Test-file of CRF
		BufferedWriter writeTest1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTestK1.txt"), Charset.defaultCharset());
		BufferedWriter writeTest2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTestK2.txt"), Charset.defaultCharset());
		BufferedWriter writeTest3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTestK3.txt"), Charset.defaultCharset());
		BufferedWriter writeTest4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTestK4.txt"), Charset.defaultCharset());
		
		// for Evaluate-file of CRF
		BufferedWriter writeEval1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiEvalK1.txt"), Charset.defaultCharset());
		BufferedWriter writeEval2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiEvalK2.txt"), Charset.defaultCharset());
		BufferedWriter writeEval3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiEvalK3.txt"), Charset.defaultCharset());
		BufferedWriter writeEval4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiEvalK4.txt"), Charset.defaultCharset());

		// for Train-file of CRF
		BufferedWriter writeTrain1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTrainK1.txt"), Charset.defaultCharset());
		BufferedWriter writeTrain2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTrainK2.txt"), Charset.defaultCharset());
		BufferedWriter writeTrain3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTrainK3.txt"), Charset.defaultCharset());
		BufferedWriter writeTrain4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiTrainK4.txt"), Charset.defaultCharset());
		
		// for Test and Evaluate file of Rule
		BufferedWriter writeFileForRule1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiRuleK1.txt"), Charset.defaultCharset());
		BufferedWriter writeFileForRule2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiRuleK2.txt"), Charset.defaultCharset());
		BufferedWriter writeFileForRule3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiRuleK3.txt"), Charset.defaultCharset());
		BufferedWriter writeFileForRule4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/medNLP1/crossValiRuleK4.txt"), Charset.defaultCharset());
		
		String[] splLine; // use to make in RuleBased
		
		for (int i = 0; i < countLine; i++) {
			
			if (counter < numberOfASplit) {
				// k = 1
				// test and evaluate of CRF
				writeTest1.write(testList.get(i));
				writeEval1.write(withTagList.get(i));
				writeTest1.newLine();
				writeEval1.newLine();
				
				// train of CRF
				writeTrain2.write(withTagList.get(i));
				writeTrain3.write(withTagList.get(i));
				writeTrain4.write(withTagList.get(i));
				
				writeTrain2.newLine();
				writeTrain3.newLine();
				writeTrain4.newLine();
				
				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					
					writeFileForRule1.write(splLine[0]);
					writeFileForRule1.write(" ");
					writeFileForRule1.write(splLine[1]);
					writeFileForRule1.write(" ");
					writeFileForRule1.write(splLine[2]);
					writeFileForRule1.write(" ");
					writeFileForRule1.write(splLine[splLine.length - 1]);
					writeFileForRule1.write(" ");
					writeFileForRule1.write("O");
				}
				writeFileForRule1.newLine();
				
				
			} else if (counter < numberOfASplit*2) {
				// k = 2
				// test and evaluate
				writeTest2.write(testList.get(i));
				writeEval2.write(withTagList.get(i));
				writeTest2.newLine();
				writeEval2.newLine();
				
				// train
				writeTrain1.write(withTagList.get(i));
				writeTrain3.write(withTagList.get(i));
				writeTrain4.write(withTagList.get(i));
				
				writeTrain1.newLine();
				writeTrain3.newLine();
				writeTrain4.newLine();
				
				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					writeFileForRule2.write(splLine[0]);
					writeFileForRule2.write(" ");
					writeFileForRule2.write(splLine[1]);
					writeFileForRule2.write(" ");
					writeFileForRule2.write(splLine[2]);
					writeFileForRule2.write(" ");
					writeFileForRule2.write(splLine[splLine.length - 1]);
					writeFileForRule2.write(" ");
					writeFileForRule2.write("O");
				}
				writeFileForRule2.newLine();
				
			} else if (counter < numberOfASplit*3) {
				// k = 3
				// test and evaluate
				writeTest3.write(testList.get(i));
				writeEval3.write(withTagList.get(i));
				writeTest3.newLine();
				writeEval3.newLine();
				
				// train
				writeTrain1.write(withTagList.get(i));
				writeTrain2.write(withTagList.get(i));
				writeTrain4.write(withTagList.get(i));
				
				writeTrain1.newLine();
				writeTrain2.newLine();
				writeTrain4.newLine();
				
				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					writeFileForRule3.write(splLine[0]);
					writeFileForRule3.write(" ");
					writeFileForRule3.write(splLine[1]);
					writeFileForRule3.write(" ");
					writeFileForRule3.write(splLine[2]);
					writeFileForRule3.write(" ");
					writeFileForRule3.write(splLine[splLine.length - 1]);
					writeFileForRule3.write(" ");
					writeFileForRule3.write("O");
				}
				writeFileForRule3.newLine();
				
			} else if (counter < numberOfASplit*4) {
				// k = 4
				// test and evaluate
				writeTest4.write(testList.get(i));
				writeEval4.write(withTagList.get(i));
				writeTest4.newLine();
				writeEval4.newLine();
				
				// train
				writeTrain1.write(withTagList.get(i));
				writeTrain2.write(withTagList.get(i));
				writeTrain3.write(withTagList.get(i));
				
				writeTrain1.newLine();
				writeTrain2.newLine();
				writeTrain3.newLine();
				
				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					writeFileForRule4.write(splLine[0]);
					writeFileForRule4.write(" ");
					writeFileForRule4.write(splLine[1]);
					writeFileForRule4.write(" ");
					writeFileForRule4.write(splLine[2]);
					writeFileForRule4.write(" ");
					writeFileForRule4.write(splLine[splLine.length - 1]);
					writeFileForRule4.write(" ");
					writeFileForRule4.write("O");
				}
				writeFileForRule4.newLine();
				
			} 
			
			// only next line sentence ???
			if (testList.get(i).length() < 1) {
				counter++;
				System.out.println("newLine");
			}
		}
		System.out.println(counter);
		System.out.println(numberOfSentence);
		// writer of CRF close 
		writeTest1.close();
		writeTest2.close();
		writeTest3.close();
		writeTest4.close();
		
		writeTrain1.close();
		writeTrain2.close();
		writeTrain3.close();
		writeTrain4.close();

		writeEval1.close();
		writeEval2.close();
		writeEval3.close();
		writeEval4.close();
		
		// writer of Rule close
		writeFileForRule1.close();
		writeFileForRule2.close();
		writeFileForRule3.close();
		writeFileForRule4.close();
		
	}

}
