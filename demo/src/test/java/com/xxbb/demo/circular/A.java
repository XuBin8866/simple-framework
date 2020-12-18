package com.xxbb.demo.circular;

import com.xxbb.framework.simplespring.core.annotation.Repository;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

/**
 * @author xubin
 * @date 2020/12/18 15:38
 */
@Repository
public class A {

    @Autowired
    B b;
    public A(){
        System.out.println("A 进行初始化");
    }
    public void showField(){
        System.out.println("A show Field:"+b);
        b.showField();
    }
}
