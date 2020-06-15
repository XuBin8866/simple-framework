package com.xxbb.framework.simplespring.mvc.render.impl;

import com.google.gson.Gson;
import com.xxbb.framework.simplespring.mvc.RequestProcessorChain;
import com.xxbb.framework.simplespring.mvc.render.ResultRender;


import java.io.PrintWriter;

/**
 * Json渲染器
 * @author xxbb
 */
public class JsonResultRender implements ResultRender {
    private Object jsonData;
    public JsonResultRender(Object jsonData) {
        this.jsonData=jsonData;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        //设置响应头
        requestProcessorChain.getResp().setContentType("application/json");
        requestProcessorChain.getResp().setCharacterEncoding("UTF-8");
        //响应流写入经过gson格式化之后的处理结果
        try(PrintWriter writer=requestProcessorChain.getResp().getWriter()){
            Gson gson=new Gson();
            writer.write(gson.toJson(jsonData));
            writer.flush();
        }
    }
}
