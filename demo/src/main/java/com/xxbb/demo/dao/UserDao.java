package com.xxbb.demo.dao;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.xxbb.demo.domain.User;
import com.xxbb.demo.domain.UserExcelCoreModel;
import com.xxbb.demo.mapper.UserMapper;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplespring.core.annotation.Component;
import com.xxbb.framework.simplespring.core.annotation.Repository;
import com.xxbb.framework.simplespring.inject.annotation.Autowired;

import java.util.ArrayList;
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
    public List<UserExcelCoreModel> getUserExcelCoreModels(){
        return assembleUserExcelCoreModels(getAll());
    }
    private List<UserExcelCoreModel> assembleUserExcelCoreModels(List<User> users){
        List<UserExcelCoreModel> models=new ArrayList<>();
        for(User u:users){
            models.add(assembleUserExcelCoreModel(u));
        }
        return models;
    }
    
    private UserExcelCoreModel assembleUserExcelCoreModel(User user) {
        UserExcelCoreModel model = new UserExcelCoreModel();
        model.setId(user.getId());
        model.setUsername(user.getUsername());
        model.setPassword(user.getPassword());
        model.setIfFreeze(user.getIfFreeze());
        return  model;
    }
    
}
