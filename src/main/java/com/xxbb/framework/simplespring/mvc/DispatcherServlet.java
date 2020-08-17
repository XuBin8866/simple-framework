package com.xxbb.framework.simplespring.mvc;


import com.xxbb.framework.simplemybatis.pool.MyDataSourceImpl;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactoryBuilder;
import com.xxbb.framework.simplespring.aop.AspectWeaver;
import com.xxbb.framework.simplespring.core.BeanContainer;
import com.xxbb.framework.simplespring.inject.DependencyInject;
import com.xxbb.framework.simplespring.mvc.processor.RequestProcessor;
import com.xxbb.framework.simplespring.mvc.processor.impl.ControllerRequestProcessor;
import com.xxbb.framework.simplespring.mvc.processor.impl.JspRequestProcessor;
import com.xxbb.framework.simplespring.mvc.processor.impl.PreRequestProcessor;
import com.xxbb.framework.simplespring.mvc.processor.impl.StaticResourceRequestProcessor;
import com.xxbb.framework.simplespring.util.LogUtil;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author xxbb
 */
@WebServlet(name="DispatcherServlet" ,urlPatterns="/*",
initParams ={@WebInitParam(name="contextConfigLocation",value = "application.properties")},
loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    /**
     * 保存application.properties配置文件中的内容
     */
    private Properties contextCofig = new Properties();
    /**
     * 请求处理器
     */
    List<RequestProcessor> PROCESSORS=new ArrayList<>();
    /**
     * 日志
     */
    private final Logger log= LogUtil.getLogger();

    @Override
    public void init(ServletConfig servletConfig) {
        //读取配置文件
        doLoadConfig(servletConfig.getInitParameter("contextConfigLocation"));
        //初始化容器
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans(contextCofig.getProperty("scanPackage"));
        System.out.println();
        //AOP织入
        new AspectWeaver().doAspectOrientedProgramming();
        //初始化简易mybatis框架，往IoC容器中注入SqlSessionFactory对象
        new SqlSessionFactoryBuilder().build(servletConfig.getInitParameter("contextConfigLocation"));
        //依赖注入
        new DependencyInject().doDependencyInject();


        //初始化请求处理器责任链
        PROCESSORS.add(new PreRequestProcessor());
        PROCESSORS.add(new StaticResourceRequestProcessor(servletConfig.getServletContext()));
        PROCESSORS.add(new JspRequestProcessor(servletConfig.getServletContext()));
        PROCESSORS.add(new ControllerRequestProcessor());
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        //1.创建责任链对象实例
        RequestProcessorChain requestProcessorChain = new RequestProcessorChain(PROCESSORS.iterator(), req, resp);
        //2.通过责任链模式来一次调用请求处理器对请求进行处理
        requestProcessorChain.doRequestProcessorChain();
        //3.对处理结果进行渲染
        requestProcessorChain.doRender();

    }

    /**
     * 加载配置文件
     *
     * @param contextConfigLocation properties配置文件
     */
    private void doLoadConfig(String contextConfigLocation) {
        //直接通过类路径找到框架主配置文件的路径
        //并将配置文件内容读取到properties对象中
        log.info("Loading configLocation--->path: "+contextConfigLocation);
        InputStream is=null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            contextCofig.load(is);
        } catch (IOException e) {
            LogUtil.getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destroy() {
        //注销驱动
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver driver = null;
        while (drivers.hasMoreElements()) {
            try {
                driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
                LogUtil.getLogger().debug("deregister success : driver {}" ,driver.toString());
            } catch (SQLException e) {
                LogUtil.getLogger().error("deregister failed : driver {}" ,driver.toString());
            }
        }
        //关闭连接池
        MyDataSourceImpl.getInstance().close();
    }

}
