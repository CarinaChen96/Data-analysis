package util;

import java.io.*;

/**
 * Created by root on 17-7-11.
 * 将多个小文件合并为一个大文件
 */
public class CombineFile {


    private void ganjiwang() throws IOException {
        String rootPath = "/home/shiweixian/项目实训/赶集网/";
        String outDirPath = "/home/shiweixian/项目实训/temp_data/赶集网/";
        File outDir = new File(outDirPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File rootDir = new File(rootPath);
        File[] dirs = rootDir.listFiles();
        if (dirs != null) {
            //遍历城市
            for (File dir : dirs) {
                File[] subDirs = dir.listFiles();
                if (subDirs != null) {
                    //以城市为名，建立输出文件
                    String dirName = dir.getName();
                    File outFile = new File(outDirPath + dirName + ".txt");
                    PrintWriter writer = new PrintWriter(new FileWriter(outFile));
                    System.out.println("开始写入 " + dirName + " 的数据");
                    //遍历城市下各职位的文件夹
                    for (File subDir : subDirs) {
                        File[] files = subDir.listFiles();
                        if (files != null) {
                            //遍历职位文件夹下的文件
                            for (File file : files) {
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                String temp;
                                while ((temp = reader.readLine()) != null) {
                                    writer.write(rootDir.getName() + ">>>" + dirName + ">>>" + temp + "\n");
                                }
                                writer.flush();
                            }
                        }
                    }
                    writer.close();
                    System.out.println(dirName + " 数据写入完成");
                }
            }
        }
    }


    private void fiveeight() throws IOException {
        String rootPath = "/home/shiweixian/项目实训/58同城/";
        String outDirPath = "/home/shiweixian/项目实训/temp_data/58同城/";
        File outDir = new File(outDirPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File rootDir = new File(rootPath);
        File[] dirs = rootDir.listFiles();
        if (dirs != null) {
            //遍历城市
            for (File dir : dirs) {
                File[] files = dir.listFiles();
                if (files != null) {
                    //以城市为名，建立输出文件
                    String filename = dir.getName();
                    File outFile = new File(outDirPath + filename + ".txt");
                    PrintWriter writer = new PrintWriter(new FileWriter(outFile));
                    System.out.println("开始写入 " + filename + " 的数据");
                    //遍历职位文件夹下的文件
                    for (File file : files) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String temp;
                        while ((temp = reader.readLine()) != null) {
                            writer.write(rootDir.getName() + ">>>" + filename + ">>>" + temp + "\n");
                        }
                        writer.flush();
                    }
                    writer.close();
                    System.out.println(filename + " 数据写入完成");
                }
            }
        }
    }

    private void combine2OneFile(String rootPath, PrintWriter writer) throws IOException {
        File rootDir = new File(rootPath);
        File[] files = rootDir.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            System.out.println("开始写入 " + file.getName() + " 的数据");
            //遍历职位文件夹下的文件
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = reader.readLine()) != null) {
                writer.write(temp + "\n");
            }
            writer.flush();
            System.out.println(file.getName() + " 数据写入完成");
        }
    }

    public static void main(String[] args) throws IOException {
        CombineFile combineFile = new CombineFile();
//        combineFile.ganjiwang();
//        combineFile.fiveeight();
        String[] paths = {"58同城", "智联招聘"};
        String outFilePath = "/home/shiweixian/项目实训/final_data/data_two.txt";
        PrintWriter writer = new PrintWriter(new FileWriter(outFilePath));
        for (int i = 0; i < paths.length; i++) {
            String path = "/home/shiweixian/项目实训/temp_data/" + paths[i];
            combineFile.combine2OneFile(path, writer);
        }
        writer.close();
        System.out.println("执行结束");
    }
}
