package com.xxbb.demo.test;

import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactoryBuilder;
import com.xxbb.framework.simplemybatis.session.defaults.DefaultSqlSession;
import com.xxbb.framework.simplemybatis.session.defaults.DefaultSqlSessionFactory;
import com.xxbb.framework.simplespring.core.BeanContainer;

/**
 * @author xxbb
 */
public class ConnectionPoolTest {
    public static void main(String[] args){
        //测试连接池连接
        BeanContainer container=BeanContainer.getInstance();
        new SqlSessionFactoryBuilder().build("application.properties");
        DefaultSqlSessionFactory sessionFactory = (DefaultSqlSessionFactory)container.getBean(DefaultSqlSessionFactory.class);
        System.out.println(sessionFactory);
        for(int i=0;i<20;i++){
            SqlSession sqlSession = sessionFactory.openSession();
        }
        System.out.println(sessionFactory.openSession());
    }
}
