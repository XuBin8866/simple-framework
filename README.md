# simple-framework
整合了之前自实现的Spring和MyBatis简易框架，项目中的demo模块为在web环境下对框架功能的简单测试

**框架使用方式**

和Spring框架的使用方式类似，如果直接将该框架作为Module引入则无需做任何修改，框架的配置文件名默认为application.properties,可在DispatcherServlet中进行对参数命名进行修改

```java
@WebServlet(name="DispatcherServlet" ,urlPatterns="/*",
initParams ={@WebInitParam(name="contextConfigLocation",value = "application.properties")} )
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
