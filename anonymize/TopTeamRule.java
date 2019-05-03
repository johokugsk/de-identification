package anonymize;

import java.util.ArrayList;

/**
 * rule is based of Top result team in MedNLP1 De-idntification task. 
 * @author kkajiyama
 *
 */

public class TopTeamRule {

	/**
	 *
	 * @param currentWord
	 * @param testingLineList
	 * @param predictTagList
	 *
	 * @param ite
	 */
	public void ruleSex(String currentWord, ArrayList<String> testingLineList, ArrayList<String> predictTagList, int ite) {
		if (currentWord.contains("女性") || currentWord.contains("男性") || currentWord.contains("male") || currentWord.contains("man")) {
			if (currentWord.equals("女性") || currentWord.equals("男性") || currentWord.equals("male") || currentWord.equals("female")
					|| currentWord.equals("man") || currentWord.equals("woman") || currentWord.equals("men") || currentWord.equals("women")) {
				predictTagList.set(ite, "B-sex");
			}
		}

	}

	/**
	 *
	 * @param currentWord
	 * @param testingLineList
	 * @param predictTagList
	 * @param ite
	 */
	public void ruleAge(String currentWord, ArrayList<String> testingLineList, ArrayList<String> predictTagList, int ite) {
		/**
		 * age Tag
		 */
		if (currentWord.contains("age") || currentWord.contains("才") || currentWord.contains("歳") || currentWord.contains("代")) {

			if (currentWord.equals("age")) {

				boolean age_B_flag = true; // haven't use B-age Tag.

				for (int k = -2; k < 3; k++) {

					if (k == 0) {
						age_B_flag = true;
						continue;
					}

					String[] spl1 = testingLineList.get(ite + k).toString().split(" ");

					if (spl1.length < 2) {
						continue;
					}

					if (spl1[2].equals("数")) {

						if (age_B_flag && k < 0) {
							// ageよりも手前に年齢となる数字が来る場合かつ、タグのB
							predictTagList.set(ite + k, "B-age");
							age_B_flag = false;

						} else if (k < 0) {
							// ageよりも手前に年齢となる数字が来る場合
							predictTagList.set(ite + k, "I-age");

						} else if (0 < k && age_B_flag) {
							// ageよりも後ろに年齢となる数字が来る場合
							predictTagList.set(ite + k, "I-age");
							age_B_flag = false;

						} else if (k == 2) {
							/*
							 * ageよりも後に年齢となる数字が来る場合 k = 2の場合はE以外は取り得ない。
							 */
							predictTagList.set(ite + k, "I-age");
							if (age_B_flag) {
								predictTagList.set(ite + 1, "I-age");
								age_B_flag = false;
							}
						}
						// age本体へのタグ付け
						if (!age_B_flag) {
							predictTagList.set(ite, "I-age");
						}
					}
				}

			} else if (currentWord.contains("才") || currentWord.contains("歳") || currentWord.contains("代")) {

				String[] spl1 = testingLineList.get(ite - 1).toString().split(" ");
				String[] spl2 = null;

				// don't look "\n"
				if (2 <= spl1.length) {

					if (spl1[2].equals("数") && ite != 0) {
						// 13 才
						predictTagList.set(ite - 1, "B-age");

						spl2 = testingLineList.get(ite - 2).toString().split(" ");

						if (1 < spl2.length && (spl2[0].equals("-") || spl2[0].equals("〜") || spl2[0].equals("~")
								|| spl2[0].equals("ー"))) {

							String[] spl3 = testingLineList.get(ite - 3).split(" ");

							// ex : 10 ~ 30 代
							if (1 < spl3.length && spl3[2].equals("数")) {
								predictTagList.set(ite - 1, "I-age");
								predictTagList.set(ite - 2, "I-age");
								predictTagList.set(ite - 3, "B-age");
							} else if (1 < spl3.length
									&& (spl3[0].equals("歳") || spl3[0].equals("才") || spl3[0].equals("代"))) {

								String[] spl4 = testingLineList.get(ite - 4).split(" ");

								// ex 30 才 ~ 50 才
								if (1 < spl4.length && spl4[2].equals("数")) {
									predictTagList.set(ite - 1, "I-age");
									predictTagList.set(ite - 2, "I-age");
									predictTagList.set(ite - 3, "I-age");
									predictTagList.set(ite - 4, "B-age");
								}
							}
						}
					}

					// post option1

					spl2 = testingLineList.get(ite + 1).toString().split(" ");

					// ex : 13 才 より
					if (spl2[0].equals("より") || spl2[0].equals("まで") || spl2[0].equals("代") || spl2[0].equals("前半")
							|| spl2[0].equals("後半") || spl2[0].equals("〜") || spl2[0].equals("~")
							|| spl2[0].equals("以上") || spl2[0].equals("以下") || spl2[0].equals("から")
							|| spl2[0].equals("時")) {

						predictTagList.set(ite, "I-age");
						predictTagList.set(ite + 1, "I-age");

						spl2 = testingLineList.get(ite + 2).toString().split(" ");

						// ex : 10 代 前半 から
						if (spl2[0].equals("より") || spl2[0].equals("まで") || spl2[0].equals("から") || spl2[0].equals("~")
								|| spl2[0].equals("～") || spl2[0].equals("頃") || spl2[0].equals("ごろ")
								|| spl2[0].equals("ころ")) {
							predictTagList.set(ite + 1, "I-age");
							predictTagList.set(ite + 2, "I-age");
						}

					} else if (spl2[0].equals("ごろ") || spl2[0].equals("ころ") || spl2[0].equals("頃")) {

						// post option2

						String[] spl3 = testingLineList.get(ite + 2).toString().split(" ");

						// ex : 13 才 ごろ まで
						if (spl3[0].equals("まで") || spl3[0].equals("より") || spl3[0].equals("から") || spl2[0].equals("~")
								|| spl2[0].equals("～")) {

							predictTagList.set(ite, "I-age");
							predictTagList.set(ite + 1, "I-age");
							predictTagList.set(ite + 2, "I-age");
						} else {
							predictTagList.set(ite, "I-age");
							predictTagList.set(ite + 1, "I-age");
						}

					} else {
						predictTagList.set(ite, "I-age");
					}
				}

			}
		}

	}

	/**
	 *
	 * @param currentWord
	 * @param testingLineList
	 * @param predictTagList
	 * @param ite
	 */
	public void ruleTime(String currentWord, ArrayList<String> testingLineList, ArrayList<String> predictTagList, int ite) {

		/**
		 * time Tag
		 */
		String[] splPreLine = null; // ite - 1
		String[] splPostLine = null; // ite + 1

		boolean timeAppaired = false;

		// main time rule unit 1
		if (currentWord.contains("年") || currentWord.contains("月") || currentWord.contains("日") || currentWord.contains("週間") || currentWord.contains("時") || currentWord.contains("分")) {

			// ここに
			if (currentWord.equals("昨年") || currentWord.equals("今年") || currentWord.equals("一昨年") || currentWord.equals("来年") || currentWord.equals("同年")
					|| currentWord.equals("翌年")) {
				// 年
				predictTagList.set(ite, "B-time");
				timeAppaired = true;

			} else if (currentWord.equals("先月") || currentWord.equals("今月") || currentWord.equals("一昨月") || currentWord.equals("来月")
					|| currentWord.equals("同月")) {
				// 月
				predictTagList.set(ite, "B-time");
				timeAppaired = true;
			} else if (currentWord.equals("先週") || currentWord.equals("先々週") || currentWord.equals("来週") || currentWord.equals("再来週")
					|| currentWord.equals("来月")) {
				// 週
				predictTagList.set(ite, "B-time");

				timeAppaired = true;
			} else if (currentWord.equals("昨日") || currentWord.equals("今日") || currentWord.equals("一昨日") || currentWord.equals("明後日")
					|| currentWord.equals("翌日") || currentWord.equals("前日") || currentWord.equals("当日")) {
				// 日
				predictTagList.set(ite, "B-time");
				timeAppaired = true;

			} else if (currentWord.equals("翌朝") || currentWord.equals("未明") || currentWord.equals("その後") || currentWord.equals("同日")) {
				// others

				predictTagList.set(ite, "B-time");
				timeAppaired = true;

			} else if (0 < ite && !(currentWord.contains("年間") || currentWord.contains("生") || currentWord.contains("ヶ"))) {
				/**
				 * tagging followed words.
				 * ※xx's partOfSpeech is  数
				 * xx
				 * 月
				 * xx
				 * 日
				 * xx
				 * 時
				 * xx
				 * 分
				 */

				splPreLine = testingLineList.get(ite - 1).toString().split(" "); // i - 1
				
				if (currentWord.equals("年") || currentWord.equals("月") ||
						currentWord.equals("日") || currentWord.equals("週間") ||
						currentWord.equals("時") || currentWord.equals("分") || currentWord.equals("月頃") ) {
					if (2 < splPreLine.length) {
						if (splPreLine[2].equals("数")) {
							predictTagList.set(ite - 1, "B-time");
							predictTagList.set(ite, "I-time");
							timeAppaired = true;
						}
					}
				}
			}
		}

		// main time rule unit 2
		if ((currentWord.contains("/") || currentWord.contains(".")) && 1 < ite) {

			/*
			 * 2018.04.24が1つの形態素(まとまり)として分割されることは決して無い。
			 */

			if (currentWord.matches("^[0-9]{4}/[01]?[0-9]/[0123]?[0-9]$") || currentWord.matches("^[01]?[0-9]/[0123]?[0-9]$")) {
				// origin yyyy/mm/dd or mm/dd
				predictTagList.set(ite, "B-time");
				timeAppaired = true;

			} else if (currentWord.equals("/") || currentWord.equals("\\.")) {

				if (ite > 1 || predictTagList.size() > ite) {

					splPreLine = testingLineList.get(ite - 1).toString().split(" ");
					if  (testingLineList.get(ite - 1).equals("") || testingLineList.get(ite - 1).matches("\n")) {
						return;
					}
					splPostLine = testingLineList.get(ite + 1).toString().split(" ");
					if  (testingLineList.get(ite + 1).equals("") || testingLineList.get(ite - 1).matches("\n")) {
						return;
					}

					if (splPreLine[2].equals("数") && splPostLine[2].equals("数")) {

						if (splPreLine[0].matches("^[0-9]{4}$") && splPostLine[0].matches("^[01]?[0-9]()$")) {
							/*
							 *  yyyy
							 *  /
							 *  mm
							 *  
							 *  ex) 
							 *  
							 *  2018
							 *  /
							 *  06
							 */
							predictTagList.set(ite - 1, "B-time");
							predictTagList.set(ite, "I-time");
							predictTagList.set(ite + 1, "I-time");
							timeAppaired = true;

						} else if (splPreLine[0].matches("^[01]?[0-9]$") && splPostLine[0].matches("^[0123]?[0-9]$")) {

							/*
							 *  mm
							 *  /
							 *  dd
							 *  
							 *  ex) 
							 *  
							 *  12
							 *  /
							 *  31
							 */
							predictTagList.set(ite - 1, "B-time");
							predictTagList.set(ite, "I-time");
							predictTagList.set(ite + 1, "I-time");
							timeAppaired = true;

						}
					}
				}
			}
		}

		// pre option unit
		if (timeAppaired && 1 <= ite) {
			splPreLine = testingLineList.get(ite - 1).toString().split(" ");

			if (2 <= splPreLine.length) {

				/*
				 * ex)
				 *  入院
				 *  同日
				 */
				if (splPreLine[0].equals("翌") || splPreLine[0].equals("前") || splPreLine[0].equals("入院") || splPreLine[0].equals("来院")
						|| splPreLine[0].equals("午前") || splPreLine[0].equals("午後") || splPreLine[0].equals("入院後") || splPreLine[0].equals("発症")) {
					predictTagList.set(ite - 1, "B-time");
					predictTagList.set(ite, "I-time");
				}

				String[] spleBeforePreLine = testingLineList.get(ite - 2).toString().split(" ");

				if (2 <= spleBeforePreLine.length) {

					// 翌(前)xx年(月日)
					if (spleBeforePreLine[0].equals("翌") || spleBeforePreLine[0].equals("前") || spleBeforePreLine[0].equals("入院") || spleBeforePreLine[0].equals("来院")) {
						predictTagList.set(ite - 2, "B-time");
						predictTagList.set(ite - 1, "I-time");
						predictTagList.set(ite, "I-time");
					} else if (spleBeforePreLine[0].equals("発症")) {
						predictTagList.set(ite - 2, "B-time");
						predictTagList.set(ite - 1, "I-time");
						predictTagList.set(ite, "I-time");
					} else if (spleBeforePreLine[0].equals("から") || spleBeforePreLine[0].equals("してから")) {
						/*
						 * 発症
						 * から
						 * xx
						 * 日
						 */
						if (3 < ite) {
							String[] splBeforeBeforePreLine = testingLineList.get(ite - 3).split(" ");
							if (2 <= splBeforeBeforePreLine.length) {
								if (splBeforeBeforePreLine[0].equals("発症") || splBeforeBeforePreLine[0].equals("治療")) {
									predictTagList.set(ite - 3, "B-time");
									predictTagList.set(ite - 2, "I-time");
									predictTagList.set(ite - 1, "I-time");
									predictTagList.set(ite, "I-time");
								}
							}
						}
					}
				}
			}
		}

		// post option 1
		if (ite < testingLineList.size() - 1) {

			splPostLine = testingLineList.get(ite + 1).toString().split(" ");

			if (timeAppaired && 2 <= splPostLine.length) {

				if (splPostLine[0].equals("前") || splPostLine[0].equals("後")) {
					predictTagList.set(ite, "I-time");
					predictTagList.set(ite + 1, "I-time");
				} else if (splPostLine[0].equals("前半") || splPostLine[0].equals("後半")) {
					predictTagList.set(ite, "I-time");
					predictTagList.set(ite + 1, "I-time");
				}
				if (splPostLine[0].equals("から") || splPostLine[0].equals("より") || splPostLine[0].equals("代") || splPostLine[0].equals("まで")
						|| splPostLine[0].equals("～") || splPostLine[0].equals("~") || splPostLine[0].equals("目") || splPostLine[0].equals("午前")
						|| splPostLine[0].equals("午後") || splPostLine[0].equals("朝") || splPostLine[0].equals("昼") || splPostLine[0].equals("夕")
						|| splPostLine[0].equals("夕刻") || splPostLine[0].equals("夜") || splPostLine[0].equals("明け方") || splPostLine[0].equals("晩")
						|| splPostLine[0].equals("明朝") || splPostLine[0].equals("春") || splPostLine[0].equals("夏") || splPostLine[0].equals("秋")
						|| splPostLine[0].equals("冬") || splPostLine[0].equals("ごろ") || splPostLine[0].equals("ころ") || splPostLine[0].equals("頃")
						|| splPostLine[0].equals("から") || splPostLine[0].equals("上旬") || splPostLine[0].equals("中旬") || splPostLine[0].equals("下旬")
						|| splPostLine[0].equals("以前") || splPostLine[0].equals("以降") || splPostLine[0].equals("程") || splPostLine[0].equals("ほど")
						|| splPostLine[0].equals("早朝")) {

					predictTagList.set(ite, "B-time");
					predictTagList.set(ite + 1, "I-time");
				}
			}

			// post option 2

			if (ite < testingLineList.size() - 2) {
				String[] splAfterPostLine = testingLineList.get(ite + 2).toString().split(" ");

				if (2 < splAfterPostLine.length) {

					if (splAfterPostLine[0].equals("まで") || splAfterPostLine[0].equals("より") || splAfterPostLine[0].equals("から") ||
							splAfterPostLine[0].equals("~") || splAfterPostLine[0].equals("〜") || splAfterPostLine[0].equals("前")) {

						predictTagList.set(ite + 2, "I-time");
					} 
				}
			}

		}
		
		/*
		 * H
		 * 30
		 * 年
		 */
		
		if (currentWord.equals("H")) {
			splPostLine = testingLineList.get(ite + 1).toString().split(" ");
			if (1 < splPostLine.length) {
				String[] splAfterPostLine = testingLineList.get(ite + 2).toString().split(" ");
				if (1 < splPostLine.length) {
					if (splAfterPostLine[0].contains("年") || splPostLine[2].equals("数")) {
						predictTagList.set(ite, "B-time");
						predictTagList.set(ite + 1, "I-time");
						predictTagList.set(ite + 2, "I-time");
					}
				}
				
			}
			
		}
	}

	/**
	 *
	 * @param currentWord
	 * @param testingLineList
	 * @param predictTagList
	 * @param ite
	 */
	public void ruleTimeAndAge(String currentWord, ArrayList<String> testingLineList, ArrayList<String> predictTagList, int ite) {

		/**
		 * time and Age Tag
		 */

		/*
		 * ... spl1[0] word spl2[0] ...
		 */
		String[] splPreLine = null; // pre word
		String[] splPostLine = null; // post word

		String preWord;
		String postWord;

		// main time rule unit 1
		if (currentWord.equals("頃") || currentWord.equals("ころ") || currentWord.equals("ごろ") || currentWord.equals("時")) {

			splPreLine = testingLineList.get(ite - 1).toString().split("");
			preWord = splPreLine[0];

			splPostLine = testingLineList.get(ite + 1).toString().split("");
			postWord = splPostLine[0];

			if (preWord.equals("才") || preWord.equals("歳")) {
				predictTagList.set(ite - 1, "I-age");
				predictTagList.set(ite, "I-age");
				if (postWord.equals("から") || postWord.equals("~") || postWord.equals("～") || postWord.equals("まで")) {
					predictTagList.set(ite + 1, "I-age");
				}

			} else if (!preWord.matches("^[\\u3040-\\u309F]+$")) {
				predictTagList.set(ite - 1, "I-time");
				predictTagList.set(ite, "I-time");
				if (postWord.equals("から") || postWord.equals("~") || postWord.equals("～") || postWord.equals("まで")) {
					predictTagList.set(ite + 1, "I-time");
				}
			}
		}
	}

	/**
	 *
	 * @param currentWord
	 * @param testingLineList
	 * @param predictTagList
	 * @param ite
	 */
	public void ruleHospital(String currentWord, ArrayList<String> testingLineList, ArrayList<String> predictTagList, int ite) {

		String[] splPreLine = null;
		String[] spl2 = null;
		String department = null;

		if (currentWord.equals("病院") || currentWord.equals("医院") || currentWord.contains("クリニック")) {
			int backIte = 1; // 0 < k < i
			
			while (backIte < (ite)) {

				splPreLine = testingLineList.get(ite - backIte).toString().split(" ");

				/*
				 * ■or名詞∧非自立:ite - k
				 * .
				 * .
				 * .
				 * ■or名詞∧非自立:ite - 1
				 * 病院orクリニックor医院:ite
				 * 
				 */
				
				// adjostIteの値を決定
				if (2 < splPreLine.length && (splPreLine[1].equals("名詞") && !splPreLine[2].equals("非自立") || splPreLine[0].equals("■"))) {
					backIte++;
				} else {
					break;
				}
			}
			// adjostIteの値を元にタグ付与
			if (backIte != 1) {
				boolean hospital_B = true;
				predictTagList.set(ite, "I-hospital");
				while (0 < backIte) {
					ite = ite + 1;
					if (!hospital_B) {
						predictTagList.set(ite - backIte, "I-hospital");
					} else if (hospital_B) {
						predictTagList.set(ite - backIte, "B-hospital");
						hospital_B = false;
					}
					ite = ite - 1;
					backIte = backIte - 1;
				}

			} 
	
		}
		if(currentWord.contains("■") || currentWord.contains("●") || currentWord.contains("▲") || currentWord.contains("○")) {
			/*
			 * ■■■■■■
			 * 消化
			 * 器
			 * 内科
			 */
			predictTagList.set(ite, "B-hospital");
			// 診療科までカバー
			
			department = testingLineList.get(ite + 1).toString();
			
			if (department.contains("外") || department.contains("眼") || department.contains("循環")
					|| department.contains("皮膚") || department.contains("内") || department.contains("整形")
					|| department.contains("脳神経外") || department.contains("小児") || department.contains("産婦人")
					|| department.contains("泌尿") || department.contains("リハビリテーション") || department.contains("呼吸外")
					|| department.contains("心臓") || department.contains("肛門") || department.contains("放射線")
					|| department.contains("耳鼻咽喉") || department.contains("心療") || department.contains("アレルギー")
					|| department.contains("神経内") || department.contains("歯")|| department.contains("呼吸")
					|| department.contains("リウマチ") || department.contains("新生児")) {

				predictTagList.set(ite + 1, "I-hospital");
				predictTagList.set(ite, "B-hospital");
			} else {
				predictTagList.set(ite, "B-hospital");
			}
		}

		if (currentWord.contains("近") || currentWord.contains("当")) {

			splPreLine = currentWord.split(" ");

			if (splPreLine[0].equals("近") || (splPreLine[0].equals("当"))) {
				spl2 = testingLineList.get(ite + 1).split(" ");
				if (splPreLine[0].equals("近") && spl2[0].equals("医")) {
					predictTagList.set(ite, "B-hospital");
					predictTagList.set(ite + 1, "I-hospital");
				}
				if (splPreLine[0].equals("当") && spl2[0].equals("院")) {
					predictTagList.set(ite, "B-hospital");
					predictTagList.set(ite + 1, "I-hospital");
				}
			}

		}
		if (currentWord.equals("同院")) {
			predictTagList.set(ite, "B-hospital");
		}
	}

	/**
	 *
	 * @param word
	 * @param test
	 * @param predict
	 * @param ite
	 */
	public void rulePerson(String word, ArrayList<String> test, ArrayList<String> predict, int ite) {

		if (word.contains("氏名") || word.contains("番号")) {

			String[] spl1 = null;
			String[] spl2 = null;

			if (word.equals("氏名") || spl1[0].equals("番号")) {

				for (int k = 0; ite < 5; k++) {

					spl2 = test.get(ite + k).toString().split(" ");

					if (spl2[2].equals("数") || spl2[1].equals("名詞")) {
						if (!predict.get(ite + k - 1).equals("B-person")) {
							predict.set(ite + k, "B-person");
						} else if (predict.get(ite + k - 1).equals("I-person")) {
							predict.set(ite + k, "E-person");
						} else {
							predict.set(ite, "B-person");
						}
					}
				}
			}
		}

		if (word.contains("Dr")) {

			String[] spl2 = null;

			if (word.equals("Dr")) {

				spl2 = test.get(ite - 1).toString().split(" ");

				if (spl2[1].equals("名詞")) {
					predict.set(ite - 1, "B-person");
					predict.set(ite, "E-person");
				}

				spl2 = test.get(ite + 1).toString().split(" ");

				if (spl2[0].equals(".")) {

					String[] spl3 = null;

					spl3 = test.get(ite + 1).toString().split(" ");
					if (spl3[1].equals("名詞")) {

						// System.out.println(spl3[0]);

						predict.set(ite, "B-person");
						predict.set(ite + 1, "I-person");
						predict.set(ite + 2, "E-person");

						String[] spl4 = test.get(ite + 3).toString().split(" ");

						if (spl4[0].equals(".")) {

							String[] spl5 = test.get(ite + 4).toString().split(" ");

							if (spl5[1].equals("名詞")) {
								predict.set(ite + 2, "I-person");
								predict.set(ite + 3, "I-person");
								predict.set(ite + 4, "E-person");
							}
						}
					}
				} else if (spl2[1].equals("名詞")) {

					predict.set(ite, "B-person");
					predict.set(ite + 1, "E-person");

					String[] spl3 = test.get(ite + 2).split(" ");

					if (1 < spl3.length && spl3[1].equals("名詞")) {
						predict.set(ite + 1, "I-person");
						predict.set(ite + 2, "E-person");
					}
				}
			}
		}

		if (word.contains("先生")) {

			String[] spl2 = null;

			if (word.equals("先生")) {
				for (int k = 1; k <= 4; k++) {

					spl2 = test.get(ite - k).toString().split(" ");

					if (spl2.length < 2 && spl2[1].equals("名詞")) {
						// xx先生
						predict.set(ite - k, "B-person");

						if (predict.get(ite - (k - 1)).toString().equals("B-person")) {
							predict.set(ite - (k - 1), "I-person");
						}

						predict.set(ite, "E-person");

					} else if (test.get(ite - k).toString().startsWith("　")
							|| test.get(ite - k).toString().startsWith(" ")) {

						// "xx xx 先生"のような記述対策

						spl2 = test.get(ite - (k + 1)).toString().split(" ");

						if (spl2[1].equals("名詞")) {
							predict.set(ite - (k + 1), "B-person");
							predict.set(ite - k, "I-person");

							if (predict.get(ite - (k - 1)).toString().equals("B-person")) {
								predict.set(ite - (k - 1), "I-person");
							}

							predict.set(ite, "E-person");
						}

					} else {
						break;
					}
				}
			}
		}
		if (word.contains("医")) {
			// oo医(整形外科医, 内科医) xx xx

			String[] spl1 = null;
			String[] spl2 = test.get(ite - 1).toString().split(" ");

			if (word.equals("医") && spl2[1].equals("名詞")) {

				spl1 = test.get(ite + 2).toString().split(" "); // 苗字と予想される

				if (test.get(ite + 1).toString().startsWith(" ") && spl1[1].equals("名詞")) {
					predict.set(ite + 2, "B-person");
				}

				spl2 = test.get(ite + 4).toString().split(" "); // 苗字と予想される

				if (test.get(ite + 3).toString().startsWith(" ") && spl1[1].equals("名詞")) {
					predict.set(ite + 3, "I-person");
					predict.set(ite + 4, "E-person");
				}

			} else if (spl1[0].equals("主治医") && 1 < spl2.length && spl2[1].equals("名詞")) {
				// oo主治医
				predict.set(ite, "B-person");
			}
		}
		if (word.contains("術者") || word.contains("助手")) {
			// 以下の手術情報の記載対策
			// 術者 xx xx

			String[] spl2 = null;

			if (word.equals("術者") || word.equals("助手")) {
				for (int k = 1; k < 3; k++) {

					spl2 = test.get(ite + k).toString().split(" ");

					if (test.get(ite + k).toString().startsWith(" ")) {

						String[] spl3 = test.get(ite + (k + 1)).toString().split(" ");

						if (spl3[1].equals("名詞")) {
							predict.set(ite, "B-person");
							predict.set(ite + k, "I-person");
							predict.set(ite + (k + 1), "E-person");
						}
					} else if (spl2[1].equals("名詞")) {
						predict.set(ite, "B-person");
						predict.set(ite + k, "E-person");
					}
				}
			}
		}

	}

}
