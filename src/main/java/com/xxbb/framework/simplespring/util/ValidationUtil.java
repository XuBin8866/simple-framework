package com.xxbb.framework.simplespring.util;

import java.util.Collection;
import java.util.Map;

/**
 * 对值的验证，如非空判断等
 * @author xxbb
 */
public class ValidationUtil {
    /**
     * 集合非空判断
     * @param collection 集合
     * @return 结果
     */
    public static boolean isEmpty(Collection<?> collection){
        return null==collection||collection.isEmpty();
    }

    /**
     * 字符串类型非空判断
     * @param str 字符串
     * @return 结果
     */
    public static boolean isEmpty(String str){
        return null==str||"".equals(str);
    }

    /**
     * 数组类型的非空判断
     * @param objects 数组
     * @return 结果
     */
    public static boolean isEmpty(Object[] objects){
        return null==objects||objects.length==0;
    }
    public static boolean isEmpty(Map<?,?> map){
        return null==map||map.isEmpty();
    }
}
