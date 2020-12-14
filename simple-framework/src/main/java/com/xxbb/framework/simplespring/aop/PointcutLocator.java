package com.xxbb.framework.simplespring.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * 解析Aspect表达式并且定位被织入的目标
 * @author xxbb
 */
public class PointcutLocator {
    /**
     * Pointcut解析器，直接给它赋值上AspectJ的所有表达式，以便支持对众多表达式的解析
     * 目前只使用到EXECUTION和WITHIN
     */
    private PointcutParser pointcutParser=PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(
            PointcutParser.getAllSupportedPointcutPrimitives()
    );

    /**
     * 表达式解析器
     */
    private PointcutExpression pointcutExpression;

    public PointcutLocator(String expression){
        this.pointcutExpression=pointcutParser.parsePointcutExpression(expression);
    }

    /**
     * 判断传入的Class对象是否是Aspect类的目标代理类，即匹配Pointcut表达式（初筛）
     * @param targetClass 目标类
     * @return 是否匹配
     */
    public boolean roughMatches(Class<?> targetClass){
        //只能校验within语法，其他语法的表达式直接返回true
        //这意味着如果一个类有多个切面表达式准备织入，如果within表达式，则可以判断是否符合该类
        //如果是execution表达式，粗筛无法判断，即使表达式对应的不是该目标类，也会将该切面存入该类的切面数组中
        //需要依赖下一步精筛来排除那些不属于该类的切面方
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * 判断传入的Method对象是否是Aspect的目标代理方法，即匹配Pointcut表达式（精确筛选）
     * @param method 方法
     * @return 是否匹配具体方法
     */
    public boolean accurateMatches(Method method){
        ShadowMatch shadowMatch=pointcutExpression.matchesMethodExecution(method);
        return shadowMatch.alwaysMatches();
    }
}
