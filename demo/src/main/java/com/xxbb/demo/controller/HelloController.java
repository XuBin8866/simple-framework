package com.xxbb.demo.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;

import com.xxbb.demo.dao.UserDao;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Autowired
    UserDao userDao;
    //没有对应void的的结果处理器，直接使用response会和
    @RequestMapping(value="/export",method = RequestMethod.GET)
    public void exportUser(@RequestParam("response") HttpServletResponse response) throws IOException {
        System.out.println(response);
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称(设置下载文件的默认名称)
        response.setHeader("Content-Disposition", "attachment;filename="+
                new String("测试样例".getBytes("gb2312"), "iso8859-1")+".xls");

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> list = mapper.getAll();
        System.out.println(list.toString());
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("用户表","用户"),
                User .class, list);
        workbook.write(response.getOutputStream());
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
    @RequestMapping(value = "/param",method = RequestMethod.GET)
    public String getHttpServletObject(@RequestParam("map")  Map<String,String[]> requestParamMap,
                                       @RequestParam("request") HttpServletRequest request,
                                       @RequestParam("response") HttpServletResponse response)
    {
        System.out.println("requestParamMap:"+requestParamMap);
        System.out.println("request:"+request);
        System.out.println("response:"+request);
        return "success";
    }

}
