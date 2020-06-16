package com.xxbb.demo.controller;

import com.xxbb.demo.domain.User;
import com.xxbb.demo.service.HelloService;
import com.xxbb.framework.simplespring.core.annotation.Controller;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;
import com.xxbb.framework.simplespring.mvc.annotation.RequestMapping;
import com.xxbb.framework.simplespring.mvc.annotation.RequestParam;
import com.xxbb.framework.simplespring.mvc.annotation.ResponseBody;
import com.xxbb.framework.simplespring.mvc.type.ModelAndView;
import com.xxbb.framework.simplespring.mvc.type.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 目前只支持modelAndView和String方式跳转
 * @author xxbb
 */
@Controller
public class HelloController {
    @Autowired
    HelloService helloService;
    @RequestMapping("/")
    public String index(){
        return "index.jsp";
    }
    /**
     *
     * @return string
     */
    @RequestMapping("/hello")
    public String hello(){
        helloService.hello();
        return "success.jsp";
    }

    @RequestMapping(value = "/show",method = RequestMethod.GET)
    @ResponseBody
    public Object showResultRender(@RequestParam("name") String name){
        System.out.println("show请求"+name);
        User user=new User();
        user.setUsername("xxbb");
        return user;
    }

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    public ModelAndView showView(){
        System.out.println("showView请求");
        User user=new User();
        user.setUsername("xxbb");
        Map<String ,Object> resultMap=new HashMap<>();
        resultMap.put("user",user);
        ModelAndView mv=new ModelAndView();
        mv.setView("result.jsp").addViewData("result",resultMap).
                addViewData("user",user);
        return mv;
    }
}