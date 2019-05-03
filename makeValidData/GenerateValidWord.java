package makeValidData;

import java.util.List;
import java.util.Random;

/**
 * 有効な単語を生成するためのクラス
 * <br/>以下の3つを扱う
 * * 日付
 * * 病院名
 * * 人名
 * @author kkajiyama
 *
 */
public class GenerateValidWord {
	
	public String generateDate() {
		String generatedDate = null;
		
		Random rand = new Random();
		int dateType = rand.nextInt(8);
		int year = rand.nextInt(19) + 2000;
		int month = rand.nextInt(12) + 1;
		int day = rand.nextInt(30) + 1;
		
		String yearStr = String.valueOf(year);
		String monthStr = String.valueOf(month);
		String dayStr = String.valueOf(day);
		
		if (dateType == 0) {
			// 12/15
			generatedDate = String.join("/", monthStr, dayStr);
			
		} else if (dateType == 1) {
			// 2018/12/15
			if (month < 10) {
				monthStr = String.join("", "0", monthStr);
			}
			if (day < 10) {
				dayStr = String.join("", "0", dayStr);
			}
			generatedDate = String.join("/",yearStr, monthStr, dayStr);
			
			
		} else if (dateType == 2) {
			// 12月15日
			generatedDate = String.join("", monthStr, "月", dayStr, "日");
			
		} else if (dateType == 3) {
			// 2018年12月15日
			generatedDate = String.join("",yearStr,"年", monthStr, "月", dayStr, "日");
			
		} else if (dateType == 4) {
			// 12-15
			if (month < 10) {
				monthStr = String.join("", "0", monthStr);
			}
			if (day < 10) {
				dayStr = String.join("", "0", dayStr);
			}
			generatedDate = String.join("-", monthStr, dayStr);
			
			
		} else if (dateType == 5) {
			// 2018-12-15
			if (month < 10) {
				monthStr = String.join("", "0", monthStr);
			}
			if (day < 10) {
				dayStr = String.join("", "0", dayStr);
			}
			generatedDate = String.join("-",yearStr, monthStr, dayStr);
						
		} else if (dateType == 6) {
			// 12.15
			if (month < 10) {
				monthStr = String.join("", "0", monthStr);
			}
			if (day < 10) {
				dayStr = String.join("", "0", dayStr);
			}
			generatedDate = String.join(".", monthStr, dayStr);
			
		} else if (dateType == 7) {
			// 2018.12.15
			if (month < 10) {
				monthStr = String.join("", "0", monthStr);
			}
			if (day < 10) {
				dayStr = String.join("", "0", dayStr);
			}
			generatedDate = String.join(".",yearStr, monthStr, dayStr);
			
		}
		
		return generatedDate;
		// Examples:
		//		09-22
		//		2000年4月10日
		//		2017-11-01
		//		2017.05.19
		//		02.21
		//		2017-01-22
		//		2010-09-12
		//		04-24
		//		2010年10月18日
		//		5/4
	}
	
	/**
	 * 
	 * @param typeNum </br>
	 * 
	 * 0 == yyyy </br>
	 * 1 == mm </br>
	 * 2 == dd </br>
	 * 
	 * @return
	 */
	public String generateDate(int typeNum) {
		String generatedDate = null;
		
		Random rand = new Random();
		int dateType = typeNum;
		int year = rand.nextInt(19) + 2000;
		int month = rand.nextInt(12) + 1;
		int day = rand.nextInt(30) + 1;
		
		String yearStr = String.valueOf(year);
		String monthStr = String.valueOf(month);
		String dayStr = String.valueOf(day);
		
		if (dateType == 0) {
			// △△年
			generatedDate = yearStr;
			
		} else if (dateType == 1) {
			// △△月
			generatedDate = monthStr;
			
			
		} else if (dateType == 2) {
			// △△日
			generatedDate = dayStr;
		}		
		return generatedDate;
	
	}
	
	public String generateHospital(List<String> hospitalList) {
		String hospitalName = null;
		Random randm = new Random();
		int num = randm.nextInt(hospitalList.size());
		hospitalName = hospitalList.get(num);
		int rand = randm.nextInt(10);
		if (rand == 3 && hospitalName.contains("病院")) {
			hospitalName = hospitalName.replace("病院", "hp");
		} else if (rand == 6 && hospitalName.contains("病院")) {
			hospitalName = hospitalName.replace("病院", "Hp");
		} else if (rand == 9 && hospitalName.contains("病院")) {
			hospitalName = hospitalName.replace("病院", "HP");
		} else if (rand == 0 && hospitalName.contains("病院")) {
			hospitalName = hospitalName.replaceAll("病院", "");
		} else if (rand == 0 && hospitalName.contains("センター")) {
			hospitalName = hospitalName.replaceAll("センター", "");
		} 
		return hospitalName;
	}
	
	public String generatePerson(List<List<String>> personList) {
		String personName = null;
		
		List<String> firstNames = personList.get(0);
		List<String> lastNames = personList.get(1);
		
		Random randm = new Random();
		int ifNum = randm.nextInt(4);
		int firstNameNum = randm.nextInt(firstNames.size());
		// 0 or 1  is firstName only [A]
		if (ifNum == 0 || ifNum == 1) {
			personName = firstNames.get(firstNameNum);
		} else {
			int lastNameNum = randm.nextInt(lastNames.size());

			if (ifNum == 2) {
				// 2 is firstName and lastName [AB]
				personName = String.join("", firstNames.get(firstNameNum), lastNames.get(lastNameNum));
			} else {
				// 3 is firstName and lastName with space [A B]
				personName = String.join(" ", firstNames.get(firstNameNum), lastNames.get(lastNameNum));
			}
		
		
		}
		return personName;
	}

}
