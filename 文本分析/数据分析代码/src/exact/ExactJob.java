package exact;

import dataio.DataReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Juan on 2017/7/11.
 */
public class ExactJob
{
    OutputStreamWriter write;
    BufferedWriter writer;
    static  int diff=0;
    //利用正则表达式提取岗位和任职要求，并写入文件
    public  void  getWork(String dec,String outputDec,int num) {
        DataReader reader ;
        String text = null;
        String jobs ="";
        String requires="";
        String differnce = "";
//        int i=0;
        for(int i=1;i<=num;i++){
            String filename =dec+i+").txt";
            System.out.println(i+filename);
            reader= new DataReader(filename);//读取文件的rea
            while((text = reader.getRequire()) != null) {
                String splits[] = text.split(">>>");
                String req="";
                if(splits.length<7){
                    diff++;
                    differnce+=splits[5];
                }else{
                    req= splits[6];
                }
//                differnce+=differnce;
                String job = splits[1];//得到岗位的目录
                System.out.println("****************"+req);
                jobs+=job+"\n";
                requires+=req+"\n";
            }
            differnce+="\n*********************************"+diff;
            reader.closeReader();
        }
        //工作类型目录
        String jobOutput = "D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\jobs\\"+outputDec+".txt";
        //岗位要求目录
        String reqOutput ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\require\\"+outputDec+".txt";
//        WriteFile(jobs,jobOutput);
        //误差输出目录
        String diffOutput ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\require\\diff.txt";
        //写入文件
        WriteFile(requires,reqOutput);
        WriteFile(differnce,diffOutput);

    }

    public void getRequirement(String dec){
        OutputStreamWriter write;
        BufferedWriter writer;
        DataReader reader ;
        String text = null;
        String txts ="";

        reader= new DataReader(dec);//读取文件的rea
        while((text = reader.getRequire()) != null) {
            String works = text.split(">>>")[6];
            System.out.println("****************"+works);
            txts+=text+"\n";
        }

        reader.closeReader();


        WriteFile(txts,"D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\require\\android.txt");

    }
    //写入文件
    public void  WriteFile(String words, String filedec) {
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
    public void handleInput(){

        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\安卓\\Android开发工程师 (","android",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\C语言\\C语言开发工程师 (","C语言",30);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\ERP\\ERP技术开发应用 (","ERP",29);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\IOS\\IOS开发工程师 (","IOS",30);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\java\\java软件开发工程师 (","java",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\PHP\\PHP开发工程师 (","PHP",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\web\\Web开发工程师 (","web",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\其他\\其他软件互联网开发 (","其他",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\嵌入式\\嵌入式软件开发 (","嵌入式",24);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\研发\\软件研发工程师 (","研发",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\移动互联\\移动互联网开发 (","移动互联",30);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\算法工程师\\算法工程师 (","算法",28);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\网站开发工程师\\网站开发工程师 (","网站开发",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\脚本开发\\脚本开发工程师 (","脚本",31);
        getWork("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\辅助设计\\计算机辅助设计师 (","计算机辅助设计师",31);

    }
    //替换得到的描述语言中的任职资格等词的格式
    public void  replaceKey(String dec){
        DataReader reader ;
        String text = null;

        String requires="";

        reader= new DataReader(dec);//读取文件的rea
        Pattern pattern = Pattern.compile("任职资格|技术要求|入职要求|任职要求|职位要求");
        while((text = reader.getRequire()) != null) {
            Matcher matcher = pattern.matcher(text);
//            System.out.println(text);
            if(matcher.find()){
                String  s = matcher.replaceAll(">>>技能要求");
                System.out.println("****************************"+s);
                requires+=s+"\n";
            }

        }

        reader.closeReader();
        WriteFile(requires,"D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\result.txt");
    }
    public  static  void main(String[]args){
            ExactJob exactJob = new ExactJob();
//            exactJob.handleInput();
//        exactJob.getRequirement("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\input\\安卓\\Android开发工程师 (1).txt");
    }

}
