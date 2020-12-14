package com.xxbb.framework.simplemybatis.executor;


import com.xxbb.framework.simplemybatis.callback.MyCallback;
import com.xxbb.framework.simplemybatis.executor.parameter.DefaultParameterHandler;
import com.xxbb.framework.simplemybatis.executor.parameter.ParameterHandler;
import com.xxbb.framework.simplemybatis.executor.resultset.DefaultResultSetHandler;
import com.xxbb.framework.simplemybatis.executor.resultset.ResultSetHandler;
import com.xxbb.framework.simplemybatis.executor.statement.SimpleStatementHandler;
import com.xxbb.framework.simplemybatis.executor.statement.StatementHandler;
import com.xxbb.framework.simplemybatis.mapping.MappedStatement;
import com.xxbb.framework.simplemybatis.pool.MyDataSource;
import com.xxbb.framework.simplemybatis.session.Configuration;
import com.xxbb.framework.simplemybatis.utils.LogUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * mysql的执行器，操作数据库
 *
 * @author xxbb
 */
public class SimpleExecutor implements Executor {

    /**
     * 连接池对象
     */
    private final MyDataSource dataSource;
    /**
     * 连接对象
     */
    private final Connection connection;
    /**
     * 是否自动提交，默认是false;
     */
    private boolean ifAutoCommit=false;
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LogUtils.getLogger();
    public SimpleExecutor(Configuration configuration) {
        dataSource = configuration.getDataSource();
        try {
            connection=dataSource.getConnection();
            if(connection==null){
                throw  new RuntimeException("连接获取失败，请重试");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 抽取出执行sql操作前的获取连接，预处理语句，赋值参数的操作成一个模板
     *
     * @param mappedStatement  封装sql信息的对象
     * @param parameter        参数
     * @param executorCallback 会调接口
     * @return List结果集或受影响的行数
     */
    private Object executeTemplate(MappedStatement mappedStatement, Object parameter, MyCallback executorCallback)  {
        try {
            connection.setAutoCommit(ifAutoCommit);
            //实例化StatementHandler对象
            StatementHandler statementHandler = new SimpleStatementHandler(mappedStatement);
            //对mapperStatement中的sql语句进行处理，去除头尾空格，将#{}替换成?,封装成preparedStatement对象
            PreparedStatement preparedStatement = statementHandler.prepared(connection);
            //给占位符?的参数赋值
            ParameterHandler parameterHandler = new DefaultParameterHandler(parameter);
            parameterHandler.setParameters(preparedStatement);
            LOGGER.debug("preparedStatement:" + preparedStatement);
            return executorCallback.doExecutor(statementHandler, preparedStatement);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行查询方法，使用了模板方法设计模式
     *
     * @param mappedStatement sql信息对象
     * @param parameter       参数
     * @param <E>             泛型
     * @return 结果集
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> doQuery(MappedStatement mappedStatement, Object parameter) {
        return (List<E>) executeTemplate(mappedStatement, parameter, new MyCallback() {
            /**
             * 编写具体的数据库操作，区分查询和更新操作
             *
             * @param statementHandler  执行sql操作的对象
             * @param preparedStatement 完全处理好的sql语句对象
             * @return List集合或者受影响的行数
             */
            @Override
            public Object doExecutor(StatementHandler statementHandler, PreparedStatement preparedStatement) {
                //执行sql语句
                try {
                    //获取结果集
                    ResultSet resultSet = statementHandler.query(preparedStatement);
                    //处理结果集，封装成泛型List对象返回
                    //这里可以获取mappedStatement对象是因为他是效果上的final，
                    //验证，将mappedStatement赋一个新的值：mappedStatement=new MappedStatement(); 会报错——>lambda 表达式中使用的变量应为 final 或 effectively final
                    ResultSetHandler resultSetHandler = new DefaultResultSetHandler(mappedStatement);
                    //封装到目标resultType的List集合中
                    return resultSetHandler.handlerResultSet(resultSet);

                } catch (SQLException throwable) {
                    LOGGER.error(throwable.getMessage());
                    throw new RuntimeException(throwable);
                }
            }
        });
    }

    /**
     * 执行增删改方法，使用了模板方法设计模式
     *
     * @param mappedStatement sql信息对象
     * @param parameter       参数
     * @return 受影响的行数
     */
    @Override
    public int doUpdate(MappedStatement mappedStatement, Object parameter) {
        Integer res = (Integer) executeTemplate(mappedStatement, parameter, new MyCallback() {
            /**
             * 编写具体的数据库操作，区分查询和更新操作
             *
             * @param statementHandler  执行sql操作的对象
             * @param preparedStatement 完全处理好的sql语句对象
             * @return List集合或者受影响的行数
             */
            @Override
            public Object doExecutor(StatementHandler statementHandler, PreparedStatement preparedStatement) {
                try {
                    return statementHandler.update(preparedStatement);
                } catch (SQLException throwable) {
                    LOGGER.error(throwable.getMessage());
                    throw new RuntimeException(throwable);
                }
            }
        });
        if (null != res) {
            return res;
        } else {
            LOGGER.error("更新数据出现错误，受影响的行数返回空值");
            throw new RuntimeException("更新数据出现错误，受影响的行数返回空值");
        }
    }

    /**
     * 设置自动提交，默认是false，不自动提交
     * @param ifAutoCommit 是否自动提交
     */
    @Override
    public void setAutoCommit(boolean ifAutoCommit){
        this.ifAutoCommit=ifAutoCommit;
    }

    /**
     * 事务回滚
     */
    @Override
    public void rollback(){
        try {
            connection.rollback();
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage());
            throw new RuntimeException(throwables.getMessage());
        }
    }

    /**
     * 提交事务
     */
    @Override
    public void commit(){
        try {
            connection.commit();
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage());
            throw new RuntimeException(throwables.getMessage());
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage());
            throw new RuntimeException(throwables.getMessage());
        }
    }
}
