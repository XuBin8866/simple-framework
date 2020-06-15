package com.xxbb.framework.simplespring.mvc.processor;


import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;

/**
 * 请求执行器
 * @author xxbb
 */
public interface RequestProcessor {
    /**
     * 处理请求
     * @param requestProcessorChain
     * @return
     * @throws Exception
     */
    boolean process(RequestProcessorChain requestProcessorChain) throws Exception;
}
