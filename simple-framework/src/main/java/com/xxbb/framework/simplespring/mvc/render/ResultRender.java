package com.xxbb.framework.simplespring.mvc.render;


import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;

/**
 * 渲染请求结果
 * @author xxbb
 */
public interface ResultRender {
    /**
     * 执行渲染
     * @param requestProcessorChain 结果渲染器
     * @throws Exception 异常
     */
     void render(RequestProcessorChain requestProcessorChain) throws Exception;
}
