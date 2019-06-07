package exact;
import java.io.*;
// import utils.SystemParas;
import com.sun.jna.Library;
import com.sun.jna.Native;
/**
 * Created by Juan on 2017/7/9.
 */
public class ExactKeyWords{
    public interface CLibrary extends Library {
        CLibrary Instance = (CLibrary) Native.loadLibrary("./win64/NLPIR",CLibrary.class);
        public int NLPIR_Init(String sDataPath,int encoding,String sLicenceCode);
        public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
        // 添加用户词汇
        public int NLPIR_AddUserWord(String sWord);
        // 删除用户词汇
        public int NLPIR_DelUsrWord(String sWord);
        // 保存用户词汇到用户词典
        public int NLPIR_SaveTheUsrDic();
        // 导入用户自定义词典：自定义词典路径，bOverwrite=true表示替代当前的自定义词典，false表示添加到当前自定义词典后
        public int NLPIR_ImportUserDict(String sFilename, boolean bOverwrite);
        public String NLPIR_GetLastErrorMsg();
        public String NLPIR_GetNewWords(String  sLine, int nMaxKeyLimit,boolean bWeightOut);
        public String NLPIR_GetFileNewWords(String  filename, int nMaxKeyLimit,boolean bWeightOut);
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
    /*************将发现的新词写入到文件中***********************/
    public static void WriteToDic(String input,String dec){
        try
        {
            File f = new File(dec);
            if (!f.exists())
            {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),"utf-8");
            BufferedWriter writer =new BufferedWriter(write);
            String[] words = input.split( "#");
            for(String key:words){
//                writer.write(key);
                String word = key.split("/")[0];
                String Normial = key.split("/")[1].substring(0,1);
                writer.write(word+' '+Normial);

                writer.write('\n');
            }

            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //初始化，以及发现新词
    public void findNewWords(){
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
        try {
            //从数据中发现新词
            String inputs[]={"android","C语言","ERP","IOS","java","PHP","web","其他","嵌入式","研发","移动互联","算法","网站开发","脚本","计算机辅助设计师"};
            for(int i =0;i<inputs.length;i++){
                String dec ="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\exactReq\\two\\"+inputs[i]+"result.txt";
                String newwords = CLibrary.Instance.NLPIR_GetFileNewWords(dec,100,true);
//                System.out.print("发现的新词为："+newwords);
                //词存储在文件中
                String output="D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\newWords\\"+inputs[i]+".txt";
                WriteToDic(newwords,output);

            }
//            String newwords = CLibrary.Instance.NLPIR_GetFileNewWords(,100,true);
//            System.out.print("发现的新词为："+newwords);
//            //词存储在文件中
//            WriteToDic(newwords,"D:\\JAVA-TEXT\\JnaTest_NLPIR\\classfy\\output\\newWords\\android.txt");


            CLibrary.Instance.NLPIR_Exit();     // 退出

        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        ExactKeyWords exactKeyWords = new ExactKeyWords();
        exactKeyWords.findNewWords();
    }
}

