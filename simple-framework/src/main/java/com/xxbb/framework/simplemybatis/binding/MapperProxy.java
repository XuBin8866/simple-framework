package com.xxbb.framework.simplemybatis.binding;



import com.xxbb.framework.simplemybatis.constants.Constant;
import com.xxbb.framework.simplemybatis.mapping.MappedStatement;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.utils.LogUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 代理类
 *
 * @author xxbb
 */
public class MapperProxy<T> implements InvocationHandler {


    private final SqlSession sqlSession;

    private final Class<T> mapperInterface;


    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            return this.execute(method, args);
        } catch (Exception e) {
            LogUtils.getLogger().error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Object execute(Method method, Object[] args) {
        //sql语句的唯一id
        String statementId = this.mapperInterface.getName() + "." + method.getName();
        //获取sql信息
        MappedStatement mappedStatement = this.sqlSession.getConfiguration().getMappedStatement(statementId);
        Object result = null;
        //根据mappedStatement提供的方法选择对应的CRUD方法
        //查询
        if (Constant.SqlType.SELECT.value().equals(mappedStatement.getSqlType())) {
            Class<?> returnType = method.getReturnType();
            //返回值为集合类型
            if (Collection.class.isAssignableFrom(returnType)) {
                result = sqlSession.selectList(statementId, args);
            } else {
                result = sqlSession.selectOne(statementId, args);
            }

        }
        //更新
        if (Constant.SqlType.UPDATE.value().equals(mappedStatement.getSqlType())) {
            result = sqlSession.update(statementId, args);
        }
        //插入
        if (Constant.SqlType.INSERT.value().equals(mappedStatement.getSqlType())) {
            result = sqlSession.insert(statementId, args);
        }
        //删除
        if (Constant.SqlType.DELETE.value().equals(mappedStatement.getSqlType())) {
            result = sqlSession.delete(statementId, args);
        }


        return result;
    }
}
