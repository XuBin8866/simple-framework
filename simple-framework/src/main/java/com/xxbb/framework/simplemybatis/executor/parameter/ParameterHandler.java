package com.xxbb.framework.simplemybatis.executor.parameter;

import java.sql.PreparedStatement;

/**
 * 处理传入参数
 *
 * @author xxbb
 */
public interface ParameterHandler {
    /**
     * 设置参数
     *
     * @param paramPreparedStatement 预处理对象
     */
    void setParameters(PreparedStatement paramPreparedStatement);
}
