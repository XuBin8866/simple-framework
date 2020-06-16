package com.xxbb.demo.aspect;


import com.xxbb.framework.simplespring.aop.annotation.Aspect;
import com.xxbb.framework.simplespring.aop.annotation.Order;
import com.xxbb.framework.simplespring.aop.aspect.DefaultAspect;
import com.xxbb.framework.simplespring.util.LogUtil;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * @author xxbb
 */
@Aspect(pointcut = "execution(* com.xxbb.demo.controller..*.*(..))")
@Order(1)
public class ControllerTimeCalculatorAspect2 extends DefaultAspect {
    private final Logger logger= LogUtil.getLogger();
    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        logger.debug("理论上第二个执行的前置方法");

    }
    @Override
    public void after(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        logger.info("执行最终方法2");
    }
}
