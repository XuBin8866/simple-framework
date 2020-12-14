package com.xxbb.framework.simplespring.mvc.render.impl;


import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;

/**
 *  默认渲染器
 * @author xxbb
 */
public class DefaultResultRender implements ResultRender {
    @Override
    public void render(RequestProcessorChain requestProcessorChain){
        requestProcessorChain.getResp().setStatus(requestProcessorChain.getResponseCode());
    }
}
