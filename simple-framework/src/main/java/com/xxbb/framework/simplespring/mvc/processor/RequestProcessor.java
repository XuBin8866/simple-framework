package com.xxbb.framework.simplespring.mvc.processor;


import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;

/**
 * 请求执行器
 * @author xxbb
 */
public interface RequestProcessor {
    /**
     * 处理请求
     * @param requestProcessorChain 请求处理器
     * @return 是否能够处理该请求
     * @throws Exception 异常
     */
    boolean process(RequestProcessorChain requestProcessorChain) throws Exception;
}
