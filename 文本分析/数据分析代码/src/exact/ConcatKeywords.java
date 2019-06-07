package exact;

import analyzer.AnalyseData;
import dataio.DataReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juan on 2017/7/14.
 */
public class ConcatKeywords
{
    Map<String,Integer> result1 = new HashMap<String, Integer>();
    Map<String,Integer> result2 = new HashMap<String, Integer>();
    Map<String,Integer> result = new HashMap<String, Integer>();

    public void FileToList(String dec,Map<String,Integer> map){
        String keywords,key;
        int value;
        DataReader keyreader = new DataReader(dec);
        while ((keywords = keyreader.getRequire()) != null) {
            int index =keywords.lastIndexOf("=");
            key =keywords.substring(0,index);
//            System.out.println(keywords.substring(index+1));
            value =Integer.parseInt( keywords.substring(index+1));
            map.put(key,value);
        }
    }
    public  Map<String,Integer> concatList(){
        String dic ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\keywords\\java1adj.txt";
        String dic2 ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\keywords\\javaadj.txt";
        String out ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\keywords\\javaadj.txt";

        FileToList(dic,result1);
        FileToList(dic2,result2);
        int count;
        String key;
        int value;
        String res = null;
        for(Map.Entry<String ,Integer>entry:result1.entrySet()){
            key = entry.getKey();
            System.out.println(key);
            value = entry.getValue();
            if(result2.containsKey(key)){
                count=value+result2.get(key);
            }else{
                count = value;
            }
            if(count>=5){
//                System.out.println(key+count);
                result.put(key,count);
                res+=key+"="+count+"\n";
            }
        }
        AnalyseData analyseData = new AnalyseData();
        analyseData.PrintBySort(result,out);
//        WriteFile(res,out);
        return  result;
    }

    //批量处理所有的文件
    public  void batchPro(){
        String inputs[]={"android","C语言","ERP","IOS","java","PHP","web","其他","嵌入式","研发","移动互联","算法","网站开发","脚本","计算机辅助设计"};

            for(int i =0;i<inputs.length;i++){
                String output =  "D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\json\\"+inputs[i]+".txt";
                 formatJson(inputs[i],output);//转为json格式的文本数据

        }

    }

    public  void formatJson(String input,String output){
        String outStr="{\"skills\":[";
        String temp="";
        String keywords,key;
        int value;
        String inputDec ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\keywords\\"+input+".txt";
//        String inputDec = ""+input+".txt";
        DataReader keyreader = new DataReader(inputDec);
        int i =0;
        while ((keywords = keyreader.getRequire()) != null) {

            int index =keywords.lastIndexOf("=");
            key =keywords.substring(0,index);
            System.out.println(input+(i++));
            value =Integer.parseInt( keywords.substring(index+1));
            temp="{\"name\":\""+key+"\",\"amount\":\""+value+"\",\"industry\":\""+input+"工程师\"},\n";
            outStr +=temp;
        }


        outStr+="]}";
        WriteFile(outStr,output);
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


}
