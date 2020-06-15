package com.xxbb.framework.simplespring.util;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xxbb
 */

public class ClassUtil {
    private final static String FILE_PROTOCOL="file";
    private final static String SUFFIX =".class";
    private final static Logger LOGGER=LogUtil.getLogger();

    /**
     * 反射设置bean对象的值
     * @param targetBean bean对象
     * @param field 属性
     * @param fieldValue 值
     * @param accessible 是否允许设置私有属性的值
     */
    public static void setField(Object targetBean , Field field, Object fieldValue,boolean accessible){
        field.setAccessible(accessible);
        try {
            field.set(targetBean,fieldValue);
        } catch (IllegalAccessException e) {
            LOGGER.error("setField error:"+e);
            throw new RuntimeException(e);
        }
    }
    /**
     * 实例化class对象
     * @param clazz bean对象的class
     * @param accessible 是否实例化私有构造方法的对象
     * @param <T> 泛型
     * @return bean
     */
    public static <T>T newInstance(Class<T> clazz,Boolean accessible){
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(accessible);
            return declaredConstructor.newInstance();
        } catch (Exception e) {
            LOGGER.error("bean newInstance error:"+e);
            throw new RuntimeException(e);
        }
    }
    /**
     * 加载类资源
     * @param packageName 需加载类所在的包
     * @return 类资源的set
     */
    public static Set<Class<?>> extractPackageClass(String packageName){

        //获取类加载器
        ClassLoader classLoader=getClassLoader();
        //通过类加载器获取到加载的资源,replace是字符或字符串匹配替换，replaceAll是字符或正则替换
        URL url = classLoader.getResource(packageName.replaceAll("\\.", "/"));
        if(null==url){
            LOGGER.warn("unable to retrieve anything from package: "+packageName);
            return null;
        }
        //依据不同的资源类型，采用不同的方式获取资源的集合
        Set<Class<?>> classSet=null;
            //判断文件协议
        if(FILE_PROTOCOL.equalsIgnoreCase(url.getProtocol())){
            classSet=new HashSet<>();
            File packageDirectory=new File(url.getFile());
            extractClassFile(classSet,packageDirectory,packageName);
        }
        return classSet;
    }

    /**
     * 获取目标package中的所有class文件，包括子package中的文件
     * @param classSet 装载目标类的集合
     * @param fileSource 文件或目录
     * @param packageName 扫描包名
     */
    private static void extractClassFile(Set<Class<?>> classSet, File fileSource, String packageName) {
        //如果传入文件非文件夹
        if(!fileSource.isDirectory()){
            return;
        }
        //如果是文件夹，调用listFile方法获取文件夹下内容
        File[] files=fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                //提取出文件夹
                if(file.isDirectory()){
                    return true;
                }else{
                    //非文件夹如果以.class结尾就直接载入set中
                    String absoluteFilePath=file.getAbsolutePath();
                    if(absoluteFilePath.endsWith(SUFFIX)){
                        addToClassSet(absoluteFilePath);
                    }
                }
                return false;
            }
            //获取类class对象载入set中
            private void addToClassSet(String absoluteFilePath) {
                absoluteFilePath=absoluteFilePath.replace(File.separator,".");
                String className=absoluteFilePath.substring(absoluteFilePath.indexOf(packageName))
                        .replace(SUFFIX,"");
                try {
                    Class<?> clazz=Class.forName(className);
                    classSet.add(clazz);
                } catch (ClassNotFoundException exception) {
                    LOGGER.error("Load class error:"+exception);
                    throw new RuntimeException(exception);
                }

            }
        });
        //递归文件夹
        //foreach遍历即使files为空也会进行，然后报空指针错误，所以这里提前判断
        if(files!=null){
            for(File file:files){
                extractClassFile(classSet,file,packageName);
            }
        }
    }

    /**
     * 获取当前线程的类加载器
     * @return 类加载器
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }


}
