package com.xxbb.framework.simplespring.mvc.type;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储处理完后的结果数据，以及显示该数据的视图
 * @author xxbb
 */
public class ModelAndView {
    /**
     * 页面所在的路径
     */
    private String view;
    /**
     * 页面的data数据
     */
    private Map<String ,Object> model=new HashMap<>();



    /**
     * 设置视图
     * @param view 视图
     * @return 当前视图
     */
    public ModelAndView setView(String view){
        this.view=view;
        return this;
    }

    /**
     * 返回当前视图是为了方便追加数据
     * 如：modelAndView.setView("demo.jsp").addViewData("aaa", "bbb");
     * @param attributeName 键名
     * @param attributeValue 键值
     * @return 当前视图
     */
    public ModelAndView addViewData(String attributeName, Object attributeValue){
        model.put(attributeName,attributeValue);
        return this;
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
