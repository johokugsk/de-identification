package makeValidData;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MakeValidDataForTXT{


	public static void main(String[] args) {
		String hopitalFileName = "~/Documents/GitHub/anonymize/NHOTask/src/makeValidData/data/HospitalNamesUTF8.csv";
		String personFileName = "~/Documents/GitHub/anonymize/NHOTask/src/makeValidData/data/person.csv";
		String karteFileName = "~/Documents/GitHub/anonymize/NHOTask/src/makeValidData//data/ByoriKarteUTF8.txt";
		String newalKatreFileName = "~/Documents/GitHub/anonymize/NHOTask/src/makeValidData/data/taggedKarte0501.xml";

		List<List<String>> personList = new ArrayList<List<String>>();
		List<String> karteText = new ArrayList<String>();
		List<String> hospitalList = new LinkedList<String>();

		GenerateValidWord generater = new GenerateValidWord();
		// 人名データの読み込み
		personList = getPersonNameList(personFileName); 

		// 病院名データの読み込み
		hospitalList = getHospitalNameList(hopitalFileName, 0);
		System.out.println("has read hospital list just now.");
		// readCSV

		FileIO ioTXT = new FileIO();
		karteText = ioTXT.readTXT(karteFileName);

		int ite = 0;
		String replacedWord; 
		for (String text :karteText) {
			text = Normalizer.normalize(text, Form.NFKC);
			System.out.println("ite = " + ite);
//			if (ite >= 1852) {
//				System.out.println("text = " + text);
//			}
//			
			// xml escape seaquence.
			if (text.contains("<")) {
				text = text.replace("<", "&lt;");
			}
			if (text.contains(">")) {
				text = text.replace(">", "&gt;");
			}
			if (text.contains("&")) {
				text = text.replace("&", "&amp;");
			}
			
			// be replaced to valid word
			if (text.contains("■■")) {
				// hospital
				while(text.contains("■■")) {
					replacedWord = String.join("", "<h>", generater.generateHospital(hospitalList), "</h>");
					text = text.replaceFirst("■■病院", replacedWord);
					text = text.replaceFirst("■■クリニック", replacedWord);
					text = text.replaceFirst("■■医院", replacedWord);
					text = text.replaceFirst("■■センター", replacedWord);
					text = text.replaceFirst("■■HP", replacedWord);
				}
				text = Normalizer.normalize(text, Form.NFKC);

			}
			if (text.contains("〇〇") || text.contains("●●") || text.contains("○○")) {
				// person
				while(text.contains("〇〇") || text.contains("●●") || text.contains("○○")) {
					replacedWord = String.join("", "<p>", generater.generatePerson(personList), "</p>");
					text = text.replaceFirst("〇〇", replacedWord);
					text = text.replaceFirst("●●", replacedWord);
					text = text.replaceFirst("○○", replacedWord);
				}
				text = Normalizer.normalize(text, Form.NFKC);
			}
			if (text.contains("△△")) {
				
				if (text.contains("△△年")) {
					replacedWord = String.join("", "<t>", generater.generateDate(0), "年", "</t>");
					text = text.replace("△△年", replacedWord);
				}
				if (text.contains("△△月")) {
					replacedWord = String.join("", "<t>", generater.generateDate(0), "月", "</t>");
					text = text.replace("△△月", replacedWord);
				}
				if (text.contains("△△日")) {
					replacedWord = String.join("", "<t>", generater.generateDate(0), "日", "</t>");
					text = text.replace("△△日", replacedWord);
				}
				// date
				while (text.contains("△△")) {
					replacedWord = String.join("", "<t>", generater.generateDate(), "</t>");
					text = text.replaceFirst("△△", replacedWord);
				}
				text = Normalizer.normalize(text, Form.NFKC);
			}
			
			karteText.set(ite, text);
			ite++;
		}

		// 対象箇所の検索・置換
		// おそらくreadLineしながら、正規表現replaceを行う
		// この情報をどうやって置き換えるか？ArrayList?

		// 置き換えたデータをcsvとして再び出力
		// writeCSV
		FileIO writeIns = new FileIO();
		writeIns.writeTXT(newalKatreFileName, karteText);
		System.out.println("完了しました。");
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
