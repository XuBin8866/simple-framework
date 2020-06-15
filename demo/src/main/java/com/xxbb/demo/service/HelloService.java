package com.xxbb.demo.service;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.xxbb.demo.dao.UserDao;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplespring.core.annotation.Service;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

/**
 * @author xxbb
 */
@Service
public class HelloService {
    @Autowired
    UserDao userDao;
    public void hello(){
        System.out.println("hello! This is service");
        userDao.select();
    }
}
