package com.xxbb.framework.simplemybatis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每个线程获取不同的Logger对象
 *
 * @author xxbb
 */
public class LogUtils {
    public static Logger getLogger() {
        return LoggerFactory.getLogger(Thread.currentThread().getName());
    }
}
