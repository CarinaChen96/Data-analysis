package exact;

import dataio.DataReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Juan on 2017/7/12.
 */
public class ExactRequire {
//    DataWriter writer =
    static  int count=0;
    static int lostNum = 0;//清洗的数据条数
    //替换得到的描述语言中的任职资格等词的格式
    public void  replaceKey(String inputdec,String outdec,String lostdic){
        DataReader reader ;
        String text = null;
        String requires="";//存储符合条件的提取之后的数据，形如“>>>技能要求”
        String lost = "";//存储没有要求有关的词的行，用于二次清洗
        reader= new DataReader(inputdec);//读取文件的rea
        Pattern pattern1 = Pattern.compile(">>>入职要求.+");
        Pattern pattern = Pattern.compile("(任职资格|招聘条件|任职基本条件|资格要求|职位条件|职位条件|应聘条件|应聘要求|招聘需求|报名要求|职务要求|职位介绍|就职要求|人员要求|能力要求|招聘要求|面试要求|技能要求|技术要求|入职要求|任职要求|职位要求|任职条件|岗位要求|岗位条件|任职需求|描述要求|能力经验要求|具体要求|入职条件)");

        while((text = reader.getRequire()) != null) {
            count++;
            Matcher matcher = pattern.matcher(text);
            if(matcher.find()){
                String  s = matcher.replaceAll(">>>入职要求");//将所有与技能要求有关的词换为技能要求
                Matcher matcher1 = pattern1.matcher(s);
                if(matcher1.find()){
//                    System.out.println("****************************"+s);
                    requires +=matcher1.group()+"\n";
                }
            }else{
                lost+=text+"\n";
                lostNum++;
            }

        }

        reader.closeReader();;
//        String reqRes="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\one\\"+outdec+"result.txt";
//        String lostRes="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\lost"+outdec+"lost.txt";
        WriteFile(requires,outdec);
        WriteFile(lost,lostdic);
    }
    /************将技能要求提取到行首*********************************/
    public  void handle(String dec,String  finalres){
        DataReader reader =new DataReader(dec);
        Pattern pattern = Pattern.compile("福利待遇|福利|工作时间|待遇|上班时间|岗位职责|工作职责|休息时间|在这里你可以|入职方向|发展方向");

        String  text;
        String other="";
        String finds ="";//存储找到的“技能要求....”字符串
        while((text = reader.getRequire()) != null) {
            Matcher matcher = pattern.matcher(text);
            if(matcher.find()){
                String  s = matcher.replaceAll("<<<");
                String find = s.split("<<<")[0];
//                System.out.println("****************************"+text);
                finds +=find+"\n";

            }else {
                finds+=text+"\n";
            }

        }
        reader.closeReader();
//        String reqRes="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\"+outdec+"result.txt";
        WriteFile(finds,finalres);
    }
    /************提取出所有的学历要求*****************/
    public void education(String inputdic,String outputdic){
        DataReader reader =new DataReader(inputdic);
        Pattern pattern = Pattern.compile("");

        String  text;
        String other="";
        String finds ="";//存储找到的“技能要求....”字符串
        while((text = reader.getRequire()) != null) {
            Matcher matcher = pattern.matcher(text);
            if(matcher.find()){
                String  s = matcher.replaceAll("<<<");
                String find = s.split("<<<")[0];
//                System.out.println("****************************"+text);
                finds +=find+"\n";

            }else {
                finds+=text+"\n";
            }

        }
        reader.closeReader();
//        String reqRes="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\"+outdec+"result.txt";
        WriteFile(finds,outputdic);
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
    public  void handleDic(){
        String inputs[]={"android","C语言","ERP","IOS","java","PHP","web","其他","嵌入式","研发","移动互联","算法","网站开发","脚本","计算机辅助设计师"};
        for(int i =0;i<inputs.length;i++){
            String inputdec = "D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\require\\"+inputs[i]+".txt";
            String resdic="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\one\\"+inputs[i]+".txt";
            String lostdic="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\lost\\"+inputs[i]+".txt";
            String finalres="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\"+inputs[i]+".txt";

            replaceKey(inputdec,resdic,lostdic);
            handle(resdic,finalres);
        }
        System.out.println("********************count:"+count);
        System.out.println("********************lost:"+lostNum);
//        replaceKey("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\require\\android.txt");
//        handle("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\result.txt");
    }
    public static  void  main(String[]args){
        ExactRequire exactRequire = new ExactRequire();
        exactRequire.handleDic();
//        exactRequire.handle("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\result.txt");
//        exactRequire.replaceKey("D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\require\\android.txt");
    }
}
