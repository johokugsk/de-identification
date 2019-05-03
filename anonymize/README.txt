4-cross validation用のデータ作成手順
・まず、MedNLP1とDummyKarteで、train rate 100でパージングし、品詞やタグが付与されたフォーマットにする。
・・TaggingBIOforDummy.java
・・TaggingBIOforMedNLP1.java
・その後、MakeData4CrossValidation4.javaで4分割公差検定ように4分割されたデータを作成する