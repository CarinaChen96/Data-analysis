import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 分析不同公司规模所对应的福利的高低程度
 */
public class AnalyzeWelfareScale extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new AnalyzeWelfareScale(), args);
    }


    static int map_count = 0;

    static class MyMapper extends TableMapper<IntWritable, Text> {
        public void map(ImmutableBytesWritable row, Result value, Context context)
                throws InterruptedException, IOException {
            for (int i = 0; i < value.size(); i++) {
                byte[] bytes = value.getValue(Bytes.toBytes("company"), Bytes.toBytes("scale"));
                String welfare = new String(value.getValue(Bytes.toBytes("description"), Bytes.toBytes("welfare")));
                if (bytes == null) {
                    System.out.println(welfare);
                } else {
                    String scale = new String(bytes);
                    if (scale.matches("^\\d+$")) {
                        map_count++;
                        context.write(new IntWritable(Integer.parseInt(scale)), new Text(welfare));
                    }
                }
            }
        }
    }

    static int count = 0;

    //奖金
    private static String[] bonuses = {"奖金", "年终", "提成", "绩效奖", "双薪", "十三薪", "全勤奖"};
    //日常补贴
    private static String[] everydays = {"话补", "补助", "补贴", "电脑补", "采暖费","高温费","车贴","车补","通讯费","交通费"};
    //其他
    private static String[] others = {"晋升", "福利", "活动", "生日", "游玩", "聚餐", "旅游", "出国", "体检", "股票", "期权",
            "培训", "团建"};
    //餐补
    private static Map<String, Integer> meals = new HashMap<String, Integer>();
    //房补
    private static Map<String, Integer> houses = new HashMap<String, Integer>();
    //保险类
    private static Map<String, Integer> assurances = new HashMap<String, Integer>();
    //假期
    private static Map<String, Integer> vacations = new HashMap<String, Integer>();

    static {
        meals.put("餐补", 1);
        meals.put("餐费", 1);
        meals.put("饭补", 1);

        houses.put("包住", 1);
        houses.put("房补", 1);
        houses.put("住宿", 1);
        houses.put("宿舍", 1);
        houses.put("住房公积金", 1);

        assurances.put("五险一金", 5);
        assurances.put("六险二金", 6);
        assurances.put("养老保险", 1);
        assurances.put("医疗", 1);
        assurances.put("生育", 1);
        assurances.put("工伤", 1);
        assurances.put("失业", 1);
        assurances.put("社保",1);

        vacations.put("婚假", 1);
        vacations.put("丧假", 1);
        vacations.put("病假", 1);
        vacations.put("年假", 1);
        vacations.put("做五休二", 2);
        vacations.put("双休", 1);
        vacations.put("法定节假日", 1);

    }

    static class MyReducer extends Reducer<IntWritable, Text, TbRecord, TbRecord> {
        @Override
        protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int welfare_data[] = new int[7];

            for (Text bean : values) {
                String value = bean.toString();
                //奖金
                for (String bonus : bonuses) {
                    if (value.contains(bonus))
                        welfare_data[0]++;
                }
                //日常补贴
                for (String everyday : everydays) {
                    if (value.contains(everyday))
                        welfare_data[1]++;
                }
                //其他
                for (String other : others) {
                    if (value.contains(other))
                        welfare_data[2]++;
                }
                //餐补
                for (String meal : meals.keySet()) {
                    if (value.contains(meal))
                        welfare_data[3] += meals.get(meal);
                }
                //房补
                for (String house : houses.keySet()) {
                    if (value.contains(house))
                        welfare_data[4] += houses.get(house);
                }
                //保险类
                for (String assurance : assurances.keySet()) {
                    if (value.contains(assurance))
                        welfare_data[5] += assurances.get(assurance);
                }
                //假期
                for (String vacation : vacations.keySet()) {
                    if (value.contains(vacation))
                        welfare_data[6] += vacations.get(vacation);
                }
            }

            context.write(new TbRecord(key.get(), Base64.encodeBytes("奖金".getBytes()), welfare_data[0]), null);
            context.write(new TbRecord(key.get(), Base64.encodeBytes("日常补贴".getBytes()), welfare_data[1]), null);
            context.write(new TbRecord(key.get(), Base64.encodeBytes("其他".getBytes()), welfare_data[2]), null);
            context.write(new TbRecord(key.get(), Base64.encodeBytes("餐补".getBytes()), welfare_data[3]), null);
            context.write(new TbRecord(key.get(), Base64.encodeBytes("房补".getBytes()), welfare_data[4]), null);
            context.write(new TbRecord(key.get(), Base64.encodeBytes("保险类".getBytes()), welfare_data[5]), null);
            context.write(new TbRecord(key.get(), Base64.encodeBytes("假期".getBytes()), welfare_data[6]), null);
        }
    }


    public int run(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum.", "localhost");
        DBConfiguration.configureDB(config, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/big_result2",
                "root", "swx142386");
        try {
            Job job = Job.getInstance(config, "AnalyzeWelfareScale");
            job.setJarByClass(AnalyzeWelfareScale.class);
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
            DBOutputFormat.setOutput(job, "welfare_scale", "KScale", "KWelfare", "company_count");
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
        String KWelfare;
        int WelfareData;


        public TbRecord() {

        }

        public TbRecord(int KScale, String KWelfare, int welfareData) {
            this.KScale = KScale;
            this.KWelfare = KWelfare;
            WelfareData = welfareData;
        }

        public void write(PreparedStatement statement) throws SQLException {
            statement.setInt(1, this.KScale);
            statement.setString(2, this.KWelfare);
            statement.setInt(3, this.WelfareData);
        }

        public void readFields(ResultSet resultSet) throws SQLException {
            this.KScale = resultSet.getInt(1);
            this.KWelfare = resultSet.getString(2);
            this.WelfareData = resultSet.getInt(3);
        }

        public void write(DataOutput out) throws IOException {
            out.writeInt(this.KScale);
            out.writeUTF(this.KWelfare);
            out.writeInt(this.WelfareData);
        }

        public void readFields(DataInput in) throws IOException {
            this.KScale = in.readInt();
            this.KWelfare = in.readUTF();
            this.WelfareData = in.readInt();
        }

        public String toString() {
            return this.KWelfare + " " + this.KScale;
        }
    }
} 