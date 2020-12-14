package com.xxbb.framework.simplemybatis.session.defaults;


import com.xxbb.framework.simplemybatis.bean.ColumnInfo;
import com.xxbb.framework.simplemybatis.bean.TableInfo;
import com.xxbb.framework.simplemybatis.constants.Constant;
import com.xxbb.framework.simplemybatis.pool.MyDataSource;
import com.xxbb.framework.simplemybatis.session.Configuration;
import com.xxbb.framework.simplemybatis.session.SqlSession;
import com.xxbb.framework.simplemybatis.session.SqlSessionFactory;
import com.xxbb.framework.simplemybatis.utils.LogUtils;
import com.xxbb.framework.simplemybatis.utils.StringUtils;
import com.xxbb.framework.simplemybatis.utils.ValidationUtils;
import com.xxbb.framework.simplemybatis.utils.XmlParseUtils;
import org.slf4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author xxbb
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    /**
     * 配置对象
     */
    private final Configuration configuration;
    /**
     * 单例工厂对象
     */
    private static volatile DefaultSqlSessionFactory instance;
    /**
     * 日志管理器
     */
    private final Logger logger = LogUtils.getLogger();

    private DefaultSqlSessionFactory(Configuration configuration) {
        //防止反射通过反射实例化对象而跳过getInstance方法
        //只能在已通过getInstance方法创建好对象后起作用
        //如果一开始就使用反射创建对象的话，由于instance对象并没有被实例化，所以能够一直用反射创建对象
        //但由于这里的工厂是通过构建者模式创建的，如果直接反射创建该工厂对象会因为Configuration对象中缺少配置文件的参数而创建失败
        //要想使用反射创建必须满足instance对象为空，Configuration类中已经加载了配置文件
        if (instance != null) {
            throw new RuntimeException("Object has been instanced,please do not create Object by Reflect!!!");
        }
        this.configuration = configuration;
        //将mappedStatement的消信息存储起来，并注册代理工厂
        loadMappersInfo(Configuration.getProperty(Constant.MAPPER_LOCATION).replaceAll("\\.", "/"));
        //加载所访问数据库的所有表结构信息
        loadTableInfo(configuration);
    }

    /**
     * 双重检测锁获取单例工厂
     *
     * @param configuration 配置信息
     * @return 单例工厂
     */
    public static DefaultSqlSessionFactory getInstance(Configuration configuration) {
        if (instance == null) {
            synchronized (DefaultSqlSessionFactory.class) {
                if (instance == null) {
                    instance = new DefaultSqlSessionFactory(configuration);
                }
            }
        }
        return instance;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(this.configuration);
    }

    /**
     * 解析mapper.xml中的信息封装进Configuration对象的mappedStatementMap中
     *
     * @param dirName mapper.xml所在的文件夹名
     */
    private void loadMappersInfo(String dirName) {
        String resource = Objects.requireNonNull
                (DefaultSqlSessionFactory.class.getClassLoader().getResource(dirName)).getPath();
        logger.debug("加载资源路径" + resource);
        File mapperDir = new File(resource);
        //判断该路径是否为文件夹
        if (mapperDir.isDirectory()) {
            //获取文件夹下所有文件
            File[] mappers = mapperDir.listFiles();
            //非空判断
            if (ValidationUtils.isNotEmpty(mappers)) {
                for (File file : mappers) {
                    //如果还存在文件夹则继续获取
                    if (file.isDirectory()) {
                        loadMappersInfo(dirName + "/" + file.getName());
                    } else if (file.getName().endsWith(Constant.MAPPER_FILE_SUFFIX)) {
                        //获取以.xml为后缀的文件,存入Configuration对象的mappedStatementMap中
                        //并注册一个该mapper接口类的代理工厂
                        XmlParseUtils.mapperParser(file, this.configuration);
                    }
                }
            }

        }
    }

    /**
     * 将数据库表信息封装到一个与po类映射的map对象中，存入Configuration对象中
     *
     * @param configuration 当前Configuration对象
     */
    private void loadTableInfo(Configuration configuration) {
        //获取数据库连接，用于读取数据库元数据
        MyDataSource dataSource = this.configuration.getDataSource();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            //获取数据库元数据
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            //当前读取的数据库名称
            String catalog = Configuration.getProperty(Constant.CATALOG);
            //获取该数据的所有表信息
            ResultSet tableResultSet = databaseMetaData.getTables(catalog, "%", "%", new String[]{"TABLE"});
            //存储表信息的集合
            List<TableInfo> tableInfos = new ArrayList<>();
            //遍历表
            while (tableResultSet.next()) {
                //获取表名
                String tableName = tableResultSet.getString("TABLE_NAME");
                //创建表对象
                TableInfo tableInfo = new TableInfo(tableName, new HashMap<>(10), new ArrayList<>(), new ArrayList<>());
                //获取该表的普通列信息
                ResultSet columnResultSet = databaseMetaData.getColumns(catalog, "%", tableName, null);
                //遍历普通列,先将所有列都设置为普通列
                while (columnResultSet.next()) {
                    ColumnInfo columnInfo = new ColumnInfo(columnResultSet.getString("COLUMN_NAME"),
                            columnResultSet.getString("TYPE_NAME"), 0);
                    tableInfo.getColumnInfoMap().put(columnInfo.getName(), columnInfo);
                }
                //获取主键列信息
                ResultSet primaryKeyResultSet = databaseMetaData.getPrimaryKeys(catalog, "%", tableName);
                //遍历
                while (primaryKeyResultSet.next()) {
                    //获取主键列
                    ColumnInfo primaryColumnInfo = tableInfo.getColumnInfoMap().get(primaryKeyResultSet.getString("COLUMN_NAME"));

                    primaryColumnInfo.setKeyType(1);
                    //添加到主键的集合中
                    tableInfo.getPrimaryKeys().add(primaryColumnInfo);
                }
                //由于在SqlSession中需要利用到主键进行修改和删除操作，所以如果当前表没有主键需要抛出异常
                if (tableInfo.getPrimaryKeys().size() == 0) {
                    logger.debug("数据库表" + tableName + "未检测到主键");
                }
                //获取外键列信息
                ResultSet foreignKeyResultSet = databaseMetaData.getExportedKeys(catalog, "%", tableName);
                //遍历
                while (foreignKeyResultSet.next()) {
                    //获取外键列
                    ColumnInfo foreignColumnInfo = tableInfo.getColumnInfoMap().get(foreignKeyResultSet.getString("COLUMN_NAME"));
                    tableInfo.getForeignKeys().add(foreignColumnInfo);
                }
                //反射获取该表对应的po类
                Class<?> clazz = Class.forName(Configuration.getProperty(Constant.PO_LOCATION) + "." + StringUtils.tableNameToClassName(tableName));
                //将类与表的映射关系存入configuration对象的map中
                logger.trace("获取数据库表对象：" + tableInfo);
                this.configuration.getClassToTableInfoMap().put(clazz, tableInfo);
                logger.trace("加载实体类与数据库表的映射：" +
                       Configuration.getProperty(Constant.PO_LOCATION) + "." + StringUtils.tableNameToClassName(tableName) + "<------>" +
                        tableName);
            }

        } catch (SQLException | ClassNotFoundException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            dataSource.returnConnection(conn,this.getClass().getName()+".loadTableInfo");
        }
    }


}
