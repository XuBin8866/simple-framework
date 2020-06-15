package com.xxbb.framework.simplespring.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author xxbb
 */
public class ProxyCreator {
    /**
     * 创建动态代理对象并返回
     * @param targetClass 被代理的目标对象
     * @param methodInterceptor 代理的回调接口
     * @return 代理对象
     */
    public static Object createProxy(Class<?> targetClass, MethodInterceptor methodInterceptor){
      return Enhancer.create(targetClass,methodInterceptor);
    }
}
