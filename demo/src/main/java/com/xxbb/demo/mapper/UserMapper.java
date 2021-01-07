
package com.xxbb.demo.mapper;


import com.xxbb.demo.domain.Time;
import com.xxbb.demo.domain.User;

import java.util.List;


/**
 * UserMapper.java
 *
 * @author PLF
 * @date 2019年3月6日
 */
public interface UserMapper {

    /**
     * 获取单个user
     *
     * @param id id
     * @return user
     */
    User getUser(String id);

    /**
     * 获取所有用户
     *
     * @return users
     */
    List<User> getAll();

    /**
     * 更新用户
     *
     * @param name name
     * @param id   id
     * @return 受影响的行数
     */
    int updateUser(String name, Integer id);

    /**
     * 添加
     *
     * @param id       id
     * @param username username
     * @param password password
     * @param ifFreeze if_freeze
     * @return 受影响的行数
     */
    int insertUser(Integer id, String username, String password, Integer ifFreeze);

    /**
     * 删除用户
     *
     * @param id id
     * @return 受影响的行数
     */
    int deleteUser(Integer id);

    /**
     *
     * @param id
     * @return
     */
    Time get(Integer id);
}
