


package anonymize.Xml2TaggingFormat;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.atilika.kuromoji.Token;
import com.atilika.kuromoji.Tokenizer;
import com.atilika.kuromoji.Tokenizer.Mode;


/**
 * This program output 2 files which  are fomarted to MALLET.
 * One file is add correct tag and the other is no one.
 * 
 * @author kkajiyama
 *
 */
public class TaggingBIOforMedNLP1 {
	// kuromoji
	private static Tokenizer tokenizer;
	static{
		Mode mode = Mode.valueOf("NORMAL");
		//使う辞書の指定
		 String userDic = "~/workspace/NHOTask/user_dic/wikipedia_addbyomei.csv";
		try {
			tokenizer = Tokenizer.builder().mode(mode).userDictionary(userDic).build();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	// other
	static final  String TIME_TAG = "t";
	static final String AGE_TAG = "a";
	static final String HOSPITAL_TAG = "h";
	static final String SEX_TAG = "x";
	// real gati
//	static final File INPUT_FILENAME = new File("~/Downloads/byoriKarte/taggedKarte.xml");
	static final File INPUT_FILENAME = new File("~/workspace/NHOTask/MedNLPs_Data/medNLP1.xml");
	static final File CRF_TRAIN_FILE = new File("~/Documents/formatedData/medNLP1/trainData.txt");
	static final File CRF_TEST_FILE = new File("~/Documents/formatedData/medNLP1/noTagData.txt");

	static boolean first = true;
	static boolean inTag = false;
	static String tagName;
	static boolean startS = true; // Beginning of Tag;
	static boolean endS = false; // End of Tag;
	static boolean BOS = true;
	static int allLineNum;
	static int lineCount;

	// features
	static String wordMode = "ひらがな";
	static String isKnown = "true";
	static String bos = "BOS"; // first word of a line.
	static String tagType = "time";
	/**
	 * bioe Tagging
	 *
	 * B : beginning of tag
	 * I : inside of tag
	 * E : end of tag
	 * O : outside of tag
	 */
	static String bioe = "O";



	// main
	public static void main(String arg[]) throws XMLStreamException, IOException {
		checkBeforeWritefile(CRF_TRAIN_FILE);
		BufferedWriter crfTrainWriter = new BufferedWriter(new FileWriter(CRF_TRAIN_FILE, false));
		checkBeforeWritefile(CRF_TEST_FILE);
		BufferedWriter crfTestWriter = new BufferedWriter(new FileWriter(CRF_TEST_FILE, false));
		// count lineNumber
		BufferedReader br = new BufferedReader(new FileReader(INPUT_FILENAME));
		String getLine;
		ArrayList<String> list = new ArrayList<String>();
		while (null != (getLine = br.readLine())) {
			list.add(getLine);
		}
		allLineNum = list.size();
		br.close();
		// StAX parser setup
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(INPUT_FILENAME));
		XMLStreamReader xmlReader = factory.createXMLStreamReader(stream);

		String text = null;
		String normalizedText = null;

		for (; xmlReader.hasNext(); xmlReader.next()) {

			int eventType = xmlReader.getEventType();

			if (eventType == XMLStreamConstants.START_ELEMENT) {

				int ite = xmlReader.getAttributeCount();
				if (ite != 0) {
					for (int index = 0; index < ite; index++) {
					}
				}

				if (xmlReader.getLocalName().equals(TIME_TAG)) {
					inTag = true;
					startS = true;
					tagName = "time";
				}
				if (xmlReader.getLocalName().equals(AGE_TAG)) {
					inTag = true;
					startS = true;
					tagName = "age";
				}
				if (xmlReader.getLocalName().equals(HOSPITAL_TAG)) {
					inTag = true;
					startS = true;
					tagName = "hospital";
				}
				if (xmlReader.getLocalName().equals(SEX_TAG)) {
					inTag = true;
					startS = true;
					tagName = "sex";
				}

			} else if (eventType == XMLStreamConstants.END_ELEMENT) {

				if (xmlReader.getLocalName().equals(TIME_TAG) || xmlReader.getLocalName().equals(AGE_TAG)
						|| xmlReader.getLocalName().equals(HOSPITAL_TAG) || xmlReader.getLocalName().equals(SEX_TAG)) {
					inTag =false;
				}
				if (xmlReader.getLocalName().equals("root")) {
					crfTrainWriter.close();
//					bw3.close();
					xmlReader.close();
					System.out.println(lineCount);
					System.out.println("END"); // 終了の確認
					return;
				}
				
			} else if (eventType == XMLStreamConstants.CHARACTERS) {
				text = xmlReader.getText();

				normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKC);
				if (normalizedText.contains("\n\n\n\n\n")) {
					System.out.println("(word.matches(\\n5))");
					normalizedText.replaceAll("\n\n\n\n\n", "\n\n");
				}
				if (normalizedText.contains("\n\n\n\n")) {
					System.out.println("(word.matches(\\n4))");
					normalizedText.replaceAll("\n\n\n\n", "\n\n");
				}
				
				if (normalizedText.contains("\n\n\n")) {
					System.out.println("(word.matches(\\n3))");
					normalizedText.replaceAll("\n\n\n", "\n\n");
				}

//				if(!text.equals(" ")|| !text.equals("  ") || !text.equals("\t") || !text.equals("　")) {
//					makeFormat(normalizedText, bw1, bw2, bw3);
//				}

//				if (!normalizedText.matches("^[\\s\\w]*$")) {
					makeFormat(normalizedText, crfTrainWriter, crfTestWriter);
//				}
			}
		}
		crfTrainWriter.close();
		crfTestWriter.close();
		xmlReader.close();
		System.out.println(lineCount);
		System.out.println("END"); // 終了の確認
	}

	private static void makeFormat(String str, BufferedWriter crfTrainWri, BufferedWriter crfTestWri) throws IOException {
//		sizeCount = 0;

		for(Token tokens:tokenizer.tokenize(str)) {

			bioe = "O";
			String[] features = tokens.getAllFeaturesArray();
			String word = tokens.getSurfaceForm();
			if (word.equals("")){
				continue;
			}
			if (word.contains(" ") || word.contains("\\s") || word.contains("\t") || word.contains("\\t")) {
				word = word.replaceAll(" ", "/space/");
				word = word.replaceAll("\t", "/tag/");
				word = word.replaceAll("\\t", "/tag/");
				word = word.replaceAll("\\s", "/space/");
			}
			wordCheck(word);
			// BOS or EOS in a tag
			if (inTag) {
				/* 評価値変わらず
				 * if (tokenizer.tokenize(str).size() == 1) {
					bioes = "S";
				} else*/ if (startS) {
					bioe = "B";
					startS = false;
				}
			else {
					bioe = "I";
				}
			}
			// dictionary has known or not
			if(tokens.isKnown()) {
				isKnown = "true";
			}else {
				isKnown = "false";
			}
			// bos
			if (BOS) {
				bos = "BOS";
			} else {
				bos = "I";
			}
			// init startS
			if (!inTag && !startS) {
				startS = true;
			}
			// train-data
	
			if ((word.matches("[\n]{1,}") || word.matches("[\r\n]{1,}") || word.matches("[\r]{1,}") || word.matches("\n")) && !first) {
				crfTrainWri.newLine();
				crfTestWri.newLine();
				BOS = true;
				lineCount++;
			} else if ((word.matches("[\n]{1,}") || word.matches("[\r\n]{1,}") || word.matches("[\r]{1,}")) && first) {
				continue;
			} else {
				if (!first) {
					crfTrainWri.newLine();
					crfTestWri.newLine();
				}

				// crf train data
				crfTrainWri.write(word);
				crfTrainWri.write(" ");
				crfTrainWri.write(features[0]);
				crfTrainWri.write(" ");
				crfTrainWri.write(features[1]);
				crfTrainWri.write(" ");
				crfTrainWri.write(wordMode);
				crfTrainWri.write(" ");
				crfTrainWri.write(isKnown);
				crfTrainWri.write(" ");
				crfTrainWri.write(bos);
				crfTrainWri.write(" ");
				crfTrainWri.write(bioe);
				if (inTag) {
					crfTrainWri.write("-");
					crfTrainWri.write(tagName);
				}
				crfTrainWri.flush();

				// crf test data
				crfTestWri.write(word);
				crfTestWri.write(" ");
				crfTestWri.write(features[0]);
				crfTestWri.write(" ");
				crfTestWri.write(features[1]);
				crfTestWri.write(" ");
				crfTestWri.write(wordMode);
				crfTestWri.write(" ");
				crfTestWri.write(isKnown);
				crfTestWri.write(" ");
				crfTestWri.write(bos);

				crfTestWri.flush();
				
				first = false;
				BOS = false;
			}
		}
	}
	
	static void checkBeforeWritefile(File file) {
		if (!file.exists()) {
			System.out.println("File don't exist");
			try {
				Files.createFile(Paths.get(file.getAbsolutePath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		else if (!file.isFile() && !file.canWrite()) {
//			System.out.println("File can't use");
//			return;
//		}
	}
	static void wordCheck(String word) {
		// 文字種
		if ( word.matches("^[a-zA-Z]+$") || word.matches("^[ａ-ｚＡ-Ｚ]+$")) {
			wordMode = "英字";
		} else if (word.matches ("^[0-9０-９]+$")) {
			wordMode = "数字";
		} else if (word.matches ("^[\\u3040-\\u309F]+$")) {
			wordMode = "ひらがな";
		} else if (word.matches ("^[\\u30A0-\\u30FF]+$")) {
			wordMode = "カタカナ";
		} else if (word.matches ("^[\\uFF65-\\uFF9F]+$")) {
			wordMode = "半角カタカナ";
		} else if (word.matches ("^[一-龠]*$")) {
			wordMode = "漢字";
		} else if (word.matches ("^[0-9０-９一-龠]*$")) {
			wordMode = "数字&漢字";
		} else if (word.matches ("^[\\u30A0-\\u30FF\\uFF65-\\uFF9F一-龠]*$")) {
			wordMode = "カタカナ&漢字";
		} else if (word.matches ("^[\\u3040-\\u309F一-龠]+$")) {
			wordMode = "漢字&ひらがな";
		} else {
			wordMode = "記号";
		}
	}
}
