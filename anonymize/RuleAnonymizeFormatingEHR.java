package anonymize;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.atilika.kuromoji.Token;
import com.atilika.kuromoji.Tokenizer;
import com.atilika.kuromoji.Tokenizer.Mode;

/**
 * ルールベースでタギングを行うためのデータフォーマットを行うプログラム
 * 出力結果："word + \s + 品詞 + \s + 詳細品詞 + \s + correct tag + O"
 * これ一つでルールベースタギングできる。
 * @author kkajiyama
 *
 */

public class RuleAnonymizeFormatingEHR {
	// kuromoji
	private static Tokenizer tokenizer;
	static{
		Mode mode = Mode.valueOf("NORMAL");
		//使う辞書の指定
		// String userDic = "/home/kkajiyama/workspace/kuromoji/user_dic/wikipedia.csv";
		String userDic = "/data/home/kkajiyama/workspace/MedNLPDoc/data/wikipedia_addbyomei.csv";
		// String userDic = "/data/home/kkajiyama/workspace/kuromoji/user_dic/wikipedia.csv";
		//		String userDic = "/data/home/kkajiyama/workspace/TaggingBIO/neologd-dic/mecab-user-dict-seed.20170727.csv";
		//		String userDic = "../kuromoji/user_dic/wikipedia.csv";
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
	// file 
//	static final File INPUT_FILENAME = new File("/data/home/kkajiyama/workspace/NHOTasks/MedNLPs_Data/medNLP1.xml");
//	static final File FORMATED_FILE = new File("/data/home/kkajiyama/workspace/NHOTasks/forRule/20180625_2.txt");

	static final File INPUT_FILENAME = new File("/data/home/kkajiyama/workspace/NHOTasks/MedNLPs_Data/medNLP1.xml");
	static final File FORMATED_FILE = new File("/data/home/kkajiyama/workspace/NHOTasks/forRule/20180625_2.txt");
	
	static boolean firstProcess = true; // first process of all process
	static boolean inTag = false;
	static String tagName;
	static boolean startInNER = true; // Beginning of Tag;
	static boolean BOS = true;
	//	static int sizeCount = 0;
	static int lineCount;

	// features
	static String tagType = "time";
	static int textRowNum = 0;
	/**
	 * iob Tagging
	 *
	 * B : beginning of tag
	 * I : inside of tag
	 * O : outside of tag
	 */
	static String iob = "O";

	// main
	public static void main(String arg[]) throws XMLStreamException, IOException {
		checkBeforeWritefile(FORMATED_FILE);
		BufferedWriter formatingWriter = new BufferedWriter(new FileWriter(FORMATED_FILE, false));

		// StAX parser setup
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(INPUT_FILENAME));
		XMLStreamReader xmlReader = factory.createXMLStreamReader(stream);

		String text = null;
		String normalizedText = null;

		for (; xmlReader.hasNext(); xmlReader.next()) {

			int eventType = xmlReader.getEventType();

			if (eventType == XMLStreamConstants.START_ELEMENT) {

				if (xmlReader.getLocalName().equals(TIME_TAG)) {
					inTag = true;
					startInNER = true;
					tagName = "time";
				}
				if (xmlReader.getLocalName().equals(AGE_TAG)) {
					inTag = true;
					startInNER = true;
					tagName = "age";
				}
				if (xmlReader.getLocalName().equals(HOSPITAL_TAG)) {
					inTag = true;
					startInNER = true;
					tagName = "hospital";
				}
				if (xmlReader.getLocalName().equals(SEX_TAG)) {
					inTag = true;
					startInNER = true;
					tagName = "sex";
				}

			} else if (eventType == XMLStreamConstants.END_ELEMENT) {

				if (xmlReader.getLocalName().equals(TIME_TAG) || xmlReader.getLocalName().equals(AGE_TAG)
						|| xmlReader.getLocalName().equals(HOSPITAL_TAG) || xmlReader.getLocalName().equals(SEX_TAG)) {
					inTag =false;
				}
				if (xmlReader.getLocalName().equals("root")) {
					formatingWriter.close();
					xmlReader.close();
					System.out.println(lineCount);
					System.out.println("END"); // 終了の確認
					return;
				}
			} else if (eventType == XMLStreamConstants.CHARACTERS) {
				text = xmlReader.getText();

				normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKC);
				makeFormat(normalizedText, formatingWriter);
			}
		}
		formatingWriter.close();
		xmlReader.close();
		System.out.println(lineCount);
		System.out.println("END"); // 終了の確認
	}

	private static void makeFormat(String str, BufferedWriter writer) throws IOException {

		String word = null;

		for(Token tokens:tokenizer.tokenize(str)) {

			iob = "O";
			word = tokens.getSurfaceForm();
			if (word.equals(" ")) {
				System.out.println("hot");
				word = word.replace(" ", "\\s");
				System.out.println("word = (" + word + ")");
			}
			// BOS or EOS in a tag
			if (inTag) {
				if (startInNER) {
					iob = "B";
					startInNER = false;
				}
				else {
					iob = "I";
				}
			}
			// init startS
			if (!inTag && !startInNER) {
				startInNER = true;
			}
			if (word.equals("\n") && !firstProcess) {
				writer.newLine();
				BOS = true;
				lineCount++;
			} 
			else if (word.equals("\n") && firstProcess) {
				continue;
			} else {
				if (!firstProcess) {
					writer.newLine();
				}
				writer.write(word);
				writer.write(" ");
				writer.write(tokens.getAllFeaturesArray()[0]);
				writer.write(" ");
				writer.write(tokens.getAllFeaturesArray()[1]);
				writer.write(" ");
				writer.write(iob);
				if(inTag){
					writer.write("-");
					writer.write(tagName);
				}
				writer.write(" ");
				writer.write("O");
				firstProcess = false; // after this process, will not change.
				BOS = false;
			} 
			//			}
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
	}
}
