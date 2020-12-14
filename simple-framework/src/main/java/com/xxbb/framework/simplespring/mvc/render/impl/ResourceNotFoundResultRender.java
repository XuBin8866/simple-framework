package com.xxbb.framework.simplespring.mvc.render.impl;



import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

/**
 * 资源未找到异常
 * @author xxbb
 */
public class ResourceNotFoundResultRender implements ResultRender {

    private String httpMethod;
    private String httpPath;
    public ResourceNotFoundResultRender(String method, String path) {
        this.httpMethod = method;
        this.httpPath = path;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        requestProcessorChain.getResp().sendError(HttpServletResponse.SC_NOT_FOUND,
                "获取不到对应的请求资源：请求路径[" + httpPath + "]" + "请求方法[" + httpMethod + "]");
    }
}
