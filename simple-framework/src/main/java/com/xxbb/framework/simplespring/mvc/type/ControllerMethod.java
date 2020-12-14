package com.xxbb.framework.simplespring.mvc.type;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 待执行的Controller及其方法实例和参数的映射
 * @author xxbb
 */
public class ControllerMethod {
    /**
     * controller对应的Class对象
     */
    private Class<?> controllerClass;
    /**
     * 执行的controller方法实例
     */
    private Method invokeMethod;
    /**
     * 方法参数名称以及其对应的参数类型
     */
    private Map<String,Class<?>> methodParameters;

    public ControllerMethod() {
    }

    public ControllerMethod(Class<?> controllerClass, Method invokeMethod, Map<String, Class<?>> methodParameters) {
        this.controllerClass = controllerClass;
        this.invokeMethod = invokeMethod;
        this.methodParameters = methodParameters;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public Method getInvokeMethod() {
        return invokeMethod;
    }

    public void setInvokeMethod(Method invokeMethod) {
        this.invokeMethod = invokeMethod;
    }

    public Map<String, Class<?>> getMethodParameters() {
        return methodParameters;
    }

    public void setMethodParameters(Map<String, Class<?>> methodParameters) {
        this.methodParameters = methodParameters;
    }

    @Override
    public String toString() {
        return "ControllerMethod{" +
                "controllerClass=" + controllerClass +
                ", invokeMethod=" + invokeMethod +
                ", methodParameters=" + methodParameters +
                '}';
    }
}
