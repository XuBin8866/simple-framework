package com.xxbb.framework.simplespring.aop;


import com.xxbb.framework.simplespring.aop.annotation.Aspect;
import com.xxbb.framework.simplespring.aop.annotation.Order;
import com.xxbb.framework.simplespring.aop.aspect.AspectInfo;
import com.xxbb.framework.simplespring.aop.aspect.DefaultAspect;
import com.xxbb.framework.simplespring.core.BeanContainer;
import com.xxbb.framework.simplespring.util.LogUtil;
import com.xxbb.framework.simplespring.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author xxbb
 */
public class AspectWeaver {
    private BeanContainer beanContainer;
    public AspectWeaver(){
        this.beanContainer=BeanContainer.getInstance();
    }
    public void doAspectOrientedProgramming(){
        //1.获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        //没有切面类的情况
        if(ValidationUtil.isEmpty(aspectSet)){
            LogUtil.getLogger().warn("There is no aspect in  bean container");
            return;
        }
        //2.拼装AspectInfoList
        List<AspectInfo> aspectInfoList=packAspectInfoList(aspectSet);

        //3.遍历容器里的类
        Set<Class<?>> classSet = beanContainer.getClasses();
        for(Class<?> targetClass:classSet){
            //排除被Aspect注解的类本身
            if(targetClass.isAnnotationPresent(Aspect.class)){
                continue;
            }
            //4.粗筛符合条件的Aspect
            List<AspectInfo> roughMatchedAspectList=collectRoughMatchedAspectListForSpecificClass(aspectInfoList,targetClass);
            //5.尝试进行Aspect织入
            wrapIfNecessary(roughMatchedAspectList,targetClass);
        }

    }


    /**
     * 将切面织入目标类
     * @param roughMatchedAspectList 粗筛的切面类
     * @param targetClass 目标对象
     */
    private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
        if(ValidationUtil.isEmpty(roughMatchedAspectList)){
            return;
        }
        //创建目标类的动态代理对象，将切面方法进行织入
        AspectListExecutor aspectListExecutor=new AspectListExecutor(targetClass,roughMatchedAspectList);
        Object proxyBean=ProxyCreator.createProxy(targetClass,aspectListExecutor);
        LogUtil.getLogger().debug("wrapping for class: {}, proxyBean: {}",targetClass.getSimpleName(),proxyBean.getClass().getSimpleName());
        beanContainer.addBean(targetClass,proxyBean);
    }

    /**
     * 粗筛切面类
     * @param aspectInfoList 所有的切面类组成的数组
     * @param targetClass 需要进行织入操作的目标类
     * @return 目标类需要的切面类的数组
     */
    private List<AspectInfo> collectRoughMatchedAspectListForSpecificClass(List<AspectInfo> aspectInfoList, Class<?> targetClass) {
        List<AspectInfo> roughMatchedAspectList=new ArrayList<>();
        for(AspectInfo aspectInfo:aspectInfoList){
            if(aspectInfo.getPointcutLocator().roughMatches(targetClass)){
                roughMatchedAspectList.add(aspectInfo);
            }
        }
        return roughMatchedAspectList;
    }

    /**
     * 将所有的切面类的信息（Order、DefaultAspect、pointcut）封装成封装成一个Aspect数组
     * @param aspectSet 切面类set集合
     * @return 数组
     */
    private List<AspectInfo> packAspectInfoList(Set<Class<?>> aspectSet) {
        List<AspectInfo> aspectInfoList=new ArrayList<>();
        for(Class<?> aspectClass: aspectSet){
            if(verifyAspect(aspectClass)){
                //获取该切面类的数据
                Order orderTag=aspectClass.getAnnotation(Order.class);
                Aspect aspectTag=aspectClass.getAnnotation(Aspect.class);
                DefaultAspect defaultAspect= (DefaultAspect) beanContainer.getBean(aspectClass);
                //初始化表达式定位器
                PointcutLocator pointcutLocator=new PointcutLocator(aspectTag.pointcut());
                AspectInfo aspectInfo=new AspectInfo(orderTag.value(),defaultAspect,pointcutLocator);
                aspectInfoList.add(aspectInfo);
            }else{
                LogUtil.getLogger().error("packAspectInfoList error!");
                throw new RuntimeException("@Aspect and @Order must be added to the Aspect class, and Aspect class must extend from DefaultAspect");
            }

        }
        return aspectInfoList;
    }

    /**
     * 验证被@Aspect注解标注的类的合法性
     * 框架一定要遵循给Aspect类添加@Aspect和@Order标签的规范，同时必须继承自DefaultAspect类，也不能是@Aspect自己
     * @param aspectClass 被注解标注类的class对象
     * @return 是否合法
     */
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class)&&
                aspectClass.isAnnotationPresent(Order.class)&&
                DefaultAspect.class.isAssignableFrom(aspectClass);
    }
}
