package com.xxbb.framework.simplemybatis.session;



import com.xxbb.framework.simplemybatis.bean.TableInfo;
import com.xxbb.framework.simplemybatis.binding.MapperRegistry;
import com.xxbb.framework.simplemybatis.mapping.MappedStatement;
import com.xxbb.framework.simplemybatis.pool.MyDataSource;
import com.xxbb.framework.simplemybatis.pool.MyDataSourceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * mybatis框架核心配置类
 *
 * @author xxbb
 */
public class Configuration {
    /**
     * 数据库连接配置
     */
    public static Properties pros = new Properties();
    /**
     * mapper代理注册器
     */
    protected final MapperRegistry mapperRegistry = new MapperRegistry();
    /**
     * mapper中的sql信息
     */
    protected final Map<String, MappedStatement> mappedStatementMap = new HashMap<>();
    /**
     * 数据库连接池对象
     */
    protected MyDataSource dataSource = MyDataSourceImpl.getInstance();
    /**
     * 所连数据库所有表的信息与类的映射
     */
    protected Map<Class<?>, TableInfo> classToTableInfoMap = new HashMap<>();


    /**
     * 注册mapper接口类
     *
     * @param type mapper接口类
     * @param <T>  泛型
     */
    public <T> void addMapper(Class<T> type) {
        this.mapperRegistry.addMapper(type);
    }

    /**
     * 获取mapper代理对象
     *
     * @param type       mapper接口
     * @param sqlSession 当前使用的sqlSession对象
     * @param <T>        泛型
     * @return 代理对象
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return this.mapperRegistry.getMapper(type, sqlSession);
    }

    /**
     * 将sql信息对象存储进map中
     *
     * @param statement       标识一个封装sql信息对象的唯一id
     * @param mappedStatement 封装了sql信息的对象
     */
    public void addMappedStatement(String statement, MappedStatement mappedStatement) {
        this.mappedStatementMap.put(statement, mappedStatement);
    }

    /**
     * 根据sql唯一id从map中获取封装sql信息的对象
     *
     * @param statement 标识一个封装sql信息对象的唯一id
     * @return 封装了sql信息的对象
     */
    public MappedStatement getMappedStatement(String statement) {
        return this.mappedStatementMap.get(statement);
    }

    public MyDataSource getDataSource() {
        return dataSource;
    }

    /**
     * 获取配置文件属性(属性不存再则返回空字符串"")
     *
     * @param key 键名
     * @return 键值
     */
    public static String getProperty(String key) {
        return getProperty(key, "");
    }

    /**
     * 获取配置文件属性(可指定属性不存在时的返回值)
     *
     * @param key          键名
     * @param defaultValue 属性不存在时的返回值
     * @return 键值
     */
    public static String getProperty(String key, String defaultValue) {
        return pros.containsKey(key) ? pros.getProperty(key) : defaultValue;
    }

    /**
     * 获取类表关系
     *
     * @return 所连数据库所有表的信息与类的映射
     */
    public Map<Class<?>, TableInfo> getClassToTableInfoMap() {
        return classToTableInfoMap;
    }

    /**
     * 设置类表关系集合
     *
     * @param classToTableInfoMap 所连数据库所有表的信息与类的映射
     */
    public void setClassToTableInfoMap(Map<Class<?>, TableInfo> classToTableInfoMap) {
        this.classToTableInfoMap = classToTableInfoMap;
    }
}
