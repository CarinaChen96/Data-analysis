package analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;

public class Stopwords {
	protected HashSet<String> m_Words = null;//存储停止词
	protected static Stopwords m_Stopwords;

	static {
		if (m_Stopwords == null) {
			m_Stopwords = new Stopwords();
		}
	}

	public Stopwords() {
		m_Words = new HashSet<String>();

		try {
			read("D:\\JAVA-TEXT\\JnaTest_NLPIR\\dictionary\\stopwords.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clear() {
		m_Words.clear();
	}

	//网停止词的哈希表中加词
	public void add(String word) {
		if (word.trim().length() > 0)
			m_Words.add(word.trim());
	}

	public boolean remove(String word) {
		return m_Words.remove(word);
	}

	public void read(String filename) throws Exception {
		read(new File(filename));
	}

	public void read(File file) throws Exception {
		read(new BufferedReader(new FileReader(file)));
	}
//读取文档，加入停止词
	public void read(BufferedReader reader) throws Exception {
		clear();

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("#"))
				continue;
			add(line);
		}
		reader.close();
	}

	//判断看是否在停止词的哈希表中
	public boolean is(String word) {
		return m_Words.contains(word.toLowerCase());
	}

	public static boolean isStopword(String str) {
		return m_Stopwords.is(str.toLowerCase());
	}

}
