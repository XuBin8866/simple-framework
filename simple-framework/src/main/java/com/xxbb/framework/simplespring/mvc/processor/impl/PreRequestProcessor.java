package com.xxbb.framework.simplespring.mvc.processor.impl;


import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.processor.RequestProcessor;
import com.xxbb.framework.simplespring.util.LogUtil;
import org.slf4j.Logger;


/**
 * 请求预处理，包括编码以及路径处理
 * @author xxbb
 */
public class PreRequestProcessor implements RequestProcessor {
    private final Logger log= LogUtil.getLogger();
    private static final String REQUEST_PATH_END="/";
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {

        //设置请求编码
        requestProcessorChain.getReq().setCharacterEncoding("UTF-8");

        //将请求路径末尾的/删除，为后续匹配Controller请求路径做准备
        // （一般Controller的处理路径是/aaa/bbb，所以如果传入的路径结尾是/aaa/bbb/，就需要处理成/aaa/bbb）
        String requestPath=requestProcessorChain.getRequestPath();
        log.info("original requestPath: {}",requestPath);
        if(requestPath.length()>1&&requestPath.endsWith(REQUEST_PATH_END)){
            requestProcessorChain.setRequestPath(requestPath.substring(0,requestPath.length()-1));
        }
        log.info("preprocess requestMethod: {}, requestPath: {}", requestProcessorChain.getRequestMethod(), requestProcessorChain.getRequestPath());
        return true;
    }
}
