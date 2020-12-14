package com.xxbb.framework.simplespring.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封装了字符串常用的操作
 *
 * @author xxbb
 */
public class StringUtil {
    /**
     * 正则表达式 用于匹配下划线
     */
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /**
     * 正则表达式 用于匹配大写字母
     */
    private static Pattern humpPattern = Pattern.compile("[A-Z]");


    /**
     * 将传入的表名去除t_字符，转化为类名
     *
     * @param str 传入字段
     * @return 取出t_的下划线转驼峰+首字母大写字段
     */
    public static String tableNameToClassName(String str) {
        if ("t_".equals(str.substring(0, 2))) {
            return firstCharToUpperCase(lineToHump(str.substring(2)));
        } else {
            return lineToHump(str);
        }

    }


    /**
     * 将数据库字段名转化为类的命名规则，即下划线改驼峰+首字母大写，例如要获取if_freeze字段的方法，方法为getIfFreeze(),
     *
     * @param str 传入字段
     * @return 下划线改驼峰+首字母大写
     */
    public static String columnNameToMethodName(String str) {
        return firstCharToUpperCase(lineToHump(str));
    }

    /**
     * 将传入字符串的首字母大写
     *
     * @param str 传入字符串
     * @return 首字母大写的字符串
     */
    public static String firstCharToUpperCase(String str) {
        char[] chars=str.toCharArray();
        chars[0]-=32;
        return String.valueOf(chars);
    }
    /**
     * 将传入字符串的首字母大写
     *
     * @param str 传入字符串
     * @return 首字母大写的字符串
     */
    public static String firstCharToLowerCase(String str) {
        char[] chars=str.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);

    }

    /**
     * 下划线转驼峰
     *
     * @param str 待转换字符串
     * @return 驼峰风格字符串
     */
    public static String lineToHump(String str) {
        //将小写转换
        String newStr = str.toLowerCase();
        Matcher matcher = linePattern.matcher(newStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param str 待转换字符串
     * @return 下划线风格字符串
     */
    public static String humpToLine(String str) {
        //将首字母先进行小写转换
        String newStr = str.substring(0, 1).toLowerCase() + str.substring(1);
        //比对字符串中的大写字符
        Matcher matcher = humpPattern.matcher(newStr);
        StringBuffer sb = new StringBuffer();
        //匹配替换
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * string is not empty
     *
     * @param str 字符串
     * @return 结果
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }



    /**
     * 对字符串去空白符和换行符等
     *
     * @return 字符串
     */
    public static String stringTrim(String src) {
        return (null != src) ? src.trim() : null;
    }

    /**
     * 删除配置文件名前的classpath:前缀
     * @param str 配置文件名
     * @return 修改后的字符串
     */
    public static String hasPrefixClassPathAndDelete(String str){
        if(hasPrefixClassPath(str)){
            return str.substring(10);
        }
        return str;
    }

    /**
     * 判断配置文件名是否带有classpath：前缀
     * @Description 在Web.xml中配置读取的配置文件参数<param-value></param-value>，如properties时
     * 如果不加上classpath：前缀会使得该行在IDEA编译器的检查中标下划线，
     * 但对程序的运行是没有影响的。
     * @param str 配置文件名
     * @return true/false
     */
    private static boolean hasPrefixClassPath(String str){
        return str.startsWith("classpath:");
    }
}
