package com.xxbb.demo.circular;

import com.xxbb.framework.simplespring.core.annotation.Repository;
import com.xxbb.framework.simplespring.core.annotation.Service;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

/**
 * @author xubin
 * @date 2020/12/18 15:39
 */
@Repository
public class B {
    @Autowired
    C c;
    public B(){
        System.out.println("B 进行初始化");
    }
    public void showField(){
        System.out.println("B show Field:"+c);
        c.showField();
    }

}
