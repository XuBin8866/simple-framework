package com.xxbb.framework.simplespring.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 静态日志管理器，每个线程拥有自己的日志管理对象
 * @author xxbb
 */
public class LogUtil {
    public static Logger getLogger(){
        return LoggerFactory.getLogger(Thread.currentThread().getName());
    }
}
