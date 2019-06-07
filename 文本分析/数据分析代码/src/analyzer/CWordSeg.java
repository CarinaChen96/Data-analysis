package analyzer;

import java.io.*;
import java.util.List;
import java.util.Map;
// import utils.SystemParas;
import com.sun.jna.Library;
import com.sun.jna.Native;
import dataio.DataReader;
import dataio.DataWriter;

import java.util.*;

/**
 * Created by Juan on 2017/7/9.
 */
public class CWordSeg {
    public interface CLibrary extends Library {
        CLibrary Instance = (CLibrary) Native.loadLibrary("./win64/NLPIR",CLibrary.class);
        public int NLPIR_Init(String sDataPath,int encoding,String sLicenceCode);
        // 对文本进行分词：读入文本，输出文本，是否标注词性（0为不标注，1为标注）
        public void NLPIR_FileProcess(String txt_input, String txt_output, int i);
        public String NLPIR_GetLastErrorMsg();
        // 添加用户词汇
        public int NLPIR_AddUserWord(String sWord);
        // 删除用户词汇
        public int NLPIR_DelUsrWord(String sWord);
        // 保存用户词汇到用户词典
        public int NLPIR_SaveTheUsrDic();
        // 导入用户自定义词典：自定义词典路径，bOverwrite=true表示替代当前的自定义词典，false表示添加到当前自定义词典后
        public int NLPIR_ImportUserDict(String sFilename, boolean bOverwrite);
//        public String NLPIR_GetLastErrorMsg();
        public void NLPIR_Exit();
    }

    public static String transString(String aidString,String ori_encoding,String new_encoding) {
        try {
            return new String(aidString.getBytes(ori_encoding),new_encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //从文件中读取，并判断是否是停止词，不是停止词则统计词频
    private Map<String,Integer>  getfWord(String txtinput) {
//        this.wordsIndexMap = wordsIndexMap;
        Map<String, Integer> wordsIndexMap = new HashMap<String,Integer>();
        WordSegmentor wordSeg = new WordSegmentor();//初始化分词器
        DataReader reader = new DataReader(txtinput);
        String text = null;
        while ((text = reader.getRequire()) != null) {
//            List<String> words = wordSeg.getWords(text);//进行分词处理
//            if (words == null)
//                break;
//            //将读取的词和存入wordsIndexMap中
//            int flag;
//            for (String word : words) {
//                String data = word.split("/")[0];
//                if (Stopwords.isStopword(data)) {
//                    continue;
//                }
//                if (wordsIndexMap.containsKey(word)) {
//                    int num = wordsIndexMap.get(word);
//                    num++;
//                    wordsIndexMap.put(word, num);
//                } else {
//                    wordsIndexMap.put(word, 1);
//                }
//            }
            String[] words = text.split("[ |　]+"); // continuous half or full
            for(String word:words){
                System.out.println(word);
            }
        }
       wordsIndexMap = PrintBySort(wordsIndexMap);
        printbyCount(wordsIndexMap);
        return  wordsIndexMap;

    }


    //打印出现次数
    public void printbyCount( Map<String,Integer> wordsIndexMap) {
        for (String key : wordsIndexMap.keySet()) {
            System.out.println(key+"的个数： "+wordsIndexMap.get(key));
        }
    }
    //对词的出现次数进行排序
    public Map<String,Integer> PrintBySort(Map<String,Integer> map) {
        List<Map.Entry<String,Integer>> fwlistList= new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(fwlistList,new Comparator<Map.Entry<String,Integer>>() {
            //升序排序
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        return  map;
    }
    public void getWordsMap(String txtInput, String txtOutput, Map<String, Integer> wordsIndexMap){
        try {
            int nCount = CLibrary.Instance.NLPIR_ImportUserDict("D:\\JAVA-TEXT\\JnaTest_NLPIR\\dictionary\\adduserdict.txt",true);
            System.out.println(String.format("已导入%d个用户词汇", nCount));
            //  对读入文本分词，并导出结果为另一个文本，要注意读入文本的编码格式（这里为UTF-8)
            CLibrary.Instance.NLPIR_FileProcess(txtInput,txtOutput,1);
            CLibrary.Instance.NLPIR_Exit();
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        //从分词之后的txt中将词存入map中
        DataReader reader = new DataReader(txtOutput);
        String text = null;
        while ((text = reader.getRequire()) != null) {

            String[] words = text.split("[ |　]+"); // continuous half or full
            for (String word : words) {
                if (Stopwords.isStopword(word)) {
                    continue;
                }
                if (wordsIndexMap.containsKey(word)) {
                    int num = wordsIndexMap.get(word);
                    num++;
                    wordsIndexMap.put(word, num);
                } else {
                    wordsIndexMap.put(word, 1);
                }
            }
        }
        //将map进行排序
        wordsIndexMap = PrintBySort(wordsIndexMap);
        printbyCount(wordsIndexMap);
        WriteFile(wordsIndexMap,"D:\\JAVA-TEXT\\JnaTest_NLPIR\\output\\wordsBysort.txt");
        printByNominal(wordsIndexMap);
    }
    //按词性输出
    public void printByNominal(Map<String, Integer> wordsIndexMap) {
        Map<String, Integer> nWordsIndexMap = new HashMap<String,Integer>();
        Map<String, Integer> adjWordsIndexMap = new HashMap<String,Integer>();
        for(String word :wordsIndexMap.keySet()) {
            String data=word.split("/")[0];
            String tempNominal =word.split("/")[1];

            String nominal = "";
            if(!tempNominal.isEmpty()){
                nominal = tempNominal.substring(0,1);
            }


            if(nominal.equals("n")){
                nWordsIndexMap.put(data, wordsIndexMap.get(word));
            }else if(nominal.equals("a")){
                adjWordsIndexMap.put(data, wordsIndexMap.get(word));
            }

        }
//        WriteFile(wordsIndexMap,"./DataFiles/allFrequency.txt");
        System.out.println("--------------形容词：------------------------");
        StringBuffer sb = new StringBuffer();
        PrintBySort(nWordsIndexMap);
        printbyCount(adjWordsIndexMap);
        for(String key :adjWordsIndexMap.keySet()) {
//			System.out.println("形容词："+key+"的个数： "+adjWordsIndexMap.get(key));
//            WriteFile(adjWordsIndexMap,"./DataFiles/adjFrequency.txt");
            WriteFile(wordsIndexMap,"D:\\JAVA-TEXT\\JnaTest_NLPIR\\output\\adj.txt");

        }


        System.out.println("----------------名词：--------------");
        for(String key :nWordsIndexMap.keySet()) {
//			System.out.println("名词："+key+"的个数： "+nWordsIndexMap.get(key));
//            WriteFile(nWordsIndexMap,"./DataFiles/nFrequency.txt");
            WriteFile(wordsIndexMap,"D:\\JAVA-TEXT\\JnaTest_NLPIR\\output\\nounce.txt");

        }

    }
    public static String transMapToString(Map map){
        Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            entry = (Map.Entry)iterator.next();
            sb.append(entry.getKey().toString()).append( " : " ).append(null==entry.getValue()?"":
                    entry.getValue().toString()).append (iterator.hasNext() ? "\r\n" : "");
        }
        return sb.toString();
    }


    public void  WriteFile(Map map,String filedec) {
        try
        {
            File f = new File(filedec);
            if (!f.exists())
            {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),"gbk");

            BufferedWriter writer =new BufferedWriter(write);
            String content = transMapToString(map);
            writer.write(content);
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    public static void main(String[] args) throws Exception {
        String argu = "./";
        // String system_charset = "UTF-8";
        int charset_type = 1;
        int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
        String nativeBytes;

        // 初始化失败提示
        if (0 == init_flag) {
            nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
            System.err.println("初始化失败！原因："+nativeBytes);
            return;
        }

        String txt_input = "D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\androidresult.txt";   // 读入文本
        String txt_output ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\output\\Android.txt";
        Map<String, Integer> wordsIndexMap = new HashMap<String,Integer>();
        CWordSeg cWordSeg = new CWordSeg();
        cWordSeg.getWordsMap(txt_input,txt_output,wordsIndexMap);


    }
}
