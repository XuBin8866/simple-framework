# simple-framework
整合了之前自实现的Spring和MyBatis简易框架，项目中的demo模块为在web环境下对框架功能的简单测试

#### **框架使用方式**

和Spring框架的使用方式类似，如果直接将该框架作为Module引入则无需做任何修改，框架的配置文件名默认为application.properties,可在DispatcherServlet中进行对参数命名进行修改

```java
@WebServlet(name="DispatcherServlet" ,urlPatterns="/*",
initParams ={@WebInitParam(name="contextConfigLocation",value = "application.properties")},
loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {}
```

如果想把该框架打成jar包引入，则需要在打包前先把上述的注解删除再进行打包，在引入该jar包的项目的web.xml中将上述注解的内容进行配置。

application.properties配置参考

```properties
##sspring配置

#spring扫描的包
scanPackage=com.xxbb.demo

##smybatis配置

####mapper接口所在的包
mapper.location=com.xxbb.demo.mapper
####与数据库表对应的po类所在的包
po.location=com.xxbb.demo.domain
####访问的数据库名
catalog=db_orm
####数据连接参数
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/db_orm?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true
jdbc.username=root
jdbc.password=123456
#初始化连接池个数
jdbc.initCount=8
#最小连接池个数
jdbc.minCount=8
#最大连接池个数
jdbc.maxCount=20
#连接池增长步长
jdbc.increasingCount=2
```

log4j.properties日志配置参考：

```properties
log4j.rootLogger =DEBUG,CONSOLE,D,E


log4j.appender.CONSOLE = org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.Target = System.out
log4j.appender.CONSOLE.layout = org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern = [%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} %l%m%n


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
#### **仿MyBatis的ORM框架使用方式**
数据库操作默认是关闭自动提交，可以使用<code>SqlSession::setAutocommit(boolean flag)</code>开启自动提交，默认值为false（关闭自动提交）。在自动提交关闭的状态下必须使用<code>SqlSession::commit()</code>方法手动提交事务。在数据库操作完成后需要调用code>SqlSession::close()</code>方法释放session持有的数据库连接对象回连接池。
###### 1.获取接口代理对象读取mapper.xml文件的方式
mapper文件和mapper接口必须同名对应
```java
public void sqlTestMain() {
        //构建sql工厂
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        System.out.println("testMain.select：" + userMapper.getAll());
        System.out.println("testMain.update：" + userMapper.updateUser("xxbb", 1));
        System.out.println("testMain.insert:" + userMapper.insertUser(24, "zzxx", "123456", 1));
        System.out.println("testMain.delete: " + userMapper.deleteUser(24));
        //提交事务
        session.commit();
        //关闭事务，释放数据库连接
        session.close();
    }
```
###### 2.以面向对象的方式
只适用于增删改操作，且修改和删除语句的检索条件是主键，无法进行范围性的我修改和删除。如果数据库表没有主键则无法进行修改和删除操作。如果传入对象对应主键的成员变量值为空则无法修改数据，即受影响的行数为0
```java
public void testUpdate() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(1);
        u.setUsername("xxbb");
        System.out.println("testUpdate：" + session.update(u));
        session.commit();
        session.close();
    }

    public void testInsert() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        u.setUsername("zzxx");
        u.setPassword("123456");
        u.setIfFreeze(1);
        System.out.println("testInsert：" + session.insert(u));
        session.commit();
        session.close();
    }

    public void testDelete() {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build("application.properties");
        SqlSession session = factory.openSession();
        User u = new User();
        u.setId(24);
        System.out.println("testDelete:" + session.delete(u));
        session.commit();
        session.close();
    }
```
