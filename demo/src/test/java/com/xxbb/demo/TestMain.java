package com.xxbb.demo;

import com.xxbb.demo.mapper.UserMapper;
import com.xxbb.demo.domain.User;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactoryBuilder;
import com.xxbb.framework.simplespring.core.BeanContainer;
import org.junit.Test;

/**
 * @author xxbb
 */

public class TestMain {

    @Test
    public void IocTest(){
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        BeanContainer beanContainer=BeanContainer.getInstance();
        beanContainer.addBean(factory.getClass(),factory);
        System.out.println(beanContainer.getClassesBySuper(SqlSessionFactory.class));
    }
    @Test
    public void classTest(){
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        System.out.println(factory.getClass());
    }
    @Test
    public void sqlTest(){
        //读取mapper文件的CRUD测试
        sqlTestMain();
        //自动生成增删改语句并执行的测试
//        testUpdate();
//        testInsert();
//        testDelete();
    }
    public void testUpdate() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(1);
        u.setUsername("xxbb");
        System.out.println("testUpdate：" + session.update(u));
    }

    public void testInsert() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        u.setUsername("zzxx");
        u.setPassword("123456");
        u.setIfFreeze(1);
        System.out.println("testInsert：" + session.insert(u));
    }

    public void testDelete() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        System.out.println("testDelete:" + session.delete(u));
    }

    public void sqlTestMain() {
        //构建sql工厂
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        System.out.println("testMain.select：" + userMapper.getAll());
        System.out.println("testMain.update：" + userMapper.updateUser("xxbb", 1));
        System.out.println("testMain.insert:" + userMapper.insertUser(24, "zzxx", "123456", 1));
        System.out.println("testMain.delete: " + userMapper.deleteUser(24));

    }
}
