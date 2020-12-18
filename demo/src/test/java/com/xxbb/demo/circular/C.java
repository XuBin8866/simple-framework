package com.xxbb.demo.circular;

import com.xxbb.framework.simplespring.core.annotation.Repository;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

/**
 * @author xubin
 * @date 2020/12/18 15:52
 */
@Repository
public class C {
    @Autowired
    A a;
    public C(){
        System.out.println("C 进行初始化");
    }
    public void showField(){
        System.out.println("C show Field:"+a);
        a.showField();
    }
}
