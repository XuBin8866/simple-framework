package com.xxbb.framework.simplemybatis.session.defaults;


import com.xxbb.framework.simplemybatis.bean.ColumnInfo;
import com.xxbb.framework.simplemybatis.bean.TableInfo;
import com.xxbb.framework.simplemybatis.callback.MyCallback;
import com.xxbb.framework.simplemybatis.executor.Executor;
import com.xxbb.framework.simplemybatis.executor.SimpleExecutor;
import com.xxbb.framework.simplemybatis.mapping.MappedStatement;
import com.xxbb.framework.simplemybatis.session.Configuration;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.utils.LogUtils;
import com.xxbb.framework.simplemybatis.utils.ReflectUtils;
import com.xxbb.framework.simplemybatis.utils.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的sql会话实现类
 *
 * @author xxbb
 */
public class DefaultSqlSession implements SqlSession {
    /**
     * 配置类
     */
    private final Configuration configuration;
    /**
     * 执行器
     */
    private final Executor executor;
    private final Logger logger = LogUtils.getLogger();

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        executor = new SimpleExecutor(configuration);
    }

    /**
     * 查询单条记录
     *
     * @param statement 封装好sql语句的唯一id
     * @param parameter 参数
     * @param <T>       泛型
     * @return 单条记录
     */
    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> results = this.selectList(statement, parameter);
        if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new RuntimeException("查询结果出错：查询出多条数据，结果集长度：" + results.size());
        }
    }

    /**
     * 查询多条数据
     *
     * @param statement 封装好sql语句的唯一id
     * @param parameter 参数
     * @param <E>       泛型
     * @return 封装好的List集合
     */
    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statement);
        return this.executor.doQuery(mappedStatement, parameter);
    }

    /**
     * 生成sql语句并执行的模板方法
     *
     * @param type     po类
     * @param callback 回调接口
     * @param <T>      泛型
     * @return 受影响的行数
     */
    private <T> Object generateSqlTemplate(T type, MyCallback callback) {
        //获取类对象
        Class<?> clazz = type.getClass();
        //获取类属性
        Field[] fields = clazz.getDeclaredFields();
        //获取表信息
        TableInfo tableInfo = this.configuration.getClassToTableInfoMap().get(clazz);
        //获取主键
        List<ColumnInfo> primaryKeys = tableInfo.getPrimaryKeys();
        //存储参数的集合
        List<Object> params = new ArrayList<>();
        //sql信息对象
        MappedStatement mappedStatement = new MappedStatement();
        //构建sql语句,利用主键作为修改的条件
        StringBuilder sql = new StringBuilder();
        //生成sql语句
        callback.generateSqlExecutor(fields, tableInfo, primaryKeys, sql, mappedStatement, params);
        //去除最后一个逗号
        sql.setCharAt(sql.length() - 1, ' ');
        //打印数据
        logger.debug(sql.toString());
        logger.debug("params:" + params);
        //封装到MappedStatement对象中
        mappedStatement.setSql(sql.toString());
        //要将List数组转化为Object[]数组，不然在ParameterHandler中会进行isArray()判断，返回false，
        //而不会去遍历参数，直接将这个list集合作为一个参数传入sql语句中，会报参数个数异常的错误
        return this.executor.doUpdate(mappedStatement, params.toArray());
    }

    /**
     * 更新,和以下的insert和delete方法存在冗余
     *
     * @param statement 封装好sql语句的唯一id
     * @param parameter 参数
     * @return 受影响的行数
     */
    @Override
    public int update(String statement, Object parameter) {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statement);
        return this.executor.doUpdate(mappedStatement, parameter);
    }

    /**
     * 传入po类对象自动生成sql语句并执行
     *
     * @param type 封装好数据的po类对象
     * @return 受影响的行数
     */
    @Override
    public <T> int update(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
            /**
             *
             * @param fields 该类的属性
             * @param tableInfo 该类对应的表
             * @param primaryKeys 该表的主键
             * @param sql 空的sql语句
             * @param mappedStatement 封装sql信息的对象
             * @param params 该po类携带的参数
             */
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("update ").append(tableInfo.getTableName()).append(" set ");
                for (Field field : fields) {
                    String fieldName = field.getName();
                    Object fieldValue = ReflectUtils.invokeGet(type, fieldName);
                    if (null != fieldValue) {
                        //判断该属性对应的列是否为非主键
                        for (ColumnInfo columnInfo : primaryKeys) {
                            if (!fieldName.equals(columnInfo.getName())) {
                                sql.append(StringUtils.humpToLine(fieldName)).append("=?,");
                                params.add(fieldValue);
                            }
                        }
                    }
                }
                //去除当前最后一个逗号
                sql.setCharAt(sql.length() - 1, ' ');
                //添加更新条件
                sql.append("where ");
                for (ColumnInfo columnInfo : primaryKeys) {
                    sql.append(StringUtils.humpToLine(columnInfo.getName())).append("=?,");
                    params.add(ReflectUtils.invokeGet(type, columnInfo.getName()));
                }

            }
        });

    }

    @Override
    public int insert(String statement, Object parameter) {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statement);
        return this.executor.doUpdate(mappedStatement, parameter);
    }

    /**
     * 传入po类对象自动生成sql语句并执行
     *
     * @param type 封装好数据的po类对象
     * @return 受影响的行数
     */
    @Override
    public <T> int insert(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
            /**
             *
             * @param fields 该类的属性
             * @param tableInfo 该类对应的表
             * @param primaryKeys 该表的主键
             * @param sql 空的sql语句
             * @param mappedStatement 封装sql信息的对象
             * @param params 该po类携带的参数
             */
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("insert into ").append(tableInfo.getTableName()).append("(");
                //遍历属性
                for (Field field : fields) {
                    String fieldName = field.getName();
                    Object fieldValue = ReflectUtils.invokeGet(type, fieldName);
                    if (null != fieldValue) {
                        sql.append(StringUtils.humpToLine(fieldName)).append(",");
                        params.add(fieldValue);
                    }
                }
                //替换当前最后一个逗号
                sql.setCharAt(sql.length() - 1, ')');
                sql.append(" values(");
                for (int i = 0; i < params.size(); i++) {
                    sql.append("?,");
                }
                //替换最后一个问号后的逗号
                sql.setCharAt(sql.length() - 1, ' ');
                //在模板方法中会替换最后一个逗号为空格
                sql.append("),");
            }
        });

    }

    @Override
    public int delete(String statement, Object parameter) {
        MappedStatement mappedStatement = this.configuration.getMappedStatement(statement);
        return this.executor.doUpdate(mappedStatement, parameter);
    }

    /**
     * 传入po类对象自动生成sql语句并执行
     *
     * @param type 封装好数据的po类对象
     * @return 受影响的行数
     */
    @Override
    public <T> int delete(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
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
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("delete from ").append(tableInfo.getTableName()).append(" where ");
                for (ColumnInfo columnInfo : primaryKeys) {
                    sql.append(StringUtils.humpToLine(columnInfo.getName())).append("=?,");
                    params.add(ReflectUtils.invokeGet(type, columnInfo.getName()));
                }
            }
        });
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return this.configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * 获取当前执行器
     *
     * @return 当前执行器
     */
    @Override
    public Executor getExecutor() {
        return executor;
    }
}
