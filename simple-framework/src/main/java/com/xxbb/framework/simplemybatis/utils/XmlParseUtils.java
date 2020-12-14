package com.xxbb.framework.simplemybatis.utils;



import com.xxbb.framework.simplemybatis.constants.Constant;
import com.xxbb.framework.simplemybatis.mapping.MappedStatement;
import com.xxbb.framework.simplemybatis.session.Configuration;
import com.xxbb.framework.simplespring.util.LogUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;

/**
 * 解析配置了sql语句的mapper.xml文件
 *
 * @author xxbb
 */
public class XmlParseUtils {
    /**
     * 解析mapper.xml中的sql语句
     *
     * @param filename      mapper.xml的文件位置
     * @param configuration 配置文件
     */
    @SuppressWarnings("rawtypes")
    public static void mapperParser(File filename, Configuration configuration) {
        try {
            //创建读取器
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding(Constant.CHARSET_UTF8);

            //读取文件内容
            Document document = saxReader.read(filename);

            //获取xml中的根元素
            Element rootElement = document.getRootElement();

            //判断根元素是否正确
            if (!Constant.XML_ROOT_LABEL.equals(rootElement.getName())) {
                System.out.println("mapper.xml文件的元素错误");
                return;
            }
            //获取标签内容
            String namespace = rootElement.attributeValue(Constant.XML_NAMESPACE);
            LogUtil.getLogger().debug("注册mapper代理工厂：{}",namespace);
            //遍历根元素内的标签
            for (Iterator iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
                //封装mappedStatement信息
                MappedStatement statement = new MappedStatement();
                //遍历的标签
                Element element = (Element) iterator.next();
                //标签名
                String elementName = element.getName();
                //标签id
                String elementId = element.attributeValue(Constant.XML_ELEMENT_ID);
                //标签返回值
                String returnType = element.attributeValue(Constant.XML_ELEMENT_RESULT_TYPE);
                //sql语句内容
                String sql = element.getStringValue();
                //设置sql的唯一Id
                String id = namespace + "." + elementId;
                //封装信息
                //判断标签名是否已定义，未定义则使用default
                if (!Constant.SqlType.SELECT.value().equals(elementName) &&
                        !Constant.SqlType.UPDATE.value().equals(elementName) &&
                        !Constant.SqlType.DELETE.value().equals(elementName) &&
                        !Constant.SqlType.INSERT.value().equals(elementName)) {
                    System.out.println("mapper.xml中存在未定义标签:" + elementName);
                    statement.setSqlType(Constant.SqlType.DEFAULT.value());
                } else {
                    statement.setSqlType(elementName);
                }
                //判断是否有返回值类型
                if (null != returnType && !"".equals(returnType)) {
                    statement.setReturnType(returnType);
                }

                statement.setId(id);
                statement.setNamespace(namespace);
                statement.setSql(StringUtils.stringTrim(sql));

                //封装进configuration对象
                configuration.addMappedStatement(id, statement);
                //注册一个该mapper对象接口类的代理工厂
                configuration.addMapper(Class.forName(namespace));
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
