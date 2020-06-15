package com.xxbb.framework.simplespring.mvc.render.impl;



import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;
import com.xxbb.framework.simplespring.mvc.type.ModelAndView;
import com.xxbb.framework.simplespring.util.LogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 页面渲染器
 * @author xxbb
 */
public class ViewResultRender implements ResultRender {
    public static final String VIEW_PATH = "/";
    private ModelAndView modeAndView;

    /**
     * 对传入参数进行处理
     * @param object 参数
     */
    public ViewResultRender(Object object) {
        if(object instanceof ModelAndView){
            //1.如果入参类型是ModelAndView，则直接赋值给成员变量
            this.modeAndView= (ModelAndView) object;
        }else if( object instanceof String){
            //2.如果入参类型是String，则为视图，需要包装后才赋值给成员变量
            this.modeAndView=new ModelAndView().setView((String) object);
        }else{
            //3.针对其他情况，则直接抛出异常
            LogUtil.getLogger().error("illegal request result type");
            throw new RuntimeException("illegal request result type");
        }
    }
    /**
     * 将请求处理结果按照视图路径转发至对应视图进行展示
     * @param requestProcessorChain 请求处理器
     * @throws Exception 异常
     */
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        HttpServletRequest request=requestProcessorChain.getReq();
        HttpServletResponse response=requestProcessorChain.getResp();
        String path=modeAndView.getView();
        Map<String,Object> model=modeAndView.getModel();
        if(!model.isEmpty()){
            for(Map.Entry<String,Object> entry:model.entrySet()){
                request.setAttribute(entry.getKey(),entry.getValue());
            }
        }
        //跳转到jsp页面
        String directPath=(VIEW_PATH+path).replace("//","/");
        request.getRequestDispatcher(directPath).forward(request,response);
    }
}
