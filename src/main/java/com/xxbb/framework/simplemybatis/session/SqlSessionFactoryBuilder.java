package com.xxbb.framework.simplemybatis.session;



import com.xxbb.framework.simplemybatis.session.defaults.DefaultSqlSessionFactory;
import com.xxbb.framework.simplespring.core.BeanContainer;
import com.xxbb.framework.simplespring.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 构建者模式创建sqlSession工厂对象
 *
 * @author xxbb
 */
public class SqlSessionFactoryBuilder {
    /**
     * 读取配置文件构建SqlSessionFactory工厂
     * 将配置文件的解析成输入流的工作也放到该方法中
     *
     * @param fileName 配置文件名
     * @return 工厂对象
     */
    public SqlSessionFactory build(String fileName) {
        InputStream is = SqlSessionFactoryBuilder.class.getClassLoader().getResourceAsStream(fileName);
        return build(is);
    }

    public SqlSessionFactory build(InputStream inputStream) {
        try {
            Configuration.pros.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory factory=DefaultSqlSessionFactory.getInstance(new Configuration());
        //将sqlFactory注入到IoC容器中
        LogUtil.getLogger().debug("load bean: "+factory.getClass().getName());
        BeanContainer.getInstance().addBean(factory.getClass(),factory);
        return factory;
    }


}
