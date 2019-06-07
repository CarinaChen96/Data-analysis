package analyzer;

import exact.ConcatKeywords;
import dataio.DataReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Created by Juan on 2017/7/7.
 */
public class AnalyseData {
    private Map<String, Integer> wordsIndexMap = new HashMap<String, Integer>();//存储所有词
    Map<String, Integer> nWordsIndexMap = new HashMap<String, Integer>();//存储名词
    Map<String, Integer> adjWordsIndexMap = new HashMap<String, Integer>();//存储形容词
    OutputStreamWriter write;
    BufferedWriter writer;
    private WordSegmentor wordSeg = new WordSegmentor();//初始化分词器
    private DataReader reader;//读取文件的reader
    private static int wCount=0;//词的总个数
    String output=null;
    String userdic=null;
    public  AnalyseData(){

    }
    public  AnalyseData(String input,String output,String userdic){
        this.output =output;
        this.userdic = userdic;
        reader = new DataReader(input);
//        reader = new DataReader("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\androidresult.txt");
    }
    //从文件中读取，并判断是否是停止词，不是停止词则统计词频


    private void  getfWord() {
//        this.wordsIndexMap = wordsIndexMap;
        String text = null;
        String texts="";
        System.out.println("词频统计输出：");
        while ((text = reader.getRequire()) != null) {
                texts+=text;
        }
        List<String> words = wordSeg.getWords(userdic,texts);//进行分词处理
        if (words == null) return;
        //将读取的词和存入wordsIndexMap中
        int flag;
        for (String word : words) {
            wCount++;
            String data = word.split("/")[0];

            if (Stopwords.isStopword(data)) {
                continue;
            }
            if (wordsIndexMap.containsKey(word)) {
                int num = wordsIndexMap.get(word);
                num++;
                wordsIndexMap.put(word, num);
            } else {
                wordsIndexMap.put(word,  1);
            }
        }
        closeIOStream();//关闭流
//        PrintBySort(wordsIndexMap);//将map排序

    }
    //将map类型写入文件
    public void  WriteFile(Map map,String filedec) {
        try
        {
            File f = new File(filedec);
            if (!f.exists())
            {
                f.createNewFile();
            }
            write = new OutputStreamWriter(new FileOutputStream(f),"utf-8");
            writer =new BufferedWriter(write);
            String content = transMapToString(map);
            writer.write(content);
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    //打印出现次数
    public void printbyCount() {
        for (String key : wordsIndexMap.keySet()) {
            System.out.println(key+"的个数： "+wordsIndexMap.get(key));
        }
    }

    //对词的出现次数进行排序
    public void PrintBySort(Map map,String dec) {
        List<Map.Entry<String,Integer>> fwlistList= new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(fwlistList,new Comparator<Map.Entry<String,Integer>>() {
            //升序排序
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });


        WriteListToFile(fwlistList,dec);//写入到文件中。
//        transListToString(fwlistList);

    }
    //写入文件
    public void  WriteFile(String words, String filedec) {
        OutputStreamWriter write;
        BufferedWriter writer;
        try
        {
            File f = new File(filedec);
            if (!f.exists())
            {
                f.createNewFile();
            }
            write = new OutputStreamWriter(new FileOutputStream(f),"utf-8");
            writer =new BufferedWriter(write);
//            String content = transMapToString(map);

            writer.write(words+"/n");
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    //将list的文件写入文件
    public void  WriteListToFile(List<Map.Entry<String,Integer>> list, String filedec) {
        try
        {
            File f = new File(filedec);
            if (!f.exists())
            {
                f.createNewFile();
            }
            write = new OutputStreamWriter(new FileOutputStream(f),"utf-8");
            writer =new BufferedWriter(write);

            String content = transListToString(list);//将list转为string类型
//            System.out.println(content);
            writer.write(content);
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //将map转为string
    public  static String transMapToString(Map map){
        Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            entry = (Map.Entry)iterator.next();
            sb.append(entry.getKey().toString()).append( " : " ).append(null==entry.getValue()?"":
                    entry.getValue().toString()).append (iterator.hasNext() ? "\r\n" : "");

        }
        System.out.print(sb.toString());
        return sb.toString();
    }

    //将list转为string类型，并返回
    public static String transListToString(List<Map.Entry<String,Integer>> list){


        StringBuffer sb = new StringBuffer();
        sb.append("{\"skills\":[");

        for(int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {

                sb.append("{\"name\":\""+list.get(i).getKey()+"\":\"amount\":\""+list.get(i).getValue()+"\"}\n");
            } else {
                sb.append("{\"name\":\""+list.get(i).getKey()+"\":\"amount\":\""+list.get(i).getValue()+"\"}\r\n");
//                sb.append("\r\n");
            }
        }
        sb.append("]}");
        System.out.println(sb);
        return sb.toString();
    }
    public void closeIOStream() {

        reader.closeReader();
    }

    //按词性分开
    public void MapByNominal(Map<String, Integer> wordsIndexMap,Map<String, Integer> nWordsIndexMap,Map<String, Integer> adjWordsIndexMap) {
        for (String word : wordsIndexMap.keySet()) {
            String data = word.split("/")[0];
            String tempNominal = word.split("/")[1];
            String nominal = "";
            if (!tempNominal.isEmpty()) {
                nominal = tempNominal.substring(0, 1);
            }

            if (nominal.equals("n")) {
                nWordsIndexMap.put(data, wordsIndexMap.get(word));
            } else if (nominal.equals("a")) {
                adjWordsIndexMap.put(data, wordsIndexMap.get(word));
            }
        }
        String ndic ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\keywords\\"+output+"n.txt";
        String adjdic ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\keywords\\"+output+"adj.txt";
        PrintBySort(nWordsIndexMap,ndic);
        PrintBySort(adjWordsIndexMap,adjdic);
    }

    //批量处理所有的文件
    public static void batchPro(){
        String inputs[]={"android","C语言","ERP","IOS","java","PHP","web","其他","嵌入式","研发","移动互联","算法","网站开发","脚本","计算机辅助设计师"};        for(int i =0;i<inputs.length;i++){
         for(int j =0;i<inputs.length;i++){
             String userdic = "D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\newWords\\"+inputs[j]+".txt";
             String input ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\"+inputs[j]+"result.txt";
             AnalyseData wfCount = new AnalyseData(input,inputs[j],userdic);
             wfCount.getfWord();//分词，统计词频存储map
             wfCount.MapByNominal(wfCount.wordsIndexMap,wfCount.nWordsIndexMap,wfCount.adjWordsIndexMap);
             //按词性排序，写入文件
         }

        }
    }

    public static  void main(String[] args){
    //        String inputs[]={"android","C语言","ERP","IOS","java","PHP","web","其他","嵌入式","研发","移动互联","算法","网站开发","脚本","计算机辅助设计师"};
//                batchPro();
    //        AnalyseData wfCount  = new AnalyseData();
    //        wfCount.getfWord();
    //        wfCount.MapByNominal(wfCount.wordsIndexMap,wfCount.nWordsIndexMap,wfCount.adjWordsIndexMap);

//        String userdic = "D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\newWords\\C语言.txt";
//        String input ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\C语言result.txt";
//        AnalyseData wfCount = new AnalyseData(input,"C",userdic);
//        wfCount.getfWord();//分词，统计词频存储map
//        wfCount.MapByNominal(wfCount.wordsIndexMap,wfCount.nWordsIndexMap,wfCount.adjWordsIndexMap);


        ConcatKeywords concatKeywords = new ConcatKeywords();
        concatKeywords.batchPro();
////        concatKeywords.concatList();//合并两个文件
        /******转换为json格式，用不着了***********************/
//       String output =  "D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\keywords\\androidjson.txt";
//        concatKeywords.formatJson("android",output);//转为json格式的文本数据
    }
}
