package com.xxbb.demo.dao;

import com.xxbb.demo.mapper.UserMapper;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplespring.core.annotation.Repository;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

/**
 * @author xxbb
 */
@Repository
public class UserDao {
    @Autowired
    SqlSessionFactory sqlSessionFactory;


    public void select(){
        SqlSession sqlSession=sqlSessionFactory.openSession();
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        System.out.println(userMapper.getAll());

    }
}
