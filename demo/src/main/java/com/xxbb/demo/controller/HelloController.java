package com.xxbb.demo.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;

import com.xxbb.demo.domain.Account;
import com.xxbb.demo.domain.User;
import com.xxbb.demo.mapper.UserMapper;
import com.xxbb.demo.service.HelloService;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplespring.core.annotation.Controller;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;
import com.xxbb.framework.simplespring.mvc.annotation.RequestMapping;
import com.xxbb.framework.simplespring.mvc.annotation.RequestParam;
import com.xxbb.framework.simplespring.mvc.annotation.ResponseBody;
import com.xxbb.framework.simplespring.mvc.type.ModelAndView;
import com.xxbb.framework.simplespring.mvc.type.RequestMethod;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 目前只支持modelAndView和String方式跳转
 * @author xxbb
 */
@Controller
public class HelloController {
    @Autowired
    HelloService helloService;
    @Autowired
    Account account;

    @Autowired
    SqlSessionFactory sqlSessionFactory;


    @RequestMapping(value="/export",method = RequestMethod.GET)
    @ResponseBody
    public Object exportUser(@RequestParam("response") HttpServletResponse response){
        System.out.println(response);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> list = mapper.getAll();
        System.out.println(list.toString());
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("用户表","用户"),
                User .class, list);
        return list;
    }

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
    public Object showResultRender(@RequestParam("username") String name,
                                   @RequestParam("password") String password,
    @RequestParam("dio") String dio){
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
