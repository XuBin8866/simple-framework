package com.xxbb.framework.simplemybatis.constants;

/**
 * 配置文件和标签的名称常量
 *
 * @author xxbb
 */
public interface Constant {
    /**
     * 编码格式
     */
   String CHARSET_UTF8 = "UTF-8";

    //读取数据库配置文件中的信息
    /**
     * mapper.xml所在的包
     */
    String MAPPER_LOCATION = "mapper.location";
    /**
     * 与数据库表对应的po类所在的包
     */
    String PO_LOCATION = "po.location";
    /**
     * 所访问的数据库名
     */
    String CATALOG = "catalog";
    /**
     * 数据库连接驱动
     */
    String JDBC_DRIVER = "jdbc.driver";
    /**
     * 数据库连接url
     */
    String JDBC_URL = "jdbc.url";
    /**
     * 登录用户名
     */
    String JDBC_USERNAME = "jdbc.username";
    /**
     * 密码
     */
    String JDBC_PASSWORD = "jdbc.password";
    /**
     * 初始化连接个数
     */
    String JDBC_INIT_COUNT = "jdbc.initCount";
    /**
     * 最小连接个数
     */
    String JDBC_MIN_COUNT = "jdbc.minCount";
    /**
     * 最大连接个数
     */
    String JDBC_MAX_COUNT = "jdbc.maxCount";
    /**
     * 连接增长步长
     */
    String JDBC_INCREASING_COUNT = "jdbc.increasingCount";
    /**
     * 获取连接的最大等待时间
     */
    String JDBC_MAX_WAITING_TIME="jdbc.maxWaitingTime";
    /**
     * 空闲连接的最大存活时间
     */
    String JDBC_MAX_IDLE_TIME="jdbc.maxIdleTime";

    //mapper文件的标签的信息
    /**
     * mapper 文件的后缀
     */
    String MAPPER_FILE_SUFFIX = ".xml";
    /**
     * xml文件的根标签
     */
    String XML_ROOT_LABEL = "mapper";
    /**
     * sql语句的ID
     */
    String XML_ELEMENT_ID = "id";
    /**
     * mapper.xml的命名空间
     */
    String XML_NAMESPACE = "namespace";
    /**
     * sql语句的返回对象
     */
    String XML_ELEMENT_RESULT_TYPE = "resultType";


    /**
     * 枚举类型定义mapper.xml中的标签
     */
    enum SqlType {
        /**
         * CRUD标签
         */
        SELECT("select"),
        INSERT("insert"),
        UPDATE("update"),
        DELETE("delete"),
        DEFAULT("default");

        private final String value;

        SqlType(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}

