package com.xxbb.framework.simplespring.mvc.processor.impl;


import com.xxbb.framework.simplespring.core.BeanContainer;
import com.xxbb.framework.simplespring.core.annotation.Controller;
import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.annotation.RequestMapping;
import com.xxbb.framework.simplespring.mvc.annotation.RequestParam;
import com.xxbb.framework.simplespring.mvc.annotation.ResponseBody;
import com.xxbb.framework.simplespring.mvc.processor.RequestProcessor;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;
import com.xxbb.framework.simplespring.mvc.render.impl.JsonResultRender;
import com.xxbb.framework.simplespring.mvc.render.impl.ResourceNotFoundResultRender;
import com.xxbb.framework.simplespring.mvc.render.impl.ViewResultRender;
import com.xxbb.framework.simplespring.mvc.type.ControllerMethod;
import com.xxbb.framework.simplespring.mvc.type.RequestPathInfo;
import com.xxbb.framework.simplespring.util.ConverterUtil;
import com.xxbb.framework.simplespring.util.LogUtil;
import com.xxbb.framework.simplespring.util.ValidationUtil;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xxbb
 */
public class ControllerRequestProcessor implements RequestProcessor {
    /**
     * IoC容器
     */
    private BeanContainer beanContainer;
    /**
     * 请求和对应Controller方法的映射集合
     */
    private Map<RequestPathInfo, ControllerMethod> requestPathInfoControllerMethodMap=new ConcurrentHashMap<>();

    public Map<RequestPathInfo, ControllerMethod> getRequestPathInfoControllerMethodMap() {
        return requestPathInfoControllerMethodMap;
    }

    /**
     * 日志
     */
    private final Logger log= LogUtil.getLogger();

    public ControllerRequestProcessor() {
        this.beanContainer = BeanContainer.getInstance();
        //获取所有被Controller标记的类
        Set<Class<?>> requestMappingSet=beanContainer.getClassesByAnnotation(Controller.class);
        initRequestPathInfoControllerMethodMap(requestMappingSet);
    }

    private void initRequestPathInfoControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if(ValidationUtil.isEmpty(requestMappingSet)){
            log.warn("requestMappingSet is Empty");
            return;
        }
        //1.遍历所有被@Controller标记的类，获取类上面@RequestMapping注解的属性值作为一级路径
        for(Class<?> requestMappingClass:requestMappingSet){
            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);
            String basePath="";
            if(requestMapping!=null){
                basePath = requestMapping.value();
            }
            if(requestMapping!=null&&!basePath.startsWith("/")){
                basePath="/"+basePath;
            }

            //2.遍历类里面所有被@RequestMapping标记的方法，获取方法上的属性值作为二级路径
            Method[] methods = requestMappingClass.getDeclaredMethods();
            if(ValidationUtil.isEmpty(methods)){
                log.warn("{} has no declared method",requestMappingClass.getSimpleName());
                return;
            }
            for(Method method:methods){
                if(method.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMappingMethod=method.getAnnotation(RequestMapping.class);
                    String methodPath=requestMappingMethod.value();
                    if(!methodPath.startsWith("/")){
                        methodPath="/"+methodPath;
                    }
                    //这里的url可能出现一级路径为斜杠的情况导致url拼接之后出现双斜杠，需要进行处理
                    String url=basePath+methodPath;
                    url=url.replaceAll("//+","/");
                    //3.解析方法里被@RequestParam标记的参数
                    //  获取该注解的属性值，作为参数名
                    //  获取被标记参数的数据类型，建立参数名到参数类型的映射
                    Map<String,Class<?>> methodParamMap=new HashMap<>(10);
                    Parameter[] parameters=method.getParameters();
                    if(!ValidationUtil.isEmpty(parameters)){
                        for(Parameter parameter:parameters){
                            RequestParam param=parameter.getAnnotation(RequestParam.class);
                            //在java8之前，编译的class文件的方法参数的并不会保存参数名，而是使用arg0，arg1...argN的形式
                            //在java8之后允许编译的class文件带方法参数名，但这个功能是默认关闭的，需要在java文件编译时添加参数 -parameters
                            //在IDEA和Eclipse中都可以设置
                            //所以这里默认要求controller的方法的参数都需要有@RequestParam注解来给参数命名
                            //即当参数未被该注解标注时需要抛出异常，避免之后发送请求而接收不到参数。
                            if(null==param){
                                //如果设置好了javac编译参数 -parameters后
                                // 可以直接使用parameter.getName()作为methodParamMap的键名，去除该异常
                                throw new RuntimeException("The parameter must has @RequestParam annotation for method:"+method);
                            }else{
                                methodParamMap.put(param.value(),parameter.getType());
                            }
                        }
                    }
                    //4.将获取到的信息封装成RequestPathInfo实例和ControllerMethod实例，放置到映射表里
                    //请求的方法类型get/post
                    String httpMethod=String.valueOf(requestMappingMethod.method());
                    RequestPathInfo requestPathInfo=new RequestPathInfo(httpMethod,url);
                    if(this.requestPathInfoControllerMethodMap.containsKey(requestPathInfo)){
                        log.warn("duplicate url:{} registration，current class {} method{} will override the former one",
                                requestPathInfo.getHttpPath(), requestMappingClass.getName(), method.getName());
                    }
                    ControllerMethod controllerMethod=new ControllerMethod(requestMappingClass,method,methodParamMap);
                    this.requestPathInfoControllerMethodMap.put(requestPathInfo,controllerMethod);
                    log.info("has set request mapping for: http_method: {}, url: {}, method: {}",httpMethod, url,method);

                }
            }
        }



    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) {
        //1.解析HttpServletRequest的请求方法，请求路径，获取对应的ControllerMethod实例
        String method=requestProcessorChain.getRequestMethod();
        String path=requestProcessorChain.getRequestPath();
        ControllerMethod controllerMethod=this.requestPathInfoControllerMethodMap.get(new RequestPathInfo(method,path));
        if(null==controllerMethod){
            log.error("can not found controllerMethod which path is{}, method is {}",path,method);
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(method,path));
            return false;
        }
        //2.解析请求参数，并传递给获取到的controllerMethod实例去执行
        Object result=invokeControllerMethod(controllerMethod,requestProcessorChain.getReq());
        //3.根据解析结果，选择对应的render进行渲染
        setResultRender(result,controllerMethod,requestProcessorChain);
        return true;
    }

    /**
     * 根据不同情况设置不同的渲染器
     * @param result 结果
     * @param controllerMethod controllerMethod
     * @param requestProcessorChain requestProcessorChain
     */
    private void setResultRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain requestProcessorChain) {
        if(null==result){
            log.warn("controller method's return result is null");
            return;
        }
        ResultRender resultRender;
        boolean isJson=controllerMethod.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if(isJson){
            resultRender=new JsonResultRender(result);
        }else{
            resultRender=new ViewResultRender(result);
        }
        requestProcessorChain.setResultRender(resultRender);
    }

    /**
     * 通过反射执行controller中对应的方法
     * @param controllerMethod controller方法实例对象
     * @param req request请求
     * @return 结果
     */
    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest req) {
        //1.从请求里获取GET或者POST的参数名及其对应的值
        Map<String,String> requestParamMap=new HashMap<>(5);
        //GET，POST方法的请求参数获取方式
        Map<String,String[]> parameterMap=req.getParameterMap();
        for(Map.Entry<String,String[]> parameter:parameterMap.entrySet()){
            if(!ValidationUtil.isEmpty(parameter.getValue())){
                //只支持一个参数对应一个值的形式
                requestParamMap.put(parameter.getKey(),parameter.getValue()[0]);
            }
        }
        //2.根据获取到的请求参数名及其对应的值，
        // 以及controllerMethod里面的参数和类型的映射关系，去实例化出方法对应的参数
        List<Object> methodParams=new ArrayList<>();
        Map<String ,Class<?>> methodParamMap=controllerMethod.getMethodParameters();
        for(String paramName:methodParamMap.keySet()){
            Class<?> type=methodParamMap.get(paramName);
            String requestValue=requestParamMap.get(paramName);
            //只支持String 以及基础类型char,int,short,byte,double,long,float,boolean,及它们的包装类型
            Object value;
            if(null==requestValue){
                //将请求里的参数值转成适配于参数类型的空值
                value= ConverterUtil.primitiveNull(type);
            }else{
                value=ConverterUtil.convert(type,requestValue);
            }
            methodParams.add(value);
        }
        //3.执行Controller里面对应的方法并返回结果
        Object controller=beanContainer.getBean(controllerMethod.getControllerClass());
        Method invokeMethod=controllerMethod.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        try {
            if(methodParams.size()==0){
                result=invokeMethod.invoke(controller);
            }else{
                result=invokeMethod.invoke(controller,methodParams.toArray());
            }
        } catch (IllegalAccessException e) {
            log.error("Access error {}",e.getMessage());
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            //如果是调用异常的话，需要通过e.getTargetException()
            // 去获取执行方法抛出的异常
            log.error("controller's method invoke error,error method: {}, controller: {}",invokeMethod.getName(),controller.getClass().getSimpleName());
            throw new RuntimeException(e.getTargetException());
        }
        return result;

    }
}
