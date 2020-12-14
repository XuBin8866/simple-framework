package com.xxbb.framework.simplemybatis.utils;

import java.lang.reflect.Method;

/**
 * 反射调用类的set，get方法
 *
 * @author xxbb
 */
public class ReflectUtils {
    /**
     * 调用Object对应属性的get方法
     *
     * @param object    类对象
     * @param fieldName 与类对象属性对应的数据库字段名
     * @return 类对象的属性
     */
    public static Object invokeGet(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        Method method;
        Object res;
        try {
            method = clazz.getDeclaredMethod("get" + StringUtils.firstCharToUpperCase(fieldName));
            res = method.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException("[" + Thread.currentThread().getName() + "]" +
                    "com.xxbb.smybatis.utils.ReflectUtils" + "--->" +
                    e.getMessage());
        }
        return res;
    }

    /**
     * 调用Object对应属性的get方法
     *
     * @param obj        类对象
     * @param columnName 与类对象属性对应的数据库字段名
     * @param value      属性值
     */
    public static void invokeSet(Object obj, String columnName, Object value) {
        Class<?> clazz = obj.getClass();
        Method method;
        try {
            method = clazz.getDeclaredMethod("set" + StringUtils.columnNameToMethodName(columnName), value.getClass());

            method.invoke(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("[" + Thread.currentThread().getName() + "]" +
                    "com.xxbb.smybatis.utils.ReflectUtils" + "--->" + "value=" +
                    value.getClass());
        }

    }
}
