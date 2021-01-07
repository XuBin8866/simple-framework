package com.xxbb.demo.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * User.java
 *
 * @author PLF
 * @date 2019年3月6日
 */
public class User implements Serializable {
    /**
     * the id
     */
    @Excel(name="ID")
    private Integer id;

    /**
     * the name
     */
    @Excel(name="用户名")
    private String username;
    @Excel(name="密码")
    private String password;
    @Excel(name="状态",replace = "正常_1,冻结_2")
    private Integer ifFreeze;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIfFreeze() {
        return ifFreeze;
    }

    public void setIfFreeze(Integer ifFreeze) {
        this.ifFreeze = ifFreeze;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ifFreeze=" + ifFreeze +
                '}';
    }
}
