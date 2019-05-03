package makeValidData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * CSVファイルのI/Oを行うクラス
 * 今後、txtファイルなどを扱う可能性も含めたクラス名となっている
 * @author kkajiyama
 *
 */

public class FileIO {
	
	public List<String> readTXT(String inputFileName) {
		List<String> lineList = new LinkedList<>();
		
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFileName), StandardCharsets.UTF_8)) {
			
			for (String line; (line = reader.readLine()) != null; ){
				
				lineList.add(line);
			}
			
		} catch (IOException e) {
			System.out.println(e);
		}
		return lineList;
		
	}
	
	/**
	 * csvデータの一行目を使うか明示的に選択していない場合にすべての列データを要求するメソッド </br>
	 * 引数がない場合は最初の一行目は使う設定
	 * @param inputFileName
	 * @return
	 */
	public List<List<String>> readCSV(String inputFileName) {
		boolean useFirstLine = true;
		
		return readCSV(inputFileName, useFirstLine);
	}
	
	/**
	 * すべての列の内容を列ごとにListに格納して、そのListが入ったListを返すメソッド
	 * @param inputFileName
	 * @param useFirstLine　: csvデータの最初の一行目を使用する場合はtrue
	 * @return
	 */
	public List<List<String>> readCSV(String inputFileName, boolean useFirstLine) {
		int[] colNums = {};
		return readCSV(inputFileName, colNums, useFirstLine);
	}
	
	/**
	 * colNumsの値の列データのみをListに格納して、そのListを返すメソッド
	 * @param inputFileName
	 * @param colNums : ほしいデータが格納された列番号
	 * @return
	 */
	public List<List<String>> readCSV(String inputFileName, int[] colNums) {
		boolean useFirstLine = true;
		return readCSV(inputFileName, colNums, useFirstLine);
	}

	/**
	 * 特定の複数の列のみを取り出せるメソッド </br>
	 * corNumsに含まれている列番号のデータを列ごとにListへ格納。それを格納したListを返す。
	 * @param inputFileName
	 * @param colNums : ほしいデータが格納された列番号
	 * @param useFirstLine
	 * @return
	 */
	public List<List<String>> readCSV(String inputFileName, int[] colNums, boolean useFirstLine) {
		
		List<List<String>> csvDataList = new ArrayList<List<String>>();

		boolean empColNum;
		if (colNums.length == 0) {
			empColNum = true;
		} else {
			empColNum = false;
		}
		
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFileName), StandardCharsets.UTF_8)) {
			
			// 一時的にすべての列のListのデータを格納するためのList
			List<List<String>> columLineLists =new ArrayList<List<String>>();
			
			String[] splitedLine; // lineをsplitしたリスト
			
			boolean firstLine = true;
			
			for (String line; (line = reader.readLine()) != null; ){
				
				// split by [","] or [,] 
				if (line.startsWith("\"") && line.endsWith("\"")) {
					// split by [","]
					splitedLine = line.split("\",\"");
					splitedLine[0] = splitedLine[0].replaceFirst("\"", "");
					splitedLine[0] = splitedLine[0].replace("[\"]$", "");
				} else {
					// split by [,]
					splitedLine = line.split(",");
				}
				
				if (firstLine) {
					for (int ite = 0; ite < splitedLine.length; ite++) {
						// bulid instance
						List<String> colummLineList = new LinkedList<String>();
						columLineLists.add(colummLineList);
					}
					
					if (useFirstLine) {
						for (int i = 0; i < splitedLine.length; i++) {
							// call list
							List colummList = columLineLists.get(i);
							colummList.add(splitedLine[i]);
							columLineLists.set(i, colummList);
						}
					}
					firstLine = false;
				} else {
					for (int i = 0; i < splitedLine.length; i++) {
						// call list
						List colummList = columLineLists.get(i);
						colummList.add(splitedLine[i]);
						columLineLists.set(i, colummList);
					}
				}
				// ここまでで、すべてのデータが列ごとに並んでいる
			}
			
			if (!empColNum) {
				for (int colNum :colNums) {
					csvDataList.add(columLineLists.get(colNum));
				}
			} else {
				csvDataList = columLineLists;
			}
			
		} catch (IOException e) {
			System.out.println(e);
		}
		return csvDataList;
	}
	
	/**
	 * ひとつだけの列が欲しい場合に使用するメソッド
	 * @param inputFileName
	 * @param corNum : ほしいデータの列番号
	 * @return
	 */
	public List<String> readCSV(String inputFileName, int corNum) {
		
		List<String> allLineList = new ArrayList<String>();
		
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFileName), StandardCharsets.UTF_8)) {
			
			String[] splitedLine; // lineをsplitしたリスト
			
			for (String line; (line = reader.readLine()) != null; ){
				
				// split by [","] or [,] 
				if (line.startsWith("\"") && line.endsWith("\"")) {
					// split by [","]
					splitedLine = line.split("\",\"");
					splitedLine[0] = splitedLine[0].replaceFirst("\"", "");
					splitedLine[0] = splitedLine[0].replace("[\"]$", "");
				} else {
					// split by [,]
					splitedLine = line.split(",");
				}
				allLineList.add(splitedLine[corNum]);
			}
			
		} catch (IOException e) {
			System.out.println(e);
		}
		return allLineList;
	}
	
	public void writeTXT(String outputFileName, List<String> lineList) {
		
		try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName), StandardCharsets.UTF_8)) {
			
			for(String line : lineList) {
				writer.write(line);
				writer.newLine();
			}
			
			System.out.println("write complete.");
			
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}
	
	public void writeCSV(String outputFileName, List<List<String>> lineList) {
		try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName), StandardCharsets.UTF_8)) {
			
			for (int row = 0; row < lineList.get(0).size(); row++) {
				for (int colum = 0; colum < lineList.size(); colum++) {
					String word = lineList.get(colum).get(row);
					
					writer.write("\"");	
					writer.write("\"");
					writer.write(word);
					writer.write("\"");
					writer.write(",");
				}
				writer.newLine();
			}
			
			System.out.println("write complete.");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
