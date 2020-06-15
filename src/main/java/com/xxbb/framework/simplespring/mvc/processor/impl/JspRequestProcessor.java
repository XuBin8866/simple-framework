package com.xxbb.framework.simplespring.mvc.processor.impl;



import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.processor.RequestProcessor;
import com.xxbb.framework.simplespring.util.LogUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * @author xxbb
 */
public class JspRequestProcessor implements RequestProcessor {
    /**
     * jsp请求的RequestDispatcher的名称
     */
    private static final String JSP_SERVLET="jsp";
    /**
     * jsp请求资源的前缀
     */
    private static final String JSP_RESOURCE_PREFIX="/";
    /**
     * jsp的RequestDispatcher,处理jsp资源
     */
    private RequestDispatcher jspServlet;

    public JspRequestProcessor(ServletContext servletContext) {
        jspServlet=servletContext.getNamedDispatcher(JSP_SERVLET);
        if(null==jspServlet) {
            throw new RuntimeException("There is no jsp servlet");
        }
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        String requestPath=requestProcessorChain.getRequestPath();
        if(isJspResource(requestPath)){
            LogUtil.getLogger().info("jsp request path: {}",requestPath);
            jspServlet.forward(requestProcessorChain.getReq(),requestProcessorChain.getResp());
            return false;
        }
        return true;
    }

    /**
     * 是否请求的是jsp资源
     */
    private boolean isJspResource(String url) {
        //idea的tomcat启动时自动会跳转index.jsp页面，此时请求路径为/，
        //我们需要放行这次请求，否则会报错
        //org.apache.jasper.JasperException: java.lang.StringIndexOutOfBoundsException: String index out of range: 0
        return url.startsWith(JSP_RESOURCE_PREFIX)&&url.length()>1&&url.contains("."+JSP_SERVLET);
    }
}
