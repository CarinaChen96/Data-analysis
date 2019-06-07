import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;


/**
 * 数据去重
 */
public class Text2HBase {

    static int count = 0;

    public static class MyMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            context.write(value, new Text(""));
        }
    }

    public static class MyReducer extends TableReducer<Text, Text, ImmutableBytesWritable> {

        @Override
        protected void reduce(Text key, Iterable<Text> value,
                              Context context)
                throws IOException, InterruptedException {
            String[] fields = key.toString().split(">>>");
            if (fields.length < 8)
                return;
            Put put = new Put(String.valueOf(count).getBytes());
            //来源网站
            put.addColumn("source".getBytes(), "source_site".getBytes(), fields[0].getBytes());
            //城市
            put.addColumn("location".getBytes(), "city".getBytes(), fields[1].getBytes());
            //公司名
            put.addColumn("company".getBytes(), "company_name".getBytes(), fields[2].getBytes());
            //职位名称
            put.addColumn("job".getBytes(), "job_name".getBytes(), fields[3].getBytes());
            //职位类别
            put.addColumn("job".getBytes(), "job_class".getBytes(), getJobClass(fields[3]).getBytes());
            //工资
            String salary = fields[4].trim().replace("元", "").replace("/", "").replace("月", "");
            if (salary.matches("^\\d+$")) {
                put.addColumn("job".getBytes(), "average_salary".getBytes(), salary.getBytes());
            } else if (salary.matches("^\\d+-\\d+$")) {
                String[] salaries = salary.split("-");
                put.addColumn("job".getBytes(), "average_salary".getBytes(), String.valueOf((Integer.parseInt(salaries[0])
                        + Integer.parseInt(salaries[1])) / 2).getBytes());
            } else {
                put.addColumn("job".getBytes(), "average_salary".getBytes(), "面议".getBytes());
            }
            //招聘人数
            put.addColumn("job".getBytes(), "recruit_number".getBytes(), fields[5].replace("人", "").getBytes());
            //福利
            put.addColumn("description".getBytes(), "welfare".getBytes(), fields[6].getBytes());
            //关键词
//            put.addColumn("description".getBytes(), "keyword".getBytes(), fields[6].getBytes());
            //关键词拆解:学历、工作经验、年龄
            String[] keywords = fields[7].trim().replace("工作经验:工作经验：", "工作经验:").split(",");
            if (keywords.length >= 3) {
                String[] words1 = keywords[0].split(":");
                String[] words2 = keywords[1].split(":");
//                String[] words3 = keywords[2].replace("岁", "").split(":");
                String education_background = "";
                String work_experience = "";
//                String age = "";
                if (words1.length >= 2)
                    education_background = words1[1];
                if (words2.length >= 2)
                    work_experience = words2[1].replace("经验不限", "不限");
//                if (words3.length >= 2)
//                    age = words3[1];

                if (work_experience.trim().equals(""))
                    work_experience = "不限";
                if (education_background.trim().equals(""))
                    education_background = "不限";

                put.addColumn("description".getBytes(), "education_background".getBytes(), education_background.getBytes());
                put.addColumn("description".getBytes(), "work_experience".getBytes(), work_experience.getBytes());
//                put.addColumn("description".getBytes(), "age".getBytes(), age.getBytes());
            } else {
                put.addColumn("description".getBytes(), "education_background".getBytes(), "".getBytes());
                put.addColumn("description".getBytes(), "work_experience".getBytes(), "".getBytes());
//                put.addColumn("description".getBytes(), "age".getBytes(), "".getBytes());
            }
            //详细描述
            put.addColumn("description".getBytes(), "detailed_description".getBytes(), fields[8].getBytes());
            if (fields.length >= 11) {
                //公司行业
                put.addColumn("company".getBytes(), "industry".getBytes(), getCompanyCategory(fields[9]).getBytes());
                //公司规模
                String scale = fields[10].replace("\\s+", "").replace("人", "").replace("以上", "");
                if (scale.matches("^\\d+-\\d+$")) {
                    put.addColumn("company".getBytes(), "scale".getBytes(), String.valueOf((Integer.parseInt(scale.split("-")[0]) +
                            Integer.parseInt(scale.split("-")[1]) + 1) / 2).getBytes());
                } else if (scale.matches("^\\d+")) {
                    put.addColumn("company".getBytes(), "scale".getBytes(), scale.getBytes());
                } else {
                    //公司规模
                    put.addColumn("company".getBytes(), "scale".getBytes(), "未知".getBytes());
                }

            } else {
                //公司行业
                put.addColumn("company".getBytes(), "industry".getBytes(), "未知".getBytes());
                //公司规模
                put.addColumn("company".getBytes(), "scale".getBytes(), "未知".getBytes());
            }
            count++;
            context.write(new ImmutableBytesWritable(String.valueOf(count).getBytes()), put);
        }

        private String[] web = {"web", "UI设计", "H5程序", "H5前端", "网页设计", "Net研发", "h5程序", "Web开发", "html前端", "网站编辑", "arcgis js api开发", ".Net", "H5+CSS前端", "Unity3D前端", "WEB页面设计", "H5+web前端", "Django web开发", "JAVA BS网页开发", "GIS软件", "网络前端", "asp.net开发", "JS程序开发", "ASP.NET", "WebHTML5前端", "web学徒", "aspnet开发 "};
        private String[] script = {"脚本开发", "AngularJS框架", "NodeJS开发", "u3d主程", "unity开发", "lua", "软件破解员", "cocos2lua脚本", "python开发", "页游开发", "手游开发", "iMessage开发", "nodejs开发", "API开发", "unity3D", "Oracle开发"};
        private String[] computer_assistant = {"计算机助理", "计算机辅助", "CAD设计", "网站开发", "计算机维修员", "信息管理", "计算机维护", "平面设计", "网站设计", "图片处理", "结构工程师", "软件系统维护", "软件技术支持", "原画设计", "软件工程师", "质量过程控制", "delphi", "Sketchup", "软件测试", "解决方案", "软件实施", "游戏开发"};
        private String[] embedded_develop = {"嵌入式", "单片机", "硬件工程师", "自动化", "硬件开发", "集成电路", "电子工程", "电控", "数字电路", "智能家居", "无人机", "机器人", "QT", "Qt", "qT", "qt", "Linux", "MCU", "ARM", "PLC", "FPGA"};
        private String[] algorithm_engineer = {"算法工程", "算法设计", "算法研发", "算法测试", "图像算法", "图像识别", "视觉算法", "视觉工程", "视觉应用", "计算机视觉", "图像处理", "计算机图形学", "机器视觉", "模式识别", "数据算法", "数据分析", "数据挖掘", "数据科学", "数据工程", "深度学习", "机器学习", "自然语言处理", "大数据", "云处理", "etl", "人工智能", "AI", "仿真", "U3D", "Unity", "3dsmax", "数据模型", "建模工程", "视觉应用", "SLAM", "NLP", "BI工程师", "VR", "AR", "算法"};
        private String[] website_develop_1 = {"网站", "网页"};
        private String[] website_develop_2 = {"开发", "构架", "架构", "制作", "设计", "维护", "运维", "运营", "管理", "建设", "策划", "美工", "编辑", "程序员", "开发员", "工程师"};
        private String[] website_develop_3 = {"网络工程", "后端", "后台", "前端（除web）", "seo", "全栈工程师"};
        private String[] website_develop_not = {"php", "PHP", "Php"};
        private String[] mobile_develop = {"app", "移动互联", "移动端", "移动开发", "移动通信", "通信工程", "手游", "手机软件", "手机应用微信小程序", "微信开发", "公众号开发"};

        private String[] android = {"Android", "android", "安卓"};
        private String[] php = {"PHP软件开发", "PHP开发", "PHP后端", "PHP网站", "php程序", "php技术", "PHP后台开发", "PHP高级开发", "PHP研发", "PHP网站开发", "PHP编程", "PHP运维", "PHP助理", "PHP学徒", "PHP编程"};
        private String[] c_1 = {"C", "c"};
        private String[] c_2 = {"语言", "开发", "程序", "研发"};
        private String[] cpp_1 = {"C++", "c++"};
        private String[] cpp_2 = {"语言", "开发", "程序", "研发"};
        private String[] erp = {"erp", "ERP"};
        private String[] ios = {"ios", "IOS", "swift", "Swift", "苹果"};
        private String[] java = {"java", "JAVA", "Java"};


        /**
         * 获取职位类别
         *
         * @param job_name
         * @return
         */
        private String getJobClass(String job_name) {
            for (String job : web) {
                if (job_name.contains(job)) {
                    return "web工程师";
                }
            }
            for (String job : script) {
                if (job_name.contains(job)) {
                    return "脚本工程师";
                }
            }
            for (String job : computer_assistant) {
                if (job_name.contains(job)) {
                    return "计算机辅助师";
                }
            }
            for (String job : embedded_develop) {
                if (job_name.contains(job)) {
                    return "嵌入式开发工程师";
                }
            }
            for (String job : algorithm_engineer) {
                if (job_name.contains(job)) {
                    return "算法工程师";
                }
            }
            for (String job1 : website_develop_1) {
                for (String job2 : website_develop_2) {
                    for (String job_not : website_develop_not) {
                        if (job_name.contains(job1 + job2) && !job_name.contains(job_not)) {
                            return "网站开发工程师";
                        }
                    }
                }
            }
            for (String job : website_develop_3) {
                for (String job_not : website_develop_not) {
                    if (job_name.contains(job) && !job_name.contains(job_not)) {
                        return "网站开发工程师";
                    }
                }
            }
            for (String job : mobile_develop) {
                if (job_name.contains(job)) {
                    return "移动互联工程师";
                }
            }
            for (String job : android) {
                if (job_name.contains(job) && !job_name.contains("转java") && !job_name.contains("转Java")) {
                    return "Android工程师";
                }
            }
            for (String job : php) {
                if (job_name.contains(job)) {
                    return "PHP工程师";
                }
            }
            for (String job1 : c_1) {
                for (String job2 : c_2) {
                    if (job_name.contains(job1 + job2)) {
                        return "C语言工程师";
                    }
                }
            }
            for (String job1 : cpp_1) {
                for (String job2 : cpp_2) {
                    if (job_name.contains(job1 + job2)) {
                        return "C++工程师";
                    }
                }
            }
            for (String job : erp) {
                if (job_name.contains(job)) {
                    return "ERP工程师";
                }
            }
            for (String job : ios) {
                if (job_name.contains(job)) {
                    return "IOS工程师";
                }
            }
            for (String job : java) {
                if (job_name.contains(job)) {
                    return "JAVA工程师";
                }
            }
            return "其他";
        }


        private String[] life_service = {"办公用品及设备", "批发/零售", "租赁服务", "服装/纺织/皮革", "交通/运输/物流", "中介/专业服务", "房地产/物业管理", "出版/印刷/造纸", "快速消费品(食品/饮料等)", "耐用消费品(家具/家电等)", "医疗/保健/卫生/美容", "家居/室内设计/装潢", "通信/电信"};
        private String[] leisure_service = {"娱乐休闲/餐饮/服务", "旅游/酒店", "文体/影视/艺术"};
        private String[] IT = {"互联网/电子商务", "游戏", "计算机软件", "IT服务/系统集成", "计算机硬件"};
        private String[] industry = {"化工/采掘/冶炼", "环保", "能源(电力/水利/矿产)", "电子技术/半导体/集成电路", "生物/制药/医疗器械", "建筑/建材", "仪器仪表/工业自动化", "原材料和加工", "汽车/摩托车", "钢铁/机械/设备/重工"};
        private String[] news_media = {"广告/创意", "媒体传播"};
        private String[] government = {"信托/拍卖", "法律/法务", "政府/非盈利机构"};
        private String[] finance = {"金融/银行", "财务/审计", "贸易/进出口"};
        private String[] education = {"教育/科研/培训", "咨询/顾问"};
        private String[] others = {"多元化集团", "人力资源服务", "农林牧渔", "公关/市场推广/会展"};

        private String getCompanyCategory(String name) {
            for (String value : life_service) {
                if (name.contains(value))
                    return "生活服务";
            }
            for (String value : leisure_service) {
                if (name.contains(value))
                    return "休闲服务";
            }
            for (String value : IT) {
                if (name.contains(value))
                    return "IT行业";
            }
            for (String value : industry) {
                if (name.contains(value))
                    return "工业";
            }
            for (String value : news_media) {
                if (name.contains(value))
                    return "新闻媒体";
            }
            for (String value : government) {
                if (name.contains(value))
                    return "政府/非盈利机构";
            }
            for (String value : finance) {
                if (name.contains(value))
                    return "金融行业";
            }
            for (String value : education) {
                if (name.contains(value))
                    return "教育行业";
            }
            return "其他行业";
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum.", "localhost");

        Job job = new Job(conf, "DataProcess");
        job.setJarByClass(Text2HBase.class);
        Path path = new Path("/DataProcess2/input/ganjiwang.txt");
        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, path);

        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        TableMapReduceUtil.initTableReducerJob("data_three", MyReducer.class, job);

        job.waitForCompletion(true);

    }


}  