package com.xxbb.framework.simplespring.mvc.render.impl;



import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

/**
 * 内部异常渲染渲染器
 * @author xxbb
 */
public class InternalErrorResultRender implements ResultRender {
    private String errorMsg;
    public InternalErrorResultRender(String errorMsg){
        this.errorMsg=errorMsg;
    }
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        requestProcessorChain.getResp().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,errorMsg);
    }
}
