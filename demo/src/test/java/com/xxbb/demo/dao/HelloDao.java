package com.xxbb.demo.dao;

import com.xxbb.framework.simplespring.core.annotation.Repository;

/**
 * @author xubin
 * @date 2020/12/18 15:03
 */
@Repository
public class HelloDao {
    public void hello(){
        System.out.println("hello! This is dao");
    }
}
