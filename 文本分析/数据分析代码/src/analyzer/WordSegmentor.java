package analyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class WordSegmentor {
	private String text;
	private  String userdic;
	public WordSegmentor(String text) {
		this.text = text;
	}

	public WordSegmentor() {
		super();
	}

	private interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"./win64/NLPIR", CLibrary.class);

		public int NLPIR_Init(byte[] sDataPath, int encoding,
                              byte[] sLicenceCode);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		// 添加用户词汇
		public int NLPIR_AddUserWord(String sWord);
		// 删除用户词汇
		public int NLPIR_DelUsrWord(String sWord);
		// 保存用户词汇到用户词典
		public int NLPIR_SaveTheUsrDic();
		// 导入用户自定义词典：自定义词典路径，bOverwrite=true表示替代当前的自定义词典，false表示添加到当前自定义词典后
		public int NLPIR_ImportUserDict(String sFilename, boolean bOverwrite);
		public String NLPIR_GetNewWords(String  sLine, int nMaxKeyLimit,boolean bWeightOut);
		public String NLPIR_GetFileNewWords(String  filename, int nMaxKeyLimit,boolean bWeightOut);
	}
//设置处理文本
	public void setText(String text) {
		this.text = text;
	}
//将string类型的文本转为list类型

	public List<String> getWords(String text) {
		this.text = text;
		return getWords();
	}
	public List<String> getWords(String userDic,String text) {
		this.text = text;
		this.userdic = userDic;
		return getWords();
	}

//	public static void StudyNewWords(String input,String output){
//		String newwords = CLibrary.Instance.NLPIR_GetFileNewWords("D:\\JAVA-TEXT\\JnaTest_NLPIR\\dictionary\\mydata.txt",20,true);
//		System.out.print("发现的新词为："+newwords);
//	}
	public List<String> getWords(String inputdic,int i){
		String libDataPath = "";
		String systemCharset = "UTF-8";
		// charset_type: input encoding.
		// 0 -- GBK; 1 -- UTF8; 2 -- BIG5; 3 -- GBK with traditional Chinese
		int charsetType = 1;
		int initFlag = 0;

		try {
			initFlag = CLibrary.Instance.NLPIR_Init(
					libDataPath.getBytes(systemCharset), charsetType,
					"0".getBytes(systemCharset));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			initFlag = 0;
		} finally {
			if (initFlag == 0) {
				System.err.println("Initialization Failed!");
				return null;
			}
		}

		if (text == null)
			return null;
		int nCount = CLibrary.Instance.NLPIR_ImportUserDict(userdic,true);
		System.out.println(String.format("已导入%d个用户词汇", nCount));
		String wordsString = CLibrary.Instance.NLPIR_ParagraphProcess(text, 1);//对字符串进行分词
		//sParagraph: The source paragraph bPOStagged:
		// Judge whether need POS tagging, 0 for no tag;1 for tagging; default:1
		List<String> result = processResult(wordsString);
		return result;
	}
	//初始化CLibrary实例，对得到的文本进行分词
	public List<String> getWords() {
		String libDataPath = "";
		String systemCharset = "UTF-8";
		// charset_type: input encoding.
		// 0 -- GBK; 1 -- UTF8; 2 -- BIG5; 3 -- GBK with traditional Chinese
		int charsetType = 1;
		int initFlag = 0;

		try {
			initFlag = CLibrary.Instance.NLPIR_Init(
					libDataPath.getBytes(systemCharset), charsetType,
					"0".getBytes(systemCharset));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			initFlag = 0;
		} finally {
			if (initFlag == 0) {
				System.err.println("Initialization Failed!");
				return null;
			}
		}

		if (text == null)
			return null;
		int nCount = CLibrary.Instance.NLPIR_ImportUserDict(userdic,true);
		System.out.println(String.format("已导入%d个用户词汇", nCount));
		String wordsString = CLibrary.Instance.NLPIR_ParagraphProcess(text, 1);//对字符串进行分词
		//sParagraph: The source paragraph bPOStagged:
		// Judge whether need POS tagging, 0 for no tag;1 for tagging; default:1
		List<String> result = processResult(wordsString);
		return result;
	}
	/**************************将分好的词去无关词********************/
	public List<String> processResult(String sString) {
		String[] words = sString.split("[ |　]+"); // continuous half or full
													// space
		List<String> result = new ArrayList<String>();//存储得到的结果

		for (String word : words) {
			if (word.length() == 0)
				continue;
			if (word.charAt(0) == '/' || word.charAt(0) == '@') // "//" or
																// username
				continue;
			String[] wordTag = word.split("/");//得到的词去掉分隔符
			if (wordTag.length == 1) // words or symbols that cannot be
										// recognized, e.g. full-width marks
				continue;
			// filter the urls and punctuations...
			if (wordTag[1].startsWith("x") || wordTag[1].startsWith("w")
					|| wordTag[1].startsWith("email")
					|| wordTag[1].startsWith("url"))
				continue;
			// filter dates...
			else if (wordTag[1].startsWith("t") && word.matches("^[0-9]"))
				continue;
			// some specific kinds of words... /f,/m may be reconsidered
			else if (wordTag[1].startsWith("f") || wordTag[1].startsWith("m")
					|| wordTag[1].startsWith("u") || wordTag[1].startsWith("c")
					|| wordTag[1].startsWith("q") || wordTag[1].startsWith("p")
					|| wordTag[1].startsWith("y") || wordTag[1].startsWith("h")
					|| wordTag[1].startsWith("k"))
				continue;
			result.add(word); // One word can have multiple tags, so we record a
								// word with tag.
		}

		return result;
	}



}
