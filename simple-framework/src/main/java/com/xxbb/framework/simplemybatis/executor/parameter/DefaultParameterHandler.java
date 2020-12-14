package com.xxbb.framework.simplemybatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author xxbb
 */
public class DefaultParameterHandler implements ParameterHandler {
    /**
     * 传入参数
     */
    private final Object parameter;

    public DefaultParameterHandler(Object parameter) {
        this.parameter = parameter;
    }

    /**
     * 设置参数到预处理对象中
     *
     * @param paramPreparedStatement 预处理对象
     */
    @Override
    public void setParameters(PreparedStatement paramPreparedStatement) {
        try {
            if (null != parameter) {

                if (parameter.getClass().isArray()) {
                    Object[] params = (Object[]) parameter;
                    for (int i = 0; i < params.length; i++) {
                        paramPreparedStatement.setObject(i + 1, params[i]);
                    }
                } else {
                    paramPreparedStatement.setObject(1, parameter);
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
