package com.xxbb.demo.service;

import com.xxbb.demo.dao.HelloDao;
import com.xxbb.demo.dao.UserDao;
import com.xxbb.framework.simplespring.core.annotation.Service;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

/**
 * @author xubin
 * @date 2020/12/18 15:23
 */
@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public void select(){
        System.out.println("userService start select!");
        userDao.select();
    }
}
