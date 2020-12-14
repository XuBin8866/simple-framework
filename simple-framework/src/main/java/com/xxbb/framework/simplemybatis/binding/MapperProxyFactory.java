package com.xxbb.framework.simplemybatis.binding;



import com.xxbb.framework.simplemybatis.session.SqlSession;

import java.lang.reflect.Proxy;

/**
 * mapper代理对象工厂
 *
 * @author xxbb
 */
public class MapperProxyFactory<T> {

    /**
     * 被代理的接口
     */
    private final Class<T> mapperInterface;


    /**
     * 构造方法
     *
     * @param mapperInterface 需要被代理的接口类
     */
    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 创建mapper代理对象
     *
     * @param sqlSession 当前的sqlSession对象
     * @return 代理实现类
     */
    public T newInstance(SqlSession sqlSession) {
        MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, this.mapperInterface);
        return newInstance(mapperProxy);
    }

    /**
     * 根据mapper代理的类型返回对应的代理实例
     *
     * @param mapperProxy 代理对象
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(this.mapperInterface.getClassLoader(),
                new Class[]{this.mapperInterface},
                mapperProxy);
    }
}
