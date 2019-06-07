import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
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
 * 分析不同公司规模具有的公司个数
 */
public class AnalyzeScaleCompanyNum extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new AnalyzeScaleCompanyNum(), args);
    }


    static class MyMapper extends TableMapper<IntWritable, Text> {
        public void map(ImmutableBytesWritable row, Result value, Context context)
                throws InterruptedException, IOException {
            for (int i = 0; i < value.size(); i++) {
                byte[] bytes = value.getValue(Bytes.toBytes("company"), Bytes.toBytes("scale"));
                String company_name = new String(value.getValue(Bytes.toBytes("company"), Bytes.toBytes("company_name")));
                if (bytes == null) {
                    System.out.println(company_name);
                } else {
                    String scale = new String(bytes);
                    if (scale.matches("^\\d+$")) {
                        context.write(new IntWritable(Integer.parseInt(scale)), new Text(company_name));
                    }
                }
            }
        }
    }

    static class MyReducer extends Reducer<IntWritable, Text, TbRecord, TbRecord> {
        @Override
        protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (Text text : values) {
                count++;
            }

            context.write(new TbRecord(key.get(),count),null);
        }
    }


    public int run(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum.", "localhost");
        DBConfiguration.configureDB(config, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/big_result2",
                "root", "swx142386");
        try {
            Job job = Job.getInstance(config);
            job.setJarByClass(AnalyzeScaleCompanyNum.class);
            System.out.println("jar名字："+job.getJar());
            job.addArchiveToClassPath(new Path("hdfs://localhost:9000/lib/mysql/mysql-connector-java.jar"));
            job.setReducerClass(MyReducer.class);
            job.setOutputFormatClass(DBOutputFormat.class);
            Scan scan = new Scan();
            scan.setCaching(500);
            scan.setCacheBlocks(false);
            TableMapReduceUtil.initTableMapperJob(
                    "data_two",        // input HBase table name
                    scan,             // Scan instance to control CF and attribute selection
                    MyMapper.class,   // mapper
                    IntWritable.class,             // mapper output key
                    Text.class,             // mapper output value
                    job);
            DBOutputFormat.setOutput(job, "scale_company_num", "KScale", "company_count");
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
        int KScale;
        int company_count;


        public TbRecord(int KScale, int company_count) {
            this.KScale = KScale;
            this.company_count = company_count;
        }

        public void write(PreparedStatement statement) throws SQLException {
            statement.setInt(1, this.KScale);
            statement.setInt(2, this.company_count);
        }

        public void readFields(ResultSet resultSet) throws SQLException {
            this.KScale = resultSet.getInt(1);
            this.company_count = resultSet.getInt(2);
        }

        public void write(DataOutput out) throws IOException {
            out.writeInt(this.KScale);
            out.writeInt(this.company_count);
        }

        public void readFields(DataInput in) throws IOException {
            this.KScale = in.readInt();
            this.company_count = in.readInt();
        }

        public String toString() {
            return this.KScale + " " + this.company_count;
        }
    }
} 