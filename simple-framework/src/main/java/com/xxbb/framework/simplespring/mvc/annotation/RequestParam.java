package com.xxbb.framework.simplespring.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求参数
 * @author xxbb
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    /**
     * q请求参数
     * @return 参数值
     */
    String value() default "";

    /**
     * 请求参数是否必须
     * @return 是否必须
     */
    boolean required() default true;
}
