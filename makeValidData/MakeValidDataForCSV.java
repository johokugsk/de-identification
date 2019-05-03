package makeValidData;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MakeValidDataForCSV {


	public static void main(String[] args) {
		String hopitalFileName = "/var/ncdadata1/ncda/output/HospitalNamesUTF8.csv"; // 非公開データ
		String personFileName = "~/workspace/NHO/forResearchment/NHOTask/src/makeValidData/data/person.csv";
		String karteFileName = "~/workspace/NHO/forResearchment/NHOTask/src/makeValidData/data/ByoriKarte.csv";
		String newalKatreFileName = "~/workspace/NHO/forResearchment/NHOTask/src/makeValidData/data/taggedKarte.csv";
		
		List<List<String>> personList = new ArrayList<List<String>>();
		List<List<String>> karteText = new ArrayList<List<String>>();
		List<String> hospitalList = new LinkedList<String>();

		GenerateValidWord generater = new GenerateValidWord();
		// 人名データの読み込み
		personList = getPersonNameList(personFileName); 
		
		// 病院名データの読み込み
		System.out.println("read hospital list");
		hospitalList = getHospitalNameList(hopitalFileName, 0);
		// readCSV
		
		// 病理データの読み込み
		
		FileIO ioCSV = new FileIO();
		karteText = ioCSV.readCSV(karteFileName);
		
		String replacedWord; 
		int columm = 0;
		for (List<String> lineList : karteText) {
			if (lineList.isEmpty()) System.out.println("empty"); //debuged and showed empty
			int row = 0;
			for (String text : lineList) {
				text = Normalizer.normalize(text, Form.NFKC);
				
				if (text.contains("■■")) {
					// hospital
					while(text.contains("■■")) {
						replacedWord = String.join("", "<h>", generater.generateHospital(hospitalList), "</h>");
						text = text.replaceFirst("■■病院", replacedWord);
						text = text.replaceFirst("■■クリニック", replacedWord);
						text = text.replaceFirst("■■医院", replacedWord);
						text = text.replaceFirst("■■センター", replacedWord);

						text = Normalizer.normalize(text, Form.NFKC);
					}
					
					
					List tempLineList = lineList;
					tempLineList.set(row, text);
					karteText.set(columm, tempLineList);
					
				} else if (text.contains("〇〇") || text.contains("●●")) {
					// person
					while(text.contains("〇〇") || text.contains("●●")) {
						replacedWord = String.join("", "<p>", generater.generatePerson(personList), "</p>");
						text = text.replaceFirst("〇〇", replacedWord);
						text = text.replaceFirst("●●", replacedWord);
						
						text = Normalizer.normalize(text, Form.NFKC);
						
					}
										
					List tempLineList = lineList;
					tempLineList.set(row, text);
					karteText.set(columm, tempLineList);
					
				} else if (text.contains("△△")) {
					// date
					while (text.contains("△△")) {
						replacedWord = String.join("", "<t>", generater.generateDate(), "</t>");
						text = text.replaceFirst("△△", replacedWord);
						
						text = Normalizer.normalize(text, Form.NFKC);
					}
					
					List tempLineList = lineList;
					tempLineList.set(row, text);
					karteText.set(columm, tempLineList);
					
				}
				row++;
			}
			columm++;
		}
		
		// 対象箇所の検索・置換
			// おそらくreadLineしながら、正規表現replaceを行う
			// この情報をどうやって置き換えるか？ArrayList?
		
		// 置き換えたデータをcsvとして再び出力
		// writeCSV
		FileIO writeIns = new FileIO();
		writeIns.writeCSV(newalKatreFileName, karteText);
	}
	
	/**
	 * 
	 * @param fileName
	 * @param corNum
	 * @return
	 */
	static private List<String> getHospitalNameList(String fileName, int corNum) {
		List<String> hospitalNameList = new LinkedList<String>();
		
		FileIO ioClass = new FileIO(); // インスタンス生成
		List<String> hospitalList = new ArrayList<String>();
		hospitalList = ioClass.readCSV(fileName, corNum); // クラスメソッド使用
		
		for (String hospitalName: hospitalList) {
			
			if(hospitalName.contains(" ")) {
				String[] hospitalNames = null;
				// 一番後ろの要素を取り出す
				hospitalNames = hospitalName.split("　");
				hospitalName = hospitalNames[hospitalNames.length -1];
				
			} else if (hospitalName.contains("　")) {
				String[] hospitalNames = null;
				// 一番後ろの要素を取り出す
				hospitalNames = hospitalName.split("　");
				hospitalName = hospitalNames[hospitalNames.length -1];
			}
			
			if (hospitalName.equals("病院") || hospitalName.equals("クリニック")
					|| hospitalName.equals("医院") || hospitalName.equals("センター")) {
				continue;
			} else if (hospitalName.contains("メンタル") || hospitalName.contains("精神")) {
				continue;
			} else if(hospitalName.endsWith("病院") || hospitalName.endsWith("クリニック")
					|| hospitalName.endsWith("医院") || hospitalName.endsWith("センター")) {
				hospitalNameList.add(hospitalName);
			}
		}
		
		if(hospitalNameList.size() == 0) {
			System.out.println("Error in getHopitalNameList");
			System.out.println("list size is 0");
		}
		return hospitalNameList;
	}
	
	static private List<List<String>> getPersonNameList(String fileName) {
		
//		String personName = null;
		List<List<String>> personNameList = new ArrayList<List<String>>();
		FileIO ioClass = new FileIO(); // インスタンス生成
		personNameList = ioClass.readCSV(fileName); // クラスメソッド使用
		
		if(personNameList.size() == 0) {
			System.out.println("Error in getHopitalNameList");
			System.out.println("list size is 0");
		}
		return personNameList;
	}

}
