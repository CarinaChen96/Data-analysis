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
 * 分析不同公司行业、不同职位类别、不同工作经验、不同学历的招聘人数和平均工资
 */
public class AnalyzeIndustryJob extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new AnalyzeIndustryJob(), args);
    }


    static class MyMapper extends TableMapper<Text, Text> {
        public void map(ImmutableBytesWritable row, Result value, Context context)
                throws InterruptedException, IOException {
            for (int i = 0; i < value.size(); i++) {
                String source_site = new String(value.getValue(Bytes.toBytes("source"), Bytes.toBytes("source_site")));
                String industry = new String(value.getValue(Bytes.toBytes("company"), Bytes.toBytes("industry")));
                String recruit_number = new String(value.getValue(Bytes.toBytes("job"), Bytes.toBytes("recruit_number")));
                String average_salary = new String(value.getValue(Bytes.toBytes("job"), Bytes.toBytes("average_salary")));
                String job_class = new String(value.getValue(Bytes.toBytes("job"), Bytes.toBytes("job_class")));
                String education_background = new String(value.getValue(Bytes.toBytes("description"), Bytes.toBytes("education_background")));
                String work_experience = new String(value.getValue(Bytes.toBytes("description"), Bytes.toBytes("work_experience")));
                if (work_experience.trim().equals(""))
                    work_experience = "不限";
                if (education_background.trim().equals(""))
                    education_background = "不限";
                context.write(new Text(source_site + "~~~" + industry + "~~~" + job_class + "~~~" + work_experience
                        + "~~~" + education_background), new Text(recruit_number + ">>>" + average_salary));
            }
        }
    }

    static int count = 0;

    static class MyReducer extends Reducer<Text, Text, TbRecord, TbRecord> {

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            //统计不同网站、不同城市、不同城市的招聘人数
            int total_recruit_num = 0;
            //统计不同网站、不同城市、不同城市的最高、最低及平均工资
            int total_salary = 0;
            int average_salary;
            int size_has_salary = 0;
            for (Text bean : values) {
                String[] value = bean.toString().split(">>>");
                String recruit_num = value[0];
                if (recruit_num.matches("^\\d+$")) {
                    total_recruit_num += Integer.parseInt(recruit_num);
                } else {
                    total_recruit_num += 3;
                }
                //工资
                String temp_average_salary = value[1];
                if (temp_average_salary.matches("^\\d+$")) {
                    size_has_salary++;
                    average_salary = Integer.parseInt(temp_average_salary);
                    total_salary += average_salary;
                }
            }
            if (size_has_salary != 0)
                average_salary = total_salary / size_has_salary;
            else
                average_salary = 0;
            count++;

            String[] keys = key.toString().split("~~~");
            context.write(new TbRecord(Base64.encodeBytes(keys[0].getBytes()), Base64.encodeBytes(keys[1].getBytes()),
                    Base64.encodeBytes(keys[2].getBytes()), Base64.encodeBytes(keys[3].getBytes()),
                    Base64.encodeBytes(keys[4].getBytes()), total_recruit_num,
                    average_salary), null);

        }
    }


    public int run(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum.", "localhost");
        DBConfiguration.configureDB(config, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/big_result2",
                "root", "swx142386");
        try {
            Job job = Job.getInstance(config, "AnalyzeIndustryJob");
            job.setJarByClass(AnalyzeIndustryJob.class);     // class that contains mapper
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
            DBOutputFormat.setOutput(job, "industry_job", "KWebsite", "KIndustry", "KJob",
                    "KExperience", "KEducation", "Requirement", "Wage");
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
        String KWebsite;
        String KIndustry;
        String KJob;
        String KExperience;
        String KEducation;
        int Requirement;
        int Wage;

        public TbRecord() {

        }

        public TbRecord(String KWebsite, String KIndustry, String KJob, String KExperience, String KEducation, int requirement, int wage) {
            this.KWebsite = KWebsite;
            this.KIndustry = KIndustry;
            this.KJob = KJob;
            this.KExperience = KExperience;
            this.KEducation = KEducation;
            Requirement = requirement;
            Wage = wage;
        }

        public void write(PreparedStatement statement) throws SQLException {
            statement.setString(1, this.KWebsite);
            statement.setString(2, this.KIndustry);
            statement.setString(3, this.KJob);
            statement.setString(4, this.KExperience);
            statement.setString(5, this.KEducation);
            statement.setInt(6, this.Requirement);
            statement.setInt(7, this.Wage);
        }

        public void readFields(ResultSet resultSet) throws SQLException {
            this.KWebsite = resultSet.getString(1);
            this.KIndustry = resultSet.getString(2);
            this.KJob = resultSet.getString(3);
            this.KExperience = resultSet.getString(4);
            this.KEducation = resultSet.getString(5);
            this.Requirement = resultSet.getInt(6);
            this.Wage = resultSet.getInt(7);
        }

        public void write(DataOutput out) throws IOException {
            out.writeUTF(this.KWebsite);
            out.writeUTF(this.KIndustry);
            out.writeUTF(this.KJob);
            out.writeUTF(this.KExperience);
            out.writeUTF(this.KEducation);
            out.writeInt(this.Requirement);
            out.writeInt(this.Wage);
        }

        public void readFields(DataInput in) throws IOException {
            this.KWebsite = in.readUTF();
            this.KIndustry = in.readUTF();
            this.KJob = in.readUTF();
            this.KExperience = in.readUTF();
            this.KEducation = in.readUTF();
            this.Requirement = in.readInt();
            this.Wage = in.readInt();
        }

        public String toString() {

            return this.KWebsite + " " + this.KIndustry + " " + this.KJob + " " + this.KExperience
                    + " " + this.KEducation + " " + this.Requirement + " " + this.Wage;
        }
    }
}