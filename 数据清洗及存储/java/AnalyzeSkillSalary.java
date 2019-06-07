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
import org.apache.hadoop.io.IntWritable;
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
 * Created by root on 17-7-18.
 * 分析技能与工资的关系
 */
public class AnalyzeSkillSalary extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new AnalyzeSkillSalary(), args);
    }

    static class MyMapper extends TableMapper<IntWritable, Text> {
        public void map(ImmutableBytesWritable row, Result value, Context context)
                throws InterruptedException, IOException {
            for (int i = 0; i < value.size(); i++) {

                String detailed_description = new String(value.getValue(Bytes.toBytes("description"), Bytes.toBytes("detailed_description")));
                String salary = new String(value.getValue(Bytes.toBytes("job"), Bytes.toBytes("average_salary")));

                if (!detailed_description.equals("") && salary.matches("^\\d+$")) {
                    context.write(new IntWritable(1), new Text(detailed_description + "<<<" + salary));
                }
            }
        }
    }

    private static String[] skills = {"java", "css", "html", "javascript", "android", "ios", "PHP", "C++", "C", "sql",
            "Python", "Excel", "Linux", "xml", "office", "sas", "spark", "BI", "PPT", "Photoshop", "Hadoop", "hive", "shell",
            "spass", "net", "asp", "mvc", "oracle", "html"};
    static int salary_data[] = new int[skills.length];
    static int salary_count[] = new int[skills.length];

    static class MyReducer extends Reducer<IntWritable, Text, TbRecord, TbRecord> {
        @Override
        protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            for (Text bean : values) {
                String[] data = bean.toString().trim().split("<<<");
                String description = data[0].toLowerCase();
                int salary = Integer.parseInt(data[1]);
                for (int i = 0; i < skills.length; i++) {
                    if (description.contains(skills[i].toLowerCase())) {
                        salary_data[i] += salary;
                        salary_count[i]++;
                    }
                }
            }
            //取平均值
            for (int i = 0; i < salary_data.length; i++) {
                if (salary_count[i] != 0)
                    salary_data[i] = salary_data[i] / salary_count[i];
            }
            //写入mysql数据库
            for (int i = 0; i < skills.length; i++) {
                context.write(new TbRecord(Base64.encodeBytes(skills[i].getBytes()), salary_data[i]), null);
            }
        }
    }


    public int run(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum.", "localhost");
        DBConfiguration.configureDB(config, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/big_result2",
                "root", "swx142386");
        try {
            Job job = Job.getInstance(config, "AnalyzeWelfareScale");
            job.setJarByClass(AnalyzeSkillSalary.class);
            job.addArchiveToClassPath(new Path("hdfs://localhost:9000/lib/mysql/mysql-connector-java.jar"));
            job.setReducerClass(MyReducer.class);
            job.setOutputFormatClass(DBOutputFormat.class);
            Scan scan = new Scan();
            scan.setCaching(500);
            scan.setCacheBlocks(false);
            TableMapReduceUtil.initTableMapperJob(
                    "data_three",        // input HBase table name
                    scan,             // Scan instance to control CF and attribute selection
                    MyMapper.class,   // mapper
                    IntWritable.class,             // mapper output key
                    Text.class,             // mapper output value
                    job);
            DBOutputFormat.setOutput(job, "skill_salary", "skill", "salary");
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
        String skill;
        int salary;

        TbRecord(String skill, int salary) {
            this.skill = skill;
            this.salary = salary;
        }

        public void write(PreparedStatement statement) throws SQLException {
            statement.setString(1, this.skill);
            statement.setInt(2, this.salary);
        }

        public void readFields(ResultSet resultSet) throws SQLException {
            this.skill = resultSet.getString(1);
            this.salary = resultSet.getInt(2);
        }

        public void write(DataOutput out) throws IOException {
            out.writeUTF(this.skill);
            out.writeInt(this.salary);
        }

        public void readFields(DataInput in) throws IOException {
            this.skill = in.readUTF();
            this.salary = in.readInt();
        }

        public String toString() {
            return this.skill + " " + this.salary;
        }
    }

}
