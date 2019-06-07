package util;

import java.sql.*;
import java.util.Base64;

/**
 * Created by root on 17-7-16.
 * 从mysql数据库中读取base64数据，经过解码后写到最终结果数据库
 */
public class Base2Text {

    Connection connSQL(String database) {
        String url = "jdbc:mysql://localhost:3306/" + database + "?characterEncoding=UTF-8";
        String username = "root";
        String password = "swx142386";
        // 加载驱动程序以连接数据库
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            return conn;
        }
        //捕获加载驱动程序异常
        catch (ClassNotFoundException cnfex) {
            System.err.println(
                    "装载 JDBC/ODBC 驱动程序失败。");
            cnfex.printStackTrace();
        }
        //捕获连接数据库异常
        catch (SQLException sqlex) {
            System.err.println("无法连接数据库");
            sqlex.printStackTrace();
        }
        return null;
    }

    void deconnSQL(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            System.out.println("关闭数据库问题 ：");
            e.printStackTrace();
        }
    }


    /**
     * 将base64编码的字符串转化为正常文字
     */
    private void transCityJob() throws SQLException {
        Connection conn = connSQL("big_result2");
        PreparedStatement statement = conn.prepareStatement("select * from city_job");
        ResultSet set = statement.executeQuery();
        Connection writeConn = connSQL("final_result");
        PreparedStatement writeStatement = writeConn.prepareStatement("INSERT INTO city_job (KWebsite,KCity,KJob,KEducation," +
                "Requirement,Wage) VALUES (?,?,?,?,?,?)");
        while (set.next()) {
            String website = decode(set.getString("KWebsite"));
            String city = decode(set.getString("KCity"));
            String job = decode(set.getString("KJob"));
            String education = decode(set.getString("KEducation"));
            int requirement = set.getInt("Requirement");
            int wage = set.getInt("Wage");
            writeStatement.setString(1, website);
            writeStatement.setString(2, city);
            writeStatement.setString(3, job);
            writeStatement.setString(4, education);
            writeStatement.setInt(5, requirement);
            writeStatement.setInt(6, wage);
            writeStatement.executeUpdate();
        }
        set.close();
        statement.close();
        writeStatement.close();
        deconnSQL(writeConn);
        deconnSQL(conn);
    }

    private void transIndustryJob() throws SQLException {
        Connection conn = connSQL("big_result2");
        PreparedStatement statement = conn.prepareStatement("select * from industry_job");
        ResultSet set = statement.executeQuery();
        Connection writeConn = connSQL("final_result");
        PreparedStatement writeStatement = writeConn.prepareStatement("INSERT INTO industry_job (KWebsite,KIndustry," +
                "KJob,KExperience,KEducation,Requirement,Wage) VALUES (?,?,?,?,?,?,?)");
        while (set.next()) {
            String website = decode(set.getString("KWebsite"));
            String KIndustry = decode(set.getString("KIndustry"));
            String job = decode(set.getString("KJob"));
            String experience = decode(set.getString("KExperience"));
            String education = decode(set.getString("KEducation"));
            int requirement = set.getInt("Requirement");
            int wage = set.getInt("Wage");
            writeStatement.setString(1, website);
            writeStatement.setString(2, KIndustry);
            writeStatement.setString(3, job);
            writeStatement.setString(4, experience);
            writeStatement.setString(5, education);
            writeStatement.setInt(6, requirement);
            writeStatement.setInt(7, wage);
            writeStatement.executeUpdate();
        }
        set.close();
        statement.close();
        writeStatement.close();
        deconnSQL(writeConn);
        deconnSQL(conn);
    }

    private void transIndustryProfession() throws SQLException {
        Connection conn = connSQL("big_result2");
        PreparedStatement statement = conn.prepareStatement("select * from industry_profession");
        ResultSet set = statement.executeQuery();
        Connection writeConn = connSQL("final_result");
        PreparedStatement writeStatement = writeConn.prepareStatement("INSERT INTO industry_profession (KWebsite,KIndustry," +
                "KProfession,Requirement,Wage,Correlation) VALUES (?,?,?,?,?,?)");
        while (set.next()) {
            String website = decode(set.getString("KWebsite"));
            String KIndustry = decode(set.getString("KIndustry"));
            String KProfession = decode(set.getString("KProfession"));
            int requirement = set.getInt("Requirement");
            int wage = set.getInt("Wage");
            writeStatement.setString(1, website);
            writeStatement.setString(2, KIndustry);
            writeStatement.setString(3, KProfession);
            writeStatement.setInt(4, requirement);
            writeStatement.setInt(5, wage);
            writeStatement.setFloat(6, 0);
            writeStatement.executeUpdate();
        }
        set.close();
        statement.close();
        writeStatement.close();
        deconnSQL(writeConn);
        deconnSQL(conn);
    }

    private void transWelfareScale() throws SQLException {
        Connection conn = connSQL("big_result2");
        PreparedStatement statement = conn.prepareStatement("select * from welfare_scale");
        ResultSet set = statement.executeQuery();
        Connection writeConn = connSQL("final_result");
        PreparedStatement writeStatement = writeConn.prepareStatement("INSERT INTO welfare_scale (KWelfare,KScale," +
                "WelfareData) VALUES (?,?,?)");
        while (set.next()) {
            String KWelfare = decode(set.getString("KWelfare"));
            String KScale = set.getString("KScale");
            int WelfareData = set.getInt("WelfareData");
            writeStatement.setString(1, KWelfare);
            writeStatement.setString(2, KScale);
            writeStatement.setInt(3, WelfareData);
            writeStatement.executeUpdate();
        }
        set.close();
        statement.close();
        writeStatement.close();
        deconnSQL(writeConn);
        deconnSQL(conn);
    }

    private void transSkillSalary() throws SQLException {
        Connection conn = connSQL("big_result2");
        PreparedStatement statement = conn.prepareStatement("select * from skill_salary");
        ResultSet set = statement.executeQuery();
        Connection writeConn = connSQL("final_result");
        PreparedStatement writeStatement = writeConn.prepareStatement("INSERT INTO skill_salary (skill,salary) VALUES (?,?)");
        while (set.next()) {
            String skill = decode(set.getString("skill"));
            int salary = set.getInt("salary");
            writeStatement.setString(1, skill);
            writeStatement.setInt(2, salary);
            writeStatement.executeUpdate();
        }
        set.close();
        statement.close();
        writeStatement.close();
        deconnSQL(writeConn);
        deconnSQL(conn);
    }

    /**
     * base64解码
     *
     * @param source
     * @return
     */
    private String decode(String source) {
        return new String(Base64.getDecoder().decode(source));
    }

    public static void main(String[] args) {
        Base2Text b = new Base2Text();
        try {
//            b.transCityJob();
//            b.transIndustryJob();
//            b.transIndustryProfession();
            b.transWelfareScale();
//            b.transSkillSalary();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
