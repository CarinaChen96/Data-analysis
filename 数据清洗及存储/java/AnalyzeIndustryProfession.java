import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 分析不同公司行业所需的不同专业的需求量和平均工资
 */
public class AnalyzeIndustryProfession extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new AnalyzeIndustryProfession(), args);
    }


    static class MyMapper extends TableMapper<Text, Text> {
        public void map(ImmutableBytesWritable row, Result value, Context context)
                throws InterruptedException, IOException {
            for (int i = 0; i < value.size(); i++) {
                String source_site = new String(value.getValue(Bytes.toBytes("source"), Bytes.toBytes("source_site")));
                String industry = new String(value.getValue(Bytes.toBytes("company"), Bytes.toBytes("industry")));
                String recruit_number = new String(value.getValue(Bytes.toBytes("job"), Bytes.toBytes("recruit_number")));
                String average_salary = new String(value.getValue(Bytes.toBytes("job"), Bytes.toBytes("average_salary")));
                String detailed_description = new String(value.getValue(Bytes.toBytes("description"), Bytes.toBytes("detailed_description")));
                if (detailed_description.equals(""))
                    detailed_description = "。。。。。";
                context.write(new Text(source_site + "~~~" + industry),
                        new Text(recruit_number + "~~~" + average_salary + "~~~" + detailed_description));
            }
        }
    }

    static int count = 0;

    static class MyReducer extends Reducer<Text, Text, TbRecord, TbRecord> {
        private String[] profession_categories = {"计算机", "软件", "电气", "自动化", "自动控制", "信息", "通信", "通讯", "信管",
                "数学", "统计学", "应用数学", "机械", "电子", "物理", "环境园林", "土建", "测仪", "测控", "生仪", "机电", "电子商务",
                "测试", "信管", "设计", "物联网", "网络", "建筑", "数控", "生物技术", "英语", "新闻", "中文", "旅游", "能源", "财务",
                "会计", "会计电算化", "审计", "经济", "金融", "人力资源", "工业工程", "销售", "理工类", "美术", "文职类", "法律", "心理学",
                "市场营销", "工商管理", "国际贸易", "物流仓储", "光学", "广告", "网页设计", "图像信号处理", "图像处理", "模式识别", "机器学习"};


        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int[] profession_count = new int[profession_categories.length];
            int[] profession_salaries = new int[profession_categories.length];
            int[] profession_has_salary_num = new int[profession_categories.length];
            int[] profession_recruit_num = new int[profession_categories.length];

            for (Text bean : values) {
                String[] value = bean.toString().split("~~~");
                if (value.length < 3) {
                    System.out.println(bean);
                    continue;
                }
                String recruit_num = value[0];
                //针对详细描述里涉及到的专业进行计数
                String detailed_description = value[2];
                for (int i = 0; i < profession_categories.length; i++) {
                    if (detailed_description.contains(profession_categories[i])) {
                        profession_count[i]++;
                        //工资
                        String temp_average_salary = value[1];
                        if (temp_average_salary.matches("^\\d+$")) {
                            profession_salaries[i] += Integer.parseInt(temp_average_salary);
                            profession_has_salary_num[i]++;
                        }
                        //招聘人数
                        if (recruit_num.matches("^\\d+$")) {
                            profession_recruit_num[i] += Integer.parseInt(recruit_num);
                        } else {
                            //若干的默认为招聘3人
                            profession_recruit_num[i] += 3;
                        }
                    }
                }
            }
            //得到各个专业的平均工资
            for (int i = 0; i < profession_salaries.length; i++) {
                if (profession_has_salary_num[i] > 0)
                    profession_salaries[i] = profession_salaries[i] / profession_has_salary_num[i];
                else
                    profession_salaries[i] = 0;
            }

            for (int i = 0; i < profession_count.length; i++) {
                if (profession_count[i] != 0) {
                    count++;
                    String keys[] = key.toString().trim().split("~~~");
                    context.write(new TbRecord(Base64.encodeBytes(keys[0].getBytes()), Base64.encodeBytes(keys[1].getBytes())
                            , Base64.encodeBytes(profession_categories[i].getBytes()),
                            profession_salaries[i], profession_salaries[i]), null);
                }
            }


        }
    }


    public int run(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum.", "localhost");
        DBConfiguration.configureDB(config, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/big_result2",
                "root", "swx142386");
        try {
            Job job = Job.getInstance(config, "AnalyzeIndustryProfession");
            job.setJarByClass(AnalyzeIndustryProfession.class);     // class that contains mapper
            job.addArchiveToClassPath(new Path("hdfs://localhost:9000/lib/mysql/mysql-connector-java.jar"));
            job.setReducerClass(MyReducer.class);
            job.setOutputFormatClass(DBOutputFormat.class);
            Scan scan = new Scan();
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs  
            scan.setCacheBlocks(false);
            TableMapReduceUtil.initTableMapperJob(
                    "data_two",        // input HBase table name
                    scan,             // Scan instance to control CF and attribute selection
                    MyMapper.class,   // mapper
                    Text.class,             // mapper output key
                    Text.class,             // mapper output value
                    job);
            DBOutputFormat.setOutput(job, "industry_profession", "KWebsite", "KIndustry", "KProfession",
                    "Requirement", "Wage");
            boolean b = job.waitForCompletion(true);
            if (!b) {
                throw new IOException("运行出错!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 重写实现写入数据到数据库
     */
    public static class TbRecord implements Writable, DBWritable {
        String KWebSite;
        String KIndustry;
        String KProfession;
        int Requirement;
        int Wage;

        public TbRecord() {

        }

        public TbRecord(String KWebSite, String KIndustry, String KProfession, int Requirement, int wage) {
            this.KWebSite = KWebSite;
            this.KIndustry = KIndustry;
            this.KProfession = KProfession;
            this.Requirement = Requirement;
            Wage = wage;
        }

        public void write(PreparedStatement statement) throws SQLException {
            statement.setString(1, this.KWebSite);
            statement.setString(2, this.KIndustry);
            statement.setString(3, this.KProfession);
            statement.setInt(4, this.Requirement);
            statement.setInt(5, this.Wage);
        }

        public void readFields(ResultSet resultSet) throws SQLException {
            this.KWebSite = resultSet.getString(1);
            this.KIndustry = resultSet.getString(2);
            this.KProfession = resultSet.getString(3);
            this.Requirement = resultSet.getInt(4);
            this.Wage = resultSet.getInt(5);
        }

        public void write(DataOutput out) throws IOException {
            out.writeUTF(this.KWebSite);
            out.writeUTF(this.KIndustry);
            out.writeUTF(this.KProfession);
            out.writeInt(this.Requirement);
            out.writeInt(this.Wage);
        }

        public void readFields(DataInput in) throws IOException {
            this.KWebSite = in.readUTF();
            this.KIndustry = in.readUTF();
            this.KProfession = in.readUTF();
            this.Requirement = in.readInt();
            this.Wage = in.readInt();
        }

        public String toString() {
            return this.KIndustry + " " + this.KProfession + "\t" + Requirement;
        }
    }
} 