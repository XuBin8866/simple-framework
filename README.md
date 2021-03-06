# Simple Framework

## 项目背景

此框架为蓝点工作室项目 《生猪养殖管理系统》提供支持而进行的开发。为了提高我们的代码设计能力和加深对软件设计思想的认识，工作室指导老师要求开发《生猪养殖管理系统》不得使用当前主流框架，需要自行研究并编写框架。故开发了simple-framework这款具备IoC、DI、AOP功能，MVC架构和ORM框架的自实现框架，框架功能的实现参考了Spring框架和MyBatis的实现原理。

## 安装 

下载[simple-framework-release](https://github.com/XuBin8866/simple-framework/releases)并引入工程中。
**注意：**
+ 在IDEA2020.3版本中将jar包放在工程内的文件夹后，从Project Structure引入，IDEA的代码检查会提示jar包不存在，但是项目编译和运行没有问题。建议jar包放在工程文件夹外再进行引入
+ 如果是直接配置的tomcat服务器进行启动还需要在项目的<code>WEB-INF/lib</code>路径下放入该jar包否则服务器会报404错误
+ 如果是使用Maven的tomcat插件进行服务器的启动需要在Maven中设置编译该jar包，指定该jar包的路径，否则该jar包不会被编译打包。pom.xml中进行如下修改


```xml
<plugin> 
  <artifactId>maven-compiler-plugin</artifactId>  
  <configuration> 
    <source>1.8</source>  
    <target>1.8</target>  
    <encoding>UTF-8</encoding>  
    <compilerArguments> 
      <!--指定jar包的路径-->  
      <extdirs>${project.basedir}/src/main/resources/lib</extdirs> 
    </compilerArguments> 
  </configuration>  
  <version>3.8.0</version> 
</plugin>			
```

## 使用

#### 1.环境变量配置

###### 基本配置

使用该框架啊需要使用一个properties配置文件指定需要扫描的包和需要访问的数据库相关参数，配置文件名自定义，配置文件中的属性名不可修改，参考如下：

application.properties

```properties
#sspring配置

##spring扫描的包
scanPackage=com.xxbb.demo

#smybatis配置

##mapper接口所在的包
mapper.location=com.xxbb.demo.mapper
##与数据库表对应的po类所在的包
po.location=com.xxbb.demo.domain
##访问的数据库名
catalog=db_orm

##数据连接参数
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/db_orm?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&allowPublicKeyRetrieval=true
jdbc.username=root
jdbc.password=123456
####初始化连接池个数
jdbc.initCount=8
####最小连接池个数
jdbc.minCount=8
####最大连接池个数
jdbc.maxCount=20
####连接池增长步长
jdbc.increasingCount=2
####获取连接的最大等待时间
jdbc.maxWaitingTime=5000
####空闲连接的最大存活时间
jdbc.maxIdleTime=10000
```

###### 日志配置

日志框架使用的是log4j，配置文件参考如下：
log4j.properties

```properties
log4j.rootLogger =DEBUG,CONSOLE,D,E


log4j.appender.CONSOLE = org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.Target = System.out
log4j.appender.CONSOLE.layout = org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern = [%-5p] [%-8t] %d{yyyy-MM-dd HH:mm:ss,SSS} %l%m%n


log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
log4j.appender.D.File = D://logs/log.log
log4j.appender.D.Append = true
log4j.appender.D.Threshold = DEBUG
log4j.appender.D.layout = org.apache.log4j.PatternLayout
log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n


log4j.appender.E = org.apache.log4j.DailyRollingFileAppender
log4j.appender.E.File=D://logs/error.log
log4j.appender.E.Append = true
log4j.appender.E.Threshold = ERROR
log4j.appender.E.layout = org.apache.log4j.PatternLayout
log4j.appender.E.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
```

###### Web配置

使用该框架的MVC功能需要指定该框架的DispatcherServlet对请求进行拦截

web.xml

```xml
<web-app>
  <display-name>dmeo</display-name>
  <servlet>
    <servlet-name>DispatcherServlet</servlet-name>
    <servlet-class>com.xxbb.framework.simplespring.mvc.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:application.properties</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>DispatcherServlet</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>
</web-app>
```

#### 2.功能使用

###### 基础功能

IoC、AOP、DI功能和ORM框架可以独立使用，参考如下：

```java
/**
     * IoC和DI需要组合使用，如果使用了AOP需要在DI前进行织入
     * 同时得益于IoC和DI的操作是串行进行的，不会出现循环依赖的情况
     * ORM框架可直接使用，在创建使用ORM的入口对象SqlSessionFactory时也会将该对象存入IoC容器
     * 但如果Bean对象依赖SqlSessionFactory,则需要将ORM框架的初始化放在DI之前
     */
    @Test
    public void baseFunctionTest(){
        //初始化IoC容器
        BeanContainer beanContainer = BeanContainer.getInstance();
        //指定需要扫描的包，加载bean对象进入IoC容器
        beanContainer.loadBeans("com.xxbb.demo");
        //如果使用了AOP的功能需要先于DI进行织入
        new AspectWeaver().doAspectOrientedProgramming();
        //ORM框架初始化不受以上流程顺序影响，但如果有Bean依赖factory对象则需要先于DI初始化
        SqlSessionFactory factory=new SqlSessionFactoryBuilder().build("application.properties");
        //依赖注入
        new DependencyInject().doDependencyInject();

        //测试基础功能
        HelloService helloService= (HelloService) beanContainer.getBean(HelloService.class);
        helloService.hello();

        //测试ORM框架功能
        System.out.println(factory.openSession());
        UserService userService= (UserService) beanContainer.getBean(UserService.class);
        userService.select();
        
        //测试日志
        Logger logger= LogUtil.getLogger();
        logger.info("log test");        
        
    }
```

**注意：**

######  ORM功能

由于ORM框架涉及到实体类与数据库表的字段映射，所以在使用ORM框架之前需要先在工程中创建与数据库表及表中字段对应的实体类，其中实体类名和数据库表名存在驼峰与下划线的映射关系，允许数据库表名前加上<code>t_</code>前缀。如实体类<code>GoodsType</code>可对应数据库表<code>goods_type</code>或<code>t_goods_type</code>。实体类属性则需要与数据库字段严格安装驼峰与下划线的映射关系。

数据库操作默认是关闭自动提交，可以使用 <code>SqlSession::setAutocommit (boolean flag)</code > 开启自动提交，默认值为 false（关闭自动提交）。在自动提交关闭的状态下必须使用 <code>SqlSession::commit ()</code> 方法手动提交事务，事务抛出异常通过<code>SqlSession::rollback ()</code>进行回滚。在数据库操作完成后需要调用 <code>SqlSession::close ()</code> 方法释放 session 持有的数据库连接对象回连接池。

ORM提供了两种对数据库的操作方式

**1.获取接口代理对象读取 mapper.xml 文件**

mapper 文件和 mapper 接口建议同名相互映射，注意 xml 中 namespace 与 mapper 接口的映射关系。查询语句中需要指明结果集要封装的 po 类全限定名，该类必须和所查询的数据库的表对应

UserMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mapper namespace="com.xxbb.demo.mapper.UserMapper">

    <select id="getUser" resultType="com.xxbb.demo.domain.User">
        select * from t_user where id = #{id}
    </select>

    <select id="getAll" resultType="com.xxbb.demo.domain.User">
        select * from t_user
    </select>
    <insert id="insertUser">
        insert into t_user(id,username,password,if_freeze) values(#{id},#{username},#{password},#{ifFreeze})
    </insert>
    <update id="updateUser">
        update t_user set username =#{username} where id = #{}
    </update>
    <delete id="deleteUser">
        delete from t_user where id=#{id}
    </delete>
</mapper>
```

UserMapper.java

```java

package com.xxbb.demo.mapper;


import com.xxbb.demo.domain.User;

import java.util.List;


/**
 * UserMapper.java
 *
 * @author PLF
 * @date 2019年3月6日
 */
public interface UserMapper {

    /**
     * 获取单个user
     *
     * @param id id
     * @return user
     */
    User getUser(String id);

    /**
     * 获取所有用户
     *
     * @return users
     */
    List<User> getAll();

    /**
     * 更新用户
     *
     * @param name name
     * @param id   id
     * @return 受影响的行数
     */
    int updateUser(String name, Integer id);

    /**
     * 添加
     *
     * @param id       id
     * @param username username
     * @param password password
     * @param ifFreeze if_freeze
     * @return 受影响的行数
     */
    int insertUser(Integer id, String username, String password, Integer ifFreeze);

    /**
     * 删除用户
     *
     * @param id id
     * @return 受影响的行数
     */
    int deleteUser(Integer id);
}
```

使用方式参考：

```java
    public void sqlTestMain() {
        //构建sql工厂
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        try {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            System.out.println("testMain.select：" + userMapper.getAll());
            System.out.println("testMain.update：" + userMapper.updateUser("xxbb", 1));
            System.out.println("testMain.insert:" + userMapper.insertUser(24, "zzxx", "123456", 1));
            System.out.println("testMain.delete: " + userMapper.deleteUser(24));
            session.commit();
        } catch (Exception e) {
            LogUtil.getLogger().error("sql test error:{}", e.getMessage());
            session.rollback();
        } finally {
            session.close();
        }
    }
```

**2.以面向对象的方式**

只适用于增删改操作，且修改和删除语句的检索条件是主键，无法进行范围性的修改和删除。如果数据库表没有主键则无法进行修改和删除操作。如果传入对象对应主键的成员变量值为空则无法修改数据，即受影响的行数为 0。

```java
     public void testUpdate() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(1);
        u.setUsername("xxbb");
        try {
            System.out.println("testUpdate：" + session.update(u));
            session.commit(); 
        } catch (Exception e) {
            session.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    public void testInsert() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        u.setUsername("zzxx");
        u.setPassword("123456");
        u.setIfFreeze(1);
        try {
            System.out.println("testInsert：" + session.insert(u));
            session.commit();
            
        } catch (Exception e) {
            session.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    public void testDelete() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        try {
            System.out.println("testDelete:" + session.delete(u));
            session.commit();
        } catch (Exception e) {
            session.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
```



###### Web功能

配置<code>web.xml</code>,使用该框架的 MVC 功能需要指定该框架的 DispatcherServlet 对请求进行拦截，之后的使用方式和Spring框架的使用方式类似。请求和相应对象在Controller通过方法参数获取，用例如下：
```java
public String getHttpServletObject(@RequestParam("map")  Map<String,String[]> requestParamMap,
                                       @RequestParam("request") HttpServletRequest request,
                                       @RequestParam("response") HttpServletResponse response)
    {
        System.out.println("requestParamMap:"+requestParamMap);
        System.out.println("request:"+request);
        System.out.println("response:"+request);
        return "success";
    }
```
关于Controller层编写的用例请参考项目内demo的[>>HelloController](https://github.com/XuBin8866/simple-framework/blob/master/demo/src/main/java/com/xxbb/demo/controller/HelloController.java)

项目具体的功能和业务逻辑介绍参考[>> 自实现简易SSM框架](https://github.com/XuBin8866/my-docs/blob/main/simple-framework-docs/%E8%87%AA%E5%AE%9E%E7%8E%B0%E7%AE%80%E6%98%93SSM%E6%A1%86%E6%9E%B6.md)、[>> 自实现简易MyBatis框架](https://github.com/XuBin8866/my-docs/blob/main/simple-framework-docs/%E8%87%AA%E5%AE%9E%E7%8E%B0%E7%AE%80%E6%98%93MyBatis%E6%A1%86%E6%9E%B6.md)

