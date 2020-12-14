package com.xxbb.framework.simplespring.mvc.annotation;



import com.xxbb.framework.simplespring.mvc.type.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示Controller的方法与请求路径和请求方法的关系映射
 * @author xxbb
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping  {
    /**
     * 请求路径
     */
    String value() default "";
    /**
     * 请求方法
     */
    RequestMethod method() default RequestMethod.GET;
}
