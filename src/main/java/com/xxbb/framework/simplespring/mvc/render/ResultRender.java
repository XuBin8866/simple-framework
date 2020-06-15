package com.xxbb.framework.simplespring.mvc.render;


import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;

/**
 * 渲染请求结果
 * @author xxbb
 */
public interface ResultRender {
    /**
     * 执行渲染
     * @param requestProcessorChain
     * @throws Exception
     */
     void render(RequestProcessorChain requestProcessorChain) throws Exception;
}
