package com.xxbb.demo.service;

import com.xxbb.demo.dao.HelloDao;
import com.xxbb.demo.dao.UserDao;
import com.xxbb.framework.simplespring.core.annotation.Service;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

/**
 * @author xxbb
 */
@Service
public class HelloService {
    @Autowired
    HelloDao helloDao;
    public void hello(){
        System.out.println("hello! This is service");
        helloDao.hello();
    }
}
