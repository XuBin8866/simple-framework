package com.xxbb.framework.simplespring.aop;


import com.xxbb.framework.simplespring.aop.aspect.AspectInfo;
import com.xxbb.framework.simplespring.util.LogUtil;
import com.xxbb.framework.simplespring.util.ValidationUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

/**
 * 向被代理对象的方法中添加横切逻辑
 * @author xxbb
 */
public class AspectListExecutor implements MethodInterceptor {
    /**
     * 被代理类
     */
    private Class<?> targetClass;
    /**
     * 排好序的切面列表
     */
    private List<AspectInfo> sortedAspectInfoList;

    public AspectListExecutor(Class<?> targetClass, List<AspectInfo> aspectInfoList) {
        this.targetClass = targetClass;
        this.sortedAspectInfoList = sortAspectInfoList(aspectInfoList);
    }



    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnValue=null;
        //对切面方法进行精确筛选
        collectAccurateMatchedAspectList(method);
        //没有切面对方法进行增强的情况
        if(ValidationUtil.isEmpty(sortedAspectInfoList)){
            LogUtil.getLogger().warn("There is no Aspect for this bean");
            returnValue=methodProxy.invokeSuper(o,args);
            return returnValue;
        }
        //1.按照order的顺序升序执行完所有的Aspect的before方法
        invokeBeforeAdvices(method,args);

        try {
            //2.执行被代理类的方法
            returnValue=methodProxy.invokeSuper(o, args);
            //3.如果代理方法返回正常，则安装order的顺序降序执行完所有的Aspect的afterRunning方法
            //目前后置通知不对返回结果returnValue进行处理
            invokeAfterReturningAdvices(method,args,returnValue);
        }catch (Exception e){
            //4.如果代理方法返回异常，则安装order的顺序降序执行完所有的Aspect的afterThrowing方法
            invokeAfterThrowingAdvices(method,args,e);
        }finally{
            //最终通知
            invokeAfterAdvices(method,args);
        }

        return returnValue;
    }

    /**
     * 根据当前被代理的目标方法精确筛选切面方法
     * @param method 被代理的目标方法
     */
    private void collectAccurateMatchedAspectList(Method method) {
        if(ValidationUtil.isEmpty(sortedAspectInfoList)){
            return;
        }
        //将不需要的切面方法去除
        sortedAspectInfoList.removeIf(aspectInfo -> !aspectInfo.getPointcutLocator().accurateMatches(method));

    }


    /**
     * 代理方法执行完成后，降序执行所有Aspect的after方法
     * @param method  方法
     * @param args 方法参数
     * @throws Throwable 异常
     */
    private void invokeAfterAdvices(Method method, Object[] args) throws Throwable {
        for(int i=sortedAspectInfoList.size()-1;i>=0;i--){
            sortedAspectInfoList.get(i).getAspectObject().after(targetClass,method,args);
        }
    }

    /**
     * 如果代理方法返回异常，则安装order的顺序降序执行完所有的Aspect的afterThrowing方法
     * @param method 方法
     * @param args 方法参数
     * @param e 异常传入值
     */
    private void invokeAfterThrowingAdvices(Method method, Object[] args, Exception e) throws Throwable {
        for(int i=sortedAspectInfoList.size()-1;i>=0;i--){
            sortedAspectInfoList.get(i).getAspectObject().afterThrowing(targetClass,method,args,e);
        }
    }

    /**
     * 如果代理方法返回正常，则安装order的顺序降序执行完所有的Aspect的afterRunning方法
     * @param method 方法
     * @param args 方法参数
     * @param returnValue 传入返回值
     * @return 返回值
     */
    private Object invokeAfterReturningAdvices(Method method, Object[] args, Object returnValue) throws Throwable {
        // 虽然result每次都被刷新，但再没有特殊处理的情况下他一直都是被代理方法的返回值
        // 也就是每次的返回值在默认情况下就是被反射执行的方法的返回值
        Object result=null;
        for(int i=sortedAspectInfoList.size()-1;i>=0;i--){
            result=sortedAspectInfoList.get(i).getAspectObject().afterReturning(targetClass,method,args,returnValue);

        }
        return result;
    }

    /**
     * 按照order的顺序升序执行完所有的Aspect的before方法
     * @param method 方法
     * @param args 方法参数
     */
    private void invokeBeforeAdvices(Method method, Object[] args) throws Throwable {
        for(AspectInfo aspectInfo:sortedAspectInfoList){
            aspectInfo.getAspectObject().before(targetClass,method,args);
        }
    }

    private List<AspectInfo> sortAspectInfoList(List<AspectInfo> aspectInfoList) {
        //按Order从小到大的顺序排序
        aspectInfoList.sort(Comparator.comparingInt(AspectInfo::getOrderIndex));
        return aspectInfoList;
    }

    public List<AspectInfo> getSortedAspectInfoList() {
        return sortedAspectInfoList;
    }
}
