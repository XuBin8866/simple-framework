package com.xxbb.demo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.xxbb.demo.circular.C;
import com.xxbb.demo.domain.Time;
import com.xxbb.demo.mapper.UserMapper;
import com.xxbb.demo.domain.User;
import com.xxbb.demo.circular.A;
import com.xxbb.demo.circular.B;
import com.xxbb.demo.service.HelloService;
import com.xxbb.demo.service.UserService;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactoryBuilder;
import com.xxbb.framework.simplespring.aop.AspectWeaver;
import com.xxbb.framework.simplespring.core.BeanContainer;
import com.xxbb.framework.simplespring.inject.DependencyInject;
import com.xxbb.framework.simplespring.util.LogUtil;
import org.junit.Test;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author xxbb
 */

public class TestMain {

    @Test
    public void test(){
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        UserMapper mapper=session.getMapper(UserMapper.class);
        Time t=new Time();
        t.setTime(new Timestamp(new Date().getTime()));
//        session.insert(t);
//        session.commit();
//        session.close();
        Time time = mapper.get(3);
        System.out.println(new Gson().toJson(time));
    }
    /**
     * IoC和DI需要组合使用，如果使用了AOP需要在DI前进行织入,
     * 同时得益于IoC和DI的操作是串行进行的，不会出现循环依赖的情况
     * ORM框架可直接使用，在创建使用ORM的入口对象SqlSessionFactory时也会将该对象存入IoC容器
     * 但如果Bean对象依赖SqlSessionFactory,则需要将ORM框架的初始化放在DI之前
     */
    @Test
    public void baseFunctionTest() {
        //初始化IoC容器
        BeanContainer beanContainer = BeanContainer.getInstance();
        //指定需要扫描的包，加载bean对象进入IoC容器
        beanContainer.loadBeans("com.xxbb.demo");
        //如果使用了AOP的功能需要先于DI进行织入
        new AspectWeaver().doAspectOrientedProgramming();
        //ORM框架初始化不受以上流程顺序影响，但如果有Bean依赖factory对象则需要先于DI初始化
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        //依赖注入
        new DependencyInject().doDependencyInject();

        //测试基础功能
        HelloService helloService = (HelloService) beanContainer.getBean(HelloService.class);
        helloService.hello();

        //测试ORM框架功能
        System.out.println(factory.openSession());
        UserService userService = (UserService) beanContainer.getBean(UserService.class);
        userService.select();
    }

    /**
     * 循环依赖测试
     */
    @Test
    public void circularDependencyTest() {
        //初始化IoC容器
        BeanContainer beanContainer = BeanContainer.getInstance();
        //指定需要扫描的包，加载bean对象进入IoC容器
        beanContainer.loadBeans("com.xxbb.demo.circular");
        //依赖注入
        new DependencyInject().doDependencyInject();

        //测试循环依赖
        A a = (A) beanContainer.getBean(A.class);
        a.showField();
        B b = (B) beanContainer.getBean(B.class);
        b.showField();
        C c = (C) beanContainer.getBean(C.class);
        c.showField();
    }

    /**
     * 日志测试
     */
    @Test
    public void logTest() {
        Logger logger = LogUtil.getLogger();
        logger.info("log test");
    }

    @Test
    public void IocTest() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.addBean(factory.getClass(), factory);
        System.out.println(beanContainer.getClassesBySuper(SqlSessionFactory.class));
    }

    @Test
    public void classTest() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        System.out.println(factory.getClass());
    }

    @Test
    public void sqlTest() {
        //读取mapper文件的CRUD测试
        sqlTestMain();
        //自动生成增删改语句并执行的测试
//        testUpdate();
//        testInsert();
//        testDelete();
    }
    @Test
    public void testUpdate() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(1);
        u.setUsername("xxbb");
        try {
            System.out.println("testUpdate：" + session.update(u));
            session.commit();

        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
    }
    @Test
    public void testInsert() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        u.setUsername("zzxx");
        u.setPassword("123456");
        u.setIfFreeze(1);
        try {
            System.out.println("testInsert：" + session.insert(u));
            session.commit();

        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
    }
    @Test
    public void testDelete() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        try {
            System.out.println("testDelete:" + session.delete(u));
            session.commit();

        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
    }
    @Test
    public void sqlTestMain() {
        //构建sql工厂
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        try {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            System.out.println("testMain.select：" + userMapper.getAll());
            System.out.println("testMain.update：" + userMapper.updateUser("xxbb", 1));
            System.out.println("testMain.insert:" + userMapper.insertUser(24, "zzxx", "123456", 1));
            System.out.println("testMain.delete: " + userMapper.deleteUser(24));
            session.commit();
        } catch (Exception e) {
            LogUtil.getLogger().error("sql test error:{}", e.getMessage());
            session.rollback();
        } finally {
            session.close();
        }
    }
}
