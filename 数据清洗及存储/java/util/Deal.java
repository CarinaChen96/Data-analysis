package util;

/**
 * Created by root on 17-7-12.
 * 将字符串转化为数组
 */
public class Deal {

    private static void toArray() {
        String line = "教育/科研/培训>>>咨询/顾问";
        String[] words = line.split(">>>");
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < words.length; i++) {
            result.append("\"").append(words[i]).append("\"");
            if (i != words.length - 1) {
                result.append(",");
            }
        }
        result.append("};");
        System.out.println(result.toString());
    }

    public static void main(String[] args) {
        String text = "你好Text sub.";
        System.out.println(text.toLowerCase());
    }
}
