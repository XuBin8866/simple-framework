package com.xxbb.framework.simplemybatis.bean;

/**
 * 封装数据库表的一个字段的信息
 *
 * @author xxbb
 */
public class ColumnInfo {
    /**
     * 字段名
     */
    private String name;
    /**
     * 字段数据类型
     */
    private String dataType;
    /**
     * 字段的键类型(规定 0:普通键，1：主键，2：外键)
     */
    private int keyType;

    public ColumnInfo() {
    }

    public ColumnInfo(String name, String dataType, int keyType) {
        this.name = name;
        this.dataType = dataType;
        this.keyType = keyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", keyType=" + keyType +
                '}';
    }
}
