package com.xxbb.demo.dao;

import com.xxbb.demo.domain.User;
import com.xxbb.demo.mapper.UserMapper;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplespring.core.annotation.Repository;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

import java.util.List;

/**
 * @author xxbb
 */
@Repository
public class UserDao {
    @Autowired
    SqlSessionFactory sqlSessionFactory;


    public List<User> getAll(){
        SqlSession sqlSession=sqlSessionFactory.openSession();
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        return userMapper.getAll();

    }
}
