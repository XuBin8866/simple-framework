package com.xxbb.framework.simplemybatis.pool;


import com.xxbb.framework.simplemybatis.constants.Constant;
import com.xxbb.framework.simplemybatis.session.Configuration;
import com.xxbb.framework.simplemybatis.utils.LogUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * 数据库连接池
 *
 * @author xxbb
 */
public class MyDataSourceImpl implements MyDataSource {
    /**
     * 数据库连接属性
     */
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    /**
     * 初始连接数量
     */
    private static int initCount = 5;
    /**
     * 最小连接数量
     */
    private static int minCount = 5;
    /**
     * 最大连接数量
     */
    private static int maxCount = 20;
    /**
     * 已创建的连接数量
     */
    private static int createdCount;
    /**
     * 连接数增长步长
     */
    private static int increasingCount = 2;
    /**
     * 存储配置文件信息
     */
    private static Configuration configuration;
    /**
     * 存储连接的集合
     */
    private LinkedList<Connection> conns = new LinkedList<>();
    /**
     * 用于获取连接和归还连接的同步锁对象
     */
    private static final Object MONITOR = new Object();
    /**
     * 日志管理器
     */
    private static final Logger LOGGER = LogUtils.getLogger();

    //属性初始化
    static {
        DRIVER = Configuration.getProperty(Constant.JDBC_DRIVER);
        URL = Configuration.getProperty(Constant.JDBC_URL);
        USERNAME = Configuration.getProperty(Constant.JDBC_USERNAME);
        PASSWORD = Configuration.getProperty(Constant.JDBC_PASSWORD);

        try {
            initCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_INIT_COUNT));
        } catch (Exception e) {
            LOGGER.debug("initCount使用默认值：" + initCount);
        }
        try {
            minCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_MIN_COUNT));
        } catch (Exception e) {
            LOGGER.debug("minCount使用默认值：" + minCount);
        }
        try {
            maxCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_MAX_COUNT));
        } catch (Exception e) {
            LOGGER.debug("maxCount使用默认值：" + maxCount);
        }
        try {
            increasingCount = Integer.parseInt(Configuration.getProperty(Constant.JDBC_INCREASING_COUNT));
        } catch (Exception e) {
            LOGGER.debug("increasingCount使用默认值：" + increasingCount);
        }

    }

    /**
     * 连接池对象
     */
    private static volatile MyDataSourceImpl instance;

    private MyDataSourceImpl() {
        //防止反射破坏单例
        //防止反射通过反射实例化对象而跳过getInstance方法
        //只能在已通过getInstance方法创建好对象后起作用
        //如果一开始就使用反射创建对象的话，由于instance对象并没有被实例化，所以能够一直用反射创建对象
        //要想使用反射创建必须满足instance对象为空，Configuration类中已经加载了配置文件
        if (instance != null) {
            LOGGER.error("Object has been instanced,reject create Object by Reflect!!!");
            throw new RuntimeException("Object has been instanced,reject create Object by Reflect!!!");
        }
        init();
    }

    public static MyDataSourceImpl getInstance() {
        //双重检测锁
        if (null == instance) {
            synchronized (MyDataSourceImpl.class) {
                if (null == instance) {
                    instance = new MyDataSourceImpl();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化连接池
     */
    private void init() {
        //循环给集合中添加初始化连接
        for (int i = 0; i < initCount; i++) {
            boolean flag = conns.add(createConnection());
            if (flag) {
                createdCount++;
            }
        }
        LOGGER.debug("连接池连接初始化----->连接池对象：" + this + "----->连接池可用连接数量：" + createdCount);
    }

    /**
     * 构建数据库连接对象
     *
     * @return 连接
     */
    private Connection createConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (Exception e) {
            LOGGER.error("数据库连接创建失败：", e);
            throw new RuntimeException("数据库连接创建失败：" + e.getMessage());
        }
    }

    /**
     * 连接自动增长
     */
    private synchronized void autoAdd() {
        //增长步长默认为2
        if (createdCount == maxCount) {
            LOGGER.error("连接池中连接已达最大数量,无法再次创建连接");
            throw new RuntimeException("连接池中连接已达最大数量,无法再次创建连接");
        }
        //临界时判断增长个数
        for (int i = 0; i < increasingCount; i++) {
            if (createdCount == maxCount) {
                break;
            }
            conns.add(createConnection());
            createdCount++;
        }

    }

    /**
     * 自动减少连接
     */
    private synchronized void autoReduce() {
        if (createdCount > minCount && conns.size() > 0) {
            //关闭池中空闲连接
            try {
                conns.removeFirst().close();
                createdCount--;
                LOGGER.debug("已关闭多余空闲连接。当前已创建连接数：" + createdCount + "  当前空闲连接数：" + conns.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.debug("空闲连接保留在到连接池中或已被使用。当前已创建连接数量：" + createdCount + "  当前空闲连接数" + conns.size());
        }
    }

    /**
     * 获取池中连接
     *
     * @return 连接
     */
    @Override
    public Connection getConnection() {
        //判断池中是否还有连接
        synchronized (MONITOR) {
            if (conns.size() > 0) {
                LOGGER.debug("获取到连接：" + conns.getFirst() + "  当前已创建连接数量：" + createdCount + "  当前空闲连接数" + (conns.size() - 1));
                return conns.removeFirst();
            }
            //如果没有空连接，则调用自动增长方法
            if (createdCount < maxCount) {
                autoAdd();
                return getConnection();
            }
            //如果连接池连接数量达到上限,则等待连接归还
            LOGGER.debug("连接池中连接已用尽，请等待连接归还");
            try {
                MONITOR.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getConnection();
        }

    }

    /**
     * 归还连接
     *
     * @param conn 连接
     */

    @Override
    public void returnConnection(Connection conn) {

        synchronized (MONITOR) {
            LOGGER.debug("准备归还数据库连接" + conn);
            conns.add(conn);
            MONITOR.notify();
            autoReduce();
        }
    }

    /**
     * 获取当前空闲连接数
     *
     * @return 空闲连接数
     */
    @Override
    public int getIdleCount() {
        return conns.size();
    }

    /**
     * 返回已创建连接数量
     *
     * @return 已创建连接数量
     */
    @Override
    public int getCreatedCount() {
        return createdCount;
    }

    /**
     * 关闭连接池
     */
    public void close(){
        LogUtils.getLogger().debug("正在关闭数据库连接池");
        for(Connection conn:conns){
            try {
                conn.close();
            } catch (SQLException throwables) {
                LogUtils.getLogger().debug("关闭连接出错：{}",throwables.getMessage());
                throwables.printStackTrace();
            }
        }
        LogUtils.getLogger().debug("数据库连接池关闭完成");
    }

}
