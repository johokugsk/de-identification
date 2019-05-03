package anonymize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MakeData4CrossValidation4 {
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String testData = "~/Documents/formatedData/Byori/noTagData.txt";
		String evaluateData = "~/Documents/formatedData/Byori/trainData.txt";
		
		int numberOfSentence = 0; // count sentence
		int countLine = 0;
		int k = 4; // k-cross-validation's "k"
		int fourSplit = 0; // for k-cross-varidation
		double rate = 0;
		 
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
		fourSplit = (numberOfSentence / k);
//		rate = fourSplit / 10;

		/*
		 * Make data
		 */
		
		int counter = 0;
		
		/*
		 *  CRF
		 */
		
		// for Test-file of CRF
		BufferedWriter writeTest1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestK1.txt"), Charset.defaultCharset());
		BufferedWriter writeTest2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestK2.txt"), Charset.defaultCharset());
		BufferedWriter writeTest3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestK3.txt"), Charset.defaultCharset());
		BufferedWriter writeTest4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestK4.txt"), Charset.defaultCharset());
		
		// for Evaluate-file of CRF
		BufferedWriter writeEval1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalK1.txt"), Charset.defaultCharset());
		BufferedWriter writeEval2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalK2.txt"), Charset.defaultCharset());
		BufferedWriter writeEval3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalK3.txt"), Charset.defaultCharset());
		BufferedWriter writeEval4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalK4.txt"), Charset.defaultCharset());

		// for Train-file of CRF
		BufferedWriter writeTrain1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainK1.txt"), Charset.defaultCharset());
		BufferedWriter writeTrain2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainK2.txt"), Charset.defaultCharset());
		BufferedWriter writeTrain3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainK3.txt"), Charset.defaultCharset());
		BufferedWriter writeTrain4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainK4.txt"), Charset.defaultCharset());
		
		/*
		 *  Rule
		 */
		
		// for Test and Evaluate file of Rule
		BufferedWriter writeFileForRule1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiRuleK1.txt"), Charset.defaultCharset());
		BufferedWriter writeFileForRule2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiRuleK2.txt"), Charset.defaultCharset());
		BufferedWriter writeFileForRule3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiRuleK3.txt"), Charset.defaultCharset());
		BufferedWriter writeFileForRule4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiRuleK4.txt"), Charset.defaultCharset());
		
		/*
		 * LSTM
		 * 
		 * train:dev:test = 2:1:1
		 * または、8:1:1 7:1:2
		 */

		// for Test-file of LSTM
		BufferedWriter writeTestLstm1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestLstmK1.txt"), Charset.defaultCharset());
		BufferedWriter writeTestLstm2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestLstmK2.txt"), Charset.defaultCharset());
		BufferedWriter writeTestLstm3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestLstmK3.txt"), Charset.defaultCharset());
		BufferedWriter writeTestLstm4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTestLstmK4.txt"), Charset.defaultCharset());
		
		// for Evaluate-file of LSTM
		BufferedWriter writeEvalLstm1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalLstmK1.txt"), Charset.defaultCharset());
		BufferedWriter writeEvalLstm2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalLstmK2.txt"), Charset.defaultCharset());
		BufferedWriter writeEvalLstm3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalLstmK3.txt"), Charset.defaultCharset());
		BufferedWriter writeEvalLstm4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiEvalLstmK4.txt"), Charset.defaultCharset());

		// for Development-file of LSTM
		BufferedWriter writeDevLstm1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiDevLstmK1.txt"), Charset.defaultCharset());
		BufferedWriter writeDevLstm2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiDevLstmK2.txt"), Charset.defaultCharset());
		BufferedWriter writeDevLstm3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiDevLstmK3.txt"), Charset.defaultCharset());
		BufferedWriter writeDevLstm4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiDevLstmK4.txt"), Charset.defaultCharset());
		
		// for Train-file of LSTM
		BufferedWriter writeTrainLstm1 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainLstmK1.txt"), Charset.defaultCharset());
		BufferedWriter writeTrainLstm2 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainLstmK2.txt"), Charset.defaultCharset());
		BufferedWriter writeTrainLstm3 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainLstmK3.txt"), Charset.defaultCharset());
		BufferedWriter writeTrainLstm4 = Files.newBufferedWriter(Paths.get("~/Documents/crossVali/Byori2/crossValiTrainLstmK4.txt"), Charset.defaultCharset());
		
		String[] splLine; // use to make in RuleBased
		
		for (int i = 0; i < countLine; i++) {
			
			System.out.println("counter = " + counter);
			
			if (counter < fourSplit) {
				
				rate = numberOfSentence * 0.05;
				// if (ite == ) {}
				
				// k = 1
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					// test and evaluate of CRF
					writeTest1.write(testList.get(i));
					writeEval1.write(withTagList.get(i));
					writeTest1.newLine();
					writeEval1.newLine();
					
				} else {
					
					// train of CRF
					writeTrain2.write(withTagList.get(i));
					writeTrain3.write(withTagList.get(i));
					writeTrain4.write(withTagList.get(i));
					
					writeTrain2.newLine();
					writeTrain3.newLine();
					writeTrain4.newLine();
					
				}
				
				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					
					if (splLine.length < 4) {
						System.out.println(withTagList.get(i));
						System.out.println("line num is " + i );
					}
					
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
				
				// lstm
				// evaluate
				
				rate = numberOfSentence * 0.025;
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					// eval
					writeEvalLstm1.write(withTagList.get(i).split(" ")[0]);
					writeEvalLstm1.write(" ");
					writeEvalLstm1.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeEvalLstm1.newLine();
					
					// test
					writeTestLstm1.write(withTagList.get(i).split(" ")[0]);
					writeTestLstm1.write(" ");
					writeTestLstm1.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeTestLstm1.newLine();
					
					
				} else if (counter < numberOfSentence * 0.05) {
					// dev
					writeDevLstm2.write(withTagList.get(i).split(" ")[0]);
					writeDevLstm2.write(" ");
					writeDevLstm2.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeDevLstm2.newLine();
					
				} else {
					// train
					writeTrainLstm3.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm3.write(" ");
					writeTrainLstm3.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeTrainLstm4.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm4.write(" ");
					writeTrainLstm4.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeTrainLstm3.newLine();
					writeTrainLstm4.newLine();
				}
//				System.out.println(withTagList.get(i).split(" ")[0]);

			} else if (counter < fourSplit*2) {
				// k = 2
				rate = numberOfSentence * 0.4;
				
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					// test and evaluate
					writeTest2.write(testList.get(i));
					writeEval2.write(withTagList.get(i));
					writeTest2.newLine();
					writeEval2.newLine();
					
				} else {
					// train
					writeTrain1.write(withTagList.get(i));
					writeTrain3.write(withTagList.get(i));
					writeTrain4.write(withTagList.get(i));
					writeTrain1.newLine();
					writeTrain3.newLine();
					writeTrain4.newLine();
				}

				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					if (splLine.length < 6) {
						System.out.println("withTagList.get(i) = " + withTagList.get(i));
						continue;
					}
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
				
				// lstm
				// evaluate
				
				rate = numberOfSentence * 0.275;
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					writeEvalLstm2.write(withTagList.get(i).split(" ")[0]);
					writeEvalLstm2.write(" ");
					writeEvalLstm2.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeEvalLstm2.newLine();
					
					writeTestLstm2.write(withTagList.get(i).split(" ")[0]);
					writeTestLstm2.write(" ");
					writeTestLstm2.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeTestLstm2.newLine();
					
					
				} else if (counter < numberOfSentence * 0.3) {
					// dev
					writeDevLstm3.write(withTagList.get(i).split(" ")[0]);
					writeDevLstm3.write(" ");
					writeDevLstm3.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeDevLstm3.newLine();
					
					
				} else {
					// train
					writeTrainLstm1.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm1.write(" ");
					writeTrainLstm1.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeTrainLstm4.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm4.write(" ");
					writeTrainLstm4.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeTrainLstm1.newLine();
					writeTrainLstm4.newLine();
				}
				
				
				
			} else if (counter < fourSplit*3) {
				
				rate = numberOfSentence * 0.55;
				// k = 3
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					// test and evaluate
					writeTest3.write(testList.get(i));
					writeEval3.write(withTagList.get(i));
					writeTest3.newLine();
					writeEval3.newLine();

				} else {
					// train
					writeTrain1.write(withTagList.get(i));
					writeTrain2.write(withTagList.get(i));
					writeTrain4.write(withTagList.get(i));
					
					writeTrain1.newLine();
					writeTrain2.newLine();
					writeTrain4.newLine();
				}
				
				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					
					if (splLine.length < 2) {
						System.out.println("withTagList.get(i) = " + withTagList.get(i));
						continue;
					}
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
				
				// lstm
				// evaluate = dev
				
				rate = numberOfSentence * 0.525;
				
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					writeEvalLstm3.write(withTagList.get(i).split(" ")[0]);
					writeEvalLstm3.write(" ");
					writeEvalLstm3.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeEvalLstm3.newLine();
					
					writeTestLstm3.write(withTagList.get(i).split(" ")[0]);
					writeTestLstm3.write(" ");
					writeTestLstm3.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeTestLstm3.newLine();
					
				} else if (counter < numberOfSentence * 0.55) {
					// dev
					writeDevLstm4.write(withTagList.get(i).split(" ")[0]);
					writeDevLstm4.write(" ");
					writeDevLstm4.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeDevLstm4.newLine();
					
				} else {
					// train
					writeTrainLstm1.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm1.write(" ");
					writeTrainLstm1.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeTrainLstm2.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm2.write(" ");
					writeTrainLstm2.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					
					writeTrainLstm1.newLine();
					writeTrainLstm2.newLine();
				}
				
			} else if (counter < fourSplit*4) {
				
				rate = numberOfSentence * 0.8;
				// k = 4
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					// test and evaluate
					writeTest4.write(testList.get(i));
					writeEval4.write(withTagList.get(i));
					writeTest4.newLine();
					writeEval4.newLine();
				} else {
					// train
					writeTrain1.write(withTagList.get(i));
					writeTrain2.write(withTagList.get(i));
					writeTrain3.write(withTagList.get(i));
					
					writeTrain1.newLine();
					writeTrain2.newLine();
					writeTrain3.newLine();
					
				}
				
				// test and evaluate of Rule
				if (!testList.get(i).matches("")) {
					splLine = withTagList.get(i).split(" ");
					System.out.println("withTagList.get(i) = " + withTagList.get(i));
					System.out.println("split length = " + splLine.length);
					if (splLine.length < 6) {
						for(String text:splLine) {
							System.out.println("text =" + text);
						}
					}
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
				
				// LSTM
				rate = numberOfSentence * 0.775;
				System.out.println("rate : couter = " + rate + " : " + counter);
				if (counter < rate) {
					// evaluate = dev
					writeEvalLstm4.write(withTagList.get(i).split(" ")[0]);
					writeEvalLstm4.write(" ");
					writeEvalLstm4.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeEvalLstm4.newLine();
					
					writeTestLstm4.write(withTagList.get(i).split(" ")[0]);
					writeTestLstm4.write(" ");
					writeTestLstm4.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeTestLstm4.newLine();
					
				} else if (counter < numberOfSentence * 0.8) {
					// dev
					writeDevLstm1.write(withTagList.get(i).split(" ")[0]);
					writeDevLstm1.write(" ");
					writeDevLstm1.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					writeDevLstm1.newLine();
					
				} else {
					// train
					writeTrainLstm2.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm2.write(" ");
					writeTrainLstm2.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeTrainLstm3.write(withTagList.get(i).split(" ")[0]);
					writeTrainLstm3.write(" ");
					writeTrainLstm3.write(withTagList.get(i).split(" ")[withTagList.get(i).split(" ").length - 1]);
					
					writeTrainLstm2.newLine();
					writeTrainLstm3.newLine();
					
				}

				
			} 
			
			if (testList.get(i).length() < 1) {
				counter++;
//				System.out.println("newLine");
			}
		}
//		System.out.println(counter);
//		System.out.println(numberOfSentence);
		
		// CRF close 
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
		
		// Rule close
		writeFileForRule1.close();
		writeFileForRule2.close();
		writeFileForRule3.close();
		writeFileForRule4.close();
		
		// LSTM close
		writeEvalLstm1.close();
		writeEvalLstm2.close();
		writeEvalLstm3.close();
		writeEvalLstm4.close();
		
		writeTestLstm1.close();
		writeTestLstm2.close();
		writeTestLstm3.close();
		writeTestLstm4.close();
		
		writeDevLstm1.close();
		writeDevLstm2.close();
		writeDevLstm3.close();
		writeDevLstm4.close();
		
		writeTrainLstm1.close();
		writeTrainLstm2.close();
		writeTrainLstm3.close();
		writeTrainLstm4.close();
		
	}

}
