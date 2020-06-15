package com.xxbb.framework.simplemybatis.session;

/**
 * sqlSession会话工厂
 *
 * @author xxbb
 */
public interface SqlSessionFactory {
    /**
     * 创建session对象，开始数据库会话
     *
     * @return 数据库会话对象
     */
    SqlSession openSession();
}
