package com.xxbb.framework.simplemybatis.binding;



import com.xxbb.framework.simplemybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * mapper注册类
 *
 * @author xxbb
 */
public class MapperRegistry {
    /**
     * 存储代理mapper工厂
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    /**
     * 注册代理工厂
     *
     * @param type 被代理的接口类
     * @param <T>  接口类泛型
     */
    public <T> void addMapper(Class<T> type) {
        this.knownMappers.put(type, new MapperProxyFactory<>(type));
    }

    /**
     * 获取代理工厂实例
     *
     * @param type       被代理接口类的类型，作为key去代理工厂的map中寻找对应的工厂
     * @param sqlSession 当前的sqlSession对象
     * @param <T>        接口类型
     * @return 接口的代理
     */
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) this.knownMappers.get(type);
        return mapperProxyFactory.newInstance(sqlSession);
    }
}
