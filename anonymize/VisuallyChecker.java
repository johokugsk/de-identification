package anonymize.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class VisuallyChecker {
	
	static File testFileName = new File("~/Documents/crossVali/mix/crossValiEvalK4.txt"); 
	static File predictTaggedFileName = new File("~/Documents/CRFModels/20190501/mixK4.txt"); // crf's predict data
	static File outputFileName = new File("~/Documents/result20190501/mix/K4.txt"); // for output

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// Reader
		BufferedReader predictRead = new BufferedReader(new FileReader(predictTaggedFileName));
		BufferedReader testRead = new BufferedReader(new FileReader(testFileName));
		// Writer
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName, false));
		ArrayList<String> predict = new ArrayList<String>();
		ArrayList<String> test = new ArrayList<String>();
		String line = null; // for test and predict
		
		String[] spl1 = null;
		
		// predict
		while ((line = predictRead.readLine()) != null) {
			if (line.contains("Number")) {
				System.out.println("hit");
				continue;
			} else {
				spl1 = line.split(" ");
				predict.add(spl1[0]);
			}
		}
		predictRead.close();
		// test
		while((line = testRead.readLine()) != null) {
			if (line.matches(" ")) {
				line.replaceAll(" ", "\n");
			} 
			test.add(line);
		}
		testRead.close();
		
		// debug
		System.out.println("predict = " + predict.size());
		System.out.println("test = " + test.size());
		
		// write
		String[] tests = null;
		for (int i = 0; i < test.size(); i++) {
			tests = test.get(i).split(" ");
			bw.write(tests[0]);
			bw.write(" ");
			bw.write(tests[tests.length - 1]);
			bw.write(" ");
			bw.write(predict.get(i));
			bw.newLine();
		}
		bw.close();

	}

}
