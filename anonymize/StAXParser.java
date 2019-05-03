package anonymize;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * XMLファイルを処理するクラス
 * @author kajiyama
 *
 */
public class StAXParser {

	private static boolean inTag = false;
	private static String tagName = "";
	private static int lineNum = 0;

	

	/**
	 * StAXで解析し、charactorのみをtextファイルに書き出すメソッド
	 * @param inputFileName : File
	 * @param outputFile : File
	 * @throws XMLStreamException
	 * @throws IOException
	 * @author kajiyama
	 */
	public static void parse(File inputFileName, File outputFile) throws XMLStreamException, IOException {
		// 引数で最初に学習データにつかうテキストデータを指定

		String tagName = "text"; // 取得するタグのコード

		// こちらではxml中のcタグの位置を格納する
		// StAXを使用するための前処理
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(inputFileName));
		XMLStreamReader reader = factory.createXMLStreamReader(stream, "UTF-8");

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true));
		String word;

		for (; reader.hasNext(); reader.next()) {
			int eventType = reader.getEventType();

			// cTagの開始位置を取る
			if (eventType == XMLStreamConstants.START_ELEMENT) {
				// cタグの開始位置を取得
				// System.out.println(reader.getLocalName());
				if (reader.getLocalName().equals(tagName)) {
					inTag = true;
				} else if (inTag) {
					tagName = reader.getLocalName();
					System.out.println("tagName = " + tagName);
				}
			}

			// cTagの終了位置を取得
			if (eventType == XMLStreamConstants.END_ELEMENT) {
				if (reader.getLocalName().equals(tagName)) {
					inTag = false;
				}
			}

			if (eventType == XMLStreamConstants.CHARACTERS) {
				word = reader.getText();
				if (inTag) {
					bw.write(word);
				}
			}
		}
		bw.close();

		reader.close();

		System.out.println(outputFile + "に書き込み完了"); // 終了の確認
	}

	public static void parse(File inputFileName, File outputFileName, boolean print) throws XMLStreamException, IOException {
		// 引数で最初に学習データにつかうテキストデータを指定

		String tagName = "text"; // 取得するタグのコード

		// こちらではxml中のcタグの位置を格納する
		// StAXを使用するための前処理
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(inputFileName));
		XMLStreamReader reader = factory.createXMLStreamReader(stream, "UTF-8");

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName, true));
		String word;

		for (; reader.hasNext(); reader.next()) {
			int eventType = reader.getEventType();

			// cTagの開始位置を取る
			if (eventType == XMLStreamConstants.START_ELEMENT) {
				// cタグの開始位置を取得
				// System.out.println(reader.getLocalName());
				if (reader.getLocalName().equals(tagName)) {
					inTag = true;
				}
			}

			// cTagの終了位置を取得
			if (eventType == XMLStreamConstants.END_ELEMENT) {
				if (reader.getLocalName().equals(tagName)) {
					inTag = false;
				}
			}

			if (eventType == XMLStreamConstants.CHARACTERS) {
				word = reader.getText();
				if (inTag) {
					bw.write(word);
				}
			}
		}
		bw.close();

		reader.close();

		if (print) {
			System.out.println(outputFileName + "に書き込み完了"); // 終了の確認
		}
	}
	
	/**
	 * StAXで解析し、タイトルも出力しつつcharactorのみをtextファイルに書き出すメソッド
	 * @param inputFileName : File
	 * @param outputFileName : File
	 * @throws XMLStreamException
	 * @throws IOException
	 * @author kajiyama
	 */
	public static void parseWithTitle(File inputFileName, File outputFileName) throws XMLStreamException, IOException {
		// 引数で最初に学習データにつかうテキストデータを指定

		boolean inTextTag = false;
		boolean inTitleTag = false;
		String textTag = "text"; // 取得するタグのコード
		String titleTag = "title";

		// こちらではxml中のcタグの位置を格納する
		// StAXを使用するための前処理
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(inputFileName));
		XMLStreamReader reader = factory.createXMLStreamReader(stream, "UTF-8");

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName, true));
		String textWords;
		boolean first = true;

		for (; reader.hasNext(); reader.next()) {
			int eventType = reader.getEventType();

			// cTagの開始位置を取る
			if (eventType == XMLStreamConstants.START_ELEMENT) {
				// cタグの開始位置を取得
				// System.out.println(reader.getLocalName());
				if (reader.getLocalName().equals(textTag)) {
					inTextTag = true;
					if (!first) {
						bw.newLine();
					} else {
						first = false;
					}
				}
				if (reader.getLocalName().equals(titleTag)) {
					inTitleTag = true;
				}
			}

			// cTagの終了位置を取得
			if (eventType == XMLStreamConstants.END_ELEMENT) {
				if (reader.getLocalName().equals(textTag)) {
					inTextTag = false;
				}
				if (reader.getLocalName().equals(titleTag)) {
					inTitleTag = false;
				}
			}

			if (eventType == XMLStreamConstants.CHARACTERS) {
				textWords = reader.getText();
				if (inTextTag) {
					textWords = Normalizer.normalize(textWords, Normalizer.Form.NFKC);
					textWords = textWords.replaceAll("^\\s+$", "");
					textWords = textWords.replaceAll("\"\\d{10}$", "");
					textWords = textWords.replaceAll("^\r\n+$", "");
					textWords = textWords.replaceAll("^\t{4,}$", "");
					bw.write(textWords);
				}
				if (inTitleTag) {
					if (textWords.equals("問題") || textWords.equals("主訴") || textWords.equals("所見") || textWords.equals("現疾患(診断内容)") || textWords.equals("計画") ) {
						bw.newLine();
						bw.write("\""+ textWords + "\"");
						bw.newLine();
					}
				}
			}
		}
		bw.newLine();
		bw.close();
		reader.close();

	}


	public static void parseWithTime(File inputFileName, File outputFileName) throws XMLStreamException, IOException {
		// 引数で最初に学習データにつかうテキストデータを指定

		boolean inTextTag = false;
		String textTag = "text"; // 取得するタグのコード
		String timeTag = "effectiveTime";

		// こちらではxml中のcタグの位置を格納する
		// StAXを使用するための前処理
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(inputFileName));
		XMLStreamReader reader = factory.createXMLStreamReader(stream, "UTF-8");

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName, true));
		String textWords;
		boolean first = true;

		for (; reader.hasNext(); reader.next()) {
			int eventType = reader.getEventType();

			// cTagの開始位置を取る
			if (eventType == XMLStreamConstants.START_ELEMENT) {
				// cタグの開始位置を取得
				if (reader.getLocalName().equals(textTag)) {
					inTextTag = true;
					if (!first) {
						bw.newLine();
					} else {
						first = false;
					}
				}
				if (reader.getLocalName().equals(timeTag)) {
					if (reader.getAttributeValue(0) != null && !reader.getAttributeValue(0).isEmpty()) {
						bw.write("timeValue==" + reader.getAttributeValue(0));
						bw.newLine();
					}
				}
			}

			// cTagの終了位置を取得
			if (eventType == XMLStreamConstants.END_ELEMENT) {
				if (reader.getLocalName().equals(textTag)) {
					inTextTag = false;
				}
			}

			if (eventType == XMLStreamConstants.CHARACTERS) {
				textWords = reader.getText();
				if (inTextTag) {
					textWords = Normalizer.normalize(textWords, Normalizer.Form.NFKC);
//					textWords = textWords.replaceAll("^\\s+$", "");
					textWords = textWords.replaceAll("\"\\d{10}$", ""); // patient id
					textWords = textWords.replaceAll("\"\\d{8}$", ""); // patient id
					textWords = textWords.replaceAll("\\R", "");
					textWords = textWords.replaceAll("\t{5,}", "");
					bw.write(textWords);
				}
			}
		}
		bw.newLine();
		bw.close();
		reader.close();
	}


}
