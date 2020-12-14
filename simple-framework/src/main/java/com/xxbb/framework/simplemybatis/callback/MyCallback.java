package com.xxbb.framework.simplemybatis.callback;

import com.xxbb.framework.simplemybatis.bean.ColumnInfo;
import com.xxbb.framework.simplemybatis.bean.TableInfo;
import com.xxbb.framework.simplemybatis.executor.statement.StatementHandler;
import com.xxbb.framework.simplemybatis.mapping.MappedStatement;

import javax.security.auth.callback.Callback;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * 回调接口
 * 使用默认实现的方式，方便在调用该接口时不必重写全部的方法
 *
 * @author xxbb
 */
public interface MyCallback extends Callback {
    /**
     * 编写具体的数据库操作，区分查询和更新操作
     *
     * @param statementHandler  执行sql操作的对象
     * @param preparedStatement 完全处理好的sql语句对象
     * @return List集合或者受影响的行数
     */
    default Object doExecutor(StatementHandler statementHandler, PreparedStatement preparedStatement) {
        return null;
    }

    /**
     * 在SqlSession对象中，通过传入po类对象来执行的增删改方法中，使用它来构建Sql语句
     *
     * @param fields          该类的属性
     * @param tableInfo       该类对应的表
     * @param primaryKeys     该表的主键
     * @param sql             空的sql语句
     * @param mappedStatement 封装sql信息的对象
     * @param params          该po类携带的参数
     */
    default void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
    }
}
