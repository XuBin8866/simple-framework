package com.xxbb.framework.simplespring.core;


import com.xxbb.framework.simplespring.aop.annotation.Aspect;
import com.xxbb.framework.simplespring.core.annotation.Component;
import com.xxbb.framework.simplespring.core.annotation.Controller;
import com.xxbb.framework.simplespring.core.annotation.Repository;
import com.xxbb.framework.simplespring.core.annotation.Service;
import com.xxbb.framework.simplespring.util.ClassUtil;
import com.xxbb.framework.simplespring.util.LogUtil;
import com.xxbb.framework.simplespring.util.ValidationUtil;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xxbb
 */
public class BeanContainer {
    /**
     * bean容器
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();
    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION = Arrays.asList(
            Component.class, Controller.class, Service.class, Repository.class, Aspect.class);
    /**
     * 判断容器是否被加载
     */
    private boolean loaded = false;
    /**
     * 获取静态日志管理器
     */
    private static final Logger LOGGER= LogUtil.getLogger();

    /**
     * 通过内部枚举类来实现bean容器的单例，线程安全，不会被反射或者序列化破坏
     */
    private enum ContainerHolder {
        /**
         * 存储bean容器的枚举对象
         */
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * 获取单例容器对象
     *
     * @return 容器对象
     */
    public static BeanContainer getInstance() {

        return ContainerHolder.HOLDER.instance;
    }

    /**
     * 将bean对象加载进容器
     *
     * @param packageName 扫描包名
     */
    public void loadBeans(String packageName) {
        if (isLoaded()) {
            LOGGER.warn("BeanContainer has been loaded");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if (ValidationUtil.isEmpty(classSet)) {
            LOGGER.warn("Extract nothing from packageName:" + packageName);
            return;
        }
        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                //如果类对象中存在注解则加载进bean容器中
                if (clazz.isAnnotationPresent(annotation)) {
                    LOGGER.debug("load bean: "+clazz.getName());
                    beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
            }
        }
        loaded = true;
    }

    /**
     * 添加元素
     *
     * @param clazz 类
     * @param bean  对象
     * @return bean实例，没有则返回null
     */
    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    /**
     * 删除元素
     *
     * @param clazz 类
     * @return bean实例，没有则返回null
     */
    public Object removeBean(Class<?> clazz) {
        return beanMap.remove(clazz);
    }

    /**
     * 判断容器是否被加载过
     *
     * @return 结果
     */
    public boolean isLoaded() {
        return loaded;
    }

    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    /**
     * 获取class集合，即所有的键
     *
     * @return set集合
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 获取所有bean对象
     *
     * @return bean对象的set集合
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解获取class对象的的集合
     * @param annotation 注解
     * @return class对象集合
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        //获取beanMap的所有class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)) {
            LOGGER.warn("nothing in beanMap");
            return null;
        }
        //通过注解筛选需要的class对象，并添加到classSet里
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            if (clazz.isAnnotationPresent(annotation)) {
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

    /**
     * 通过接口或者父类获取到对应的class
     * @param classOrInterface 接口或父类
     * @return 集合
     */
    public Set<Class<?>> getClassesBySuper(Class<?> classOrInterface) {
        //获取beanMap的所有class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)) {
            LOGGER.warn("nothing in beanMap");
            return null;
        }
        //通过注解筛选需要的class对象，并添加到classSet里
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            //这里只想判断该类是否是作为classOrInterface的子类，而不想获取到自己本身
            if (classOrInterface.isAssignableFrom(clazz)&&!clazz.equals(classOrInterface)) {
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

    /**
     * 容器大小
     *
     * @return 大小
     */
    public int size() {
        return beanMap.size();
    }
}
