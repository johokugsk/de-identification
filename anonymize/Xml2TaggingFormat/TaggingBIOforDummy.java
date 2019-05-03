package anonymize.Xml2TaggingFormat;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Arrays;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.atilika.kuromoji.Token;
import com.atilika.kuromoji.Tokenizer;
import com.atilika.kuromoji.Tokenizer.Mode;

public class TaggingBIOforDummy {
	// kuromoji
	private static Tokenizer tokenizer;
	static {
		Mode mode = Mode.valueOf("NORMAL");
		// 使う辞書の指定
		 String userDic = "~/workspace/MedNLPDoc/data/wikipedia_addbyomei.csv";
//		String userDic = "~/workspace/kuromoji/user_dic/wikipedia.csv";
		try {
			tokenizer = Tokenizer.builder().mode(mode).userDictionary(userDic).build();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	// other
	static final String TIME_TAG = "t";
	static final String AGE_TAG = "a";
	static final String HOSPITAL_TAG = "h";
	static final String SEX_TAG = "x";
	static final String PERSON_TAG = "p";
	// for dummyCarte
	static final File INPUT_DIR = new File("~/workspace/dummyCarte/followMedNLP1/");
	static final File CRF_TRAIN_FILE = new File("~/Documents/formatedData/dummy/trainData.txt");
	static final File CRF_TEST_FILE = new File("~/Documents/formatedData/dummy/noTagData.txt");
	static boolean first = true;
	static boolean inTag = false;
	static String tagName;
	static boolean startS = true; // Beginning of Tag;
	static boolean endS = false; // End of Tag;
	static boolean BOS = true;
	static int sizeCount = 0;
	static int allLineNum;
	static int count;
	// features
	static String wordMode = "ひらがな";
	static String isKnown = "true";
	static String bos = "BOS";
	static String tagType = "time";
	static String bioe = "O";

	// main
	public static void main(String arg[]) throws XMLStreamException, IOException {
		checkBeforeWritefile(CRF_TRAIN_FILE);
		BufferedWriter crfTrainWriter = new BufferedWriter(new FileWriter(CRF_TRAIN_FILE, false));
		checkBeforeWritefile(CRF_TEST_FILE);
		BufferedWriter crfTestWriter = new BufferedWriter(new FileWriter(CRF_TEST_FILE, false));
		
		File[] files = INPUT_DIR.listFiles();
		Arrays.sort(files, new java.util.Comparator<File>() {
			public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}
		});

		for (File file : files) {
			System.out.println("file name : " + file.getName());
			if (file.isHidden()) {
				// 隠しファイルなら次のforへ
				continue; // 次のforへ
			}
			if (file.isDirectory()) {
				return;
			}
			if (file.isFile() && file.canRead()) {
				labbeling(file, crfTrainWriter, crfTestWriter);
			}
		}
		
		crfTrainWriter.close();
		crfTestWriter.close();
		

		System.out.println(count);
		System.out.println("END"); // 終了の確認
	}

	private static void labbeling (File file, BufferedWriter crfTrainWri, BufferedWriter crfTestWri) throws XMLStreamException, IOException {

		// StAXを使用するための前処理
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
		XMLStreamReader reader = factory.createXMLStreamReader(stream);
		String text = null;
		String normalizedText = null;

		for (; reader.hasNext(); reader.next()) {

			int eventType = reader.getEventType();
			if (eventType == XMLStreamConstants.START_ELEMENT) {
				if (reader.getLocalName().equals(TIME_TAG)) {
					inTag = true;
					startS = true;
					tagName = "time";
				}
				if (reader.getLocalName().equals(AGE_TAG)) {
					inTag = true;
					startS = true;
					tagName = "age";
				}
				if (reader.getLocalName().equals(HOSPITAL_TAG)) {
					inTag = true;
					startS = true;
					tagName = "hospital";
				}
				if (reader.getLocalName().equals(SEX_TAG)) {
					inTag = true;
					startS = true;
					tagName = "sex";
				}
				if (reader.getLocalName().equals(PERSON_TAG)) {
					inTag = true;
					startS = true;
					tagName = "person";
				}
			}
			if (eventType == XMLStreamConstants.END_ELEMENT) {
				if (reader.getLocalName().equals(TIME_TAG) || reader.getLocalName().equals(AGE_TAG)
						|| reader.getLocalName().equals(HOSPITAL_TAG) || reader.getLocalName().equals(SEX_TAG)
						|| reader.getLocalName().equals(PERSON_TAG)) {
					inTag = false;
				}
				if (reader.getLocalName().equals("root")) {
//					bw1.close();
//					bw2.close();
					reader.close();
				}
			}
			if (eventType == XMLStreamConstants.CHARACTERS) {
				/*
				 * @date 2017/9/7
				 * 
				 * @author kkajiyama "==="関連のコードはDummyカルテ固有の問題なので 別個のオリジナルを作った。
				 */
				text = reader.getText().replaceAll("[\n]+", "\n");
				normalizedText = Normalizer.normalize(text, Normalizer.Form.NFKC);
				if (normalizedText.matches("^[\n]*===[\n]*$")) {
					// 改行と"==="のみのものを取り除く。
					continue;
				}
				if (normalizedText.contains("\n===")) {
					normalizedText = normalizedText.replaceAll(".\n", "");
//					System.out.println("normalizedText = \"" + normalizedText + "\"");
				}
				if (!normalizedText.matches("^[\\s\\w]*$")) {
					makeFormat(normalizedText, crfTrainWri, crfTestWri);
				}
			}
		}

	}

	private static void makeFormat(String str, BufferedWriter crfTrain, BufferedWriter crfTest) throws IOException {
		sizeCount = 0;
		for (Token tokens : tokenizer.tokenize(str)) {
			
			bioe = "O";
			String[] features = tokens.getAllFeaturesArray();
			String word = tokens.getSurfaceForm();
			if (word.matches(" .*")) {
				word = "\\s";
			} 
			
			// 1行内にほしい表現を伴う"==="があるので、それを取り除く。
			if (word.matches("===") || word.matches("---")) {
				continue;
			}
			if (word.matches(" .*")) {
				word = "\\s";
			} else if (word.matches("\n .*")) {
				word = "\\s";
			}  else if (word.matches("\n .*\n")) {
				word = "\\s";
			}   else if (word.matches(" \n")) {
				word = "\\s";
			}    else if (word.matches(" .*\n *")) {
				word = "\\s";
			} 
			wordCheck(word);
			// BOS or EOS in a tag
			if (inTag) {
				if (startS) {
					bioe = "B";
					startS = false;
				} 
				else {
					bioe = "I";
				}
			}
			if (word.matches("\n\\s")) {
				/**
				 * @author kkajiyama 空白混じりの改行がひとつのtokenとして認識されてしまい、
				 *         うまくBOSを付与できないためこの処理を入れている。 また、改行としてカウントされないため、
				 *         count++;も入れている。
				 */
				BOS = true;
				count++;
			}
			// BOS or EOS in a line
			if (BOS) {
				bos = "BOS";
				BOS = false;
			} else {
				bos = "I";
			}
			// dictionary has known or not
			if (tokens.isKnown()) {
				isKnown = "true";
			} else if (word.matches("\n") && first) {
				continue;
			} else {
				isKnown = "false";
			}
			// init startS
			if (!inTag && !startS) {
				startS = true;
			}
				// train-data
				if (word.matches("\n") && !first) {
					crfTrain.newLine();
					crfTest.newLine();
					BOS = true;
					count++;
				} else {
					if (!first) {
						// ファイルの一行目に改行を入れないため
						crfTrain.newLine();
						crfTest.newLine();
						
					}
					
					// crf train data
					crfTrain.write(word);
					crfTrain.write(" ");
					crfTrain.write(features[0]);
					crfTrain.write(" ");
					crfTrain.write(features[1]);
					crfTrain.write(" ");
					crfTrain.write(wordMode);
					crfTrain.write(" ");
					crfTrain.write(isKnown);
					crfTrain.write(" ");
					crfTrain.write(bos);
					crfTrain.write(" ");
					crfTrain.write(bioe);
					if (inTag) {
						crfTrain.write("-");
						crfTrain.write(tagName);
					}
					crfTrain.flush();
					
					// crf test data
					crfTest.write(word);
					crfTest.write(" ");
					crfTest.write(features[0]);
					crfTest.write(" ");
					crfTest.write(features[1]);
					crfTest.write(" ");
					crfTest.write(wordMode);
					crfTest.write(" ");
					crfTest.write(isKnown);
					crfTest.write(" ");
					crfTest.write(bos);
					crfTest.flush();
					
					
					first = false;
				}
			sizeCount++;
		}
	}

	static void checkBeforeWritefile(File file) {
		if (!file.exists()) {
			System.out.println("File don't exist");
			return;
		} else if (!file.isFile() && !file.canWrite()) {
			System.out.println("File can't use");
			return;
		}
	}

	static void wordCheck(String word) {
		// 文字種
		if (word.matches("^[a-zA-Z]+$") || word.matches("^[ａ-ｚＡ-Ｚ]+$")) {
			wordMode = "英字";
		} else if (word.matches("^[0-9０-９]+$")) {
			wordMode = "数字";
		} else if (word.matches("^[\\u3040-\\u309F]+$")) {
			wordMode = "ひらがな";
		} else if (word.matches("^[\\u30A0-\\u30FF]+$")) {
			wordMode = "カタカナ";
		} else if (word.matches("^[\\uFF65-\\uFF9F]+$")) {
			wordMode = "半角カタカナ";
		} else if (word.matches("^[一-龠]*$")) {
			wordMode = "漢字";
		} else if (word.matches("^[0-9０-９一-龠]*$")) {
			wordMode = "数字&漢字";
		} else if (word.matches("^[\\u30A0-\\u30FF\\uFF65-\\uFF9F一-龠]*$")) {
			wordMode = "カタカナ&漢字";
		} else if (word.matches("^[\\u3040-\\u309F一-龠]+$")) {
			wordMode = "漢字&ひらがな";
		} else {
			wordMode = "記号";
		}
	}
}